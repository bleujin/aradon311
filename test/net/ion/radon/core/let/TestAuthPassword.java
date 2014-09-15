package net.ion.radon.core.let;

import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ObjectUtil;
import net.ion.nradon.HttpControl;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.handler.AbstractHttpHandler;
import net.ion.nradon.handler.authentication.BasicAuthenticationHandler;
import net.ion.nradon.handler.authentication.InMemoryPasswords;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Realm;
import net.ion.radon.aclient.Realm.RealmBuilder;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.resteasy.spi.HttpRequest;

@Path("")
public class TestAuthPassword extends TestCase {


	@GET
	@Path("/login")
	public String login(@Context HttpRequest request, @Context SecurityContext context){
		Object userName = request.getAttribute(BasicAuthenticationHandler.USERNAME) ;
		
		HttpHeaders headers = request.getHttpHeaders() ;
		MultivaluedMap<String, String> map = headers.getRequestHeaders() ;
		for (Entry<String, List<String>> entry : headers.getRequestHeaders().entrySet()) {
//			Debug.line(entry.getKey(), entry.getValue());
		}
		return ObjectUtil.toString(userName) ;
	}
	
	public void testClientInfo() throws Exception {

		Radon radon = RadonConfiguration.newBuilder(8500)
//				.add(new WhoAmIHttpHandler())
				.add(new BasicAuthenticationHandler(new InMemoryPasswords().add("bleujin", "1"), "USER"))
				.add("/login", new PathHandler(getClass())).start().get() ;
		
		
		Realm realm = new RealmBuilder().setPrincipal("bleujin").setPassword("1").build() ;
		Request req = new RequestBuilder(HttpMethod.GET).setRealm(realm).setUrl("http://localhost:8500/login").build() ;
		Response response = NewClient.create().prepareRequest(req).execute().get() ;

		Debug.line(response.getTextBody());
	}
	
}


class WhoAmIHttpHandler extends AbstractHttpHandler {
    public void handleHttpRequest(net.ion.nradon.HttpRequest request, net.ion.nradon.HttpResponse response, HttpControl control) throws Exception {
        response.header("Content-type", "text/html")
            // .content("Whomai : You are: " + request.data(BasicAuthenticationHandler.USERNAME))
            .content("Whomai : You are: ")
            .end();
    }
}
