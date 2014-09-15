package net.ion.radon.core;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.client.StubServer;

public class TestContextParam extends TestCase {

	
	private StubServer ss;


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.ss = StubServer.create(MyLet.class) ;
		ss.treeContext().putAttribute("greeting", new StringBuilder("Hello")) ;
	}
	
	public void testContextMethodInjector() throws Exception {
		StubHttpResponse response = ss.request("/context/1/bleujin").get() ;
		Debug.line(response.contentsString());

		Debug.line(ss.request("/context/2/bleujin").get().status(), ss.request("/context/2/bleujin").get().contentsString());
	}
	
	
	public void testInjectSelf() throws Exception {
		StubHttpResponse response = ss.request("/context/3/bleujin").get() ;
		assertEquals("Hello-bleujin", response.contentsString());
	}
	
	
	public void testInjectSelf2() throws Exception {
		StubHttpResponse response = ss.request("/context/4/bleujin").get() ;
		assertEquals("Hello-bleujin", response.contentsString());
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

	
	@GET
	@Path("/3/{name}")
	public String hello3(@ContextParam("net.ion.radon.core.TreeContext") TreeContext context,  @PathParam("name") String name){
		return context.getAttributeObject("greeting")  + "-" + name ; 
	}


	@GET
	@Path("/4/{name}")
	public String hello4(@Context TreeContext context,  @PathParam("name") String name){
		return context.getAttributeObject("greeting")  + "-" + name ; 
	}



}
