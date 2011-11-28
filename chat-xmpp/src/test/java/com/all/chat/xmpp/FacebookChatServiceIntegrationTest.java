package com.all.chat.xmpp;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;

import com.all.chat.ChatCredentials;
import com.all.chat.ChatService;
import com.all.chat.ChatServiceListener;
import com.all.chat.ChatType;
import com.all.chat.ChatUser;
import com.all.chat.Message;
import com.all.chat.exceptions.ChatException;

@Ignore
public class FacebookChatServiceIntegrationTest {
	private static Log log = LogFactory.getLog(GmailChatServiceIntegrationTest.class);

	public static void main(String[] args) throws ChatException {
		ChatService facebookChatService = new FacebookChatService();
		facebookChatService.addListener(new ChatServiceListener() {
			
			@Override
			public void onMessage(Message arg0) {
				log.info("onMessage " + arg0);
			}
			
			@Override
			public void onChatContactsRetrieved(Collection<ChatUser> contacts) {
				log.info("onChatContactsRetrieved");
				for (ChatUser chatUser : contacts) {
					log.info("" + chatUser);
				}
			}
			
			@Override
			public void onChatContactPresenceChanged(ChatUser arg0) {
				log.info("onChatContactPresenceChanged " + arg0);
			}
			
			@Override
			public void onChatContactAvatarChanged(ChatUser arg0) {
				log.info("onChatContactAvatarChanged " + arg0);
			}
		});

		
		//See the facebook chat service class to see how to build the username and password
		ChatCredentials chatCredentials = new ChatCredentials(
				"455cd6bc104968b10e9b4ea1335dc57e|2.AQAXlsCAtWvlD6Os.3600.1305860400.1-100002261422146",
//				"AQDI7UeOdbqkl0b-", ChatType.FACEBOOK);
				"225ce6bf7a193870690514db2e0259f5", ChatType.FACEBOOK);
		
		facebookChatService.login(chatCredentials);

	}
}
