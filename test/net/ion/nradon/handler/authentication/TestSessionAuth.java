package net.ion.nradon.handler.authentication;



import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import junit.framework.TestCase;
import net.ion.framework.db.ThreadFactoryBuilder;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.HttpControl;
import net.ion.nradon.HttpHandler;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.handler.event.ServerEvent.EventType;
import net.ion.radon.aclient.Cookie;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Realm;
import net.ion.radon.aclient.Realm.RealmBuilder;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.resteasy.util.HttpHeaderNames;

public class TestSessionAuth extends TestCase {
	
	public void testInfinityRun() throws Exception {
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(ThreadFactoryBuilder.createThreadFactory("sauth-thread-%d")) ;
		SimpleSessionManager smanager = SimpleSessionManager.create(ses) ;
		Radon radon = RadonConfiguration.newBuilder(9800)
				.add(new SessionAuthenticationHandler(new InMemoryPasswords().add("bleujin", "redf"), smanager))
				.add(new HelloWorld()).startRadon() ;
		new InfinityThread().startNJoin(); 
	}

	public void testSession() throws Exception {
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(ThreadFactoryBuilder.createThreadFactory("sauth-thread-%d")) ;
		SimpleSessionManager smanager = SimpleSessionManager.create(ses) ;
		Radon radon = RadonConfiguration.newBuilder(9800)
			.add(new SessionAuthenticationHandler(new InMemoryPasswords().add("bleujin", "redf"), smanager))
			.add(new HelloWorld()).startRadon() ;
		
		
		NewClient nc = NewClient.create() ;
		Response r1 = nc.prepareRequest(firstRequst()).execute().get(); // auth
		assertEquals("HelloWorld", r1.getTextBody());

		Request req2 = secondRequest(r1.getCookies());
		Response r2 = nc.prepareRequest(req2).execute().get();
		assertEquals("HelloWorld", r2.getTextBody());
		
		nc.close(); 
		ses.shutdown(); 
		radon.stop().get() ;
	}

	private Request firstRequst() {
		Realm realm = new RealmBuilder().setPrincipal("bleujin").setPassword("redf").build() ;
		Request request = new RequestBuilder().setRealm(realm).setUrl("http://localhost:9800/hello").setMethod(HttpMethod.GET) .build() ;
		return request;
	}
	
	private Request secondRequest(List<Cookie> cookies) {
		RequestBuilder builder = new RequestBuilder().setUrl("http://localhost:9800/hello").setMethod(HttpMethod.GET);
		for(Cookie c : cookies){
			builder.addCookie(c) ;
		}
		return builder.build() ;
		
	}
	
}

class HelloWorld implements HttpHandler{

	@Override
	public int order() {
		return 0;
	}
	
	@Override
	public void onEvent(EventType event, Radon radon) {
	}
	
	@Override
	@Produces(MediaType.TEXT_PLAIN)
	public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
		
		SimpleSessionInfo sinfo = (SimpleSessionInfo)request.data(SimpleSessionInfo.class.getCanonicalName()) ;
		Debug.line(sinfo, sinfo.hasValue("myinfo"), request.data("user"));
		sinfo.register("myinfo", request.header(HttpHeaderNames.USER_AGENT)) ;
		
		response.content("HelloWorld").charset(Charset.forName("UTF-8")).end() ;
	}	
}
