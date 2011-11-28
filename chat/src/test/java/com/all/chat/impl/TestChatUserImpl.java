package com.all.chat.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.all.chat.ChatStatus;
import com.all.chat.ChatType;
import com.all.chat.ChatUser;

public class TestChatUserImpl {

	@Test
	public void shouldCreateImmutableBean() throws Exception {
		String chatId = "chatId";
		String chatName = "chatName";
		byte[] avatar = new byte[]{};

		ChatUser chatUser = new ChatUserImpl(chatId, chatName, ChatStatus.OFFLINE, ChatType.ALL, avatar);

		assertEquals(chatId, chatUser.getChatId());
		assertEquals(chatName, chatUser.getChatName());
		assertEquals(ChatStatus.OFFLINE, chatUser.getChatStatus());
		assertEquals(ChatType.ALL, chatUser.getChatType());
		assertEquals(avatar, chatUser.getAvatar());
	}
	
}
