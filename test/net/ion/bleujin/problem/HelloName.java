package net.ion.bleujin.problem;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;


@Path("/hihi")
public class HelloName {
	
	
	@GET
	@Path("/{name}")
	public String hello(@PathParam("name") String name){
		return "dcdcd  " + name ;
	}
	
	@GET
	@Path("/{name}/{greeting}")
	public String hi(@PathParam("name") String name, @PathParam("greeting") String greeting){
		return greeting + " " + name ;
	}
	
}
