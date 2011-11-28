package com.all.chat.xmpp;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smackx.packet.VCard;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.chat.ChatCredentials;
import com.all.chat.ChatServiceListener;
import com.all.chat.ChatStatus;
import com.all.chat.ChatType;
import com.all.chat.ChatUser;
import com.all.chat.exceptions.ChatException;
import com.all.chat.impl.ChatUserImpl;
import com.all.chat.xmpp.CommonSmackChatService.VCardFactory;
import com.all.chat.xmpp.CommonSmackChatService.XMPPConnectionFactory;

public class TestCommonSmackChatService {

	@InjectMocks
	private CommonSmackChatService commonSmackChatService = new TestableCommonSmackChatService();
	@Mock
	private XMPPConnectionFactory xmppConnectionFactory;
	@Mock
	private VCardFactory vCardFactory;
	@Mock
	private ChatUserImpl loggedChatUser;
	@Mock
	private ChatServiceListener chatServiceListener;
	@Mock
	private XMPPConnection xmppConnection;
	@Mock
	private Roster roster;
	@Mock
	private VCard vCard;
	@Mock
	private ChatUser chatUser;
	@Mock
	private Presence presence;
	@Mock
	private ConnectionConfiguration connectionConfiguration;
	@Captor
	private ArgumentCaptor<Collection<ChatUser>> contactsRetrievedCaptor;
	@Captor
	private ArgumentCaptor<PacketListener> packetListenerCaptor;
	@Captor
	private ArgumentCaptor<Message> messageCaptor;
	@Captor
	private ArgumentCaptor<RosterListener> rosterListenerCaptor;
	@Captor
	private ArgumentCaptor<ChatUser> chatUserCaptor;
	
	private String username = "username";

	private String password = "password";
	

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		when(xmppConnection.getRoster()).thenReturn(roster);

