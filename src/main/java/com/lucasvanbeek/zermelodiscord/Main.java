package com.lucasvanbeek.zermelodiscord;

import java.util.Timer;

import javax.security.auth.login.LoginException;

import com.lucasvanbeek.zermelodiscord.commands.LinkCMD;
import com.lucasvanbeek.zermelodiscord.commands.ScheduleCMD;
import com.lucasvanbeek.zermelodiscord.commands.UnlinkCMD;
import com.lucasvanbeek.zermelodiscord.listeners.CommandListener;
import com.lucasvanbeek.zermelodiscord.listeners.ReadyListener;
import com.lucasvanbeek.zermelodiscord.tasks.AnnouncementTask;
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
    private static Main instance;
    private JDA jda;

	public static void main(String[] args) {
		instance = new Main();
		try {
			instance.setJDA(new JDABuilder(API_TOKEN).build());

			HikariSQL.getInstance().setup(IP_ADDRESS, PORT, DB_NAME, USERNAME, PASSWORD);
			EmbedHelper.getInstance().setJDA(instance.getJDA());

			instance.getJDA().addEventListener(new ReadyListener());
			instance.getJDA().addEventListener(new CommandListener());

			CommandFactory.getInstance().registerCommand("!link", new LinkCMD());
			CommandFactory.getInstance().registerCommand("!unlink", new UnlinkCMD());
			CommandFactory.getInstance().registerCommand("!schedule", new ScheduleCMD()).addAlias("!rooster");

			try {
				// Wait for JDA to succesfully load. When JDA has been loaded start the
				// cancelledlessontask
				instance.getJDA().awaitReady();
			} catch (Exception ex) {
				// ignored
			}
			Timer timer = new Timer();

			timer.scheduleAtFixedRate(new CancelledLessonTask(instance.getJDA()), 1000L, 1000 * 60 * 10);
			timer.scheduleAtFixedRate(new AnnouncementTask(instance.getJDA()), 1000L, 1000 * 60 * 10);
		} catch (LoginException exception) {
			System.out.println("[ERROR] An internal error has occured whilst attempting to login.");
			exception.printStackTrace();
		}
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public JDA getJDA() {
		return jda;
	}
	
	private void setJDA(JDA jda) {
		this.jda = jda;
	}
}
