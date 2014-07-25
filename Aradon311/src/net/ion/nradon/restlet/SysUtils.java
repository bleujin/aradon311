package net.ion.nradon.restlet;

import net.ion.nradon.restlet.data.CharacterSet;

public class SysUtils {

	public static CharacterSet UTF8_CHARSET = CharacterSet.UTF_8 ;

	public static int hashCode(Object... objects) {
		int result = 17;

		if (objects != null) {
			for (final Object obj : objects) {
				result = 31 * result + (obj == null ? 0 : obj.hashCode());
			}
		}

		return result;
	}

	public static byte[] getAsciiBytes(String string) {
		if (string != null) {
			try {
				return string.getBytes("US-ASCII");
			} catch (Exception e) {
				// Should not happen.
				return null;
			}
		}
		return null;
	}

	public static byte[] getLatin1Bytes(String string) {
		if (string != null) {
			try {
				return string.getBytes("ISO-8859-1");
			} catch (Exception e) {
				// Should not happen.
				return null;
			}
		}
		return null;
	}
}