		commonSmackChatService.addListener(chatServiceListener);
	}

	@After
	public void releaseResources() throws Exception {
		commonSmackChatService.releaseResources();
	}
	
	public void prepareConnectionMocks(){
		when(xmppConnectionFactory.create()).thenReturn(xmppConnection);
		when(roster.getEntries()).thenReturn(new ArrayList<RosterEntry>());
		when(vCardFactory.create()).thenReturn(vCard);
	}

	@Test
	public void shouldLoginAndSendMessagesafterLogin() throws Exception {
		String chatId = "jid";
		String chatName = "nickname";
		ChatCredentials chatCredentials = new ChatCredentials(username, password, ChatType.ALL);

		prepareConnectionMocks();
		when(vCard.getFrom()).thenReturn(chatId);
		when(vCard.getField(CommonSmackChatService.FN)).thenReturn(chatName);

		ChatUser loggedUser = commonSmackChatService.login(chatCredentials);

		verify(xmppConnection).connect();
		verify(xmppConnection).login(username, password);
		verify(chatServiceListener, timeout(1000)).onChatContactsRetrieved(contactsRetrievedCaptor.capture());
		verify(xmppConnection, timeout(1000)).addPacketListener(packetListenerCaptor.capture(), isA(PacketFilter.class));
		verify(roster, timeout(1000)).addRosterListener(isA(RosterListener.class));
		verify(xmppConnection, timeout(1000)).sendPacket(isA(Presence.class));

		assertEquals(chatId, loggedUser.getChatId());
		assertEquals(chatName, loggedUser.getChatName());
		assertEquals(ChatStatus.ONLINE, loggedUser.getChatStatus());
		assertEquals(ChatType.ALL, loggedUser.getChatType());
	}

	@Test
	public void shouldSetUserNameAsChatIdAndChatNameForLoggedUser() throws Exception {
		ChatCredentials chatCredentials = new ChatCredentials(username, password, ChatType.ALL);
		
		prepareConnectionMocks();
		doThrow(new XMPPException()).when(vCard).load(xmppConnection);
		
		ChatUser loggedUser = commonSmackChatService.login(chatCredentials);
		
		assertEquals(username, loggedUser.getChatId());
		assertEquals(username, loggedUser.getChatName());
		assertEquals(ChatStatus.ONLINE, loggedUser.getChatStatus());
		assertEquals(ChatType.ALL, loggedUser.getChatType());
	}
	
	@Test
	public void shouldNotifyPresenceChange() throws Exception {
		String chatUserIdWithResource = "username@host.name/resource";
		String chatUserId = "username@host.name";
		ChatCredentials chatCredentials = new ChatCredentials(username, password, ChatType.ALL);
		RosterEntry rosterEntry = mock(RosterEntry.class);
		
		prepareConnectionMocks();
		when(presence.getFrom()).thenReturn(chatUserIdWithResource);
		when(roster.getPresence(chatUserId)).thenReturn(presence);
		when(roster.getEntry(chatUserId)).thenReturn(rosterEntry);
		when(presence.isAway()).thenReturn(true, false);
		when(rosterEntry.getUser()).thenReturn(chatUserId);
		when(rosterEntry.getName()).thenReturn(username);
		
		commonSmackChatService.login(chatCredentials);
		verify(roster, timeout(1000)).addRosterListener(rosterListenerCaptor.capture());
		RosterListener rosterListener = rosterListenerCaptor.getValue();
		
		rosterListener.presenceChanged(presence);
		verify(chatServiceListener).onChatContactPresenceChanged(chatUserCaptor.capture());
		ChatUser chatUser = chatUserCaptor.getValue();
		assertEquals(chatUserId, chatUser.getChatId());
		assertEquals(username, chatUser.getChatName());
		assertEquals(ChatStatus.AWAY, chatUser.getChatStatus());
		
		
		when(presence.getFrom()).thenReturn(chatUserId);
		when(presence.getType()).thenReturn(Type.available);

		rosterListener.presenceChanged(presence);
		assertEquals(chatUserId, chatUser.getChatId());
		assertEquals(username, chatUser.getChatName());
		assertEquals(ChatStatus.ONLINE, chatUser.getChatStatus());
	}
	
	@Test
	public void shouldCreateChatUsersFromRosterEntries() throws Exception {
		String userA = "userA";
		String nameA = "nameA";
		String userB = "userB";
		String nameB = "nameB";
		String userC = "userC";
		String nameC = "nameC";
		ChatCredentials chatCredentials = new ChatCredentials(username, password, ChatType.ALL);
		ArrayList<RosterEntry> entries = new ArrayList<RosterEntry>();

		RosterEntry rosterEntryA = mock(RosterEntry.class);
		RosterEntry rosterEntryB = mock(RosterEntry.class);
		RosterEntry rosterEntryC = mock(RosterEntry.class);
		Presence presence = mock(Presence.class);
		presence.setStatus("");
		entries.add(rosterEntryA);
		entries.add(rosterEntryB);
		entries.add(rosterEntryC);
		
		when(rosterEntryA.getUser()).thenReturn(userA);
		when(rosterEntryA.getName()).thenReturn(nameA);
		when(rosterEntryB.getUser()).thenReturn(userB);
		when(rosterEntryB.getName()).thenReturn(nameB);
		when(rosterEntryC.getUser()).thenReturn(userC);
		when(rosterEntryC.getName()).thenReturn(nameC);
		when(roster.getPresence(userA)).thenReturn(presence);
		when(roster.getPresence(userB)).thenReturn(presence);
		when(roster.getPresence(userC)).thenReturn(null); //explicit
		when(xmppConnectionFactory.create()).thenReturn(xmppConnection);
		when(roster.getEntries()).thenReturn(entries);
		when(vCardFactory.create()).thenReturn(vCard);
		when(presence.getType()).thenReturn(Type.available);
		when(presence.isAway()).thenReturn(true, false);
		
		commonSmackChatService.login(chatCredentials);
		
		verify(chatServiceListener, timeout(1000)).onChatContactsRetrieved(contactsRetrievedCaptor.capture());
		
		List<ChatUser> contactsRetrieved = new ArrayList<ChatUser>(contactsRetrievedCaptor.getValue());
		assertEquals(userA, contactsRetrieved.get(0).getChatId());
		assertEquals(nameA, contactsRetrieved.get(0).getChatName());
		assertEquals(ChatStatus.AWAY, contactsRetrieved.get(0).getChatStatus());
		assertEquals(ChatType.ALL, contactsRetrieved.get(0).getChatType());
		assertEquals(userB, contactsRetrieved.get(1).getChatId());
		assertEquals(nameB, contactsRetrieved.get(1).getChatName());
		assertEquals(ChatStatus.ONLINE, contactsRetrieved.get(1).getChatStatus());
		assertEquals(ChatType.ALL, contactsRetrieved.get(1).getChatType());
		assertEquals(userC, contactsRetrieved.get(2).getChatId());
		assertEquals(nameC, contactsRetrieved.get(2).getChatName());
		assertEquals(ChatStatus.OFFLINE, contactsRetrieved.get(2).getChatStatus());
		assertEquals(ChatType.ALL, contactsRetrieved.get(2).getChatType());
	}
	
	@Test
	public void shouldLogOut() throws Exception {
		commonSmackChatService.logout(chatUser);
		verify(xmppConnection).disconnect();
	}

	@Test
	public void shouldSendMessage() throws Exception {
		String chatId = "chatId";
		String strMessage = "message";
		when(chatUser.getChatId()).thenReturn(chatId);

		com.all.chat.Message sentMessage = commonSmackChatService.sendMessage(strMessage, chatUser);

		assertEquals(chatUser, sentMessage.getTo());
		assertEquals(loggedChatUser, sentMessage.getFrom()); // injected as logged user by mockito
		assertEquals(strMessage, sentMessage.getMessage());
		assertEquals(ChatType.ALL, sentMessage.getChatType());

		verify(xmppConnection).sendPacket(messageCaptor.capture());

		Message message = messageCaptor.getValue();
		assertEquals(chatId, message.getTo());
		assertEquals(Message.Type.chat, message.getType());
		assertEquals(strMessage, message.getBody());
	}

	@Test(expected = ChatException.class)
	public void shouldFailIfMessageIsBlank() throws Exception {
		commonSmackChatService.sendMessage(" ", chatUser);
	}
	
	class TestableCommonSmackChatService extends CommonSmackChatService {

		@Override
		public ChatType getChatType() {
			return ChatType.ALL;
		}

		@Override
		protected ConnectionConfiguration getConnectionConfiguration() {
			return connectionConfiguration;
		}

	}

}
