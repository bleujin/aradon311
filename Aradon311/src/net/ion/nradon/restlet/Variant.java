package net.ion.nradon.restlet;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.ion.nradon.restlet.data.CharacterSet;

public class Variant {

	/** The character set or null if not applicable. */
	private volatile CharacterSet characterSet;

	/** The additional content codings applied to the entity-body. */
	private volatile List<Encoding> encodings;

	/** The location reference. */
	private volatile Reference locationRef;

	/** The natural language(s) of the intended audience for this variant. */
	private volatile List<Language> languages;

	/** The media type. */
	private volatile MediaType mediaType;

	/**
	 * Default constructor.
	 */
	public Variant() {
		this(null);
	}

	/**
	 * Constructor.
	 * 
	 * @param mediaType
	 *            The media type.
	 */
	public Variant(MediaType mediaType) {
		this(mediaType, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param mediaType
	 *            The media type.
	 * @param language
	 *            The language.
	 */
	public Variant(MediaType mediaType, Language language) {
		this.characterSet = null;
		this.encodings = null;

		if (language != null) {
			getLanguages().add(language);
		} else {
			this.languages = null;
		}

		this.mediaType = mediaType;
		this.locationRef = null;
	}


	/**
	 * Indicates if the current variant is equal to the given variant.
	 * 
	 * @param other
	 *            The other variant.
	 * @return True if the current variant includes the other.
	 */
	@Override
	public boolean equals(Object other) {
		boolean result = (other instanceof Variant);

		if (result && (other != this)) {
			Variant otherVariant = (Variant) other;

			// Compare the character set
			if (result) {
				result = ((getCharacterSet() == null) && (otherVariant.getCharacterSet() == null) || (getCharacterSet() != null) && getCharacterSet().equals(otherVariant.getCharacterSet()));
			}

			// Compare the media type
			if (result) {
				result = ((getMediaType() == null) && (otherVariant.getMediaType() == null) || (getMediaType() != null) && getMediaType().equals(otherVariant.getMediaType()));
			}

			// Compare the languages
			if (result) {
				result = getLanguages().equals(otherVariant.getLanguages());
			}

			// Compare the encodings
			if (result) {
				result = getEncodings().equals(otherVariant.getEncodings());
			}

			// Compare the location URI
			if (result) {
				result = ((getLocationRef() == null) && (otherVariant.getLocationRef() == null) || (getLocationRef() != null) && getLocationRef().equals(otherVariant.getLocationRef()));
			}
		}

		return result;
	}

	/**
	 * Returns the character set or null if not applicable. Note that when used with HTTP connectors, this property maps to the "Content-Type" header.
	 * 
	 * @return The character set or null if not applicable.
	 */
	public CharacterSet getCharacterSet() {
		return this.characterSet;
	}

	/**
	 * Returns the modifiable list of encodings applied to the entity-body. Creates a new instance if no one has been set. An "IllegalArgumentException" exception is thrown when adding a null encoding to this list.<br>
	 * <br>
	 * Note that when used with HTTP connectors, this property maps to the "Content-Encoding" header.
	 * 
	 * @return The list of encodings applied to the entity-body.
	 */
	public List<Encoding> getEncodings() {
		if (this.encodings == null) {
			this.encodings = new WrapperList<Encoding>() {

				@Override
				public boolean add(Encoding element) {
					if (element == null) {
						throw new IllegalArgumentException("Cannot add a null encoding.");
					}

					return super.add(element);
				}

				@Override
				public void add(int index, Encoding element) {
					if (element == null) {
						throw new IllegalArgumentException("Cannot add a null encoding.");
					}

					super.add(index, element);
				}

				@Override
				public boolean addAll(Collection<? extends Encoding> elements) {
					boolean addNull = (elements == null);
					if (!addNull) {
						for (final Iterator<? extends Encoding> iterator = elements.iterator(); !addNull && iterator.hasNext();) {
							addNull = (iterator.next() == null);
						}
					}
					if (addNull) {
						throw new IllegalArgumentException("Cannot add a null encoding.");
					}

					return super.addAll(elements);
				}

				@Override
				public boolean addAll(int index, Collection<? extends Encoding> elements) {
					boolean addNull = (elements == null);
					if (!addNull) {
						for (final Iterator<? extends Encoding> iterator = elements.iterator(); !addNull && iterator.hasNext();) {
							addNull = (iterator.next() == null);
						}
					}
					if (addNull) {
						throw new IllegalArgumentException("Cannot add a null encoding.");
					}

					return super.addAll(index, elements);
				}
			};
		}

		return this.encodings;
	}

	/**
	 * Returns the modifiable list of languages. Creates a new instance if no one has been set. An "IllegalArgumentException" exception is thrown when adding a null language to this list.<br>
	 * <br>
	 * Note that when used with HTTP connectors, this property maps to the "Content-Language" header.
	 * 
	 * @return The list of languages.
	 */
	public List<Language> getLanguages() {
		if (this.languages == null) {
			this.languages = new WrapperList<Language>() {

				@Override
				public void add(int index, Language element) {
					if (element == null) {
						throw new IllegalArgumentException("Cannot add a null language.");
					}

					super.add(index, element);
				}

				@Override
				public boolean add(Language element) {
					if (element == null) {
						throw new IllegalArgumentException("Cannot add a null language.");
					}

					return super.add(element);
				}

				@Override
				public boolean addAll(Collection<? extends Language> elements) {
					boolean addNull = (elements == null);
					if (!addNull) {
						for (final Iterator<? extends Language> iterator = elements.iterator(); !addNull && iterator.hasNext();) {
							addNull = (iterator.next() == null);
						}
					}
					if (addNull) {
						throw new IllegalArgumentException("Cannot add a null language.");
					}

					return super.addAll(elements);
				}

				@Override
				public boolean addAll(int index, Collection<? extends Language> elements) {
					boolean addNull = (elements == null);
					if (!addNull) {
						for (final Iterator<? extends Language> iterator = elements.iterator(); !addNull && iterator.hasNext();) {
							addNull = (iterator.next() == null);
						}
					}
					if (addNull) {
						throw new IllegalArgumentException("Cannot add a null language.");
					}

					return super.addAll(index, elements);
				}

			};
		}
		return this.languages;
	}

	/**
	 * Returns an optional location reference. This is useful when the representation is accessible from a location separate from the representation's resource URI, for example when content negotiation occurs.<br>
	 * <br>
	 * Note that when used with HTTP connectors, this property maps to the "Content-Location" header.
	 * 
	 * @return The identifier.
	 */
	public Reference getLocationRef() {
		return this.locationRef;
	}

	/**
	 * Returns the media type.<br>
	 * <br>
	 * Note that when used with HTTP connectors, this property maps to the "Content-Type" header.
	 * 
	 * @return The media type.
	 */
	public MediaType getMediaType() {
		return this.mediaType;
	}

	/**
	 * Indicates if the current variant includes the given variant.
	 * 
	 * @param other
	 *            The other variant.
	 * @return True if the current variant includes the other.
	 */
	public boolean includes(Variant other) {
		boolean result = other != null;

		// Compare the character set
		if (result) {
			result = (getCharacterSet() == null) || getCharacterSet().includes(other.getCharacterSet());
		}

		// Compare the media type
		if (result) {
			result = (getMediaType() == null) || getMediaType().includes(other.getMediaType());
		}

		// Compare the languages
		if (result) {
			result = (getLanguages().isEmpty()) || getLanguages().contains(Language.ALL) || getLanguages().containsAll(other.getLanguages());
		}

		// Compare the encodings
		if (result) {
			result = (getEncodings().isEmpty()) || getEncodings().contains(Encoding.ALL) || getEncodings().containsAll(other.getEncodings());
		}

		return result;
	}

	/**
	 * Indicates if the current variant is compatible with the given variant.
	 * 
	 * @param other
	 *            The other variant.
	 * @return True if the current variant is compatible with the other.
	 */
	public boolean isCompatible(Variant other) {
		return (other != null) && (includes(other) || other.includes(this));
	}

	/**
	 * Sets the character set or null if not applicable.<br>
	 * <br>
	 * Note that when used with HTTP connectors, this property maps to the "Content-Type" header.
	 * 
	 * @param characterSet
	 *            The character set or null if not applicable.
	 */
	public void setCharacterSet(CharacterSet characterSet) {
		this.characterSet = characterSet;
	}

	/**
	 * Sets the list of encodings applied to the entity-body.<br>
	 * <br>
	 * Note that when used with HTTP connectors, this property maps to the "Content-Encoding" header.
	 * 
	 * @param encodings
	 *            The list of encodings applied to the entity-body.
	 */
	public void setEncodings(List<Encoding> encodings) {
		this.encodings = encodings;
	}

	/**
	 * Sets the list of languages.<br>
	 * <br>
	 * Note that when used with HTTP connectors, this property maps to the "Content-Language" header.
	 * 
	 * @param languages
	 *            The list of languages.
	 */
	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}

	/**
	 * Sets the optional identifier. This is useful when the representation is accessible from a location separate from the representation's resource URI, for example when content negotiation occurs.<br>
	 * <br>
	 * Note that when used with HTTP connectors, this property maps to the "Content-Location" header.
	 * 
	 * @param location
	 *            The location reference.
	 */
	public void setLocationRef(Reference location) {
		this.locationRef = location;
	}

	/**
	 * Sets the identifier from a URI string.<br>
	 * <br>
	 * Note that when used with HTTP connectors, this property maps to the "Content-Location" header.
	 * 
	 * @param locationUri
	 *            The location URI to parse.
	 */
	public void setLocationRef(String locationUri) {
		setLocationRef(new Reference(locationUri));
	}

	/**
	 * Sets the media type.<br>
	 * <br>
	 * Note that when used with HTTP connectors, this property maps to the "Content-Type" header.
	 * 
	 * @param mediaType
	 *            The media type.
	 */
	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		boolean first = true;

		if (getMediaType() != null) {
			first = false;
			sb.append(getMediaType());
		}

		if (getCharacterSet() != null) {
			if (!first) {
				sb.append(",");
			} else {
				first = false;
			}

			sb.append(getCharacterSet());
		}

		if (!getLanguages().isEmpty()) {
			if (!first) {
				sb.append(",");
			} else {
				first = false;
			}

			sb.append(getLanguages());
		}

		if (!getEncodings().isEmpty()) {
			if (!first) {
				sb.append(",");
			} else {
				first = false;
			}

			sb.append(getEncodings());
		}

		sb.append("]");
		return sb.toString();
	}
}
