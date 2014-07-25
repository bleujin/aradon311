package net.ion.nradon.restlet;

import java.io.IOException;
import java.nio.charset.Charset;

public class Parameter implements Comparable<Parameter>, NamedValue<String> {

	/** The first object. */
	private volatile String name;

	/** The second object. */
	private volatile String value;

	public static Parameter create(CharSequence name, CharSequence value) {
		if (value != null) {
			return new Parameter(name.toString(), value.toString());
		} else {
			return new Parameter(name.toString(), null);
		}
	}

	public Parameter() {
		this(null, null);
	}

	public Parameter(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public int compareTo(Parameter o) {
		return getName().compareTo(o.getName());
	}


	public void encode(Appendable buffer, Charset characterSet) throws IOException {
		if (getName() != null) {
			buffer.append(Reference.encode(getName(), characterSet));

			if (getValue() != null) {
				buffer.append('=');
				buffer.append(Reference.encode(getValue(), characterSet));
			}
		}
	}

	public String encode(Charset characterSet) throws IOException {
		StringBuilder sb = new StringBuilder();
		encode(sb, characterSet);
		return sb.toString();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		// if obj == this no need to go further
		boolean result = (obj == this);

		if (!result) {
			result = obj instanceof Parameter;

			// if obj isn't a parameter or is null don't evaluate further
			if (result) {
				Parameter that = (Parameter) obj;
				result = (((that.getName() == null) && (getName() == null)) || ((getName() != null) && getName().equals(that.getName())));

				// if names are both null or equal continue
				if (result) {
					result = (((that.getValue() == null) && (getValue() == null)) || ((getValue() != null) && getValue().equals(that.getValue())));
				}
			}
		}

		return result;
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return SysUtils.hashCode(getName(), getValue());
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "[" + getName() + "=" + getValue() + "]";
	}

}
