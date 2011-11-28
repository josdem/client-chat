package com.all.chat;

import java.util.Collection;

public interface ChatServiceListener {

	void onChatContactsRetrieved(Collection<ChatUser> chatUsers);

	void onMessage(Message message);
	
	void onChatContactPresenceChanged(ChatUser chatUser);
	
	void onChatContactAvatarChanged(ChatUser chatUser);
}
