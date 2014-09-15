package org.jboss.resteasy.plugins.server.resourcefactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

public 
@Path("")
class Hallo {
	
	@GET
	@Path("/{name}")
	public String hello(@PathParam("name") String name){
		return "Hello " + name ;
	}
}