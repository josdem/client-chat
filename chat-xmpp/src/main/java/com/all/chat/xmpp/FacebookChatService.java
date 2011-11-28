package com.all.chat.xmpp;

import static com.all.shared.messages.MessEngineConstants.REPORT_USER_ACTION;

import javax.annotation.PreDestroy;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smackx.packet.VCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.chat.ChatType;
import com.all.chat.ChatUser;
import com.all.chat.Message;
import com.all.chat.exceptions.ChatException;
import com.all.messengine.MessEngine;
import com.all.shared.model.AllMessage;
import com.all.shared.stats.usage.UserActions;

@Service
public class FacebookChatService extends CommonSmackChatService {

	public static final String FACEBOOK_HOST = "chat.facebook.com";

	public static final int FACEBOOK_PORT = 5222;

	@Autowired
	private MessEngine messengine;

	@PreDestroy
	public void release() {
		super.releaseResources();
	}

	@Override
	protected ConnectionConfiguration getConnectionConfiguration() {
		/* This handles the X-FACEBOOK-PLATFORM mechanism */
		SASLAuthentication.registerSASLMechanism(SASLXFacebookPlatformMechanism.NAME, SASLXFacebookPlatformMechanism.class);
		SASLAuthentication.supportSASLMechanism(SASLXFacebookPlatformMechanism.NAME, 0);

		/* This handles the DIGEST-MD5 mechanism */
		SASLAuthentication.registerSASLMechanism(SASLFacebookDigestMD5Mechanism.NAME, SASLFacebookDigestMD5Mechanism.class);
		SASLAuthentication.supportSASLMechanism(SASLFacebookDigestMD5Mechanism.NAME, 1);

		return new ConnectionConfiguration(FACEBOOK_HOST, FACEBOOK_PORT);
	}

	@Override
	public ChatType getChatType() {
		return ChatType.FACEBOOK;
	}

	@Override
	protected VCard loadUserInfo(String username) {
		if (!username.endsWith(FACEBOOK_HOST)) {
			return super.loadUserInfo(new StringBuilder(username).append("@").append(FACEBOOK_HOST).toString());
		}
		return super.loadUserInfo(username);
	}

	@Override
	public Message sendMessage(String message, ChatUser recipient) throws ChatException {
		messengine.send(new AllMessage<Integer>(REPORT_USER_ACTION, UserActions.SocialNetworks.FACEBOOK_CHAT_MESSAGE_SENT));
		return super.sendMessage(message, recipient);
	}
}
