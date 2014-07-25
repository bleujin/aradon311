package net.ion.nradon.restlet.util;

import java.util.Date;

public final class ImmutableDate extends Date {

	private static final long serialVersionUID = -5946186780670229206L;

	/**
	 * Private constructor. A factory method is provided.
	 * 
	 * @param date
	 *            date to be made immutable
	 */
	public ImmutableDate(Date date) {
		super(date.getTime());
	}

	/** {@inheritDoc} */
	@Override
	public Object clone() {
		throw new UnsupportedOperationException("ImmutableDate is immutable");
	}

	/**
	 * As an ImmutableDate is immutable, this method throws an UnsupportedOperationException exception.
	 */
	@Override
	public void setDate(int arg0) {
		throw new UnsupportedOperationException("ImmutableDate is immutable");
	}

	/**
	 * As an ImmutableDate is immutable, this method throws an UnsupportedOperationException exception.
	 */
	@Override
	public void setHours(int arg0) {
		throw new UnsupportedOperationException("ImmutableDate is immutable");
	}

	/**
	 * As an ImmutableDate is immutable, this method throws an UnsupportedOperationException exception.
	 */
	@Override
	public void setMinutes(int arg0) {
		throw new UnsupportedOperationException("ImmutableDate is immutable");
	}

	/**
	 * As an ImmutableDate is immutable, this method throws an UnsupportedOperationException exception.
	 */
	@Override
	public void setMonth(int arg0) {
		throw new UnsupportedOperationException("ImmutableDate is immutable");
	}

	/**
	 * As an ImmutableDate is immutable, this method throws an UnsupportedOperationException exception.
	 */
	@Override
	public void setSeconds(int arg0) {
		throw new UnsupportedOperationException("ImmutableDate is immutable");
	}

	/**
	 * As an ImmutableDate is immutable, this method throws an UnsupportedOperationException exception.
	 */
	@Override
	public void setTime(long arg0) {
		throw new UnsupportedOperationException("ImmutableDate is immutable");
	}

	/**
	 * As an ImmutableDate is immutable, this method throws an UnsupportedOperationException exception.
	 */
	@Override
	public void setYear(int arg0) {
		throw new UnsupportedOperationException("ImmutableDate is immutable");
	}

}