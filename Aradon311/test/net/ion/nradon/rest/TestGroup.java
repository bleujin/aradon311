package net.ion.nradon.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;

import org.jboss.resteasy.spi.HttpRequest;

public class TestGroup extends TestCase {
	
	
	
	public void testHello() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9000).add(new Rest311Handler()).start().get() ;
		
//		new InfinityThread().startNJoin();
		
		sayHello("/namaste/bleujin");
		sayHello("/hi/bleujin");
		sayHello("/apps/namaste/bleujin");
//		sayHello("/gombangwa/bleujin?name=bleujin"); 
//		sayHello("/notfound"); 

		Thread.sleep(100);
		radon.stop().get() ;
	}


	private void sayHello(String path) throws IOException, UnknownHostException, UnsupportedEncodingException {
		Socket client = new Socket(InetAddress.getLocalHost(), 9000) ;
		OutputStream output = client.getOutputStream() ;
		output.write(("GET " + path + " HTTP/1.0\r\n" + "host: www.radon.com\r\nContent-Type: application/x-www-form-urlencoded\r\n\r\n\r\n").getBytes("UTF-8"));
		output.flush(); 
		
		InputStream input = client.getInputStream() ;
		String result = IOUtil.toStringWithClose(input) ;
		
		Debug.line(result);
		client.close();
	}
	
}




@Path("/gombangwa")
class Gombangwa {
	
	public Gombangwa(){}
	
	
	@GET
	@Path("{name}")
	public String hi(@PathParam("name") String name, @HeaderParam(HttpHeaders.HOST) String host, @Context HttpRequest request){
		UriInfo uinfo = request.getUri();
		Debug.line(uinfo.getPath(), request.getFormParameters());
		return "Gombangwa " + name + " by " + host;
	}
}

