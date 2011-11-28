package com.all.chat.xmpp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;

import com.all.chat.ChatCredentials;
import com.all.chat.ChatStatus;
import com.all.chat.ChatUser;
import com.all.chat.ManagedChatService;
import com.all.chat.exceptions.ChatException;
import com.all.chat.impl.ChatUserImpl;
import com.all.chat.impl.CommonChatService;
import com.all.chat.impl.MessageImpl;
import com.all.commons.IncrementalNamedThreadFactory;

/**
 * We can implement more services to implement XMPP chat services <br>
 * <ul>
 * <li>AIM: login.oscar.aol.com 5190</li>
 * <li>Gadu-Gadu: appmsg.gadu-gadu.pl 80</li>
 * <li>GTalk: talk.google.com 5222</li>
 * <li>ICQ: login.oscar.aol.com 5190</li>
 * <li>IRC: irc.freenode.net 7000</li>
 * <li>MSN: messenger.hotmail.com 1863 (also 443 for initial connection)</li>
 * <li>SIMPLE: localhost 5060 (of course there's a good chance you'll point it elsewhere)</li>
 * <li>XMPP: jabber.org 5222</li>
 * <li>Yahoo: scs.msg.yahoo.com 5050</li>
 * </ul>
 * Obtained from <a href="http://community.igniterealtime.org/docs/DOC-1005">IM Gateway Plugin Documentation</a> <br>
 */
public abstract class CommonSmackChatService extends CommonChatService implements ManagedChatService {

	private final static Log LOG = LogFactory.getLog(CommonSmackChatService.class);

	public static final String FN = "FN";

	private XMPPConnectionFactory xmppConnectionFactory = new XMPPConnectionFactory();

	private VCardFactory vCardFactory = new VCardFactory();

	protected XMPPConnection xmppConnection;

	protected Map<String, ChatUserImpl> chatUserMap = new HashMap<String, ChatUserImpl>();

	protected ChatUserImpl loggedChatUser;

	private ExecutorService executor = Executors.newCachedThreadPool(new IncrementalNamedThreadFactory(
			"CommonSmackChatServiceThread"));

	protected abstract ConnectionConfiguration getConnectionConfiguration();

	protected void releaseResources() {
		executor.shutdownNow();
	}

