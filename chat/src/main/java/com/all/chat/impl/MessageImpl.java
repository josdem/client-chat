package com.all.chat.impl;

import com.all.chat.ChatType;
import com.all.chat.ChatUser;
import com.all.chat.Message;

public class MessageImpl implements Message {

	private final ChatUser to;
	private final ChatUser from;
	private final String message;
	private final ChatType chatType;

	public MessageImpl(ChatUser sender, ChatUser recipient, String message, ChatType chatType) {
		this.to = recipient;
		this.from = sender;
		this.message = message;
		this.chatType = chatType;
	}

	@Override
	public ChatUser getTo() {
		return to;
	}

	@Override
	public ChatUser getFrom() {
		return from;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
	@Override
	public ChatType getChatType() {
		return chatType;
	}

}
