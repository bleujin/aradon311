package org.jboss.resteasy.plugins.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import net.ion.framework.util.ObjectUtil;

import org.jboss.resteasy.util.HttpHeaderNames;

public class UncertainOutputProvider implements MessageBodyWriter<UncertainOutput> {

	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return UncertainOutput.class.isAssignableFrom(type);
	}

	public long getSize(UncertainOutput uoutput, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	public void writeTo(UncertainOutput uoutput, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
		httpHeaders.putSingle(HttpHeaderNames.CONTENT_TYPE, ObjectUtil.toString(uoutput.getMediaType(), "*/*"));
		
		uoutput.write(entityStream);
	}
	
}
