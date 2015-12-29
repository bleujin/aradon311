package net.ion.nradon.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
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
import net.ion.radon.core.let.PathHandler;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.plugins.providers.multipart.FormDataHandler;
import org.jboss.resteasy.plugins.providers.multipart.InputBody;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.spi.HttpRequest;

public class TestMultiPart extends TestCase {

	private Radon radon;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.radon = RadonConfiguration.newBuilder(9200).add(new PathHandler(UploadFileService.class)).startRadon();
	}

	@Override
	protected void tearDown() throws Exception {
		radon.stop().get();
		super.tearDown();
	}


	public void testUpload() throws Exception {
		RequestBuilder builder = new RequestBuilder().setUrl("http://localhost:9200/file/upload").setMethod(HttpMethod.POST);
		builder.addBodyPart(new StringPart("name", "value", "UTF-8"))
			.addBodyPart(new FilePart("myfile", new File("resource/temp/img/sadness.png")));

		NewClient client = NewClient.create();
		Response response = client.prepareRequest(builder.build()).execute().get();
		Debug.line(response.getTextBody(), response.getStatus());
		//
		// assertEquals("hello.txt", json.asString("myfile"));
		// assertEquals("value", json.asString("name"));
	}
	
	public void testInput() throws Exception {
		RequestBuilder builder = new RequestBuilder().setUrl("http://localhost:9200/file/input").setMethod(HttpMethod.POST);
		builder.addBodyPart(new StringPart("name", "value", "UTF-8"))
			.addBodyPart(new FilePart("myfile", new File("resource/temp/anger2.png")));

		NewClient client = NewClient.create();
		Response response = client.prepareRequest(builder.build()).execute().get();
		Debug.line(response.getTextBody(), response.getStatus());
		//
		// assertEquals("hello.txt", json.asString("myfile"));
		// assertEquals("value", json.asString("name"));
	}

	public void testParse() throws Exception {
		RequestBuilder builder = new RequestBuilder().setUrl("http://localhost:9200/file/parse").setMethod(HttpMethod.POST);
		builder.addBodyPart(new StringPart("name", "value", "UTF-8"))
			.addBodyPart(new FilePart("myfile", new File("resource/temp/anger2.png")))
			.addBodyPart(new FilePart("myfile", new File("resource/ptest.prop"), "plain/txt", "UTF-8"));

		NewClient client = NewClient.create();
		Response response = client.prepareRequest(builder.build()).execute().get();
		Debug.line(response.getTextBody(), response.getStatus());
	}

	
	public void testParseInfo() throws Exception {
		RequestBuilder builder = new RequestBuilder().setUrl("http://localhost:9200/file/pinfo").setMethod(HttpMethod.POST);
		builder.addBodyPart(new StringPart("name", "value", "UTF-8"))
			.addBodyPart(new FilePart("myfile", new File("resource/favicon.ico"), "image/jpeg", "UTF-8"))
			.addBodyPart(new FilePart("myfile", new File("resource/한글.prop"), "plain/txt", "UTF-8")) ;

		NewClient client = NewClient.create();
		Response response = client.prepareRequest(builder.build()).execute().get();
		Debug.line(response.getTextBody(), response.getStatus());
	}

	
	public void testHandler() throws Exception {
		RequestBuilder builder = new RequestBuilder().setUrl("http://localhost:9200/file/handle").setMethod(HttpMethod.POST);
		builder
//			.addBodyPart(new StringPart("name", "value", "UTF-8"))
			.addBodyPart(new FilePart("myfile", new File("resource/temp/anger2.png")))
//			.addBodyPart(new FilePart("myfile", new File("resource/한글.prop"), "plain/txt", "UTF-8"))
			;

		NewClient client = NewClient.create();
		Response response = client.prepareRequest(builder.build()).execute().get();
		assertEquals(200, response.getStatus().getCode());
	}
	
	
}

@Path("/file")
class UploadFileService {

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String uploadFile(@MultipartForm FileUploadForm uform, @Context HttpRequest req) throws IOException {

		Debug.line(uform.name, IOUtil.toByteArrayWithClose(uform.input).length);

		return "";
	}

	@POST
	@Path("/input")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String inputFile(@Context HttpRequest req, InputStream input) throws IOException {

		HttpHeaders headers = req.getHttpHeaders();
		for (String hname : headers.getRequestHeaders().keySet()) {
			System.out.println(hname + ":" + headers.getRequestHeaders().getFirst(hname));
		}
		System.out.println();
		System.out.print(IOUtil.toByteArrayWithClose(input).length);
		return "";
	}

	@POST
	@Path("/parse")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String parseEntity(@Context HttpRequest request, MultipartFormDataInput  input) throws IOException {
// http://www.mkyong.com/webservices/jax-rs/file-upload-example-in-resteasy/
		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		for (Entry<String, List<InputPart>> entry : uploadForm.entrySet()) {
			Debug.line(entry.getKey(), entry.getValue());
			for (InputPart part : entry.getValue()) {
				InputStream inputStream = part.getBody(InputStream.class,null);
				Debug.debug(part.getMediaType(),  IOUtil.toByteArrayWithClose(inputStream).length) ;
			}
		}

		return "";
	}

	@POST
	@Path("pinfo")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String parseEntityInfo(@Context HttpRequest request, MultipartFormDataInput  input) throws IOException {
// http://www.mkyong.com/webservices/jax-rs/file-upload-example-in-resteasy/
		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		for (Entry<String, List<InputPart>> entry : uploadForm.entrySet()) {
			for (InputPart part : entry.getValue()) {
				
				InputBody ib = InputBody.create(entry.getKey(), part) ;
				Debug.line(ib.name(), ib.isFilePart(), ib.mediaType(), ib.charset(), ib.transferEncoding(), ib.filename(), ib.asStream());
			}
		}

		return "";
	}

	@POST
	@Path("handle")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String parseEntityHandler(@Context HttpRequest request, MultipartFormDataInput  input) throws IOException {
		input.dataHandle(new FormDataHandler<Void>() {
			@Override
			public Void handle(InputBody ib) throws IOException {
//				if (ib.mediaType().toString().startsWith("image/png")) Debug.line(IOUtil.toByteArrayWithClose(ib.asStream()).length);
				
				Debug.line(ib.name(), ib.isFilePart(), ib.mediaType(), ib.charset(), ib.transferEncoding(), ib.filename(), IOUtil.toByteArrayWithClose(ib.asStream()).length);
				return null;
			}
		}) ;
		return "";
	}
}

class FileUploadForm implements Serializable {
	private static final long serialVersionUID = -2975137795423083295L;

	public FileUploadForm() {
		super();
	}

	@FormParam("myfile")
	@PartType("image/png")
	InputStream input;

	@FormParam("name")
	String name;
	
	public void setInput(InputStream input){
		this.input = input ;
	}
}
