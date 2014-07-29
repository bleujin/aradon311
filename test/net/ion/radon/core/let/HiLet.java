package net.ion.radon.core.let;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;


@Path("/hi/{name}")
@Produces("application/octet-stream")
public class HiLet {
	
//	@GET
//	@Produces("text/plain")
//	public String hi(@PathParam("name") String name){
//		return "Hi " + name ;
//	}

	@GET
	@Produces("application/octet-stream")
	public StreamingOutput hi(@PathParam("name") final String name){
		return new StreamingOutput() {
			@Override
			public void write(OutputStream os) throws IOException, WebApplicationException {
				PrintStream writer = new PrintStream(os);
				writer.println("Hello ");
				writer.flush(); 
				writer.println("Hm");
			}
			
			public String toString(){
				return "StreamOutput" ;
			}
		};
	}

	
	@Override
	public String toString(){
		return HiLet.class.getSimpleName() + "." ;
	}
}