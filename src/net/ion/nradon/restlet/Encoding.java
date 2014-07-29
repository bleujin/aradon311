package net.ion.nradon.restlet;

public final class Encoding extends Metadata {

	public static Encoding valueOf(String name) {
		Encoding result = null;
		if (name != null && !name.equals(""))
			if (name.equalsIgnoreCase(ALL.getName()))
				result = ALL;
			else if (name.equalsIgnoreCase(GZIP.getName()))
				result = GZIP;
			else if (name.equalsIgnoreCase(ZIP.getName()))
				result = ZIP;
			else if (name.equalsIgnoreCase(COMPRESS.getName()))
				result = COMPRESS;
			else if (name.equalsIgnoreCase(DEFLATE.getName()))
				result = DEFLATE;
			else if (name.equalsIgnoreCase(IDENTITY.getName()))
				result = IDENTITY;
			else if (name.equalsIgnoreCase(FREEMARKER.getName()))
				result = FREEMARKER;
			else if (name.equalsIgnoreCase(VELOCITY.getName()))
				result = VELOCITY;
			else
				result = new Encoding(name);
		return result;
	}

	public Encoding(String name) {
		this(name, "Encoding applied to a representation");
	}

	public Encoding(String name, String description) {
		super(name, description);
	}

	public boolean equals(Object object) {
		return (object instanceof Encoding) && getName().equalsIgnoreCase(((Encoding) object).getName());
	}

	public Metadata getParent() {
		return equals(ALL) ? null : ALL;
	}

	public int hashCode() {
		return getName() != null ? getName().toLowerCase().hashCode() : 0;
	}

	public boolean includes(Metadata included) {
		return equals(ALL) || included == null || equals(included);
	}

	public static final Encoding ALL = new Encoding("*", "All encodings");
	public static final Encoding COMPRESS = new Encoding("compress", "Common Unix compression");
	public static final Encoding DEFLATE = new Encoding("deflate", "Deflate compression using the zlib format");
	public static final Encoding FREEMARKER = new Encoding("freemarker", "FreeMarker templated representation");
	public static final Encoding GZIP = new Encoding("gzip", "GZip compression");
	public static final Encoding IDENTITY = new Encoding("identity", "The default encoding with no transformation");
	public static final Encoding VELOCITY = new Encoding("velocity", "Velocity templated representation");
	public static final Encoding ZIP = new Encoding("zip", "Zip compression");

}
