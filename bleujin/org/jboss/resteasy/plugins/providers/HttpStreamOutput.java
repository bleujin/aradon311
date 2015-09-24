package org.jboss.resteasy.plugins.providers;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;

public interface HttpStreamOutput {
	public abstract void write(OutputStream outputstream) throws IOException, WebApplicationException;
	public abstract void header(MultivaluedMap<String, Object> headers);
}
