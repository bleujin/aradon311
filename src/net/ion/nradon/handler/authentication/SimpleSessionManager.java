package net.ion.nradon.handler.authentication;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;

public class SimpleSessionManager implements SessionManager {
	private Map<String, SimpleSessionInfo> sessionMap =  MapUtil.newSyncMap();
	private static long LimitSecond = 60 * 60;
	private static int CheckSecond = 30;

	private SimpleSessionManager(final ScheduledExecutorService ses) {
		ses.schedule(new Callable<Integer>() {
			@Override
			public Integer call() {
				int count = 0;
				try {
					long stdTime = System.currentTimeMillis() - LimitSecond;
					for (SimpleSessionInfo sinfo : sessionMap.values()) {
						if (sinfo.downTouched(stdTime)) {
							sessionMap.remove(sinfo.sessionKey());
							count++;
						}
					}
				} finally {
					ses.schedule(this, CheckSecond, TimeUnit.SECONDS);
				}

				return count;
			}
		}, CheckSecond, TimeUnit.SECONDS);
	}

	public final static SimpleSessionManager create(ScheduledExecutorService ses) {
		return new SimpleSessionManager(ses);
	}

	public SessionInfo newSession(String sessionKey) {
		SimpleSessionInfo created = SimpleSessionInfo.create(sessionKey);
		sessionMap.put(sessionKey, created);
		return created ;
	}

	public boolean hasSession(String sessionKey) {
		return sessionMap.containsKey(sessionKey) ;
	}

	public SessionInfo findSession(String sessionKey) {
		SessionInfo found = sessionMap.get(sessionKey);
		SessionInfo result = ObjectUtil.coalesce(found, SimpleSessionInfo.NOTEXIST);
		
		result.touch();  // 
		return result;
	}

	
	
}