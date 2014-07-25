package net.ion.bleujin.aradon;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class HelloServerResource  {

	@GET
	public String represent() {
		return "hello, world";
	}
}
