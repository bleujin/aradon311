package net.ion.radon.core.let;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import junit.framework.TestCase;
import net.ion.radon.client.StubServer;

@Path("/hello")
public class TestRemainPath extends TestCase{

	@GET @POST
	@Path("/path/{remain: [A-Za-z0-9_]*}")
	public String hello(@PathParam("remain") String remain){
		return remain ; 
	}
	
	@GET 
	@Path("/eee/{remain: [A-Za-z0-9_]*}.{ext}")
	public String hi(@PathParam("remain") String remain){
		return remain ; 
	}

	
	public void testStub() throws Exception {
		StubServer ss = StubServer.create(getClass()) ;
		assertEquals("bleujin", ss.request("/hello/path/bleujin").get().contentsString());
	}

	public void testStubWithParameter() throws Exception {
		StubServer ss = StubServer.create(getClass()) ;
		assertEquals("bleujin", ss.request("/hello/path/bleujin?name=bleujin").get().contentsString());
	}

	public void testStubWithParameter2() throws Exception {
		StubServer ss = StubServer.create(getClass()) ;
		assertEquals("bleujin", ss.request("/hello/path/bleujin?name=bleujin").post().contentsString());
	}

	public void testExtend() throws Exception {
		StubServer ss = StubServer.create(getClass()) ;
		assertEquals("bleujin", ss.request("/hello/eee/bleujin.exe").get().contentsString());
	}
	
}
