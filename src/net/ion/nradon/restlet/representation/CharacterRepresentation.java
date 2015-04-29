package net.ion.nradon.restlet.representation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.ion.nradon.restlet.MediaType;
import net.ion.nradon.restlet.data.CharacterSet;
import net.ion.nradon.restlet.util.IoUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;

/**
 * Representation based on a BIO character stream.
 * 
 * @author Jerome Louvel
 */
public abstract class CharacterRepresentation extends Representation {
	/**
	 * Constructor.
	 * 
	 * @param mediaType
	 *            The media type.
	 */
	public CharacterRepresentation(MediaType mediaType) {
		super(mediaType);
		setCharacterSet(CharacterSet.UTF_8);
	}

	@Override
	public java.nio.channels.ReadableByteChannel getChannel() throws IOException {
		return IoUtils.getChannel(getStream());
	}

	@Override
	public InputStream getStream() throws IOException {
		return new ReaderInputStream(getReader(), getCharacterSet().toCharset()); 
	}

	@Override
	public void write(OutputStream outputStream) throws IOException {
		IOUtils.copy(getStream(), outputStream);
	}


}
