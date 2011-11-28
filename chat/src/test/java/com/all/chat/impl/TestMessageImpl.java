package com.all.chat.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mockito.Mock;

import com.all.chat.ChatType;
import com.all.chat.ChatUser;
import com.all.chat.UnitTestCase;

public class TestMessageImpl extends UnitTestCase {

	@Mock
	ChatUser to;
	@Mock
	ChatUser from;

	@Test
	public void shouldCreateInmutableBean() throws Exception {
		String strMessage = "message";
		MessageImpl message = new MessageImpl(from, to, strMessage, ChatType.ALL);
		assertEquals(to, message.getTo());
		assertEquals(from, message.getFrom());
		assertEquals(strMessage, message.getMessage());
		assertEquals(ChatType.ALL, message.getChatType());
	}

}
