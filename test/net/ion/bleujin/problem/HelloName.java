package net.ion.bleujin.problem;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;


@Path("/hello")
public class HelloName {
	
	
	@GET
	@Path("/c1/{name}")
	public String hi(@PathParam("name") String name){
		return "eee." + name ;
	}

	@GET
	@Path("/c2/{name}")
	public String hi2(@PathParam("name") String name){
		return "cc . " + name ;
	}
	

	@GET
	@Path("/c3/{name}")
	public String hi3(@PathParam("name") String name){
		return "ee. " + name ;
	}
	


}
