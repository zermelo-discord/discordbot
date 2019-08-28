package com.lucasvanbeek.zermelodiscord.utils.commands;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;

public interface BotCommand {

	public void execute(Command cmd, String[] args, Message msg, ChannelType type);
	
}