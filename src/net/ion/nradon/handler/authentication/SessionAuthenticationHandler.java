package net.ion.nradon.handler.authentication;

import java.net.InetSocketAddress;

import net.ion.framework.util.HashUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.HttpControl;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.handler.AbstractHttpHandler;
import net.ion.nradon.helpers.Base64;
import net.ion.nradon.helpers.HttpCookie;

import org.jboss.resteasy.util.HttpHeaderNames;

public class SessionAuthenticationHandler extends AbstractHttpHandler {

	public static final String USERNAME = "user";

	private static final String BASIC_PREFIX = "Basic ";

	private final String realm;
	private final PasswordAuthenticator authenticator;

	private final static String SessionID = "_AradonSessionId" ;
	private final static String SessionTime = "_AradonSessionTime" ;
	
	public SessionAuthenticationHandler(PasswordAuthenticator authenticator) {
		this(authenticator, "Secure Area");
	}

	public SessionAuthenticationHandler(PasswordAuthenticator authenticator, String realm) {
		this.realm = realm;
		this.authenticator = authenticator;
	}

	public void handleHttpRequest(final HttpRequest request, final HttpResponse response, final HttpControl control) throws Exception {
		request.data(SessionTime, System.currentTimeMillis()) ;
		
		String authHeader = request.header("Authorization");
		
		if (authHeader == null && hasNotSessionKey(request)) {
			needAuthentication(response);
		} else if (hasValidSessionKey(request)) {
			control.nextHandler();
		} else {
			if (authHeader.startsWith(BASIC_PREFIX)) {
				String decoded = new String(Base64.decode(authHeader.substring(BASIC_PREFIX.length())));
				final String[] pair = decoded.split(":", 2);
				if (pair.length == 2) {
					final String username = pair[0];
					final String password = pair[1];
					PasswordAuthenticator.ResultCallback callback = new PasswordAuthenticator.ResultCallback() {
						public void success() {
							request.data(USERNAME, username);
							saveSessionKey(request, response) ;
							control.nextHandler();
						}

						public void failure() {
							needAuthentication(response);
						}
					};

					authenticator.authenticate(request, username, password, callback, control);
				} else {
					needAuthentication(response);
				}
			}
		}
	}

	private void saveSessionKey(HttpRequest request, HttpResponse response) {
		InetSocketAddress inetAddr = (InetSocketAddress)request.remoteAddress() ;
		String hostName = inetAddr.getHostName() ;
		String agentName = StringUtil.defaultIfEmpty(request.header(HttpHeaderNames.USER_AGENT), "") ;

		response.cookie(new HttpCookie(SessionID, "" + HashUtil.hash(hostName + agentName))) ;
//		request.data(SessionID, HashUtil.hash(hostName + agentName)) ;
	}

	private boolean hasValidSessionKey(HttpRequest request) {
		InetSocketAddress inetAddr = (InetSocketAddress)request.remoteAddress() ;
		String hostName = inetAddr.getHostName() ;
		String agentName = StringUtil.defaultIfEmpty(request.header(HttpHeaderNames.USER_AGENT), "") ;
		
//		Object svalue = request.data(SessionID);
//		return svalue != null && svalue.equals(new Long(HashUtil.hash(hostName + agentName))) ;
		
		HttpCookie svalue = request.cookie(SessionID) ;
		
		return svalue != null && svalue.getValue().equals("" + HashUtil.hash(hostName + agentName)) ;
	}

	private boolean hasNotSessionKey(HttpRequest request) {
		return request.cookie(SessionID) == null ;
//		return request.data(SessionID) == null;
	}

	private void needAuthentication(HttpResponse response) {
		response.status(401).header("WWW-Authenticate", "Basic realm=\"" + realm + "\"").content("Need authentication").end();
	}
	
	public int order() {
		return -1;
	}
}