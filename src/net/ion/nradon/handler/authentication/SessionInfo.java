package net.ion.nradon.handler.authentication;

public interface SessionInfo {

	public void touch();

	public boolean downTouched(long std);

	public String sessionKey();

	public SessionInfo register(String name, Object value);

	public boolean hasValue(String name);

}
