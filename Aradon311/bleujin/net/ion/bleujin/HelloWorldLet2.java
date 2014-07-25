package net.ion.bleujin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/hello2")
public class HelloWorldLet2 {

	@GET
	public String hello(){
		return "Hello2" ;
	}
}
