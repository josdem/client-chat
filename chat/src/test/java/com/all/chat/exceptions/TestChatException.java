package com.all.chat.exceptions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import static org.mockito.Mockito.*;

public class TestChatException {

	@Test
	public void shouldIncreaseCoverage() throws Exception {
		Throwable throwable = mock(Throwable.class);

		String message = "message";
		ChatException chatException = new ChatException(message);
		assertEquals(message, chatException.getMessage());

		chatException = new ChatException(throwable);
		assertEquals(throwable, chatException.getCause());

		chatException = new ChatException(message, throwable);
		assertEquals(message, chatException.getMessage());
		assertEquals(throwable, chatException.getCause());
	}

}
