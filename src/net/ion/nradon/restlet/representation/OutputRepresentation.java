package net.ion.nradon.restlet.representation;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

import net.ion.nradon.restlet.MediaType;
import net.ion.nradon.restlet.util.IoUtils;


public abstract class OutputRepresentation extends StreamRepresentation {

	private ExecutorService es;
	public OutputRepresentation(ExecutorService es, MediaType mediaType) {
		super(mediaType);
		this.es = es;
	}

	public OutputRepresentation(ExecutorService es, MediaType mediaType, long expectedSize) {
		super(mediaType);
		setSize(expectedSize);
		this.es = es ;
	}


	public InputStream getStream() throws IOException {
		return IoUtils.getStream(es, this);
	}

}
