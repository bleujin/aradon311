package net.ion.nradon.handler.authentication;


public interface SessionManager {

	public SessionInfo newSession(String sessionKey);

	public boolean hasSession(String sessionKey);

	public SessionInfo findSession(String sessionKey);

}
