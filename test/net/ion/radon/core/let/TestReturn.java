package net.ion.radon.core.let;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;

import org.jboss.resteasy.spi.HttpRequest;

@Path("")
public class TestReturn extends TestCase {

	@GET
	@Path("/view")
	public Object printParam(@Context HttpRequest request){
		return "hello" ;
	}
	
	
	public void testType() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(10000).add(new PathHandler(getClass())).startRadon() ;
		
		NewClient client = NewClient.create() ;
		
		Response response = client.prepareGet("http://localhost:10000/view").execute().get() ;
		
		Debug.line(response.getTextBody());
		
		client.close(); 
		radon.stop().get() ;
		
	}
	
	
}
