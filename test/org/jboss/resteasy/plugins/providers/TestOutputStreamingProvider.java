package org.jboss.resteasy.plugins.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.client.StubServer;

import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.util.HttpHeaderNames;

@Path("/")
public class TestOutputStreamingProvider extends TestCase{

	public void testSearch() throws Exception {
		StubServer ss = StubServer.create(getClass()) ;
		StubHttpResponse response = ss.request("/hello").get();
		
		Debug.line(response.charset(), response.header(HttpHeaderNames.CONTENT_TYPE));
		Debug.line(response.contentsString());
	}


	@GET
	@Path("/hello")
	//@Produces(MediaType.APPLICATION_JSON)
	public UncertainOutput hello(@Context HttpResponse response){
		
		return new UncertainOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				OutputStreamWriter writer = new OutputStreamWriter(output);
				writer.write("Hello bleujin");
				writer.flush();
			}

			@Override
			public MediaType getMediaType() {
				return MediaType.APPLICATION_XML_TYPE;
			}
		};
	}

	@GET
	@Path("/hi")
//	@Produces(MediaType.APPLICATION_JSON)
	public void hi(@Context HttpResponse response) throws IOException{
		response.getOutputHeaders().putSingle(HttpHeaderNames.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		
		
		new OutputStreamWriter(response.getOutputStream()).write("Hello bleujin");
	}

}
