package net.ion.radon.core.let;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import junit.framework.TestCase;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;

public class TestPathHandlerAutoReload extends TestCase{

	public void testSayHello() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9100).add("/*", new PathHandler(SayHello.class)).startRadon() ;
		
		
		new InfinityThread().startNJoin(); 
	}
}


@Path("/hello/{name}")
class SayHello {
	
	@GET
	public String sayHello(@PathParam("name") String name){
		return "Hello " + name ;
	}
}