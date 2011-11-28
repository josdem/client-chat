package com.all.chat.impl;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.all.chat.ChatStatus;
import com.all.chat.ChatType;
import com.all.chat.ChatUser;

public class ChatUserImpl implements ChatUser {

	private String chatId;
	private String chatName;
	private ChatStatus chatStatus;
	private ChatType chatType;
	private byte[] avatar;
	private String quote;

	public ChatUserImpl(String chatId, String chatName, ChatStatus chatStatus, ChatType chatType) {
		this.chatId = chatId;
		this.chatName = chatName;
		this.chatStatus = chatStatus;
		this.chatType = chatType;
	}
	
	public ChatUserImpl(String chatId, String chatName, ChatStatus chatStatus, ChatType chatType, byte[] avatar) {
		this(chatId, chatName, chatStatus, chatType);
		this.avatar = avatar;
	}
	
	public ChatUserImpl(String chatId, String chatName, ChatStatus chatStatus, ChatType chatType, byte[] avatar, String quote) {
		this(chatId, chatName, chatStatus, chatType, avatar);
		this.quote = quote;
	}

	@Override
	public String getChatId() {
		return chatId;
	}

	@Override
	public String getChatName() {
		return chatName;
	}

	@Override
	public ChatStatus getChatStatus() {
		return chatStatus;
	}

	@Override
	public ChatType getChatType() {
		return chatType;
	}

	@Override
	public byte[] getAvatar() {
		return avatar;
	}
	
	@Override
	public String getQuote() {
		return quote;
	}

	@Override
	public int compareTo(ChatUser o) {
		if (o != null && o instanceof ChatUserImpl) {
			ChatUserImpl other = (ChatUserImpl) o;
			if (this.chatName == null || other.chatName == null) {
				return this.chatId.compareToIgnoreCase(other.chatId);
			}
			return this.chatName.compareToIgnoreCase(other.chatName);
		}
		return 0;
	}

	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}

	public void setChatStatus(ChatStatus chatStatus) {
		this.chatStatus = chatStatus;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chatId == null) ? 0 : chatId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ChatUserImpl other = (ChatUserImpl) obj;
		if (chatId == null) {
			if (other.chatId != null) {
				return false;
			}
		} else if (!chatId.equals(other.chatId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
