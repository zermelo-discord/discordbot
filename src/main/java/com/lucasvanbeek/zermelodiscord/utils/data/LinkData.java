package com.lucasvanbeek.zermelodiscord.utils.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;

public class LinkData {

	private static LinkData instance;
	private HashMap<Long, LinkedUser> users = new HashMap<>();

	public static LinkData getInstance() {
		if (instance == null) {
			instance = new LinkData();
		}
		return instance;
	}

	public boolean isLinked(long userId) {
		if (users.isEmpty()) {
			pullData();
		}
		return users.containsKey(userId);
	}

	public void pullData() {
		if (!users.isEmpty()) {
			return;
		}
		try (Connection connection = HikariSQL.getInstance().getConnection();) {

			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT `userId`, `school`, `accessToken` FROM Links");

			while (result.next()) {
				long userId = result.getLong("userId");
				String school = result.getString("school");
				String accessToken = result.getString("accessToken");
				users.put(userId, new LinkedUser(userId, school, accessToken));
			}

			result.close();
			statement.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void link(long userId, String school, String accessToken) {
		try (Connection connection = HikariSQL.getInstance().getConnection();) {
			PreparedStatement statement = connection
					.prepareStatement("INSERT INTO Links (`userId`, `school`, `accessToken`) VALUES (?, ?, ?)");
			statement.setLong(1, userId);
			statement.setString(2, school);
			statement.setString(3, accessToken);

			statement.executeUpdate();
			statement.close();
			users.put(userId, new LinkedUser(userId, school, accessToken));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void unlink(long userId) {
		users.remove(userId);
		try (Connection connection = HikariSQL.getInstance().getConnection();) {
			PreparedStatement statement = connection.prepareStatement("DELETE FROM Links WHERE `userId`=?");
			statement.setLong(1, userId);

			statement.executeUpdate();
			statement.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public LinkedUser getUser(long userId) {
		return users.get(userId);
	}

	public Collection<LinkedUser> getLinkedUsers() {
		if (users.isEmpty()) {
			pullData();
		}
		return users.values();
	}

}
