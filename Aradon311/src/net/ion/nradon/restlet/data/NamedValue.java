package net.ion.nradon.restlet.data;

public interface NamedValue<V> {

	public abstract String getName();

	public abstract V getValue();

	public abstract void setValue(V value);

}