package net.ion.radon.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import org.jboss.resteasy.util.HttpHeaderNames;

public class TestStaticTagCache extends TestCase {

	public void testRunner() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(8900)
				.add(new AppCacheHandler()
					.appendDir("/img", new File("./resource/temp/img"), "*.png")
					.appendDir("/js", new File("./resource/temp/js"), "*.js")
//					.referenceDir("", new File("./resource/temp"), "*.htm")
					.startMonitor(Executors.newScheduledThreadPool(1)) )
				.add(new PathHandler(NormalResource.class))
//				.add(new SimpleStaticFileHandler("./resource/temp"))
				.startRadon();

		new InfinityThread().startNJoin();
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
	public Response view(@PathParam("remain") String remainPath) throws FileNotFoundException {
		File file = new File("./resource/temp/" + remainPath);
		if (!file.exists())
			return Response.status(404).build();
		Debug.line(remainPath);
		String type = FileMetaType.mediaType(file.getName()) ;		
		return Response
				.status(javax.ws.rs.core.Response.Status.OK.getStatusCode())
				.header(HttpHeaderNames.CACHE_CONTROL, "no-cache").header(HttpHeaderNames.EXPIRES, "-1")
				.type(type).entity(new FileInputStream(file)).build();
	}
}


