package net.ion.radon.core.let;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;


@Path("namaste")
public class NamasteLet {
	
	@GET
	@Path("{name}")
	@Produces("text/plain")
	public String hi(@PathParam("name") String name, @Context UriInfo uinfo){
		return "Namaste " + uinfo.getPath();
	}
}


