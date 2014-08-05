package net.ion.nradon;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;
import net.ion.radon.core.let.PathHandler;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.resteasy.spi.HttpRequest;

public class TestWhenGetParameter extends TestCase{

	
	public void testBodyEntity() throws Exception {
		
		Radon radon = RadonConfiguration.newBuilder(9500).add(new PathHandler(ResourceLet.class)) .startRadon() ;
		
		NewClient nc = NewClient.create() ;
		Request request = new RequestBuilder().setMethod(HttpMethod.POST).setBody("Hello").setUrl("http://localhost:9500/resource?name=bleujin").build() ;
		
		Response response = nc.prepareRequest(request).execute().get() ;
		assertEquals("Hello bleujin", response.getTextBody());
		
		nc.close(); 
		radon.stop().get() ;
	}
}

@Path("/resource")
class ResourceLet {
	
	@POST
	public String hello(@Context HttpRequest request, @QueryParam("name") String name) throws IOException{
		InputStream input = request.getInputStream() ;
		String bodyValue = IOUtil.toStringWithClose(input) ;
		
		MultivaluedMap<String, String> map = request.getHttpHeaders().getRequestHeaders() ;
		for(String hname : map.keySet()){
			Debug.line(hname, map.get(hname));
		}
		
		return bodyValue + " " + name ;
	}
}
