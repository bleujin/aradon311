package net.ion.radon.core.let;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import junit.framework.TestCase;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.client.StubServer;
import net.ion.radon.core.TreeContext;

import org.jboss.resteasy.spi.HttpRequest;

@Path("/context")
public class TestContext extends TestCase{

	public void testFindContext() throws Exception {
		StubServer ss = StubServer.create(getClass());
		ss.treeContext().putAttribute("Greeting", "Hello") ;

		StubHttpResponse response = ss.request("/context/bleujin").get() ;
		assertEquals("Hello bleujin", response.contentsString());
	}
	
	
	public void testFindContextAtServer() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9000).add(new PathHandler(getClass())).startRadon() ;
		TreeContext root = radon.getConfig().getServiceContext() ;
		root.putAttribute("Greeting", "Hello") ;
		
		NewClient nc = NewClient.create();
		Response response = nc.prepareGet("http://localhost:9000/context/bleujin").execute().get() ;
		assertEquals("Hello bleujin", response.getTextBody());
		nc.close(); 
		
		radon.stop().get() ;
	}
	
	
	@GET
	@Path("/{name}")
	public String findContext(@Context HttpRequest req, @PathParam("name") String name){
		TreeContext context = (TreeContext) req.getAttribute(TreeContext.class.getCanonicalName());
		return context.getAttributeObject("Greeting") +  " " + name ;
	}
}
