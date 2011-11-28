package com.all.chat;

public interface Message {

	ChatUser getTo();
	
	ChatUser getFrom();
	
	String getMessage();
	
	ChatType getChatType();
	
}
