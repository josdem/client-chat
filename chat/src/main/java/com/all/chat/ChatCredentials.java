package com.all.chat;


public class ChatCredentials {

	private String username;
	private String password;
	private ChatType type;
	private boolean rememberMe;

	public ChatCredentials(String username, String password, ChatType type) {
		this.username = username;
		this.password = password;
		this.type = type;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public ChatType getType() {
		return type;
	}
	
	public void setRememberMe(boolean rememberMe){
		this.rememberMe = rememberMe;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	@Override
	public String toString() {
		return new StringBuilder(username).append(" at ").append(type).toString();
	}
}
