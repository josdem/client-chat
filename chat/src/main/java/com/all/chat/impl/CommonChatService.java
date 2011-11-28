package com.all.chat.impl;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.chat.ChatService;
import com.all.chat.ChatServiceListener;
import com.all.chat.ChatUser;
import com.all.chat.Message;

public abstract class CommonChatService implements ChatService {

	private final static Log LOG = LogFactory.getLog(CommonChatService.class);
	protected Set<ChatServiceListener> chatServiceListeners = new CopyOnWriteArraySet<ChatServiceListener>();

	@Override
	public final void addListener(ChatServiceListener chatServiceListener) {
		chatServiceListeners.add(chatServiceListener);
	}

	@Override
	public final void removeListener(ChatServiceListener chatServiceListener) {
		chatServiceListeners.remove(chatServiceListener);
	}

	protected final void notifyMessage(Message message) {
		for (ChatServiceListener chatMessageListener : chatServiceListeners) {
			try {
				chatMessageListener.onMessage(message);
			} catch (Exception e) {
				LOG.error("Catched excpetion while notyfing message", e);
			}
		}
	}

	protected final void notifyChatUsers(Collection<ChatUser> chatUsers) {
		for (ChatServiceListener chatMessageListener : chatServiceListeners) {
			try {
				chatMessageListener.onChatContactsRetrieved(chatUsers);
			} catch (Exception e) {
				LOG.error("Catched excpetion while notyfing chat users", e);
			}

		}
	}

	protected final void notifyPresenceChanged(ChatUser chatUser) {
		for (ChatServiceListener chatMessageListener : chatServiceListeners) {
			try {
				chatMessageListener.onChatContactPresenceChanged(chatUser);
			} catch (Exception e) {
				LOG.error("Catched excpetion while notyfing presence changed", e);
			}

		}
	}
	
	protected final void notifyAvatarChanged(ChatUser chatUser){
		for (ChatServiceListener chatMessageListener: chatServiceListeners) {
			try {
				chatMessageListener.onChatContactAvatarChanged(chatUser);
			} catch (Exception e) {
				LOG.error("Catched exception while notifying avatar changed", e);
			}
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}
