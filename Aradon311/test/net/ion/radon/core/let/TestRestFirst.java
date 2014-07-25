package net.ion.radon.core.let;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;


@Path("/hello")
public class TestRestFirst extends TestCase {
 
	@GET
	@Path("/{name}")
	@Produces("text/plain")
	public String hello(@PathParam("name") String name, @Context HttpRequest request, @Context HttpResponse response, @Context HttpHeaders headers){
		Debug.line(request, response, headers);
		return "hello " +  name;
	}
	
	public void testHello() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9000).add(new PathHandler(TestRestFirst.class)).start().get() ;
		
//		new InfinityThread().startNJoin(); 
		TinyClient.local9000().sayHello("/hello/bleujin");
		
		
		TinyClient.local9000().sayHello("/hi/bleujin"); 
		radon.stop().get() ;
	}

	
}
