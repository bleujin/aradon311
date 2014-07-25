package net.ion.nradon.restlet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Series<T extends NamedValue<String>> extends WrapperList<T> {

	public static final Object EMPTY_VALUE = new Object();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Series<? extends NamedValue> unmodifiableSeries(final Series<? extends NamedValue> series) {
		return new Series(series.entryClass, java.util.Collections.unmodifiableList(series.getDelegate()));
	}

	/** The entry class. */
	private final Class<T> entryClass;

	public Series(Class<T> entryClass) {
		super();
		this.entryClass = entryClass;
	}

	public Series(Class<T> entryClass, int initialCapacity) {
		super(initialCapacity);
		this.entryClass = entryClass;
	}

	public Series(Class<T> entryClass, List<T> delegate) {
		super(delegate);
		this.entryClass = entryClass;
	}

	public boolean add(String name, String value) {
		return add(createEntry(name, value));
	}

	@SuppressWarnings("unchecked")
	public void copyTo(Map<String, Object> params) {
		NamedValue<String> param;
		Object currentValue = null;

		for (Iterator<T> iter = iterator(); iter.hasNext();) {
			param = iter.next();

			if (params.containsKey(param.getName())) {
				currentValue = params.get(param.getName());

				if (currentValue != null) {
					List<Object> values = null;

					if (currentValue instanceof List) {
						// Multiple values already found for this entry
						values = (List<Object>) currentValue;
					} else {
						// Second value found for this entry
						// Create a list of values
						values = new ArrayList<Object>();
						values.add(currentValue);
						params.put(param.getName(), values);
					}

					if (param.getValue() == null) {
						values.add(Series.EMPTY_VALUE);
					} else {
						values.add(param.getValue());
					}
				} else {
					if (param.getValue() == null) {
						params.put(param.getName(), Series.EMPTY_VALUE);
					} else {
						params.put(param.getName(), param.getValue());
					}
				}
			}
		}
	}

	public T createEntry(String name, String value) {
		try {
			return this.entryClass.getConstructor(String.class, String.class).newInstance(name, value);
		} catch (Exception e) {
			return null;
		}
	}

	private boolean equals(String value1, String value2, boolean ignoreCase) {
		boolean result = (value1 == value2);

		if (!result) {
			if ((value1 != null) && (value2 != null)) {
				if (ignoreCase) {
					result = value1.equalsIgnoreCase(value2);
				} else {
					result = value1.equals(value2);
				}
			}
		}

		return result;
	}

	public T getFirst(String name) {
		return getFirst(name, false);
	}

	public T getFirst(String name, boolean ignoreCase) {
		for (T param : this) {
			if (equals(param.getName(), name, ignoreCase)) {
				return param;
			}
		}

		return null;
	}

	public String getFirstValue(String name) {
		return getFirstValue(name, false);
	}

	public String getFirstValue(String name, boolean ignoreCase) {
		return getFirstValue(name, ignoreCase, null);
	}

	public String getFirstValue(String name, boolean ignoreCase, String defaultValue) {
		String result = defaultValue;
		NamedValue<String> param = getFirst(name, ignoreCase);

		if ((param != null) && (param.getValue() != null)) {
			result = param.getValue();
		}

		return result;
	}

	public String getFirstValue(String name, String defaultValue) {
		return getFirstValue(name, false, defaultValue);
	}

	public Set<String> getNames() {
		Set<String> result = new HashSet<String>();

		for (NamedValue<String> param : this) {
			result.add(param.getName());
		}

		return result;
	}

	public String getValues(String name) {
		return getValues(name, ",", true);
	}

	public String getValues(String name, String separator, boolean ignoreCase) {
		String result = null;
		StringBuilder sb = null;

		for (final T param : this) {
			if ((ignoreCase && param.getName().equalsIgnoreCase(name)) || param.getName().equals(name)) {
				if (sb == null) {
					if (result == null) {
						result = param.getValue();
					} else {
						sb = new StringBuilder();
						sb.append(result).append(separator).append(param.getValue());
					}
				} else {
					sb.append(separator).append(param.getValue());
				}
			}
		}

		if (sb != null) {
			result = sb.toString();
		}

		return result;
	}

	public String[] getValuesArray(String name) {
		return getValuesArray(name, false);
	}

	public String[] getValuesArray(String name, boolean ignoreCase) {
		return getValuesArray(name, ignoreCase, null);
	}

	public String[] getValuesArray(String name, boolean ignoreCase, String defaultValue) {
		String[] result = null;
		List<T> params = subList(name, ignoreCase);

		if ((params.size() == 0) && (defaultValue != null)) {
			result = new String[1];
			result[0] = defaultValue;
		} else {
			result = new String[params.size()];

			for (int i = 0; i < params.size(); i++) {
				result[i] = params.get(i).getValue();
			}
		}

		return result;
	}

	public String[] getValuesArray(String name, String defaultValue) {
		return getValuesArray(name, false, defaultValue);
	}

	public Map<String, String> getValuesMap() {
		Map<String, String> result = new LinkedHashMap<String, String>();

		for (NamedValue<String> param : this) {
			if (!result.containsKey(param.getName())) {
				result.put(param.getName(), param.getValue());
			}
		}

		return result;
	}

	public boolean removeAll(String name) {
		return removeAll(name, false);
	}

	public boolean removeAll(String name, boolean ignoreCase) {
		boolean changed = false;
		NamedValue<String> param = null;

		for (Iterator<T> iter = iterator(); iter.hasNext();) {
			param = iter.next();

			if (equals(param.getName(), name, ignoreCase)) {
				iter.remove();
				changed = true;
			}
		}

		return changed;
	}

	public boolean removeFirst(String name) {
		return removeFirst(name, false);
	}

	public boolean removeFirst(String name, boolean ignoreCase) {
		boolean changed = false;
		NamedValue<String> param = null;

		for (final Iterator<T> iter = iterator(); iter.hasNext() && !changed;) {
			param = iter.next();
			if (equals(param.getName(), name, ignoreCase)) {
				iter.remove();
				changed = true;
			}
		}

		return changed;
	}

	public T set(String name, String value) {
		return set(name, value, false);
	}

	public T set(String name, String value, boolean ignoreCase) {
		T result = null;
		T param = null;
		boolean found = false;

		for (final Iterator<T> iter = iterator(); iter.hasNext();) {
			param = iter.next();

			if (equals(param.getName(), name, ignoreCase)) {
				if (found) {
					// Remove other entries with the same name
					iter.remove();
				} else {
					// Change the value of the first matching entry
					found = true;
					param.setValue(value);
					result = param;
				}
			}
		}

		if (!found) {
			add(name, value);
		}

		return result;
	}

	@Override
	public Series<T> subList(int fromIndex, int toIndex) {
		return new Series<T>(this.entryClass, getDelegate().subList(fromIndex, toIndex));
	}

	public Series<T> subList(String name) {
		return subList(name, false);
	}

	public Series<T> subList(String name, boolean ignoreCase) {
		Series<T> result = new Series<T>(this.entryClass);

		for (T param : this) {
			if (equals(param.getName(), name, ignoreCase)) {
				result.add(param);
			}
		}

		return result;
	}

}