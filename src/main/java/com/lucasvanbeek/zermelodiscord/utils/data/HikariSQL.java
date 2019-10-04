package com.lucasvanbeek.zermelodiscord.utils.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariDataSource;

public class HikariSQL {

	public static HikariSQL instance = null;
	private HikariDataSource hikari;

	public static HikariSQL getInstance() {
		if (instance == null) {
			instance = new HikariSQL();
		}
		return instance;
	}

	public Connection getConnection() throws SQLException {
		return getHikari().getConnection();
	}

	public HikariDataSource getHikari() {
		return hikari;
	}

	public void setup(String ip, int port, String dbname, String username, String password) {
		hikari = new HikariDataSource();
		hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
		hikari.addDataSourceProperty("serverTimezone", "UTC");
		hikari.addDataSourceProperty("serverName", ip);
		hikari.addDataSourceProperty("port", port);
		hikari.addDataSourceProperty("databaseName", dbname);
		hikari.addDataSourceProperty("user", username);
		hikari.addDataSourceProperty("password", password);
		hikari.setLeakDetectionThreshold(10000);
		hikari.setMaximumPoolSize(10);
		hikari.addDataSourceProperty("cachePrepStmts", "true");
		hikari.addDataSourceProperty("prepStmtCacheSize", "250");
		hikari.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		hikari.setPoolName("ZermeloBotPool");
		hikari.setConnectionTestQuery("SELECT 1");

		buildTables();
	}

	private void buildTables() {
		try (Connection connection = getConnection();) {
			Statement statement = connection.createStatement();
			statement.executeUpdate(
					"CREATE TABLE IF NOT EXISTS Links(rowId INTEGER NOT NULL AUTO_INCREMENT, userId bigint(22), school varchar(32), accessToken varchar(32), PRIMARY KEY(rowId))");
			statement.close();

			Statement createCancelledLessonsTable = connection.createStatement();
			createCancelledLessonsTable.executeUpdate(
					"CREATE TABLE IF NOT EXISTS cancelledLessons(rowId INTEGER NOT NULL AUTO_INCREMENT, userId bigint(22), lessonId bigint(22), lessonStartTime bigint(13), PRIMARY KEY(rowId))");
			createCancelledLessonsTable.close();
			
			Statement createAnnouncementsTable = connection.createStatement();
			createAnnouncementsTable.executeUpdate(
					"CREATE TABLE IF NOT EXISTS Announcements(rowId INTEGER NOT NULL AUTO_INCREMENT, userId bigint(22), announcementId bigint(22), PRIMARY KEY(rowId))");
			createAnnouncementsTable.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
