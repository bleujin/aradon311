package net.ion.nradon.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.multipart.FilePart;
import net.ion.radon.aclient.multipart.StringPart;
import net.ion.radon.core.let.SectionHandler;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

public class TestMultiPart extends TestCase {

	private Radon radon;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.radon = RadonConfiguration.newBuilder(9000).add(new SectionHandler(new UploadFileService())).startRadon();
	}
	
	@Override
	protected void tearDown() throws Exception {
		radon.stop().get() ;
		super.tearDown();
	}
	
	public void testUpload() throws Exception {
		RequestBuilder builder = new RequestBuilder().setUrl("http://localhost:9000/file/upload").setMethod(HttpMethod.POST);
		builder.addBodyPart(new StringPart("name", "value", "UTF-8")).addBodyPart(new FilePart("myfile", new File("resource/ptest.prop")));

		NewClient client = NewClient.create();
		Response response = client.prepareRequest(builder.build()).execute().get() ;
		Debug.line(response.getTextBody(), response.getStatus());
//
//		assertEquals("hello.txt", json.asString("myfile"));
//		assertEquals("value", json.asString("name"));
	}
}

@Path("/file")
class UploadFileService {

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String uploadFile(@MultipartForm FileUploadForm uform) throws IOException {

		Debug.line(uform.name, IOUtil.toString(uform.input));
		
		return "";
	}

}


class FileUploadForm implements Serializable {
	private static final long serialVersionUID = -2975137795423083295L;

	public FileUploadForm() {
		super() ;
	}


	@FormParam("myfile")
	InputStream input ;
	
	@FormParam("name")
	String name ;
}
