package net.ion.nradon.restlet.representation;

import java.util.Date;

import net.ion.nradon.restlet.MediaType;
import net.ion.nradon.restlet.Tag;
import net.ion.nradon.restlet.Variant;
import net.ion.nradon.restlet.util.ImmutableDate;

public class RepresentationInfo extends Variant {

	public RepresentationInfo() {
		this(null);
	}

	public RepresentationInfo(MediaType mediaType) {
		this(mediaType, null, null);
	}

	public RepresentationInfo(MediaType mediaType, Date modificationDate) {
		this(mediaType, modificationDate, null);
	}

	public RepresentationInfo(MediaType mediaType, Date modificationDate, Tag tag) {
		super(mediaType);
		this.modificationDate = modificationDate;
		this.tag = tag;
	}

	public RepresentationInfo(MediaType mediaType, Tag tag) {
		this(mediaType, null, tag);
	}

	public RepresentationInfo(Variant variant, Date modificationDate) {
		this(variant, modificationDate, null);
	}

	public RepresentationInfo(Variant variant, Date modificationDate, Tag tag) {
		setCharacterSet(variant.getCharacterSet());
		setEncodings(variant.getEncodings());
		setLocationRef(variant.getLocationRef());
		setLanguages(variant.getLanguages());
		setMediaType(variant.getMediaType());
		setModificationDate(modificationDate);
		setTag(tag);
	}

	public RepresentationInfo(Variant variant, Tag tag) {
		this(variant, null, tag);
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public Tag getTag() {
		return tag;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = unmodifiable(modificationDate);
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public static Date unmodifiable(Date date) {
		return (date == null) ? null : new ImmutableDate(date);
	}

	private volatile Date modificationDate;
	private volatile Tag tag;
}

