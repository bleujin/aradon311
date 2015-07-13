package net.ion.nradon.restlet.representation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import net.ion.framework.util.IOUtil;
import net.ion.nradon.restlet.MediaType;
import net.ion.nradon.restlet.util.IoUtils;

public class InputStreamRepresentation extends Representation {

	private InputStream input;

	public InputStreamRepresentation(MediaType mediaType, InputStream input) {
		super(mediaType);
		this.input = input ;
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

	@Override
	public InputStream getStream() throws IOException {
		return input;
	}

	@Override
	public void write(OutputStream output) throws IOException {
		IOUtil.copy(input, output);
		IOUtil.close(input);
	}

}
