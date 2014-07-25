package net.ion.radon.core.let;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;

public class TestSectionHandler extends TestCase {

	public void testFirst() throws Exception {
		
		Radon radon = RadonConfiguration.newBuilder(9000).add("/bleujin/*", new PathHandler(AsterikLet.class, ViewFileLet.class)).startRadon() ;
//		printGet("/bleujin/hi/rulala/d"); 
		printGet("/bleujin/resource/ptest.prop");
		radon.stop().get() ;
	}
	
	private void printGet(String path) throws IOException, UnknownHostException, UnsupportedEncodingException {
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