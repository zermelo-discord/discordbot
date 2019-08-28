package com.lucasvanbeek.zermelodiscord.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.lucasvanbeek.zermelodiscord.utils.EmbedHelper;
import com.lucasvanbeek.zermelodiscord.utils.commands.BotCommand;
import com.lucasvanbeek.zermelodiscord.utils.commands.Command;
import com.lucasvanbeek.zermelodiscord.utils.data.LinkData;
import com.lucasvanbeek.zermelodiscord.utils.data.LinkedUser;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import nl.mrwouter.zermelo4j.ZermeloAPI;
import nl.mrwouter.zermelo4j.annoucements.Announcement;
import nl.mrwouter.zermelo4j.appointments.Appointment;

public class ScheduleCMD implements BotCommand {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

	@Override
	public void execute(Command cmd, String[] args, Message msg, ChannelType type) {
		if (!LinkData.getInstance().isLinked(msg.getAuthor().getIdLong())) {
			msg.getChannel()
					.sendMessage(
							EmbedHelper.getInstance().createError("Jouw account is niet gekoppeld aan de ZermeloBot!"))
					.queue();
			return;
		}
		Calendar startCal = Calendar.getInstance();
		startCal.set(Calendar.HOUR_OF_DAY, 0);
		startCal.set(Calendar.MINUTE, 0);
		if (args.length >= 1 && args[0].equalsIgnoreCase("morgen")) {
			startCal.add(Calendar.DAY_OF_YEAR, 1);
		}else if (args.length >= 1 && args[0].equalsIgnoreCase("overmorgen")) {
			startCal.add(Calendar.DAY_OF_YEAR, 2);
		}else if (args.length >= 1 && args[0].equalsIgnoreCase("gister")) {
			startCal.add(Calendar.DAY_OF_YEAR, -1);
		}
		

		Calendar endCal = (Calendar) startCal.clone();
		endCal.set(Calendar.HOUR_OF_DAY, 23);
		startCal.set(Calendar.MINUTE, 59);

		LinkedUser user = LinkData.getInstance().getUser(msg.getAuthor().getIdLong());
		ZermeloAPI api = ZermeloAPI.getAPI(user.getSchool(), user.getAccessToken());

		List<Appointment> appointments = new ArrayList<>();
		String announcements = "";
		for (Appointment app : api.getAppointments(startCal.getTime(), endCal.getTime())) {
			appointments.add(app);
		}
		for (Announcement ann : api.getAnnouncements()) {
			announcements = announcements + "-" + ann.getText() + "\n";
		}

		EmbedBuilder scheduleEmbed = EmbedHelper.getInstance()
				.createScheduleMessage(msg.getAuthor().getName() + "#" + msg.getAuthor().getDiscriminator());
		if (!announcements.isEmpty()) {
			scheduleEmbed.addField("Aankondigingen", announcements, false);
		}

		if (appointments.isEmpty()) {
			scheduleEmbed.addField("Lessen", "Geen! :tada:", false);
		} else {
			for (Appointment app : appointments) {
				String startTime = (app.getStartTimeSlot() == -1 ? dateFormat.format(new Date(app.getStart() * 1000))
						: "" + app.getStartTimeSlot());
				String remark = app.getRemark().isEmpty() ? "" : "\nOpmerking: " + app.getRemark().replace("\n", " ");

				String cancelStriped = app.isCancelled() ? "~~" : "";

				String changed = app.isModified() ? "\nAanpassing: " + app.getChangeDescription() : "";

				String location = app.getLocations().isEmpty() ? "" : "\nDocent: " + String.join(", ", app.getLocations());
				String teacher = app.getTeachers().isEmpty() ? "" : "\nLokaal: " + String.join(", ", app.getTeachers());

				scheduleEmbed.addField(
						cancelStriped + "Lesuur " + startTime + " (" + String.join(", ", app.getSubjects()) + ")" + cancelStriped,
								location
								+ teacher
								+ "\nTijd: " + dateFormat.format(new Date(app.getStart() * 1000)) + " - " + dateFormat.format(new Date(app.getEnd() * 1000)) 
								+ remark 
								+ changed,
						false);
			}
		}

		msg.getChannel().sendMessage(scheduleEmbed.build()).queue();

	}
}
