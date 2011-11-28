package com.all.chat.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.chat.ChatCredentials;
import com.all.chat.ChatService;
import com.all.chat.ChatServiceListener;
import com.all.chat.ChatType;
import com.all.chat.ChatUser;
import com.all.chat.ManagedChatService;
import com.all.chat.Message;
import com.all.chat.exceptions.ChatException;

@Service
public class ChatServiceManager implements ChatService {

	private static final Log LOG = LogFactory.getLog(ChatServiceManager.class);

	private Map<ChatType, ManagedChatService> managedChatServiceMap = new HashMap<ChatType, ManagedChatService>();

	@Autowired
	public ChatServiceManager(Collection<ManagedChatService> managedChatServices) {
		for (ManagedChatService chatService : managedChatServices) {
			if (managedChatServiceMap.put(chatService.getChatType(), chatService) != null) {
				throw new IllegalArgumentException("Chat services serves the same chat type " + chatService.getChatType());
			}
		}

		if (managedChatServices.isEmpty()) {
			throw new IllegalArgumentException("No managed downloaders found");
		}

		LOG.info("Found managed chat services: " + managedChatServices);
	}

	@Override
	public ChatUser login(ChatCredentials chatCredentials) throws ChatException {
		ManagedChatService managedChatService = chatServiceForType(chatCredentials.getType());
		return managedChatService.login(chatCredentials);
	}

	@Override
	public void logout(ChatUser loggedContact) {
		ManagedChatService managedChatService = chatServiceForType(loggedContact.getChatType());
		managedChatService.logout(loggedContact);
	}

	@Override
	public Message sendMessage(String message, ChatUser to) throws ChatException {
		ManagedChatService managedChatService = chatServiceForType(to.getChatType());
		return managedChatService.sendMessage(message, to);
	}

	@Override
	public void addListener(ChatServiceListener chatServiceListener) {
		//we don't need to do anything special to the listener so we just delegate 
		for (ManagedChatService managedchatService : managedChatServiceMap.values()) {
			managedchatService.addListener(chatServiceListener);
		}
	}

	@Override
	//we don't need to do anything special to the listener so we just delegate 
	public void removeListener(ChatServiceListener chatServiceListener) {
		for (ManagedChatService managedchatService : managedChatServiceMap.values()) {
			managedchatService.removeListener(chatServiceListener);
		}
	}

	private ManagedChatService chatServiceForType(ChatType chatType) {
		ManagedChatService managedChatService = managedChatServiceMap.get(chatType);
		if (managedChatService == null) {
			throw new IllegalArgumentException("No chat service associated to chat type: " + chatType);
		}
		return managedChatService;
	}

}
