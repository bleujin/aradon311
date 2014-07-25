package net.ion.nradon.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;


@Path("/hello")
public class TestRestFirst extends TestCase {
 
	@GET
	@Path("/{name}")
	@Produces("text/plain")
	public String hello(@PathParam("name") String name, @Context HttpRequest request, @Context HttpResponse response, @Context HttpHeaders headers){
		Debug.line(request, response, headers);
		return "hello " +  name;
	}
	
	public void testHello() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9000).add(new Rest311Handler(TestRestFirst.class)).start().get() ;
		
//		new InfinityThread().startNJoin(); 
		sayHello("/hello/bleujin");
		
		
		sayHello("/hi/bleujin"); 
		radon.stop().get() ;
	}


	private void sayHello(String path) throws IOException, UnknownHostException, UnsupportedEncodingException {
		Socket client = new Socket(InetAddress.getLocalHost(), 9000) ;
		OutputStream output = client.getOutputStream() ;
		output.write(("GET " + path + " HTTP/1.0\r\n" + "host: www.radon.com\r\n\r\n").getBytes("UTF-8"));
		output.flush(); 
		
		InputStream input = client.getInputStream() ;
		String result = IOUtil.toStringWithClose(input) ;
		
		Debug.line(result);
		client.close();
	}
	
}
