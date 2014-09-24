package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.ws.rs.core.MediaType;

import net.ion.framework.util.IOUtil;
import net.ion.framework.util.StringUtil;

import org.jboss.resteasy.util.HttpHeaderNames;

public class InputBody {

	private InputPart part;
	private String name;
	private InputBody(String name, InputPart part) {
		this.name = name ;
		this.part = part ;
	}

	public static InputBody create(String name, InputPart part) {
		return new InputBody(name, part) ;
	}

	public String name(){
		return name ;
	}
	
	public MediaType mediaType() {
		return part.getMediaType() ;
	}
	
	public InputStream asStream() throws IOException{
		return part.getBody(InputStream.class,null);
	}
	
	public String asString() throws IOException {
		return part.getBodyAsString() ;
	}

	public Charset charset(){
		String strVal = firstHeader(HttpHeaderNames.CONTENT_TYPE);
		if (StringUtil.isBlank(strVal)) return Charset.defaultCharset() ;
		return Charset.forName(StringUtil.substringAfter(strVal, "charset=")) ;
	}

	private String firstHeader(String name) {
		String val = part.getHeaders().getFirst(name) ;
		if (val == null) return "" ;
		return val ;
	}
	
	public String transferEncoding(){
		return firstHeader("Content-Transfer-Encoding") ;
	}
	
	public String filename(){
		String val = firstHeader("Content-Disposition") ;
		
		if (StringUtil.isBlank(val)) return "" ;
		String result = StringUtil.substringAfterLast(val, "filename=") ;
		return result.startsWith("\"") ? StringUtil.substringBetween(result, "\"", "\"") : result ;
	}

	public boolean isFilePart() {
		return StringUtil.isNotBlank(filename());
	}

	public void close()  {
		try {
			IOUtil.closeQuietly(asStream()) ;
		} catch (IOException ignore) {
		}
	}
	
}
