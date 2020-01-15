package com.lucasvanbeek.zermelodiscord.commands;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.lucasvanbeek.zermelodiscord.Main;
import com.lucasvanbeek.zermelodiscord.utils.EmbedHelper;
import com.lucasvanbeek.zermelodiscord.utils.commands.BotCommand;
import com.lucasvanbeek.zermelodiscord.utils.commands.Command;
import com.lucasvanbeek.zermelodiscord.utils.data.LinkData;
import com.lucasvanbeek.zermelodiscord.utils.data.LinkedUser;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
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

		boolean hasMention = false;
		User mentionedUser = null;
		if (args.length >= 1) {
			int argIncrement = 0;
			List<Long> userIds = Arrays.asList(args).stream()
					.filter(arg -> arg.contains("<@!") && arg.contains(">")
							&& isInteger(arg.replace("<@!", "").replace(">", ""), 10))
					.map(arg -> Long.valueOf(arg.replace("<@!", "").replace(">", ""))).collect(Collectors.toList());

			if (userIds.size() == 1) {
				mentionedUser = Main.getInstance().getJDA().getUserById(userIds.get(0));

				if (mentionedUser == null || !LinkData.getInstance().isLinked(mentionedUser.getIdLong())) {
					msg.getChannel().sendMessage(EmbedHelper.getInstance()
							.createError("De gebruiker heeft zijn of haar Zermelo niet gekoppeld!")).queue();
					return;
				}
				hasMention = true;
				if (args.length > 1)
					argIncrement = 1;
			}
			if (args[argIncrement].equalsIgnoreCase("morgen")) {
				startCal.add(Calendar.DAY_OF_YEAR, 1);
			} else if (args[argIncrement].equalsIgnoreCase("overmorgen")) {
				startCal.add(Calendar.DAY_OF_YEAR, 2);
			} else if (args[argIncrement].equalsIgnoreCase("gister")) {
				startCal.add(Calendar.DAY_OF_YEAR, -1);
			}
		}

		Calendar endCal = (Calendar) startCal.clone();
		endCal.set(Calendar.HOUR_OF_DAY, 23);
		startCal.set(Calendar.MINUTE, 59);

		LinkedUser user;
		ZermeloAPI api;
		if (!hasMention) {
			user = LinkData.getInstance().getUser(msg.getAuthor().getIdLong());
			api = ZermeloAPI.getAPI(user.getSchool(), user.getAccessToken());
		} else {
			user = LinkData.getInstance().getUser(mentionedUser.getIdLong());
			api = ZermeloAPI.getAPI(user.getSchool(), user.getAccessToken());
		}

		StringBuilder announcements = new StringBuilder();
		List<Appointment> appointments = api
				.getAppointmentParticipations(startCal.get(Calendar.YEAR), startCal.get(Calendar.WEEK_OF_YEAR)).stream()
				.filter(app -> app.getStart() >= (startCal.getTimeInMillis() / 1000)
						&& app.getEnd() <= (endCal.getTimeInMillis() / 1000))
				.collect(Collectors.toList());

		for (Announcement ann : api.getAnnouncements().stream()
				.filter(ann -> ann.getStart() >= (startCal.getTimeInMillis() / 1000)
						&& ann.getEnd() >= (startCal.getTimeInMillis() / 1000))
				.collect(Collectors.toList())) {
			announcements.append("- ").append(ann.getText()).append("\n");
		}

		EmbedBuilder scheduleEmbed = null;
		if (!hasMention) {
			scheduleEmbed = EmbedHelper.getInstance()
					.createScheduleMessage(msg.getAuthor().getName() + "#" + msg.getAuthor().getDiscriminator());
		} else {
			scheduleEmbed = EmbedHelper.getInstance()
					.createScheduleMessage(mentionedUser.getName() + "#" + mentionedUser.getDiscriminator());
		}

		if (announcements.length() > 0) {
			scheduleEmbed.addField("Aankondigingen", announcements.toString(), false);
		}

		if (appointments.isEmpty()) {
			scheduleEmbed.addField("Lessen", "Geen! :tada:", false);
		} else {
			for (Appointment app : appointments) {
				String startTime = (app.getStartTimeSlot() == null ? dateFormat.format(new Date(app.getStart() * 1000))
						: "" + app.getStartTimeSlot().replaceAll("[^\\d]", ""));
				String remark = app.getRemark().isEmpty() ? "" : "\nOpmerking: " + app.getRemark().replace("\n", " ");

				String cancelStriped = app.isCancelled() ? "~~" : "";

				String changed = app.isModified() ? "\nAanpassing: " + app.getChangeDescription() : "";

				String location = app.getLocations().isEmpty() ? ""
						: "\nDocent: " + String.join(", ", app.getTeachers());
				String teacher = app.getTeachers().isEmpty() ? ""
						: "\nLokaal: " + String.join(", ", app.getLocations());

				scheduleEmbed.addField(
						cancelStriped + "Lesuur " + startTime + " (" + String.join(", ", app.getSubjects()) + ")"
								+ cancelStriped,
						location + teacher + "\nTijd: " + dateFormat.format(new Date(app.getStart() * 1000)) + " - "
								+ dateFormat.format(new Date(app.getEnd() * 1000)) + remark + changed,
						false);
			}
		}

		msg.getChannel().sendMessage(scheduleEmbed.build()).queue();
	}

	public static boolean isInteger(String s, int radix) {
		if (s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (i == 0 && s.charAt(i) == '-') {
				if (s.length() == 1)
					return false;
				else
					continue;
			}
			if (Character.digit(s.charAt(i), radix) < 0)
				return false;
		}

		return true;
	}
}
