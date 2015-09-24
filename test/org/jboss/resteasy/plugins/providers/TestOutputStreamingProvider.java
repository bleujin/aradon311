package org.jboss.resteasy.plugins.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.client.StubServer;

import org.jboss.resteasy.util.HttpHeaderNames;

@Path("/")
public class TestOutputStreamingProvider extends TestCase{

	public void testOutput() throws Exception {
		StubServer ss = StubServer.create(getClass()) ;
		StubHttpResponse response = ss.request("/output").get();
		
		Debug.line(response.charset(), response.header(HttpHeaderNames.CONTENT_TYPE));
		Debug.line(response.contentsString());
	}
	
	public void testStream() throws Exception {
		StubServer ss = StubServer.create(getClass()) ;
		StubHttpResponse response = ss.request("/stream").get();
		
		Debug.line(response.charset(), response.header(HttpHeaderNames.CONTENT_TYPE));
		Debug.line(response.contentsString());
		for (String hname : response.headerNames()){
			Debug.line(hname, response.header(hname));
		}
	}


	@GET
	@Path("/output")
	//@Produces(MediaType.APPLICATION_JSON)
	public UncertainOutput output(){
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
	@Path("/stream")
	public HttpStreamOutput stream(){
		return new HttpStreamOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				OutputStreamWriter writer = new OutputStreamWriter(output);
				writer.write("Hello bleujin");
				writer.flush();
			}
			
			@Override
			public void header(MultivaluedMap<String, Object> headers) {
				headers.putSingle("Content-Type", MediaType.APPLICATION_XML_TYPE.toString());
				headers.putSingle("myHeader", "by aradon");
			}
		};
	}

}
