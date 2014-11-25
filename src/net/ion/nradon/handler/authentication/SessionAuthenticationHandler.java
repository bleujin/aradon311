package net.ion.nradon.handler.authentication;

import java.net.HttpCookie;

import net.ion.framework.util.ObjectId;
import net.ion.nradon.HttpControl;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.handler.AbstractHttpHandler;
import net.ion.nradon.helpers.Base64;

public class SessionAuthenticationHandler extends AbstractHttpHandler {

	public static final String USERNAME = "user";

	private static final String BASIC_PREFIX = "Basic ";

	private final String realm;
	private final PasswordAuthenticator authenticator;

	private final static String SessionKey = "_radon_sessionid" ;
	private SessionManager sessions ;

	private String domainName = "localhost";
	
	public SessionAuthenticationHandler(PasswordAuthenticator authenticator, SessionManager smanager) {
		this(authenticator, "Secure Area", smanager);
	}

	public SessionAuthenticationHandler(PasswordAuthenticator authenticator, String realm, SessionManager smanager) {
		this.realm = realm;
		this.authenticator = authenticator;
		this.sessions = smanager ;
	}
	
	public SessionAuthenticationHandler domainName(String domainName){
		this.domainName = domainName ;
		return this ;
	}
	

	public void handleHttpRequest(final HttpRequest request, final HttpResponse response, final HttpControl control) throws Exception {
		String authHeader = request.header("Authorization");
		
		SessionInfo sinfo = session(request);
		if (hasSessionKey(request) && sinfo != SimpleSessionInfo.NOTEXIST) {
			request.data(SimpleSessionInfo.class.getCanonicalName(), sinfo) ;
			control.nextHandler();
		} else if (authHeader == null) {
			needAuthentication(response);
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
		String sessionId = hasSessionKey(request) ? request.cookieValue(SessionKey) : new ObjectId().toString();
		SessionInfo created = sessions.newSession(sessionId) ;
		request.data(SimpleSessionInfo.class.getCanonicalName(), created) ;
		
		HttpCookie cookie = new HttpCookie(SessionKey, sessionId);
		cookie.setVersion(0);
		cookie.setDomain(domainName) ;
		cookie.setPath("/");
		cookie.setSecure(true);
		cookie.setMaxAge(60*60*3); // 3 hour
		response.cookie(cookie) ;
	}
	
	private SessionInfo session(HttpRequest request){
		HttpCookie cookie = request.cookie(SessionKey) ;
		if (cookie == null) return SimpleSessionInfo.NOTEXIST ;
		return sessions.findSession(cookie.getValue()) ;
	}

	private boolean hasSessionKey(HttpRequest request) {
		return request.cookie(SessionKey) != null ;
	}

	private void needAuthentication(HttpResponse response) {
		response.status(401).header("WWW-Authenticate", "Basic realm=\"" + realm + "\"").content("Need authentication").end();
	}
	
	public int order() {
		return -1;
	}
}

