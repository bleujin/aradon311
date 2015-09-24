package org.jboss.resteasy.plugins.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

public class HttpStreamOutputProvider implements MessageBodyWriter<HttpStreamOutput> {

	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return HttpStreamOutput.class.isAssignableFrom(type);
	}

	public long getSize(HttpStreamOutput hsoutput, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	public void writeTo(HttpStreamOutput hsoutput, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
		hsoutput.header(httpHeaders);
		hsoutput.write(entityStream);
	}
	
}
