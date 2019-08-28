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

			timer.scheduleAtFixedRate(task, 1000l, 1000 * 60 * 10);
		} catch (LoginException exception) {
			System.out.println("[ERROR] An internal error has occured whilst attempting to login.");
			exception.printStackTrace();
		}
	}
}
