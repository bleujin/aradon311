package net.ion.nradon.restlet.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import net.ion.nradon.restlet.data.CharacterSet;

public class WriterOutputStream extends OutputStream {

    /** The character set to use when parsing byte arrays. */
    private final Charset charSet;

    /** The wrapped writer. */
    private final Writer writer;

    /**
     * Constructor.
     * 
     * @param writer
     *            The wrapped writer.
     * @param characterSet
     *            The character set. Use {@link CharacterSet#ISO_8859_1} by
     *            default if a null value is given.
     */
    public WriterOutputStream(Writer writer, CharacterSet characterSet) {
        this.writer = writer;
        this.charSet = (characterSet == null) ? Charset.forName("ISO-8859-1")
                : characterSet.toCharset();
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.writer.close();
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        this.writer.flush();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        CharBuffer charBuffer = this.charSet.decode(ByteBuffer
                .wrap(b, off, len));
        this.writer.write(charBuffer.toString());
    }

    @Override
    public void write(int b) throws IOException {
        this.writer.write(b);
    }
}