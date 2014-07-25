package net.ion.nradon.restlet;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Form which is a specialized modifiable list of parameters.
 * 
 * @see <a href="http://wiki.restlet.org/docs_2.2/58-restlet.html">User Guide - Getting parameter values</a>
 * @author Jerome Louvel
 */
public class Form extends Series<Parameter> {

	public Form() {
		super(Parameter.class);
	}

	public Form(int initialCapacity) {
		super(Parameter.class, initialCapacity);
	}

	public Form(List<Parameter> delegate) {
		super(Parameter.class, delegate);
	}

	public Form(String queryString) {
		this(queryString, true);
	}

	public Form(String queryString, boolean decode) {
		this(queryString, SysUtils.UTF8_CHARSET, decode);
	}

	public Form(String parametersString, char separator) {
		this(parametersString, separator, true);
	}

	public Form(String parametersString, char separator, boolean decode) {
		this(parametersString, SysUtils.UTF8_CHARSET, separator, decode);
	}

	public Form(String queryString, Charset characterSet) {
		this(queryString, characterSet, true);
	}

	public Form(String queryString, Charset characterSet, boolean decode) {
		this(queryString, characterSet, '&', decode);
	}

	public Form(String parametersString, Charset characterSet, char separator) {
		this(parametersString, characterSet, separator, true);
	}

	public Form(String parametersString, Charset characterSet, char separator, boolean decode) {
		this();
		FormUtils.parse(this, parametersString, characterSet, decode, separator);
	}

	@Override
	public Parameter createEntry(String name, String value) {
		return new Parameter(name, value);
	}

	public String encode() throws IOException {
		return encode(SysUtils.UTF8_CHARSET);
	}


	public String encode(Charset characterSet) throws IOException {
		return encode(characterSet, '&');
	}

	public String encode(Charset characterSet, char separator) throws IOException {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < size(); i++) {
			if (i > 0) {
				sb.append(separator);
			}

			get(i).encode(sb, characterSet);
		}

		return sb.toString();
	}

	public String getMatrixString() {
		return getMatrixString(SysUtils.UTF8_CHARSET);
	}

	public String getMatrixString(Charset characterSet) {
		try {
			return encode(characterSet, ';');
		} catch (IOException ioe) {
			return null;
		}
	}

	public String getQueryString() {
		return getQueryString(SysUtils.UTF8_CHARSET);
	}

	public String getQueryString(Charset characterSet) {
		try {
			return encode(characterSet);
		} catch (IOException ioe) {
			return null;
		}
	}


}
