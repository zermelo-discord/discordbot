package com.lucasvanbeek.zermelodiscord.utils;

import java.util.function.Consumer;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

//DirectMessageConsumer can be used to notify users that they have their direct messages disabled (or swallow the exception).
public class DirectMessageConsumer<T> implements Consumer<Throwable> {

	private User user;
	private MessageChannel channel;

	public DirectMessageConsumer(User user, MessageChannel channel) {
		this.user = user;
		this.channel = channel;
	}

	@Override
	public void accept(Throwable t) {
		if (channel != null) {
			channel.sendMessage(user.getAsMention() + ", je moet jouw priveberichten aanzetten!").queue();
		}
	}
}
