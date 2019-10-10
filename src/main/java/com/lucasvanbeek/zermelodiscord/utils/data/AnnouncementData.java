package com.lucasvanbeek.zermelodiscord.utils.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnnouncementData {

	private static AnnouncementData instance;
	private static HashMap<Long, List<Long>> knownAnnouncements = new HashMap<>();

	public static AnnouncementData getInstance() {
		if (instance == null) {
			instance = new AnnouncementData();
		}
		return instance;
	}

	public void pullData() {
		knownAnnouncements.clear();
		try (Connection connection = HikariSQL.getInstance().getConnection();) {

			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT `userId`, `announcementId` FROM Announcements");

			while (result.next()) {
				long userId = result.getLong("userId");
				long announcementId = result.getLong("announcementId");

				List<Long> announcementIds = knownAnnouncements.containsKey(userId) ? knownAnnouncements.get(userId)
						: new ArrayList<Long>();
				announcementIds.add(announcementId);
				announcementIds.remove(userId);
				knownAnnouncements.put(userId, announcementIds);
			}

			result.close();
			statement.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addKnownAnnouncement(long userId, long announcementId) {
		try (Connection connection = HikariSQL.getInstance().getConnection();) {
			PreparedStatement statement = connection
					.prepareStatement("INSERT INTO `Announcements` (userId, announcementId) VALUES (?, ?)");
			statement.setLong(1, userId);
			statement.setLong(2, announcementId);

			statement.execute();
			statement.close();

			List<Long> announcementIds = knownAnnouncements.containsKey(userId) ? knownAnnouncements.get(userId)
					: new ArrayList<Long>();
			announcementIds.add(announcementId);
			announcementIds.remove(userId);
			knownAnnouncements.put(userId, announcementIds);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public List<Long> getKnownAnnouncementIds(long userId) {
		return knownAnnouncements.containsKey(userId) ? knownAnnouncements.get(userId) : new ArrayList<Long>();
	}
}
