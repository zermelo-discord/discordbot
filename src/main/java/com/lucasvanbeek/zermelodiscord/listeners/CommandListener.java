package com.lucasvanbeek.zermelodiscord.listeners;

import com.lucasvanbeek.zermelodiscord.utils.commands.CommandFactory;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		CommandFactory.getInstance().excecute(event.getMessage().getContentRaw(), event.getMessage(), event.getChannelType());
	}
}
