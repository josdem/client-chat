package com.all.chat.xmpp;

import java.util.Collection;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;

import com.all.chat.ChatCredentials;
import com.all.chat.ChatServiceListener;
import com.all.chat.ChatType;
import com.all.chat.ChatUser;
import com.all.chat.Message;

@Ignore
public class AllChatServiceIntegrationTest {
	private static Log log = LogFactory.getLog(AllChatServiceIntegrationTest.class);

	public static void main(String[] args) throws Exception {
		AllChatService allChatService = new AllChatService();
		allChatService.addListener(new ChatServiceListener() {
			
			@Override
			public void onChatContactPresenceChanged(ChatUser arg0) {
				log.info("onChatContactPresenceChanged " + arg0);
			}
			
			@Override
			public void onChatContactsRetrieved(Collection<ChatUser> contacts) {
				log.info("onChatContactsRetrieved");
				for (ChatUser chatUser : contacts) {
					log.info("" + chatUser);
				}
			}
			
			@Override
			public void onMessage(Message arg0) {
				log.info("onMessage " + arg0);
			}
			
			@Override
			public void onChatContactAvatarChanged(ChatUser arg0) {
				log.info("onChatContactAvatarChanged " + arg0);
			}
		});
		
		ChatUser chatUser = allChatService.login(new ChatCredentials("227", "25d55ad283aa400af464c76d713c07ad", ChatType.ALL));
		
		allChatService.setPresenceStatus("Quote de prueba :P");
		

		System.in.read();
	}
}
