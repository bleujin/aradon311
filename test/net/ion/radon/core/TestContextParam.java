package net.ion.radon.core;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.client.StubServer;

public class TestContextParam extends TestCase {

	
	public void testContextMethodInjector() throws Exception {
		
		StubServer ss = StubServer.create(MyLet.class) ;
		ss.treeContext().putAttribute("greeting", new StringBuilder("Hello")) ;
		
		StubHttpResponse response = ss.request("/context/1/bleujin").get() ;
		Debug.line(response.contentsString());

		Debug.line(ss.request("/context/2/bleujin").get().status(), ss.request("/context/2/bleujin").get().contentsString());

	}
}

@Path("/context")
class MyLet {
	
	@GET
	@Path("/1/{name}")
	public String hello1(@ContextParam("greeting") StringBuilder greeting,  @PathParam("name") String name){
		return greeting  + "-" + name ; 
	}

	
	@GET
	@Path("/2/{name}")
	public String hello2(@ContextParam("greeting") StringBuilder greeting,  @PathParam("name") String name){
		return greeting  + "-" + name ; 
	}

}
