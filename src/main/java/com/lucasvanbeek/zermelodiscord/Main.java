package com.lucasvanbeek.zermelodiscord;

import javax.security.auth.login.LoginException;

import com.lucasvanbeek.zermelodiscord.commands.LinkCMD;
import com.lucasvanbeek.zermelodiscord.listeners.CommandListener;
import com.lucasvanbeek.zermelodiscord.utils.commands.CommandFactory;
import com.lucasvanbeek.zermelodiscord.utils.data.HikariSQL;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

public class Main {

	private static String API_TOKEN = "";
	private static String IP_ADDRESS = "";
	private static int PORT = 3306;
	private static String DB_NAME = "";
	private static String USERNAME = "";
	private static String PASSWORD = "";
	
	public static void main(String[] args) {
		try {
			JDA jda = new JDABuilder(API_TOKEN).build();
			
			HikariSQL.getInstance().setup(IP_ADDRESS, PORT, DB_NAME, USERNAME, PASSWORD);
			
			jda.addEventListener(new CommandListener());
			CommandFactory.getInstance().registerCommand("link", new LinkCMD());
		} catch (LoginException exception) {
			System.out.println("[ERROR] An internal error has occured whilst attempting to login.");
			exception.printStackTrace();
		}
	}
}
