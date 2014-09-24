package net.ion.nradon.handler.authentication;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import net.ion.framework.db.ThreadFactoryBuilder;
import net.ion.framework.util.ObjectId;
import net.ion.nradon.HttpControl;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.handler.AbstractHttpHandler;
import net.ion.nradon.helpers.Base64;
import net.ion.nradon.helpers.HttpCookie;

public class SessionAuthenticationHandler extends AbstractHttpHandler {

	public static final String USERNAME = "user";

	private static final String BASIC_PREFIX = "Basic ";

	private final String realm;
	private final PasswordAuthenticator authenticator;

	private final static String SessionKey = "_sessionid" ;
	private SessionManager sessions ;
	
	public SessionAuthenticationHandler(PasswordAuthenticator authenticator) {
		this(authenticator, "Secure Area");
	}

	public SessionAuthenticationHandler(PasswordAuthenticator authenticator, String realm) {
		this(authenticator, realm, Executors.newSingleThreadScheduledExecutor(ThreadFactoryBuilder.createThreadFactory("sauth-thread-%d")));
	}

	public SessionAuthenticationHandler(PasswordAuthenticator authenticator, String realm, ScheduledExecutorService ses) {
		this.realm = realm;
		this.authenticator = authenticator;
		this.sessions = SessionManager.create(ses) ;
	}
	

	public void handleHttpRequest(final HttpRequest request, final HttpResponse response, final HttpControl control) throws Exception {
		String authHeader = request.header("Authorization");
		
		SessionInfo sinfo = session(request);
		if (hasSessionKey(request) && sinfo != SessionInfo.NOTEXIST) {
			request.data(SessionInfo.class.getCanonicalName(), sinfo) ;
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
		String sessionKey = new ObjectId().toString();
		SessionInfo created = sessions.newSession(sessionKey) ;
		request.data(SessionInfo.class.getCanonicalName(), created) ;
		
		HttpCookie cookie = new HttpCookie(SessionKey, sessionKey);
		cookie.setVersion(0);
//		cookie.setDomain("61.250.201.157") ;
//		cookie.setPath("/");
		cookie.setSecure(true);
		cookie.setMaxAge(60*60*8); // 8hour
		response.cookie(cookie) ;
	}
	
	private SessionInfo session(HttpRequest request){
		HttpCookie cookie = request.cookie(SessionKey) ;
		if (cookie == null) return SessionInfo.NOTEXIST ;
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

