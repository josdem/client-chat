package com.all.chat.xmpp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.Sasl;

import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.Base64;

//this class was copied from an example to connect to facebook
//found on "Problems with connecting to Facebook XMMP MD5-DIGEST" http://community.igniterealtime.org/thread/41080
public class SASLFacebookDigestMD5Mechanism extends SASLMechanism {

	public static final String NAME = "DIGEST-MD5";
	
	public SASLFacebookDigestMD5Mechanism(SASLAuthentication saslAuthentication) {
		super(saslAuthentication);
	}

	@Override
	protected void authenticate() throws IOException, XMPPException {
		String[] mechanisms = { getName() };
		Map<String, String> props = new HashMap<String, String>();
		sc = Sasl.createSaslClient(mechanisms, null, "xmpp", hostname, props, this);

		super.authenticate();
	}

	@Override
	public void authenticate(String username, String host, String password) throws IOException, XMPPException {
		this.authenticationId = username;
		this.password = password;
		this.hostname = host;

		String[] mechanisms = { getName() };
		Map<String, String> props = new HashMap<String, String>();
		sc = Sasl.createSaslClient(mechanisms, null, "xmpp", host, props, this);
		super.authenticate();
	}

	@Override
	public void authenticate(String username, String host, CallbackHandler cbh) throws IOException, XMPPException {
		String[] mechanisms = { getName() };
		Map<String, String> props = new HashMap<String, String>();
		sc = Sasl.createSaslClient(mechanisms, null, "xmpp", host, props, cbh);
		super.authenticate();
	}

	@Override
	protected String getName() {
		return NAME;
	}

	@Override
	public void challengeReceived(String challenge) throws IOException {
		// Build the challenge response stanza encoding the response text
		final StringBuilder stanza = new StringBuilder();

		byte response[];
		if (challenge != null) {
			response = sc.evaluateChallenge(Base64.decode(challenge));
		} else {
			response = sc.evaluateChallenge(null);
		}

		String authenticationText = "";

		if (response != null) { // fix from 3.1.1
			authenticationText = Base64.encodeBytes(response, Base64.DONT_BREAK_LINES);
			if (authenticationText.equals("")) {
				authenticationText = "=";
			}
		}

		stanza.append("<response xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">");
		stanza.append(authenticationText);
		stanza.append("</response>");

		// Send the authentication to the server
//		getSASLAuthentication().send(stanza.toString());
		getSASLAuthentication().send(new Packet() {
			@Override
			public String toXML() {
				return stanza.toString();
			}
		});
	}
}
