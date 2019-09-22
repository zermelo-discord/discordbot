package com.lucasvanbeek.zermelodiscord.utils.commands;

import java.util.List;

public class Command {

	private String name;
	private List<String> aliases;

	public Command(String name, List<String> aliases) {
		this.name = name;
		this.aliases = aliases;
	}

	public String getName() {
		return name;
	}

	public List<String> getAliases() {
		return aliases;
	}

	public Command addAlias(String alias) {
		aliases.add(alias);
		return this;
	}
}
