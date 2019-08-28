package com.lucasvanbeek.zermelodiscord.utils.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;

public class CommandFactory {

	private static CommandFactory instance = null;
	private HashMap<Command, BotCommand> commands = new HashMap<Command, BotCommand>();

	public static CommandFactory getInstance() {
		if (instance == null) {
			instance = new CommandFactory();
		}
		return instance;
	}

	public Command registerCommand(String commandName, BotCommand executor) {
		commands.keySet().forEach(cmd -> {
			if (cmd.getName().equalsIgnoreCase(commandName)) {
				return;
			}
		});
		Command command = new Command(commandName, new ArrayList<>());
		commands.put(command, executor);
		return command;
	}

	public void registerAlias(String alias, String command) {
		commands.keySet().forEach(cmd -> {
			if (cmd.getName().equalsIgnoreCase(command)) {
				cmd.addAlias(alias.toLowerCase());
			}
		});
	}

	public Command execute(String fullProvided, Message msg, ChannelType type) {
		String[] args = fullProvided.split(" ");
		if (args.length == 0) {
			return null;
		}
		ArrayList<String> tempArgs = new ArrayList<>(Arrays.asList(args));
		tempArgs.remove(0);
		args = tempArgs.toArray(new String[0]);
		for (Command cmd : commands.keySet()) {
			String label = fullProvided.split(" ")[0].toLowerCase();
			if (cmd.getName().equalsIgnoreCase(label) || cmd.getAliases().contains(label)) {
				commands.get(cmd).execute(cmd, args, msg, type);
				return cmd;
			}
		}
		return null;
	}
}
