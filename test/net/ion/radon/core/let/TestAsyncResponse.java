package net.ion.radon.core.let;

import java.io.OutputStreamWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.core.let.PathHandler;

import org.jboss.resteasy.spi.HttpResponse;
import org.junit.Test;

@Path("/async")
public class TestAsyncResponse {

	@GET
	public String delay(final HttpResponse response) throws Exception{
		
		Debug.line();
//		new Thread(){
//			
//			public void run(){
//				Response r = Response.ok().type(MediaType.TEXT_PLAIN).entity("Hello World").build() ;
//				response.setResponse(r);
//				
//			}
//		}.start() ;

		
		OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), "UTF-8") ;
		writer.write("hello");
		writer.flush();
		return " world" ;
	}

	@Test
	public void run() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9000).add(new PathHandler(TestAsyncResponse.class)).start().get() ;
		
		
		new InfinityThread().startNJoin(); 
	}
}
