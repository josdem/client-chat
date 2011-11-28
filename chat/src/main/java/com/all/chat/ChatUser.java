package com.all.chat;

public interface ChatUser extends Comparable<ChatUser> {

	String getChatId();

	String getChatName();

	ChatStatus getChatStatus();

	ChatType getChatType();

	byte[] getAvatar();

	String getQuote();

}
