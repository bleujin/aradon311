package net.ion.nradon.restlet.representation;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import org.jboss.resteasy.util.HttpHeaderNames;

public class RepresentationProvider implements MessageBodyWriter<Representation> {
	
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return Representation.class.isAssignableFrom(type);
	}

	public long getSize(Representation rep, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return rep.getSize();
	}

	public void writeTo(Representation rep, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
		httpHeaders.putSingle(HttpHeaderNames.CONTENT_TYPE, rep.getMediaType() + (rep.getCharacterSet() != null ? "; charset=" + rep.getCharacterSet() : "") );
		rep.write(entityStream);
	}
	
}