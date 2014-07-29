package net.ion.radon.core.let;

import java.io.File;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import junit.framework.TestCase;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;

public class TestPathHandler extends TestCase {

	public void testFirst() throws Exception {
		
		Radon radon = RadonConfiguration.newBuilder(9000).add("/bleujin/*", new PathHandler(AsterikLet.class, ViewFileLet.class)).startRadon() ;
//		TinyClient.local9000().sayHello("/bleujin/hi/rulala/d"); 
		TinyClient.local9000().sayHello("/bleujin/resource/ptest.prop");
		radon.stop().get() ;
	}

}


@Path("bleujin")
class AsterikLet {
	@GET
	@Path("/{greeting}/{gd : .*}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.TEXT_PLAIN) 
	public String say(@PathParam("gd") String greeting, @Context UriInfo uinfo){
		return greeting + " " + uinfo.getPath();
	}
}

@Path("bleujin")
class ViewFileLet {
	
	@GET
	@Path("/resource/{remain : .*}")
	@Produces(MediaType.TEXT_PLAIN)
	public File viewFile(@PathParam("remain") String remain){
		return new File("./resource/" + remain) ;
	}
	
}