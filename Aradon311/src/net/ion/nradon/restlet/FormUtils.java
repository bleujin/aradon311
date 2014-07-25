package net.ion.nradon.restlet;

import java.nio.charset.Charset;

public class FormUtils {

	public static void parse(Form form, String parametersString, Charset characterSet, boolean decode, char separator) {
		if ((parametersString != null) && !parametersString.equals("")) {
			FormReader fr = null;
			fr = new FormReader(parametersString, characterSet, separator, decode);
			fr.addParameters(form);
		}
	}

	public static Parameter create(CharSequence name, CharSequence value, boolean decode, Charset characterSet) {
		Parameter result = null;

		if (name != null) {
			String nameStr;
			if (decode) {
				nameStr = Reference.decode(name.toString(), characterSet);
			} else {
				nameStr = name.toString();
			}
			if (value != null) {
				String valueStr;
				if (decode) {
					valueStr = Reference.decode(value.toString(), characterSet);
				} else {
					valueStr = value.toString();
				}
				result = new Parameter(nameStr, valueStr);
			} else {
				result = new Parameter(nameStr, null);
			}
		}
		return result;
	}
}
