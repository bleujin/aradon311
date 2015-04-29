package org.jboss.resteasy.plugins.providers;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

public interface UncertainOutput {

	public abstract void write(OutputStream outputstream) throws IOException, WebApplicationException;

	public abstract MediaType getMediaType();
}