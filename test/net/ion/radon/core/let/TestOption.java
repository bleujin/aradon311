package net.ion.radon.core.let;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import junit.framework.TestCase;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;

import org.jboss.resteasy.spi.HttpResponse;

@Path("/hello")
public class TestOption extends TestCase {

	public void testFirst() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9100).add("/*", new PathHandler(getClass())).startRadon() ;
//		TinyClient.local9000().sayHello("/bleujin/hi/rulala/d"); 
		
		new InfinityThread().startNJoin(); 
	}
	
	@Path("/{name}")
	@GET
	public String sayHello(@PathParam("name") String name, @Context HttpResponse response){
		return "hello " + name ;
	}
	

	

}