	public ChatUser login(ChatCredentials chatCredentials) throws ChatException {
		XMPPConnection.DEBUG_ENABLED = true;

		doLogin(chatCredentials);

		// if doLogin does not throw exception then execute this in background
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					wirePresenceListener();
					wireMessageListener();
					obtainAndNotifyChatUsers();
				} catch (Exception e) {
					LOG.error("Error performing post login operations", e);
				}
			}
		});

		createLoggedChatUser(chatCredentials);
		announcePresenceAvailable();
		return loggedChatUser;
	}

	private void createLoggedChatUser(ChatCredentials chatCredentials) {
		VCard card = vCardFactory.create();
		String chatId;
		String chatName;

		try {
			card.load(xmppConnection);

			chatId = card.getFrom();
			chatName = card.getField(FN);
		} catch (XMPPException e) {
			LOG.error("Unable to retrive info for the logged user", e);

			chatId = chatCredentials.getUsername();
			chatName = chatCredentials.getUsername();
		}

		loggedChatUser = new ChatUserImpl(chatId, chatName, ChatStatus.ONLINE, getChatType(), card.getAvatar());
	}

	private void wirePresenceListener() {
		xmppConnection.getRoster().addRosterListener(new RosterListener() {

			@Override
			public void presenceChanged(Presence presence) {
				String presenceValidate = validatePresenceFormat(presence.getFrom());
				checkForPresence(presenceValidate);

			}

			@Override
			public void entriesUpdated(Collection<String> addresses) {
				LOG.error(ToStringBuilder.reflectionToString(addresses));
			}

			@Override
			public void entriesDeleted(Collection<String> addresses) {
				LOG.error(ToStringBuilder.reflectionToString(addresses));
			}

			@Override
			public void entriesAdded(Collection<String> addresses) {
				LOG.error(ToStringBuilder.reflectionToString(addresses));
			}
		});
	}

	private String validatePresenceFormat(String presence) {
		if (presence.contains("/")) {
			return presence.split("/")[0];
		}
		return presence;
	}

	private void checkForPresence(final String chatUserId) {
		final ChatUserImpl chatUser = chatUserMap.get(chatUserId);
		Roster roster = xmppConnection.getRoster();

		if (chatUser != null) {
			// the documentation suggests to retrieve the presence to the roster once received the event
			Presence bestPresence = getPresence(chatUserId);

			ChatStatus oldStatus = chatUser.getChatStatus();
			ChatStatus newStatus = adaptPresence(bestPresence);

			if (oldStatus != newStatus) {
				chatUser.setChatStatus(newStatus);
				notifyPresenceChanged(chatUser);
			}

			// avatar retrieval is long, so we use another thread
			executor.execute(new Runnable() {
				@Override
				public void run() {
					byte[] oldAvatar = chatUser.getAvatar();
					byte[] newAvatar = retrieveAvatar(chatUserId);
					if (!Arrays.equals(oldAvatar, newAvatar)) {
						chatUser.setAvatar(newAvatar);
						notifyAvatarChanged(chatUser);
					}
				}
			});

		} else {
			RosterEntry rosterEntry = roster.getEntry(chatUserId);
			if (rosterEntry != null) {
				chatUserMap.put(chatUserId, createChatUserFromRosterEntry(rosterEntry));
				checkForPresence(chatUserId);
			}
		}
	}

	protected Presence getPresence(String chatUserId) {
		return xmppConnection.getRoster().getPresence(chatUserId);
	}

	private void obtainAndNotifyChatUsers() {
		Collection<ChatUser> chatUsers = obtainChatUsers();
		notifyChatUsers(chatUsers);
		verifyUsersPresence(chatUsers);
	}

	private void verifyUsersPresence(Collection<ChatUser> chatUsers) {
		String chatUserId = "";
		for (ChatUser chatUser : chatUsers) {
			chatUserId = chatUser.getChatId();
			checkForPresence(chatUserId);
		}
	}

	private Collection<ChatUser> obtainChatUsers() {
		Roster roster = xmppConnection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		for (RosterEntry entry : entries) {
			chatUserMap.put(entry.getUser(), createChatUserFromRosterEntry(entry));
		}
		return new ArrayList<ChatUser>(chatUserMap.values());
	}

	private ChatUserImpl createChatUserFromRosterEntry(RosterEntry entry) {
		String chatId = entry.getUser();
		Presence presence = getPresence(chatId);
		String status = presence != null ? presence.getStatus() : null;
		return new ChatUserImpl(chatId, entry.getName(), ChatStatus.OFFLINE, getChatType(), null, status);
	}

	private byte[] retrieveAvatar(String jid) {
		VCard card = loadUserInfo(jid);
		return card.getAvatar();
	}

	private ChatStatus adaptPresence(Presence presence) {
		if (presence.isAway()) {
			return ChatStatus.AWAY;
		}

		if (presence.getType() == Type.available) {
			return ChatStatus.ONLINE;
		}

		// should we consider an error status for the default case?
		return ChatStatus.OFFLINE;
	}

	private void wireMessageListener() {
		PacketFilter filter = new PacketTypeFilter(Message.class);
		xmppConnection.addPacketListener(new PacketListener() {
			@Override
			public void processPacket(Packet packet) {
				// we used a org.jivesoftware.smack.filter.PacketFilter so we only receive org.jivesoftware.smack.packet.Message
				processMessage((Message) packet);
			}
		}, filter);
	}

	protected VCard loadUserInfo(String username) {
		VCard card = vCardFactory.create();
		try {
			card.load(xmppConnection, username);
		} catch (XMPPException e) {
			LOG.error("Exception thrown when trying to acquire contact's avatar", e);
		}
		return card;
	}

	private void announcePresenceAvailable() {
		Presence presence = getPresence(loggedChatUser.getChatId());
		if (presence == null) {
			presence = new Presence(Presence.Type.available);
		}
		presence.setType(Presence.Type.available);
		xmppConnection.sendPacket(presence);
	}

	private void doLogin(ChatCredentials chatCredentials) throws ChatException {
		xmppConnection = xmppConnectionFactory.create();

		try {
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
					new org.jivesoftware.smackx.provider.VCardProvider());
			xmppConnection.connect();
		} catch (XMPPException e) {
			throw new ChatException("An error ocurred while connecting to the service", e);
		}

		try {
			xmppConnection.login(chatCredentials.getUsername(), chatCredentials.getPassword());
		} catch (XMPPException e) {
			throw new ChatException("An error ocurred while login with the user: " + chatCredentials, e);
		}
	}

	@Override
	public void logout(ChatUser chatUser) {
		xmppConnection.disconnect();
		chatUserMap.clear();
	}

	@Override
	public com.all.chat.Message sendMessage(String message, ChatUser recipient) throws ChatException {
		if (org.apache.commons.lang.StringUtils.isBlank(message)) {
			throw new ChatException("Message is blank");
		}

		Message msg = new Message(recipient.getChatId(), Message.Type.chat);
		msg.setBody(message);
		xmppConnection.sendPacket(msg);

		return new MessageImpl(loggedChatUser, recipient, message, getChatType());
	}

	private void processMessage(Message message) {
		String bodyMessage = message.getBody();
		if (bodyMessage != null) {
			String email = StringUtils.parseBareAddress(message.getFrom());
			ChatUser chatUser = chatUserMap.get(email);
			if (chatUser == null) {
				LOG.warn(String.format("ChatUser[%s] not cached, retrieving it", email));
				RosterEntry entry = xmppConnection.getRoster().getEntry(email);
				chatUser = createChatUserFromRosterEntry(entry);
			}
			notifyMessage(new MessageImpl(chatUser, loggedChatUser, bodyMessage, getChatType()));
		}
	}

	class XMPPConnectionFactory {
		public XMPPConnection create() {
			return new XMPPConnection(getConnectionConfiguration());
		}
	}

	class VCardFactory {
		public VCard create() {
			return new VCard();
		}
	}
}
