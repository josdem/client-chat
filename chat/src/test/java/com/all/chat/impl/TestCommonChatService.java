package com.all.chat.impl;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.chat.ChatCredentials;
import com.all.chat.ChatServiceListener;
import com.all.chat.ChatUser;
import com.all.chat.Message;
import com.all.chat.UnitTestCase;
import com.all.chat.exceptions.ChatException;


public class TestCommonChatService extends UnitTestCase {

	private CommonChatServiceMock commonChatService = new CommonChatServiceMock();
	@Mock
	private ChatServiceListener chatServiceListenerA;
	@Mock
	private ChatServiceListener chatServiceListenerB;
	@Mock
	private Message message;
	@Mock
	private Collection<ChatUser> chatUsers;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		commonChatService.addListener(chatServiceListenerA);
		commonChatService.addListener(chatServiceListenerB);
	}
	
	@Test
	public void shouldNotifyMessage() throws Exception {
		commonChatService.notifyMessage(message);
		
		verify(chatServiceListenerA).onMessage(message);
		verify(chatServiceListenerB).onMessage(message);
	}

	@Test
	public void shouldNotifyChatUsers() throws Exception {
		commonChatService.notifyChatUsers(chatUsers);
		
		verify(chatServiceListenerA).onChatContactsRetrieved(chatUsers);
		verify(chatServiceListenerB).onChatContactsRetrieved(chatUsers);
	}
	
	@Test
	public void shouldRemoveListener() throws Exception {
		commonChatService.removeListener(chatServiceListenerA);
		
		commonChatService.notifyMessage(message);
		
		verify(chatServiceListenerA, never()).onMessage(message);
		verify(chatServiceListenerB).onMessage(message);
		
	}
	
	class CommonChatServiceMock extends CommonChatService {

		@Override
		public ChatUser login(ChatCredentials chatCredentials) throws ChatException {
			return null;
		}

		@Override
		public void logout(ChatUser loggedContact) {
		}

		@Override
		public Message sendMessage(String message, ChatUser to) throws ChatException {
			return null;
		}
		
	}
	
}
