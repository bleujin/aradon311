package net.ion.radon.cload.cloader;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.cload.monitor.AbstractListener;
import net.ion.radon.cload.monitor.FileAlterationMonitor;
import net.ion.radon.core.ContextParam;
import net.ion.radon.core.let.PathHandler;

import org.apache.commons.io.monitor.FileAlterationObserver;

public class TestPathClassLoader extends TestCase {

	
	public void testStart() throws Exception {
		
		DynamicClassLoader inner = new DynamicClassLoader("./test") ;
		final OuterClassLoader reloader = new OuterClassLoader(inner);

		FileAlterationObserver fo = new FileAlterationObserver(new File("./test")) ;
		fo.addListener(new AbstractListener() {
			@Override
			public void onFileChange(File file) {
				try {
					Debug.line(file + " changed !");
					reloader.change(new DynamicClassLoader("./test"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		FileAlterationMonitor fam = new FileAlterationMonitor(1000, Executors.newScheduledThreadPool(1), fo);
		fam.start();

		
		
		Radon radon = RadonConfiguration.newBuilder(9000)
					.add("/hello/*", PathHandler.reload(SayHello.class).prefixURI("/hello"))
					.add("/hi/*", new PathHandler(SayHi.class).prefixURI("/hi"))
					.start().get() ;
		radon.getConfig().getServiceContext().putAttribute("greeting", "Hello") ;
		
		new InfinityThread().startNJoin(); 
	}
	
}


@Path("")
class SayHello {

	private String greeting;
	public SayHello(@ContextParam("greeting") String greeting, @QueryParam("num") int i){
		this.greeting = greeting ;
	}
	
	@GET
	@Path("/{name}")
	public String sayHello(@PathParam("name") String name ){
		return "Hello " + name ;
	}
}

@Path("")
class SayHi {
	@GET
	@Path("/{name}")
	public String sayHello(@PathParam("name") String name){
		return "Hi " + name ;
	}
	
}