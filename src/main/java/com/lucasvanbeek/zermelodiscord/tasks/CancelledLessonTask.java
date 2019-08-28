package com.lucasvanbeek.zermelodiscord.tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.lucasvanbeek.zermelodiscord.utils.DirectMessageConsumer;
import com.lucasvanbeek.zermelodiscord.utils.EmbedHelper;
import com.lucasvanbeek.zermelodiscord.utils.data.CancelledData;
import com.lucasvanbeek.zermelodiscord.utils.data.LinkData;
import com.lucasvanbeek.zermelodiscord.utils.data.LinkedUser;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import nl.mrwouter.zermelo4j.ZermeloAPI;
import nl.mrwouter.zermelo4j.appointments.Appointment;
import nl.mrwouter.zermelo4j.appointments.AppointmentComparator;

public class CancelledLessonTask extends TimerTask {

	private JDA jda;
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM dd");

	public CancelledLessonTask(JDA jda) {
		this.jda = jda;
	}

	@Override
	public void run() {
		long startMs = System.currentTimeMillis();
		System.out.println("Start running CancelledLessonTask task.");
		CancelledData.getInstance().throwAwayOldCancellations();
		
		int cancellations = 0;
		for (LinkedUser user : LinkData.getInstance().getLinkedUsers()) {
			List<Long> knownCancelledLessons = CancelledData.getInstance().getKnownCancelledLessonIDs(user.getUserId());
			List<Appointment> newCancelledLessons = new ArrayList<>();

			Date endDate = new Date();
			endDate.setTime(endDate.getTime() + (86400000 * 7));

			ZermeloAPI api = ZermeloAPI.getAPI(user.getSchool(), user.getAccessToken());
			for (Appointment app : api.getAppointments(new Date(), endDate)) {
				if (app.isCancelled() && !knownCancelledLessons.contains(app.getId())) {
					newCancelledLessons.add(app);
					CancelledData.getInstance().addKnownCancel(user.getUserId(), app.getId(), app.getStart() * 1000);
					cancellations++;
				}
			}
			if (!newCancelledLessons.isEmpty()) {
				User discordUser = jda.getUserById(user.getUserId());
				if (discordUser == null) {
					break;
				}
				Collections.sort(newCancelledLessons, new AppointmentComparator());

				EmbedBuilder cancelledLessonEmbed = EmbedHelper.getInstance()
						.createCancelledAlert(discordUser.getName() + "#" + discordUser.getDiscriminator());

				for (Appointment app : newCancelledLessons) {

					String startTime = (app.getStartTimeSlot() == -1
							? timeFormat.format(new Date(app.getStart() * 1000))
							: "" + app.getStartTimeSlot());

					String teachers = app.getTeachers().isEmpty() ? "" : "\nDocent: " + String.join(", ", app.getTeachers());
 					
					cancelledLessonEmbed.addField(
							"Lesuur " + startTime + " op " + dateFormat.format(app.getStart()  * 1000) + " valt uit!",
							"Vak: " + String.join(", ", app.getSubjects()) 
							+ teachers,
							false);
				}
				discordUser.openPrivateChannel().queue((channel) -> {
					channel.sendMessage(cancelledLessonEmbed.build()).queue();
				}, new DirectMessageConsumer<Throwable>(discordUser, null));
			}
		}
		System.out.println("Succesfully ran CancelledLessonTask task. Found " + cancellations + " cancellations. (Took " + (System.currentTimeMillis() - startMs) + "ms)");
	}
}