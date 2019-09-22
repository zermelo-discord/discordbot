package com.lucasvanbeek.zermelodiscord.utils.data;

public class LinkedUser {

	private long userId;
	private String school, accessToken;

	public LinkedUser(long userId, String school, String accessToken) {
		this.userId = userId;
		this.school = school;
		this.accessToken = accessToken;
	}

	public long getUserId() {
		return userId;
	}

	public String getSchool() {
		return school;
	}

	public String getAccessToken() {
		return accessToken;
	}
}
