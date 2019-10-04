package com.lucasvanbeek.zermelodiscord.tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.lucasvanbeek.zermelodiscord.utils.DirectMessageConsumer;
import com.lucasvanbeek.zermelodiscord.utils.EmbedHelper;
import com.lucasvanbeek.zermelodiscord.utils.data.AnnouncementData;
import com.lucasvanbeek.zermelodiscord.utils.data.LinkData;
import com.lucasvanbeek.zermelodiscord.utils.data.LinkedUser;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import nl.mrwouter.zermelo4j.ZermeloAPI;
import nl.mrwouter.zermelo4j.annoucements.Announcement;

public class AnnouncementTask extends TimerTask {

	private JDA jda;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM dd");

	public AnnouncementTask(JDA jda) {
		this.jda = jda;
	}

	@Override
	public void run() {
		long startMs = System.currentTimeMillis();
		System.out.println("Start running AnnouncementTask.");
		AnnouncementData.getInstance().pullData();
		
		int announcements = 0;
		for (LinkedUser user : LinkData.getInstance().getLinkedUsers()) {
			List<Long> knownAnnouncements = AnnouncementData.getInstance().getKnownAnnouncementIds(user.getUserId());
			List<Announcement> newAnnouncements = new ArrayList<>();

			ZermeloAPI api = ZermeloAPI.getAPI(user.getSchool(), user.getAccessToken());
			for (Announcement announcement : api.getAnnouncements()) {
				if (!knownAnnouncements.contains(announcement.getId())) {
					newAnnouncements.add(announcement);
					AnnouncementData.getInstance().addKnownAnnouncement(user.getUserId(), announcement.getId());
					announcements++;
				}
			}
			if (!newAnnouncements.isEmpty()) {
				User discordUser = jda.getUserById(user.getUserId());
				if (discordUser == null) {
					break;
				}

				EmbedBuilder announcementEmbed = EmbedHelper.getInstance()
						.createAnnouncementAlert(discordUser.getName() + "#" + discordUser.getDiscriminator());

				for (Announcement announcement : newAnnouncements) {
					announcementEmbed
							.addField(
									announcement.getTitle() + " ("
											+ dateFormat.format(new Date(announcement.getStart() * 1000)) + ")",
									announcement.getText(), false);
				}
				discordUser.openPrivateChannel().queue((channel) -> {
					channel.sendMessage(announcementEmbed.build()).queue();
				}, new DirectMessageConsumer<Throwable>(discordUser, null));
			}
		}
		System.out.println("Succesfully ran AnnouncementTask task. Found " + announcements
				+ " new announcements. (Took " + (System.currentTimeMillis() - startMs) + "ms)");
	}
}
