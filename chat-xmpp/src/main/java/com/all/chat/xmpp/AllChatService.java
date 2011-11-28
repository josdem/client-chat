package com.all.chat.xmpp;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.sasl.SASLPlainMechanism;

import com.all.chat.ChatType;

//@org.springframework.stereotype.Service;
public class AllChatService extends CommonSmackChatService {

	private static final String ALL_HOST = "localhost";

	private static final int ALL_PORT = 5222;

	@Override
	protected ConnectionConfiguration getConnectionConfiguration() {
		SASLAuthentication.registerSASLMechanism("PLAIN", SASLPlainMechanism.class);
		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
		return new ConnectionConfiguration(ALL_HOST, ALL_PORT);
	}

	@Override
	public ChatType getChatType() {
		return ChatType.ALL;
	}

	public void setPresenceStatus(String status) {
		Presence presence = new Presence(Presence.Type.available);
		presence.setStatus(status);
		xmppConnection.sendPacket(presence);
	}

}
