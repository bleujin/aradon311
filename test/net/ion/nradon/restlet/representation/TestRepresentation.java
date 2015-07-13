package net.ion.nradon.restlet.representation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;

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

@Path("")
public class TestRepresentation extends TestCase {

	@GET
	@Path("/rep/{type}")
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
	
	
	@GET
	@Path("/char/{ctype}")
	public InputStreamRepresentation say(@PathParam("ctype") String charset) throws UnsupportedEncodingException{
		return new InputStreamRepresentation(MediaType.TEXT_PLAIN, new ByteArrayInputStream("안녕".getBytes(charset))) ;
	}
	
	public void testRepresentation() throws Exception {
		StubServer ss = StubServer.create(getClass()) ;
		
		StubHttpResponse response = ss.request("/rep/file").get() ;
		Debug.line(response.contentType());
	}
	
	public void testCharset() throws Exception {
		StubServer ss = StubServer.create(getClass()) ;
		StubHttpResponse response = ss.request("/char/euc-kr").get() ;
		
		Debug.line(response.contentsString(), new String(response.contents(), "euc-kr") ) ;
		Debug.line(response.charset());
		
	}
}
