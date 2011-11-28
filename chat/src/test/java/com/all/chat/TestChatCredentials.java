package com.all.chat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class TestChatCredentials {

	@Test
	public void shouldCreateImmutableBean() throws Exception {
		String username = "username";
		String password = "password";
		ChatCredentials chatCredentials = new ChatCredentials(username, password, ChatType.ALL);

		assertEquals(username, chatCredentials.getUsername());
		assertEquals(password, chatCredentials.getPassword());
		assertEquals(ChatType.ALL, chatCredentials.getType());
		assertFalse(chatCredentials.isRememberMe());
	}

}
