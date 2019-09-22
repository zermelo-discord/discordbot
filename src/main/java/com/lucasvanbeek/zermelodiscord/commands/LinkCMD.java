package com.lucasvanbeek.zermelodiscord.commands;

import java.io.IOException;

import com.lucasvanbeek.zermelodiscord.utils.EmbedHelper;
import com.lucasvanbeek.zermelodiscord.utils.commands.BotCommand;
import com.lucasvanbeek.zermelodiscord.utils.commands.Command;
import com.lucasvanbeek.zermelodiscord.utils.data.LinkData;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import nl.mrwouter.zermelo4j.ZermeloAPI;

public class LinkCMD implements BotCommand {

	@Override
	public void execute(Command cmd, String[] args, Message msg, ChannelType type) {
		if (type != ChannelType.PRIVATE) {
			msg.delete().queue();
			msg.getChannel().sendMessage(EmbedHelper.getInstance().createError(
					"Je moet dit commando uitvoeren in de priv\u00E9berichten van de bot. Dit is om te voorkomen dat jouw koppel-app code publiek wordt!"))
					.queue();
			return;
		}
		if (LinkData.getInstance().isLinked(msg.getAuthor().getIdLong())) {
			msg.getChannel()
					.sendMessage(
							EmbedHelper.getInstance().createError("Jouw account is al gekoppeld aan de ZermeloBot!"))
					.queue();
			return;
		} else {
			if (args.length < 2) {
				msg.getChannel().sendMessage(EmbedHelper.getInstance().createError("Gebruik !link <School> <Code>"))
						.queue();
				return;
			}
			String school = args[0];
			String linkAppCode = "";
			for (int i = 1; i < args.length; i++) {
				linkAppCode = linkAppCode + args[i];
			}
			try {
				String accessToken = ZermeloAPI.getAccessToken(school, linkAppCode);
				msg.getChannel().sendMessage(EmbedHelper.getInstance().createInfo("Succesvol jouw account gelinked!"))
						.queue();
				LinkData.getInstance().link(msg.getAuthor().getIdLong(), school, accessToken);

			} catch (IOException ex) {
				msg.getChannel().sendMessage(EmbedHelper.getInstance().createError("Ongeldige link-app code!")).queue();
			}

		}
	}
}
