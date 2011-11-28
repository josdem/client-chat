package com.all.chat.manager;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.chat.ChatCredentials;
import com.all.chat.ChatServiceListener;
import com.all.chat.ChatType;
import com.all.chat.ChatUser;
import com.all.chat.ManagedChatService;

public class TestChatServiceManager {

	private ChatServiceManager chatServiceManager;
	@Mock
	private ManagedChatService allManagedChatService;
	@Mock
	private ManagedChatService facebookManagedChatService;
	@Mock
	private ManagedChatService gmailManagedChatService;
	@Mock
	private ChatServiceListener listener;
	@Mock
	private ChatUser chatUser;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		when(allManagedChatService.getChatType()).thenReturn(ChatType.ALL);
		when(facebookManagedChatService.getChatType()).thenReturn(ChatType.FACEBOOK);
		when(gmailManagedChatService.getChatType()).thenReturn(ChatType.GTALK);
		when(chatUser.getChatType()).thenReturn(ChatType.ALL);
		
		Collection<ManagedChatService> managedChatServices = new ArrayList<ManagedChatService>() ;
		managedChatServices.add(allManagedChatService);
		managedChatServices.add(facebookManagedChatService);
		managedChatServices.add(gmailManagedChatService);
		
		chatServiceManager = new ChatServiceManager(managedChatServices);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionIfManagedchatServicesNotFound() throws Exception {
		new ChatServiceManager(new ArrayList<ManagedChatService>());
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionIfManagedChatServiceHandleTheSameChatType() throws Exception {
		when(facebookManagedChatService.getChatType()).thenReturn(ChatType.ALL);
		
		Collection<ManagedChatService> managedChatServices = new ArrayList<ManagedChatService>() ;
		managedChatServices.add(allManagedChatService);
		managedChatServices.add(facebookManagedChatService);
		managedChatServices.add(gmailManagedChatService);
		
		chatServiceManager = new ChatServiceManager(managedChatServices);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionIfManagedchatServiceTypeNotFound() throws Exception {
		//we don't add ChatType.ALL and send a chatUser with type ALL
		Collection<ManagedChatService> managedChatServices = new ArrayList<ManagedChatService>() ;
		managedChatServices.add(facebookManagedChatService);
		managedChatServices.add(gmailManagedChatService);
		
		chatServiceManager = new ChatServiceManager(managedChatServices);

		chatServiceManager.sendMessage("message", chatUser);
	}
	
	@Test
	public void shouldLoginIntoSpecificChatServiceType() throws Exception {
		ChatCredentials chatCredentials = new ChatCredentials("username", "password", ChatType.ALL);
		chatServiceManager.login(chatCredentials);
		verify(allManagedChatService).login(chatCredentials);
	}
	
	@Test
	public void shouldLogoutIntoSpecificChatServiceType() throws Exception {
		chatServiceManager.logout(chatUser);
		verify(allManagedChatService).logout(chatUser);
		verify(facebookManagedChatService, never()).logout(chatUser);
		verify(gmailManagedChatService, never()).logout(chatUser);
	}
	
	@Test
	public void shouldSendMessageIntoSpecificChatServiceType() throws Exception {
		chatServiceManager.sendMessage("message", chatUser);
		verify(allManagedChatService).sendMessage("message", chatUser);
		verify(facebookManagedChatService, never()).sendMessage("message", chatUser);
		verify(gmailManagedChatService, never()).sendMessage("message", chatUser);
	}
	
	@Test
	public void shouldAddListenerToAllManagedChatServices() throws Exception {
		chatServiceManager.addListener(listener);
		verify(allManagedChatService).addListener(listener);
		verify(gmailManagedChatService).addListener(listener);
		verify(facebookManagedChatService).addListener(listener);
	}
	
	@Test
	public void shouldRemoveListenerFromAllManagedChatServices() throws Exception {
		chatServiceManager.removeListener(listener);
		verify(allManagedChatService).removeListener(listener);
		verify(gmailManagedChatService).removeListener(listener);
		verify(facebookManagedChatService).removeListener(listener);
	}
	
	
}
