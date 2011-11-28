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

@Ignore
public class GmailChatServiceIntegrationTest {
	private static Log log = LogFactory.getLog(GmailChatServiceIntegrationTest.class);

	public static void main(String[] args) throws Exception {
		ChatService gmailChatService = new GtalkChatService();
		gmailChatService.addListener(new ChatServiceListener() {
			
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
		
		gmailChatService.login(new ChatCredentials("YOURUSER@gmail.com", "YOURPASSWORD", ChatType.GTALK));
	}
}
