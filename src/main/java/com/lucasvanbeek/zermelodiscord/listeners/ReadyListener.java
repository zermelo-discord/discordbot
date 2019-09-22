package com.lucasvanbeek.zermelodiscord.listeners;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter {

	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("ZermeloBot by MrWouter#3441 and Lucas_#6183 has been loaded!"
				+ "\nGitHub: https://github.com/zermelo-discord/discordbot");
	}
}