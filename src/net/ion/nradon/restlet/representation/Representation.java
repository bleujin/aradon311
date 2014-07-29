package net.ion.nradon.restlet.representation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Date;

import net.ion.nradon.restlet.MediaType;
import net.ion.nradon.restlet.Tag;
import net.ion.nradon.restlet.Variant;
import net.ion.nradon.restlet.data.Digest;
import net.ion.nradon.restlet.data.Disposition;
import net.ion.nradon.restlet.data.Range;
import net.ion.nradon.restlet.util.DateUtils;
import net.ion.nradon.restlet.util.IoUtils;

public abstract class Representation extends RepresentationInfo {
	public static final long UNKNOWN_SIZE = -1L;

	private volatile boolean available;

	private volatile Digest digest;

	private volatile Disposition disposition;

	private volatile Date expirationDate;

	private volatile boolean isTransient;

	private volatile Range range;

	private volatile long size;

	public Representation() {
		this(null);
	}

	public Representation(MediaType mediaType) {
		super(mediaType);
		this.available = true;
		this.disposition = null;
		this.isTransient = false;
		this.size = UNKNOWN_SIZE;
		this.expirationDate = null;
		this.digest = null;
		this.range = null;
	}

	public Representation(MediaType mediaType, Date modificationDate) {
		this(mediaType, modificationDate, null);
	}

	public Representation(MediaType mediaType, Date modificationDate, Tag tag) {
		super(mediaType, modificationDate, tag);
	}

	public Representation(MediaType mediaType, Tag tag) {
		this(mediaType, null, tag);
	}

	public Representation(Variant variant, Date modificationDate) {
		this(variant, modificationDate, null);
	}

	public Representation(Variant variant, Date modificationDate, Tag tag) {
		setCharacterSet(variant.getCharacterSet());
		setEncodings(variant.getEncodings());
		setLocationRef(variant.getLocationRef());
		setLanguages(variant.getLanguages());
		setMediaType(variant.getMediaType());
		setModificationDate(modificationDate);
		setTag(tag);
	}

	public Representation(Variant variant, Tag tag) {
		this(variant, null, tag);
	}

	public void append(Appendable appendable) throws IOException {
		appendable.append(getText());
	}

	public long exhaust() throws IOException {
		long result = -1L;

		// [ifndef gwt]
		if (isAvailable()) {
			InputStream is = getStream();
			result = IoUtils.exhaust(is);
			is.close();
		}
		// [enddef]

		return result;
	}

	public long getAvailableSize() {
		return IoUtils.getAvailableSize(this);
	}

	public abstract java.nio.channels.ReadableByteChannel getChannel() throws IOException;

	public Digest getDigest() {
		return this.digest;
	}

	public Disposition getDisposition() {
		return disposition;
	}

	public Date getExpirationDate() {
		return this.expirationDate;
	}

	public Range getRange() {
		return this.range;
	}

	public abstract Reader getReader() throws IOException;

	public long getSize() {
		return this.size;
	}

	public abstract InputStream getStream() throws IOException;

	public String getText() throws IOException {
		String result = null;

		if (isEmpty()) {
			result = "";
		} else if (isAvailable()) {
			java.io.StringWriter sw = new java.io.StringWriter();
			write(sw);
			sw.flush();
			result = sw.toString();
		}

		return result;
	}

	public boolean hasKnownSize() {
		return getSize() >= 0;
	}

	public boolean isAvailable() {
		return this.available && (getSize() != 0);
	}

	public boolean isEmpty() {
		return getSize() == 0;
	}

	public boolean isTransient() {
		return this.isTransient;
	}

	public void release() {
		setAvailable(false);
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public void setDigest(Digest digest) {
		this.digest = digest;
	}

	public void setDisposition(Disposition disposition) {
		this.disposition = disposition;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = DateUtils.unmodifiable(expirationDate);
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public void setSize(long expectedSize) {
		this.size = expectedSize;
	}

	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	public abstract void write(java.io.Writer writer) throws IOException;

	public abstract void write(java.nio.channels.WritableByteChannel writableChannel) throws IOException;

	public abstract void write(OutputStream outputStream) throws IOException;

}