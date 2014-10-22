package net.ion.nradon.handler.authentication;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;

public class SessionManager {
	private Map<String, SessionInfo> sessionMap =  MapUtil.newSyncMap();
	private ScheduledExecutorService ses;
	private static long LimitSecond = 60 * 60;
	private static int CheckSecond = 5;

	private SessionManager(final ScheduledExecutorService ses) {
		this.ses = ses;

		ses.schedule(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				int count = 0;
				try {
					long stdTime = System.currentTimeMillis() - LimitSecond;
					for (SessionInfo sinfo : sessionMap.values()) {
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

	public final static SessionManager create(ScheduledExecutorService ses) {
		return new SessionManager(ses);
	}

	public SessionInfo newSession(String sessionKey) {
		SessionInfo created = SessionInfo.create(sessionKey);
		sessionMap.put(sessionKey, created);
		return created ;
	}

	public boolean hasSession(String sessionKey) {
		SessionInfo session = findSession(sessionKey);
		if (session != null)
			session.touch();
		return session != null;
	}

	public SessionInfo findSession(String sessionKey) {
		SessionInfo result = sessionMap.get(sessionKey);
		return ObjectUtil.coalesce(result, SessionInfo.NOTEXIST);
	}

	
	
}