package net.ion.nradon.restlet;

import net.ion.nradon.restlet.data.NamedValue;

public class Header implements NamedValue<String> {

	private volatile String name;

	private volatile String value;

	public Header() {
	}

	public Header(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		// if obj == this no need to go further
		boolean result = (obj == this);

		if (!result) {
			result = obj instanceof Header;

			// if obj isn't a header or is null don't evaluate further
			if (result) {
				Header that = (Header) obj;
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
		return name;
	}

	public String getValue() {
		return value;
	}

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
		return "[" + getName() + ": " + getValue() + "]";
	}

}
