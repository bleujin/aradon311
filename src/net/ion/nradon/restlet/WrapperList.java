package net.ion.nradon.restlet;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

/**
 * List wrapper. Modifiable list that delegates all methods to a wrapped list. This allows an easy sub-classing. By default, it wraps a thread-safe {@link Vector} instance.

 */
public class WrapperList<E> implements List<E>, Iterable<E> {
	private final List<E> delegate;

	public WrapperList() {
		this(10);
	}

	public WrapperList(int initialCapacity) {
		this(new Vector<E>(initialCapacity));
	}

	public WrapperList(List<E> delegate) {
		this.delegate = delegate;
	}

	public boolean add(E element) {
		return getDelegate().add(element);
	}

	public void add(int index, E element) {
		getDelegate().add(index, element);
	}

	public boolean addAll(Collection<? extends E> elements) {
		return getDelegate().addAll(elements);
	}

	public boolean addAll(int index, Collection<? extends E> elements) {
		return getDelegate().addAll(index, elements);
	}

	public void clear() {
		getDelegate().clear();
	}

	public boolean contains(Object element) {
		return getDelegate().contains(element);
	}

	public boolean containsAll(Collection<?> elements) {
		return getDelegate().containsAll(elements);
	}

	@Override
	public boolean equals(Object o) {
		return getDelegate().equals(o);
	}

	public E get(int index) {
		return getDelegate().get(index);
	}

	protected List<E> getDelegate() {
		return this.delegate;
	}

	@Override
	public int hashCode() {
		return getDelegate().hashCode();
	}

	public int indexOf(Object element) {
		return getDelegate().indexOf(element);
	}

	public boolean isEmpty() {
		return getDelegate().isEmpty();
	}

	public Iterator<E> iterator() {
		return getDelegate().iterator();
	}

	public int lastIndexOf(Object element) {
		return getDelegate().lastIndexOf(element);
	}

	public ListIterator<E> listIterator() {
		return getDelegate().listIterator();
	}

	public ListIterator<E> listIterator(int index) {
		return getDelegate().listIterator(index);
	}

	public E remove(int index) {
		return getDelegate().remove(index);
	}

	public boolean remove(Object element) {
		return getDelegate().remove(element);
	}

	public boolean removeAll(Collection<?> elements) {
		return getDelegate().removeAll(elements);
	}

	public boolean retainAll(Collection<?> elements) {
		return getDelegate().retainAll(elements);
	}


	public E set(int index, E element) {
		return getDelegate().set(index, element);
	}

	public int size() {
		return getDelegate().size();
	}


	public List<E> subList(int fromIndex, int toIndex) {
		// [ifndef gwt] instruction
		return new WrapperList<E>(getDelegate().subList(fromIndex, toIndex));
		// [ifdef gwt] instruction uncomment
		// return org.restlet.engine.util.ListUtils.copySubList(this, fromIndex,
		// toIndex);
	}

	public Object[] toArray() {
		return getDelegate().toArray();
	}


	public <T> T[] toArray(T[] a) {
		return getDelegate().toArray(a);
	}


	@Override
	public String toString() {
		return getDelegate().toString();
	}
}
