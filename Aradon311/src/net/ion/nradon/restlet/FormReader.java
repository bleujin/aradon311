package net.ion.nradon.restlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.ion.nradon.restlet.data.CharacterSet;
import net.ion.nradon.restlet.data.Parameter;
import net.ion.nradon.restlet.representation.Representation;
import net.ion.radon.core.TreeContext;

public class FormReader {
	private volatile CharacterSet characterSet;

	/** Indicates if the parameters should be decoded. */
	private volatile boolean decode;

	/** The separator character used between parameters. */
	private volatile char separator;

	/** The form stream. */
	private volatile InputStream stream;

	public FormReader(String parametersString, CharacterSet characterSet, char separator) {
		this(parametersString, characterSet, separator, true);
	}

	public FormReader(Representation representation) throws IOException {
		this(representation, true);
	}

	public FormReader(Representation representation, boolean decode) throws IOException {
		this.decode = decode;
		stream = representation.getStream();
		separator = '&';
		if (representation.getCharacterSet() != null)
			characterSet = representation.getCharacterSet();
		else
			characterSet = CharacterSet.UTF_8;
	}

	public FormReader(String parametersString, CharacterSet characterSet, char separator, boolean decode) {
		this.decode = decode;
		this.stream = new ByteArrayInputStream(parametersString.getBytes());
		this.characterSet = characterSet;
		this.separator = separator;
	}

	public void addParameters(Series<Parameter> parameters) {
		boolean readNext = true;
		Parameter param = null;

		if (this.stream != null) {
			// Let's read all form parameters
			try {
				while (readNext) {
					param = readNextParameter();

					if (param != null) {
						// Add parsed parameter to the form
						parameters.add(param);
					} else {
						// Last parameter parsed
						readNext = false;
					}
				}
			} catch (IOException ioe) {
				TreeContext.getCurrentLogger().log(Level.WARNING, "Unable to parse a form parameter. Skipping the remaining parameters.", ioe);
			}

			try {
				this.stream.close();
			} catch (IOException ioe) {
				TreeContext.getCurrentLogger().log(Level.WARNING, "Unable to close the form input stream", ioe);
			}
		}
	}

	public Form read() throws IOException {
		Form result = new Form();

		if (this.stream != null) {
			Parameter param = readNextParameter();

			while (param != null) {
				result.add(param);
				param = readNextParameter();
			}

			this.stream.close();
		}

		return result;
	}

	public Parameter readFirstParameter(String name) throws IOException {
		Parameter result = null;

		if (this.stream != null) {
			Parameter param = readNextParameter();

			while ((param != null) && (result == null)) {
				if (param.getName().equals(name)) {
					result = param;
				}

				param = readNextParameter();
			}

			this.stream.close();
		}

		return result;
	}

	public Parameter readNextParameter() throws IOException {
		Parameter result = null;

		if (this.stream != null) {
			try {
				boolean readingName = true;
				boolean readingValue = false;
				StringBuilder nameBuffer = new StringBuilder();
				StringBuilder valueBuffer = new StringBuilder();
				int nextChar = 0;

				while ((result == null) && (nextChar != -1)) {
					nextChar = this.stream.read();

					if (readingName) {
						if (nextChar == '=') {
							if (nameBuffer.length() > 0) {
								readingName = false;
								readingValue = true;
							} else {
								throw new IOException("Empty parameter name detected. Please check your form data");
							}
						} else if ((nextChar == this.separator) || (nextChar == -1)) {
							if (nameBuffer.length() > 0) {
								result = FormUtils.create(nameBuffer, null, this.decode, this.characterSet);
							} else if (nextChar == -1) {
								// Do nothing return null preference
							} else {
								TreeContext.getCurrentLogger().fine("Empty parameter name detected. Please check your form data");
							}
						} else {
							nameBuffer.append((char) nextChar);
						}
					} else if (readingValue) {
						if ((nextChar == this.separator) || (nextChar == -1)) {
							result = FormUtils.create(nameBuffer, valueBuffer, this.decode, this.characterSet);
						} else {
							valueBuffer.append((char) nextChar);
						}
					}
				}
			} catch (UnsupportedEncodingException uee) {
				throw new IOException("Unsupported encoding. Please contact the administrator");
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public Object readParameter(String name) throws IOException {
		Object result = null;

		if (this.stream != null) {
			Parameter param = readNextParameter();

			while (param != null) {
				if (param.getName().equals(name)) {
					if (result != null) {
						List<Object> values = null;

						if (result instanceof List) {
							// Multiple values already found for this parameter
							values = (List<Object>) result;
						} else {
							// Second value found for this parameter
							// Create a list of values
							values = new ArrayList<Object>();
							values.add(result);
							result = values;
						}

						if (param.getValue() == null) {
							values.add(Series.EMPTY_VALUE);
						} else {
							values.add(param.getValue());
						}
					} else {
						if (param.getValue() == null) {
							result = Series.EMPTY_VALUE;
						} else {
							result = param.getValue();
						}
					}
				}

				param = readNextParameter();
			}

			this.stream.close();
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public void readParameters(Map<String, Object> parameters) throws IOException {
		if (this.stream != null) {
			Parameter param = readNextParameter();
			Object currentValue = null;

			while (param != null) {
				if (parameters.containsKey(param.getName())) {
					currentValue = parameters.get(param.getName());

					if (currentValue != null) {
						List<Object> values = null;

						if (currentValue instanceof List) {
							// Multiple values already found for this parameter
							values = (List<Object>) currentValue;
						} else {
							// Second value found for this parameter
							// Create a list of values
							values = new ArrayList<Object>();
							values.add(currentValue);
							parameters.put(param.getName(), values);
						}

						if (param.getValue() == null) {
							values.add(Series.EMPTY_VALUE);
						} else {
							values.add(param.getValue());
						}
					} else {
						if (param.getValue() == null) {
							parameters.put(param.getName(), Series.EMPTY_VALUE);
						} else {
							parameters.put(param.getName(), param.getValue());
						}
					}
				}

				param = readNextParameter();
			}

			this.stream.close();
		}
	}
}