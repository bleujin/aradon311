package net.ion.radon.core.let;

import java.io.File;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.HttpControl;
import net.ion.nradon.HttpHandler;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.handler.SimpleStaticFileHandler;
import net.ion.nradon.handler.event.ServerEvent.EventType;
import net.ion.nradon.handler.logging.LoggingHandler;
import net.ion.nradon.handler.logging.SimpleLogSink;
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.client.StubServer;

import org.jboss.resteasy.spi.HttpRequest;

public class TestPathHandler extends TestCase {

	public void testFirst() throws Exception {
		
		Radon radon = RadonConfiguration.newBuilder(9100).add("/bleujin/*", new PathHandler(ViewFileLet.class)).startRadon() ;
//		TinyClient.local9000().sayHello("/bleujin/hi/rulala/d"); 
		TinyClient.local(9100).sayHello("/bleujin/resource/ptest.prop");
		radon.stop().get() ;
	}
	
	public void testPrefix() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9100)
				.add("/bleujin/*", new PathHandler(ViewFileLet.class, PrefixLet.class).prefixURI("/bleujin"))
				.add(new LoggingHandler(new SimpleLogSink()))
//				.add(new DebugHandler())
				.startRadon() ;

		TinyClient.local(9100).sayHello("/bleujin/resource/ptest.prop2");
		
		new InfinityThread().startNJoin();
		radon.stop().get() ;
		
	}
	
	
	public void testFile() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9100)
				.add(new LoggingHandler(new SimpleLogSink()))
				.add(new SimpleStaticFileHandler(new File("./resource")))
				.add(new DebugHandler())
				.startRadon() ;
		
		NewClient nc = NewClient.create();
		Response response = nc.prepareGet("http://localhost:9100/ptest.prop").execute().get() ;
		Debug.line(response.getStatus(), response.getTextBody()) ;
		nc.close(); 
		
		radon.stop().get() ;
	}
	
	public void testRemainPath() throws Exception {
		StubServer ss = StubServer.create(AsterikLet.class) ;
		StubHttpResponse response = ss.request("/bleujin/greeting/hello/hi/3").get() ;
		Debug.line(response.contentsString());
	}
	
	
	public void testDefault() throws Exception {
		StubServer ss = StubServer.create(DefaultLet.class) ;
		StubHttpResponse response = ss.request("/boost").postParam("boost", "").post() ;
		
		Debug.line(response.contentsString());
	}
	
	public void testOnlyPath() throws Exception {
		StubServer ss = StubServer.create(OnlyPath.class) ;
		StubHttpResponse response = ss.request("/onlypath").postParam("name", "bleujin").post() ;
		
		Debug.line(response.contentsString());
		
	}

	
}

@Path("/boost")
class DefaultLet {
	
	@POST
	@Path("")
	public String dftFn(@DefaultValue("1.0") @FormParam("boost") String boost){
		return boost ;
	}
}

class DebugHandler implements HttpHandler {

	@Override
	public void onEvent(EventType event, Radon radon) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int order() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void handleHttpRequest(net.ion.nradon.HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
//		Debug.debug(request, response, control);
//		response.status(404) ;
		control.nextHandler(request, response);
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
	public File viewFile(@PathParam("remain") String remain, @Context HttpRequest request){
		return new File("./resource/" + remain) ;
	}
	
}

@Path("/resource")
class PrefixLet {
	
	@GET
	@Path("/{remain : .*}")
	@Produces(MediaType.TEXT_PLAIN)
	public String viewFile(@PathParam("remain") String remain, @Context HttpRequest request, @Context HttpResponse response){
		Debug.line(request.getUri().getPath());
		File file = new File("./resource/" + remain);
		if (! file.exists()) {
//			response.error(new WebApplicationException(507));
		}
		return String.valueOf(file.exists()) ;
	}
	
}

@Path("/onlypath")
class OnlyPath {
	
	@POST
	public String onlyPath(@FormParam("name") String name){
		return name ;
	}
	
	
}