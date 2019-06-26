package com.lucasvanbeek.zermelodiscord.utils.commands;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;

public interface BotCommand {

	public void excecute(Command cmd, String[] args, Message msg, ChannelType type);
	
}