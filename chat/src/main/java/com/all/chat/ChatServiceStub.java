package com.all.chat;

import com.all.chat.exceptions.ChatException;

public class ChatServiceStub implements ChatService {

	@Override
	public ChatUser login(ChatCredentials chatCredentials) throws ChatException {
		return null;
	}

	@Override
	public void logout(ChatUser loggedContact) {
	}

	@Override
	public Message sendMessage(String message, ChatUser to) throws ChatException {
		return null;
	}

	@Override
	public void addListener(ChatServiceListener chatServiceListener) {
	}

	@Override
	public void removeListener(ChatServiceListener chatServiceListener) {
	}

}
