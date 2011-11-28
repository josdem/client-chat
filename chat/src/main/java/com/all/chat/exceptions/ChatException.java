package com.all.chat.exceptions;

public class ChatException extends Exception {

	private static final long serialVersionUID = -1817421890838643955L;

	public ChatException(String message, Throwable cause) {
		super(message, cause);
	}

	public ChatException(String message) {
		super(message);
	}

	public ChatException(Throwable t) {
		super(t);
	}

}
