package net.ion.radon.core.let;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import junit.framework.TestCase;
import net.ion.framework.util.IOUtil;
import net.ion.nradon.restlet.MediaType;
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.client.StubServer;

import org.jboss.resteasy.spi.HttpResponse;

@Path("/resource")
public class TestFileHandler extends TestCase {

	@GET
	@Path("/path/{remain : .*}")
	public Response viewFile(@PathParam("remain") String remainPath, @Context HttpResponse response){
		response.getOutputHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_ICON.toString());
		return new FileResponseBuilder(new File("./resource", remainPath)).build() ;
	}
	
	@GET
	@Path("/sout/{remain : .*}")
	public StreamingOutput streamFile(@PathParam("remain") final String remainPath, @Context HttpResponse response){
		response.getOutputHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_ICON.toString());
		return new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				FileInputStream fis = new FileInputStream(new File("./resource", remainPath)); 
				IOUtil.copy(fis, output);
			}
		};
	}
	
	@GET
	@Path("/response/{remain : .*}")
	public Response responseFile(@PathParam("remain") final String remainPath){
		File file = new File("./resource", remainPath) ;
		return Response.status(200).type(MediaType.IMAGE_ICON.toString()).entity(file).build() ;
	}

	
	@GET
	@Path("/input/{remain : .*}")
	public Response responseInputstream(@PathParam("remain") final String remainPath) throws FileNotFoundException{
		File file = new File("./resource", remainPath) ;
		return Response.status(200).type(MediaType.IMAGE_ICON.toString()).entity(new FileInputStream(file)).build() ;
	}
	

	
	public void testViewFile() throws Exception {
		StubServer ss = StubServer.create(getClass()) ;
		StubHttpResponse response = ss.request("/resource/input/favicon.ico").get() ;
		
		assertEquals(MediaType.IMAGE_ICON.toString(), response.header(HttpHeaders.CONTENT_TYPE)) ;
		assertEquals(true, response.contents().length > 0) ;
	}
}


