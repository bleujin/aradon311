package net.ion.nradon.restlet.representation;

import java.io.IOException;
import java.io.Reader;

import net.ion.framework.util.IOUtil;
import net.ion.nradon.restlet.MediaType;
import net.ion.nradon.restlet.util.IoUtils;

public abstract class StreamRepresentation extends Representation {

	/**
	 * Constructor.
	 * 
	 * @param mediaType
	 *            The media type.
	 */
	public StreamRepresentation(MediaType mediaType) {
		super(mediaType);
	}

	@Override
	public java.nio.channels.ReadableByteChannel getChannel() throws IOException {
		return IoUtils.getChannel(getStream());
	}

	@Override
	public Reader getReader() throws IOException {
		return IoUtils.getReader(getStream(), getCharacterSet());
	}

	@Override
	public void write(java.io.Writer writer) throws IOException {
		IOUtil.copy(getReader(), writer) ;
		writer.flush();
	}

}
