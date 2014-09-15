package net.ion.radon.core.let;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import junit.framework.TestCase;
import net.ion.radon.client.StubServer;

public class TestSamePath extends TestCase{

	
	public void testSameClzPath() throws Exception {
		StubServer ss = StubServer.create(Path1.class, Path2.class) ;
		
		assertEquals("hello bleujin", ss.request("/hello/bleujin").get().contentsString()) ;
		assertEquals("hi bleujin", ss.request("/hi/bleujin").get().contentsString()) ;
	}
	
}


@Path("")
class Path1 {
	
	@GET
	@Path("/hello/{name}")
	public String hello(@PathParam("name") String name){
		return "hello " + name ;
	}
	
}

@Path("")
class Path2 {
	
	@GET
	@Path("/hi/{name}")
	public String hello(@PathParam("name") String name){
		return "hi " + name ;
	}
	
}