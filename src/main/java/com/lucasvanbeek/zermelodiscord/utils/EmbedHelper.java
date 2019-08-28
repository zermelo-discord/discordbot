package com.lucasvanbeek.zermelodiscord.utils;

import java.awt.Color;
import java.util.Calendar;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbedHelper {

	private static EmbedHelper instance;
	private JDA jda;

	public static EmbedHelper getInstance() {
		if (instance == null) {
			instance = new EmbedHelper();
		}
		return instance;
	}

	public JDA getJDA() {
		return jda;
	}

	public void setJDA(JDA jda) {
		this.jda = jda;
	}

	public MessageEmbed createError(String error) {
		return new EmbedBuilder().setColor(Color.RED).setAuthor("ZermeloBot", "https://github.com/zermelo-discord/discordbot",
						getJDA().getSelfUser().getAvatarUrl())
				.addField("Foutmelding", error, true)
				.setFooter("\u00A9 ZermeloBot - " + Calendar.getInstance().get(Calendar.YEAR),
						getJDA().getSelfUser().getAvatarUrl())
				.build();
	}
	
	public MessageEmbed createInfo(String info) {
		return new EmbedBuilder().setColor(Color.GREEN).setAuthor("ZermeloBot", "https://github.com/zermelo-discord/discordbot",
						getJDA().getSelfUser().getAvatarUrl())
				.addField("Informatie", info, true)
				.setFooter("\u00A9 ZermeloBot - " + Calendar.getInstance().get(Calendar.YEAR),
						getJDA().getSelfUser().getAvatarUrl())
				.build();
	}
	
	public EmbedBuilder createScheduleMessage(String user) {
		return new EmbedBuilder().setColor(Color.GREEN).setAuthor("Rooster voor " + user, "https://github.com/zermelo-discord/discordbot",
						getJDA().getSelfUser().getAvatarUrl())
				.setFooter("\u00A9 ZermeloBot - " + Calendar.getInstance().get(Calendar.YEAR),
						getJDA().getSelfUser().getAvatarUrl());
	}
	
	public EmbedBuilder createCancelledAlert(String user) {
		return new EmbedBuilder().setColor(Color.GREEN).setAuthor("Nieuwe lesuitval voor " + user, "https://github.com/zermelo-discord/discordbot",
						getJDA().getSelfUser().getAvatarUrl())
				.setFooter("\u00A9 ZermeloBot - " + Calendar.getInstance().get(Calendar.YEAR),
						getJDA().getSelfUser().getAvatarUrl());
	}
}