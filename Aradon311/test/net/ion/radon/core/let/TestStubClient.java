package net.ion.radon.core.let;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.client.StubClient;
import net.ion.radon.core.TreeContext;

import org.jboss.resteasy.spi.HttpRequest;

public class TestStubClient extends TestCase{

	private StubClient sclient;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.sclient = StubClient.create(new HelloLet()) ;
	}
	
	public void testUseStupClient() throws Exception {
		StubHttpResponse response = sclient.request("/hello/bleujin").get() ;
		
		assertEquals(200, response.status());
		assertEquals(true, response.contentsString().startsWith("Hello bleujin"));
	}
	
	public void testPostParam() throws Exception {
		StubHttpResponse response = sclient.request("/hello").postParam("name", "bleujin").post() ;
		assertEquals(200, response.status());
		assertEquals(true, response.contentsString().startsWith("Hello bleujin"));
	}

	
	public void testContext() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9000).add(new SectionHandler(new HelloLet())).startRadon() ;
		radon.getConfig().getServiceContext().putAttribute("Greeting", "Hello") ;
	
		Response response = NewClient.create().preparePut("http://localhost:9000/hello/bleujin").execute().get() ;
		Debug.line(response.getTextBody());
		
		radon.stop().get() ;
	}
	
}


@Path("hello")
class HelloLet {
	
	@GET
	@Path("/{name}")
	@Produces(MediaType.TEXT_PLAIN) 
	public String say(@PathParam("name") String name, @Context UriInfo uinfo){
		return "Hello " + name;
	}
	
	@POST
	public String post(@FormParam("name") String name){
		return "Hello " + name; 
	}
	
	@PUT
	@Path("/{name}")
	public Object context(@Context HttpRequest request){
		TreeContext context = (TreeContext) request.getAttribute(TreeContext.class.getCanonicalName());
		return context.getAttributeObject("Greeting") ;
	}
}


