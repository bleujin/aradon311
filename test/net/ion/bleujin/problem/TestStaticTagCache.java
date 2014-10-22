package net.ion.bleujin.problem;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.restlet.FileMetaType;
import net.ion.radon.core.let.PathHandler;
import net.ion.radon.handler.AppCacheHandler;

import org.jboss.resteasy.util.HttpHeaderNames;

public class TestStaticTagCache extends TestCase {

	public void testRunner() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9000)
				.add("/img/*", new PathHandler(BotResource.class).prefixURI("img"))
				.add("/cache.appcache", new AppCacheHandler(new File("./resource/temp/img")).startMonitor(Executors.newScheduledThreadPool(1)).prefixPath("/img") )
				.add(new PathHandler(NormalResource.class)).startRadon();

		new InfinityThread().startNJoin();
	}
	
	
	public void testFileAlterMonitor() throws Exception {
		
	}

	public void testRegular() throws Exception {
		String s = "This is a sample<img src=\"bot/image/angry.jpg\" /> text";
		
		Pattern p = Pattern.compile("<img[^>]*src=[\"']([^\"^']*)", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(s);
		if (m.find()) {
			String src = m.group(0);
			Debug.line(src, m.groupCount(), src + m.replaceAll("?_id=123456789"));
		}
		s = s.replaceAll("[<](/)?img[^>]*[>]", "");
		System.out.println(s);
	}
	
}


@Path("")
class NormalResource {
	
	@GET
	@Path("{remain : .*}")
	public Response view(@PathParam("remain") String remainPath) {
		File file = new File("./resource/temp/" + remainPath);
		if (!file.exists())
			return Response.status(404).build();
		Debug.line(remainPath);
		String type = FileMetaType.mediaType(file.getName()) ;		
		return Response.status(javax.ws.rs.core.Response.Status.OK.getStatusCode()).type(type).entity(file).build();
	}
}


@Path("/bot")
class BotResource {

	@GET
	@Path("{remain : .*}")
	public Response view(@PathParam("remain") String remainPath) {
		File file = new File("./resource/temp/" + remainPath);
		if (!file.exists())
			return Response.status(404).build();

		String type = FileMetaType.mediaType(file.getName()) ;		
		return Response.status(javax.ws.rs.core.Response.Status.OK.getStatusCode()).type(type).entity(file).header(HttpHeaderNames.EXPIRES, "Wed 22 Oct 2014 00:00:00").header("max-age", "3600").build();
	}
}
