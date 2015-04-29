package net.ion.nradon.restlet.representation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import net.ion.nradon.restlet.MediaType;
import net.ion.nradon.restlet.data.CharacterSet;

public abstract class WriterRepresentation extends CharacterRepresentation {

	public WriterRepresentation(MediaType mediaType) {
		super(mediaType);
	}

	public WriterRepresentation(MediaType mediaType, long expectedSize) {
		super(mediaType);
		setSize(expectedSize);
	}

	@Override
	public Reader getReader() throws IOException {
		throw new UnsupportedOperationException("not use : call bleujin") ;
	}

	/**
	 * Calls parent's implementation.
	 */
	@Override
	public void release() {
		super.release();
	}

	@Override
	public void write(OutputStream outputStream) throws IOException {
		Writer writer = null;

		if (getCharacterSet() != null) {
			writer = new OutputStreamWriter(outputStream, getCharacterSet().getName());
		} else {
			// Use the default HTTP character set
			writer = new OutputStreamWriter(outputStream, CharacterSet.ISO_8859_1.getName());
		}

		write(writer);
		writer.flush();
	}

}