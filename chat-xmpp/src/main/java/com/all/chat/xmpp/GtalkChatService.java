package com.all.chat.xmpp;

import javax.annotation.PreDestroy;

import org.jivesoftware.smack.ConnectionConfiguration;

import com.all.chat.ChatType;

//@org.springframework.stereotype.Service
public class GtalkChatService extends CommonSmackChatService {

	public static final String GMAIL_SERVICE_NAME = "gmail.com";

	public static final String GMAIL_HOST = "talk.google.com";

	public static final int GMAIL_PORT = 5222;

	@PreDestroy
	public void release() {
		super.releaseResources();
	}
	
	@Override
	protected ConnectionConfiguration getConnectionConfiguration() {
		return new ConnectionConfiguration(GMAIL_HOST, GMAIL_PORT, GMAIL_SERVICE_NAME);
	}
	
	@Override
	public ChatType getChatType() {
		return ChatType.GTALK;
	}
	
}
