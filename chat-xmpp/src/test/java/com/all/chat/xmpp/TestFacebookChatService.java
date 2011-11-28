package com.all.chat.xmpp;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.packet.VCard;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.chat.ChatType;
import com.all.chat.xmpp.CommonSmackChatService.VCardFactory;

public class TestFacebookChatService {

	@InjectMocks
	private TestableFacebookChatService facebookChatService = new TestableFacebookChatService();
	@Mock
	private XMPPConnection xmppConnection;
	@Mock
	private VCardFactory vCardFactory;
	@Mock
	private VCard vCard;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldGetFacebookChatType() throws Exception {
		assertEquals(ChatType.FACEBOOK, facebookChatService.getChatType());
	}

	@Test
	public void shouldGetConfiguration() throws Exception {
		ConnectionConfiguration connectionConfiguration = facebookChatService.getConnectionConfiguration();
		assertEquals(FacebookChatService.FACEBOOK_HOST, connectionConfiguration.getHost());
		assertEquals(FacebookChatService.FACEBOOK_PORT, connectionConfiguration.getPort());
	}

	@Test
	public void shouldReleaseReousrces() throws Exception {
		facebookChatService.release();
	}

	@Test
	public void shouldAppendHostToUsernameWhenLoadingVCard() throws Exception {
		when(vCardFactory.create()).thenReturn(vCard);
		facebookChatService.loadUserInfo("username");
		verify(vCard).load(xmppConnection, "username@chat.facebook.com");
	}

	@Test
	public void shouldNotAppendHostToUsernameWhenLoadingVCard() throws Exception {
		when(vCardFactory.create()).thenReturn(vCard);
		facebookChatService.loadUserInfo("username@chat.facebook.com");
		verify(vCard).load(xmppConnection, "username@chat.facebook.com");
	}

	class TestableFacebookChatService extends FacebookChatService {
	}

}
