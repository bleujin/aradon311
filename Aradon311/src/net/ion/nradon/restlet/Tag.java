package net.ion.nradon.restlet;

import java.util.logging.Level;

import net.ion.radon.core.TreeContext;

public final class Tag {
	/** Tag matching any other tag, used in call's condition data. */
	public static final Tag ALL = Tag.parse("*");

	/**
	 * Parses a tag formatted as defined by the HTTP standard.
	 * 
	 * @param httpTag
	 *            The HTTP tag string; if it starts with 'W/' the tag will be marked as weak and the data following the 'W/' used as the tag; otherwise it should be surrounded with quotes (e.g., "sometag").
	 * @return A new tag instance.
	 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.11">HTTP Entity Tags</a>
	 */
	public static Tag parse(String httpTag) {
		Tag result = null;
		boolean weak = false;
		String httpTagCopy = httpTag;

		if (httpTagCopy.startsWith("W/")) {
			weak = true;
			httpTagCopy = httpTagCopy.substring(2);
		}

		if (httpTagCopy.startsWith("\"") && httpTagCopy.endsWith("\"")) {
			result = new Tag(httpTagCopy.substring(1, httpTagCopy.length() - 1), weak);
		} else if (httpTagCopy.equals("*")) {
			result = new Tag("*", weak);
		} else {
			TreeContext.getCurrentLogger().log(Level.WARNING, "Invalid tag format detected: " + httpTagCopy);
		}

		return result;
	}

	/** The name. */
	private volatile String name;

	/** The tag weakness. */
	private final boolean weak;

	/**
	 * Default constructor. The opaque tag is set to null and the weakness indicator is set to true.
	 */
	public Tag() {
		this(null, true);
	}

	/**
	 * Constructor of weak tags.
	 * 
	 * @param opaqueTag
	 *            The tag value.
	 */
	public Tag(String opaqueTag) {
		this(opaqueTag, true);
	}

	/**
	 * Constructor.
	 * 
	 * @param opaqueTag
	 *            The tag value.
	 * @param weak
	 *            The weakness indicator.
	 */
	public Tag(final String opaqueTag, boolean weak) {
		this.name = opaqueTag;
		this.weak = weak;
	}

	/**
	 * Indicates if both tags are equal.
	 * 
	 * @param object
	 *            The object to compare to.
	 * @return True if both tags are equal.
	 */
	@Override
	public boolean equals(final Object object) {
		return equals(object, true);
	}

	/**
	 * Indicates if both tags are equal.
	 * 
	 * @param object
	 *            The object to compare to.
	 * @param checkWeakness
	 *            The equality test takes care or not of the weakness.
	 * 
	 * @return True if both tags are equal.
	 */
	public boolean equals(final Object object, boolean checkWeakness) {
		boolean result = (object != null) && (object instanceof Tag);

		if (result) {
			final Tag that = (Tag) object;

			if (checkWeakness) {
				result = (that.isWeak() == isWeak());
			}

			if (result) {
				if (getName() == null) {
					result = (that.getName() == null);
				} else {
					result = getName().equals(that.getName());
				}
			}
		}

		return result;
	}

	/**
	 * Returns tag formatted as an HTTP tag string.
	 * 
	 * @return The formatted HTTP tag string.
	 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.11">HTTP Entity Tags</a>
	 */
	public String format() {
		if (getName().equals("*")) {
			return "*";
		}

		final StringBuilder sb = new StringBuilder();
		if (isWeak()) {
			sb.append("W/");
		}
		return sb.append('"').append(getName()).append('"').toString();
	}

	/**
	 * Returns the name, corresponding to an HTTP opaque tag value.
	 * 
	 * @return The name, corresponding to an HTTP opaque tag value.
	 */
	public String getName() {
		return this.name;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return format().hashCode();
	}

	/**
	 * Indicates if the tag is weak.
	 * 
	 * @return True if the tag is weak, false if the tag is strong.
	 */
	public boolean isWeak() {
		return this.weak;
	}

	/**
	 * Returns the name.
	 * 
	 * @return The name.
	 */
	@Override
	public String toString() {
		return getName();
	}
}