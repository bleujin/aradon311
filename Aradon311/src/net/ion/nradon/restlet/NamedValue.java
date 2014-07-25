package net.ion.nradon.restlet;

public interface NamedValue<V> {

	public abstract String getName();

	public abstract V getValue();

	public abstract void setValue(V value);

}