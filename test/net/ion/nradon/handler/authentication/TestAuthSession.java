package net.ion.nradon.handler.authentication;



import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nradon.HttpControl;
import net.ion.nradon.HttpHandler;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.handler.event.ServerEvent.EventType;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Realm;
import net.ion.radon.aclient.Realm.RealmBuilder;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;

import org.jboss.netty.handler.codec.http.HttpMethod;

public class TestAuthSession extends TestCase {
	
	public void testInfinityRun() throws Exception {
		
	}

	public void testSession() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9800)
			.add(new SessionAuthenticationHandler(new InMemoryPasswords().add("bleujin", "redf")))
			.add(new HelloWorld()).startRadon() ;
		
		
		NewClient nc = NewClient.create() ;
		Response r1 = nc.prepareRequest(firstRequst()).execute().get();
		Debug.line(r1.getCookies()) ;
		assertEquals("HelloWorld", r1.getTextBody());

		Request req2 = secondRequest();
		Debug.line(req2.getCookies());
		Response response = nc.prepareRequest(req2).execute().get();
		Debug.line(response.getTextBody());
		
		nc.close(); 
		radon.stop().get() ;
	}

	private Request firstRequst() {
		Realm realm = new RealmBuilder().setPrincipal("bleujin").setPassword("redf").build() ;
		Request request = new RequestBuilder().setRealm(realm).setUrl("http://localhost:9800/hello").setMethod(HttpMethod.GET) .build() ;
		return request;
	}
	
	private Request secondRequest() {
		return new RequestBuilder().setUrl("http://localhost:9800/hello").setMethod(HttpMethod.GET) .build() ;
		
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
	public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
		response.content("HelloWorld").end() ;
	}	
}
