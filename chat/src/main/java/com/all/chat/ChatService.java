package com.all.chat;

import com.all.chat.exceptions.ChatException;

public interface ChatService {

	ChatUser login(ChatCredentials chatCredentials) throws ChatException;

	void logout(ChatUser loggedContact);

	Message sendMessage(String message, ChatUser to) throws ChatException;

	void addListener(ChatServiceListener chatServiceListener);

	void removeListener(ChatServiceListener chatServiceListener);
	
}
