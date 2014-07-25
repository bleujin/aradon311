package net.ion.nradon.restlet;

public abstract class Metadata {

	public Metadata(String name) {
		this(name, null);
	}

	public Metadata(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public boolean equals(Object object) {
		return (object instanceof Metadata) && ((Metadata) object).getName().equals(getName());
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public abstract Metadata getParent();

	public int hashCode() {
		return getName() != null ? getName().hashCode() : 0;
	}

	public abstract boolean includes(Metadata metadata);

	public boolean isCompatible(Metadata otherMetadata) {
		return otherMetadata != null && (includes(otherMetadata) || otherMetadata.includes(this));
	}

	public String toString() {
		return getName();
	}

	private final String description;
	private final String name;
}