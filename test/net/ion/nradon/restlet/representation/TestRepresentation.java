package net.ion.nradon.restlet.representation;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.nradon.restlet.Language;
import net.ion.nradon.restlet.MediaType;
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.client.StubServer;

@Path("/rep")
public class TestRepresentation extends TestCase {

	@GET
	@Path("/{type}")
	public Representation hello(@PathParam("type") String type){
		if ("json".equals(type)){
			return new JsonObjectRepresentation(new JsonObject().put("name", "bleujin")) ;
		} else if ("string".equals(type)){
			return new StringRepresentation("hello bleujin", Language.ENGLISH) ;
		} else if ("file".equals(type)){
			return new FileRepresentation(new File("./resource/imsi/chart.png"), MediaType.IMAGE_PNG) ;
		} else {
			return new EmptyRepresentation() ;
		}
	}
	
	public void testRepresentation() throws Exception {
		StubServer ss = StubServer.create(getClass()) ;
		
		StubHttpResponse response = ss.request("/rep/file").get() ;
		Debug.line(response.contentType());
	}
}
