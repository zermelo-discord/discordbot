package com.lucasvanbeek.zermelodiscord.commands;

import com.lucasvanbeek.zermelodiscord.utils.EmbedHelper;
import com.lucasvanbeek.zermelodiscord.utils.commands.BotCommand;
import com.lucasvanbeek.zermelodiscord.utils.commands.Command;
import com.lucasvanbeek.zermelodiscord.utils.data.LinkData;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;

public class UnlinkCMD implements BotCommand {

	@Override
	public void execute(Command cmd, String[] args, Message msg, ChannelType type) {
		if (type != ChannelType.PRIVATE) {
			msg.delete().queue();
			msg.getChannel().sendMessage(EmbedHelper.getInstance()
					.createError("Je moet dit commando uitvoeren in de priv\u00EBberichten van de bot!")).queue();
			return;
		}
		if (!LinkData.getInstance().isLinked(msg.getAuthor().getIdLong())) {
			msg.getChannel()
					.sendMessage(
							EmbedHelper.getInstance().createError("Jouw account is niet gekoppeld aan de ZermeloBot!"))
					.queue();
			return;
		}
		msg.getChannel().sendMessage(
				EmbedHelper.getInstance().createInfo("Jouw account is nu niet meer gekoppeld aan de ZermeloBot!"))
				.queue();
		LinkData.getInstance().unlink(msg.getAuthor().getIdLong());
	}
}
