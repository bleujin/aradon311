package net.ion.bleujin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/hello")
public class HelloWorldLet {

	@GET
	@Path("{name}")
	public String hello(@PathParam("name") String name){
		return "Hello " + name ;
	}
}
