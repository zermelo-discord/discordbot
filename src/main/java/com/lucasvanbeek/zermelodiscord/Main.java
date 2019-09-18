package com.lucasvanbeek.zermelodiscord;

import java.util.Timer;

import javax.security.auth.login.LoginException;

import com.lucasvanbeek.zermelodiscord.commands.LinkCMD;
import com.lucasvanbeek.zermelodiscord.commands.ScheduleCMD;
import com.lucasvanbeek.zermelodiscord.commands.UnlinkCMD;
import com.lucasvanbeek.zermelodiscord.listeners.CommandListener;
import com.lucasvanbeek.zermelodiscord.listeners.ReadyListener;
import com.lucasvanbeek.zermelodiscord.tasks.CancelledLessonTask;
import com.lucasvanbeek.zermelodiscord.utils.EmbedHelper;
import com.lucasvanbeek.zermelodiscord.utils.commands.CommandFactory;
import com.lucasvanbeek.zermelodiscord.utils.data.HikariSQL;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Main {

	private static String API_TOKEN = "NTkzNTI4Mzk1NTA2MjUzODMw.XWZ5oA.SFqIxOUMj6yyUJGa5htLgIuU6J8";
	private static String IP_ADDRESS = "server01.yvanwatchman.nl";
	private static int PORT = 3306;
	private static String DB_NAME = "mrwouter_zermelobot";
	private static String USERNAME = "mrwouter_zermelobot";
	private static String PASSWORD = "0oWyYreM26";

	public static void main(String[] args) {
		try {
			JDA jda = new JDABuilder(API_TOKEN).build();

			HikariSQL.getInstance().setup(IP_ADDRESS, PORT, DB_NAME, USERNAME, PASSWORD);
			EmbedHelper.getInstance().setJDA(jda);

			jda.addEventListener(new ReadyListener());
			jda.addEventListener(new CommandListener());

			CommandFactory.getInstance().registerCommand("!link", new LinkCMD());
			CommandFactory.getInstance().registerCommand("!unlink", new UnlinkCMD());
			CommandFactory.getInstance().registerCommand("!schedule", new ScheduleCMD()).addAlias("!rooster");

			try {
				//Wait for JDA to succesfully load. When JDA has been loaded start the cancelledlessontask
				jda.awaitReady();
			} catch (Exception ex) {
				//ignored
			}
			CancelledLessonTask task = new CancelledLessonTask(jda);
			Timer timer = new Timer();

			timer.scheduleAtFixedRate(task, 1000L, 1000 * 60 * 10);
		} catch (LoginException exception) {
			System.out.println("[ERROR] An internal error has occured whilst attempting to login.");
			exception.printStackTrace();
		}
	}
}
