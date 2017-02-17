package net.ion.framework.util;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

public class StringInputStream extends ByteArrayInputStream {

	public StringInputStream(String s) throws UnsupportedEncodingException {
		super(s.getBytes("UTF-8"));
		string = s;
	}

	public String getString() {
		return string;
	}

	private final String string;
}