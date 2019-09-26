package com.lucasvanbeek.zermelodiscord.utils.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CancelledData {

	private static CancelledData instance;
	private static HashMap<Long, List<Long>> knownCancelledAppointments = new HashMap<>();

	public static CancelledData getInstance() {
		if (instance == null) {
			instance = new CancelledData();
		}
		return instance;
	}

	public void throwAwayOldCancellations() {
		try (Connection connection = HikariSQL.getInstance().getConnection();) {
			PreparedStatement statement = connection
					.prepareStatement("DELETE FROM `cancelledLessons` WHERE lessonStartTime < ?");
			//Current time - 1 week.
			statement.setLong(1, System.currentTimeMillis() - 604800000);
			statement.executeUpdate();
			statement.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		pullData();
	}

	public void pullData() {
		knownCancelledAppointments.clear();
		try (Connection connection = HikariSQL.getInstance().getConnection();) {

			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT `userId`, `lessonId` FROM cancelledLessons");

			while (result.next()) {
				long userId = result.getLong("userId");
				long cancelledLessonId = result.getLong("lessonId");

				List<Long> cancelledIds = knownCancelledAppointments.containsKey(userId)
						? knownCancelledAppointments.get(userId)
						: new ArrayList<Long>();
				cancelledIds.add(cancelledLessonId);
				knownCancelledAppointments.remove(userId);
				knownCancelledAppointments.put(userId, cancelledIds);
			}

			result.close();
			statement.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addKnownCancel(long userId, long lessonId, long appStartTime) {
		try (Connection connection = HikariSQL.getInstance().getConnection();) {
			PreparedStatement statement = connection.prepareStatement(
					"INSERT INTO `cancelledLessons` (userId, lessonId, lessonStartTime) VALUES (?, ?, ?)");
			statement.setLong(1, userId);
			statement.setLong(2, lessonId);
			statement.setLong(3, appStartTime);

			statement.execute();
			statement.close();

			List<Long> cancelledIds = knownCancelledAppointments.containsKey(userId)
					? knownCancelledAppointments.get(userId)
					: new ArrayList<Long>();
			cancelledIds.add(lessonId);
			knownCancelledAppointments.remove(userId);
			knownCancelledAppointments.put(userId, cancelledIds);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public List<Long> getKnownCancelledLessonIDs(long userId) {
		return knownCancelledAppointments.containsKey(userId) ? knownCancelledAppointments.get(userId)
				: new ArrayList<Long>();
	}
}
