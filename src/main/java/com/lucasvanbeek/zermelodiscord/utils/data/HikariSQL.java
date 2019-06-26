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
		hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
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

		buildTables();
	}

	private void buildTables() {
		try (Connection connection = getConnection();) {
			Statement statement = connection.createStatement();
			statement.executeUpdate(
					"CREATE TABLE IF NOT EXISTS Links(rowId INTEGER NOT NULL AUTO_INCREMENT, userId integer, accessToken varchar(32), PRIMARY KEY(rowId))");
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
