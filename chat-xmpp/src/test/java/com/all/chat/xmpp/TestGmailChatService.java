package com.all.chat.xmpp;

import static org.junit.Assert.assertEquals;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.junit.Test;

import com.all.chat.ChatType;

public class TestGmailChatService {

	private TestableGmailChatService gmailChatService = new TestableGmailChatService();

	@Test
	public void shouldGetFacebookChatType() throws Exception {
		assertEquals(ChatType.GTALK, gmailChatService.getChatType());
	}

	@Test
	public void shouldGetConfiguration() throws Exception {
		ConnectionConfiguration connectionConfiguration = gmailChatService.getConnectionConfiguration();
		assertEquals(GtalkChatService.GMAIL_HOST, connectionConfiguration.getHost());
		assertEquals(GtalkChatService.GMAIL_PORT, connectionConfiguration.getPort());
		assertEquals(GtalkChatService.GMAIL_SERVICE_NAME, connectionConfiguration.getServiceName());
	}

	@Test
	public void shouldReleaseReousrces() throws Exception {
		gmailChatService.release();
	}

	class TestableGmailChatService extends GtalkChatService {
	}

}
