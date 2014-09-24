package net.ion.nradon.handler.authentication;

import java.util.Map;

import net.ion.framework.util.MapUtil;

public class SessionInfo {
	
	static SessionInfo NOTEXIST = new SessionInfo("not_exist");
	private Map<String, Object> sinfo = MapUtil.newMap();
	private String sessionKey;
	private long touched;

	private SessionInfo(String sessionKey) {
		this.sessionKey = sessionKey ;
		this.touched = System.currentTimeMillis();
	}

	public static SessionInfo create(String sessionKey) {
		return new SessionInfo(sessionKey);
	}

	public void touch() {
		this.touched = System.currentTimeMillis();
	}

	public boolean downTouched(long std) {
		return this.touched <= std;
	}

	public String sessionKey() {
		return sessionKey;
	}
	
	public SessionInfo register(String name, Object value){
		sinfo.put(name, value) ;
		return this ;
	}

	
	public boolean hasValue(String name){
		return sinfo.containsKey(name) ;
	}
}