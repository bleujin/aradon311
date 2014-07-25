package net.ion.nradon.restlet;

import java.io.IOException;
import java.util.logging.Level;

import net.ion.nradon.restlet.data.CharacterSet;
import net.ion.nradon.restlet.data.Parameter;
import net.ion.nradon.restlet.representation.Representation;
import net.ion.radon.core.TreeContext;

public class FormUtils {

	public static void parse(Form form, Representation post, boolean decode) {
		if (post != null)
			if (post.isAvailable()) {
				FormReader fr = null;
				try {
					fr = new FormReader(post, decode);
				} catch (IOException ioe) {
					TreeContext.getCurrentLogger().log(Level.WARNING, "Unable to create a form reader. Parsing aborted.", ioe);
				}
				if (fr != null)
					fr.addParameters(form);
			} else {
				TreeContext.getCurrentLogger().log(Level.FINE, "The form wasn't changed as the given representation isn't available.");
			}
	}

	public static void parse(Form form, String parametersString, CharacterSet characterSet, boolean decode, char separator) {
		if ((parametersString != null) && !parametersString.equals("")) {
			FormReader fr = null;
			fr = new FormReader(parametersString, characterSet, separator, decode);
			fr.addParameters(form);
		}
	}

	public static Parameter create(CharSequence name, CharSequence value, boolean decode, CharacterSet characterSet) {
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
