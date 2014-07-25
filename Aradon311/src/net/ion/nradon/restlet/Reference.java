package net.ion.nradon.restlet;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import net.ion.nradon.restlet.data.CharacterSet;
import net.ion.nradon.restlet.data.Parameter;
import net.ion.radon.core.TreeContext;

public class Reference {

	private static CharacterSet UTF8_CHARSET = CharacterSet.UTF_8 ;
	
	public static String decode(String toDecode) {
		return decode(toDecode, UTF8_CHARSET);
	}

	public static String decode(String toDecode, CharacterSet characterSet) {
		String result = null;
		try {
			result = characterSet != null ? URLDecoder.decode(toDecode, characterSet.getName()) : toDecode;
		} catch (UnsupportedEncodingException uee) {
			TreeContext.getCurrentLogger().log(Level.WARNING, "Unable to decode the string with the UTF-8 character set.", uee);
		}
		return result;
	}

	public static String encode(String toEncode) {
		return encode(toEncode, true, UTF8_CHARSET);
	}

	public static String encode(String toEncode, boolean queryString) {
		return encode(toEncode, queryString, UTF8_CHARSET);
	}

	public static String encode(String toEncode, boolean queryString, CharacterSet characterSet) {
		String result = null;
		try {
			result = characterSet != null ? URLEncoder.encode(toEncode, characterSet.getName()) : toEncode;
		} catch (UnsupportedEncodingException uee) {
			TreeContext.getCurrentLogger().log(Level.WARNING, "Unable to encode the string with the UTF-8 character set.", uee);
		}
		if (queryString)
			result = result.replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
		return result;
	}

	public static String encode(String toEncode, CharacterSet characterSet) {
		return encode(toEncode, true, characterSet);
	}

	private static boolean isAlpha(int character) {
		return isUpperCase(character) || isLowerCase(character);
	}

	private static boolean isDigit(int character) {
		return character >= 48 && character <= 57;
	}

	public static boolean isGenericDelimiter(int character) {
		return character == 58 || character == 47 || character == 63 || character == 35 || character == 91 || character == 93 || character == 64;
	}

	private static boolean isLowerCase(int character) {
		return character >= 97 && character <= 122;
	}

	public static boolean isReserved(int character) {
		return isGenericDelimiter(character) || isSubDelimiter(character);
	}

	public static boolean isSubDelimiter(int character) {
		return character == 33 || character == 36 || character == 38 || character == 39 || character == 40 || character == 41 || character == 42 || character == 43 || character == 44 || character == 59 || character == 61;
	}

	public static boolean isUnreserved(int character) {
		return isAlpha(character) || isDigit(character) || character == 45 || character == 46 || character == 95 || character == 126;
	}

	private static boolean isUpperCase(int character) {
		return character >= 65 && character <= 90;
	}

	public static boolean isValid(int character) {
		return character >= 0 && character < 127 && charValidityMap[character];
	}

	public static String toString(String scheme, String hostName, Integer hostPort, String path, String query, String fragment) {
		String host = hostName;
		if (hostPort != null) {
			int defaultPort = Protocol.valueOf(scheme).getDefaultPort();
			if (hostPort.intValue() != defaultPort)
				host = (new StringBuilder()).append(hostName).append(':').append(hostPort).toString();
		}
		return toString(scheme, host, path, query, fragment);
	}

	public static String toString(String relativePart, String query, String fragment) {
		StringBuilder sb = new StringBuilder();
		if (relativePart != null)
			sb.append(relativePart);
		if (query != null)
			sb.append('?').append(query);
		if (fragment != null)
			sb.append('#').append(fragment);
		return sb.toString();
	}

	public static String toString(String scheme, String host, String path, String query, String fragment) {
		StringBuilder sb = new StringBuilder();
		if (scheme != null)
			sb.append(scheme.toLowerCase()).append("://").append(host);
		if (path != null)
			sb.append(path);
		if (query != null)
			sb.append('?').append(query);
		if (fragment != null)
			sb.append('#').append(fragment);
		return sb.toString();
	}

	public Reference() {
		this((Reference) null, (String) null);
	}

	public Reference(URI uri) {
		this(uri.toString());
	}

	public Reference(URL url) {
		this(url.toString());
	}

	public Reference(Protocol protocol, String hostName) {
		this(protocol, hostName, protocol.getDefaultPort());
	}

	public Reference(Protocol protocol, String hostName, int hostPort) {
		this(protocol.getSchemeName(), hostName, hostPort, null, null, null);
	}

	public Reference(Reference ref) {
		this(ref.baseRef, ref.internalRef);
	}

	public Reference(Reference baseRef, Reference uriReference) {
		this(baseRef, uriReference.toString());
	}

	public Reference(Reference baseRef, String uriRef) {
		uriRef = encodeInvalidCharacters(uriRef);
		this.baseRef = baseRef;
		internalRef = uriRef;
		updateIndexes();
	}

	public Reference(Reference baseRef, String relativePart, String query, String fragment) {
		this(baseRef, toString(relativePart, query, fragment));
	}

	public Reference(String uriReference) {
		this((Reference) null, uriReference);
	}

	public Reference(String identifier, String fragment) {
		this(fragment != null ? (new StringBuilder()).append(identifier).append('#').append(fragment).toString() : identifier);
	}

	public Reference(String scheme, String hostName, int hostPort, String path, String query, String fragment) {
		this(toString(scheme, hostName, Integer.valueOf(hostPort), path, query, fragment));
	}

	public Reference addQueryParameter(Parameter parameter) {
		return addQueryParameter(parameter.getName(), parameter.getValue());
	}

	public Reference addQueryParameter(String name, String value) {
		String query = getQuery();
		if (query == null) {
			if (value == null)
				setQuery(encode(name));
			else
				setQuery((new StringBuilder()).append(encode(name)).append('=').append(encode(value)).toString());
		} else if (value == null)
			setQuery((new StringBuilder()).append(query).append('&').append(encode(name)).toString());
		else
			setQuery((new StringBuilder()).append(query).append('&').append(encode(name)).append('=').append(encode(value)).toString());
		return this;
	}

	public Reference addQueryParameters(Iterable parameters) {
		Parameter param;
		for (Iterator i$ = parameters.iterator(); i$.hasNext(); addQueryParameter(param))
			param = (Parameter) i$.next();

		return this;
	}

	public Reference addSegment(String value) {
		String path = getPath();
		if (value != null)
			if (path == null)
				setPath((new StringBuilder()).append("/").append(value).toString());
			else if (path.endsWith("/"))
				setPath((new StringBuilder()).append(path).append(encode(value)).toString());
			else
				setPath((new StringBuilder()).append(path).append("/").append(encode(value)).toString());
		return this;
	}

	public Reference clone() {
		Reference newRef = new Reference();
		if (baseRef == null)
			newRef.baseRef = null;
		else if (equals(baseRef))
			newRef.baseRef = newRef;
		else
			newRef.baseRef = baseRef.clone();
		newRef.fragmentIndex = fragmentIndex;
		newRef.internalRef = internalRef;
		newRef.queryIndex = queryIndex;
		newRef.schemeIndex = schemeIndex;
		return newRef;
	}

	private String encodeInvalidCharacters(String uriRef) throws IllegalArgumentException {
		String result = uriRef;
		if (uriRef != null) {
			boolean valid = true;
			for (int i = 0; valid && i < uriRef.length(); i++) {
				if (!isValid(uriRef.charAt(i))) {
					valid = false;
					TreeContext.getCurrentLogger().fine((new StringBuilder()).append("Invalid character detected in URI reference at index '").append(i).append("': \"").append(uriRef.charAt(i)).append("\". It will be automatically encoded.").toString());
					continue;
				}
				if (uriRef.charAt(i) == '%' && i > uriRef.length() - 2) {
					valid = false;
					TreeContext.getCurrentLogger().fine((new StringBuilder()).append("Invalid percent encoding detected in URI reference at index '").append(i).append("': \"").append(uriRef.charAt(i)).append("\". It will be automatically encoded.").toString());
				}
			}

			if (!valid) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < uriRef.length(); i++)
					if (isValid(uriRef.charAt(i))) {
						if (uriRef.charAt(i) == '%' && i > uriRef.length() - 2)
							sb.append("%25");
						else
							sb.append(uriRef.charAt(i));
					} else {
						sb.append(encode(String.valueOf(uriRef.charAt(i))));
					}

				result = sb.toString();
			}
		}
		return result;
	}

	public boolean equals(Object object) {
		if (object instanceof Reference) {
			Reference ref = (Reference) object;
			if (internalRef == null)
				return ref.internalRef == null;
			else
				return internalRef.equals(ref.internalRef);
		} else {
			return false;
		}
	}

	public String getAuthority() {
		String part = isRelative() ? getRelativePart() : getSchemeSpecificPart();
		if (part != null && part.startsWith("//")) {
			int index = part.indexOf('/', 2);
			if (index != -1)
				return part.substring(2, index);
			index = part.indexOf('?');
			if (index != -1)
				return part.substring(2, index);
			else
				return part.substring(2);
		} else {
			return null;
		}
	}

	public String getAuthority(boolean decode) {
		return decode ? decode(getAuthority()) : getAuthority();
	}

	public Reference getBaseRef() {
		return baseRef;
	}

	public String getExtensions() {
		String result = null;
		String lastSegment = getLastSegment();
		if (lastSegment != null) {
			int extensionIndex = lastSegment.indexOf('.');
			int matrixIndex = lastSegment.indexOf(';');
			if (extensionIndex != -1)
				if (matrixIndex != -1)
					result = lastSegment.substring(extensionIndex + 1, matrixIndex);
				else
					result = lastSegment.substring(extensionIndex + 1);
		}
		return result;
	}

	public String[] getExtensionsAsArray() {
		String result[] = null;
		String extensions = getExtensions();
		if (extensions != null)
			result = extensions.split("\\.");
		return result;
	}

	public String getFragment() {
		if (hasFragment())
			return internalRef.substring(fragmentIndex + 1);
		else
			return null;
	}

	public String getFragment(boolean decode) {
		return decode ? decode(getFragment()) : getFragment();
	}

	public String getHierarchicalPart() {
		if (hasScheme()) {
			if (hasQuery())
				return internalRef.substring(schemeIndex + 1, queryIndex);
			if (hasFragment())
				return internalRef.substring(schemeIndex + 1, fragmentIndex);
			else
				return internalRef.substring(schemeIndex + 1);
		}
		if (hasQuery())
			return internalRef.substring(0, queryIndex);
		if (hasFragment())
			return internalRef.substring(0, fragmentIndex);
		else
			return internalRef;
	}

	public String getHierarchicalPart(boolean decode) {
		return decode ? decode(getHierarchicalPart()) : getHierarchicalPart();
	}

	public String getHostDomain() {
		String result = null;
		String authority = getAuthority();
		if (authority != null) {
			int index1 = authority.indexOf('@');
			int index2 = authority.indexOf(':', index1 != -1 ? index1 : 0);
			if (index1 != -1) {
				if (index2 != -1)
					result = authority.substring(index1 + 1, index2);
				else
					result = authority.substring(index1 + 1);
			} else if (index2 != -1)
				result = authority.substring(0, index2);
			else
				result = authority;
		}
		return result;
	}

	public String getHostDomain(boolean decode) {
		return decode ? decode(getHostDomain()) : getHostDomain();
	}

	public String getHostIdentifier() {
		StringBuilder result = new StringBuilder();
		result.append(getScheme()).append("://").append(getAuthority());
		return result.toString();
	}

	public String getHostIdentifier(boolean decode) {
		return decode ? decode(getHostIdentifier()) : getHostIdentifier();
	}

	public int getHostPort() {
		int result = -1;
		String authority = getAuthority();
		if (authority != null) {
			int index1 = authority.indexOf('@');
			int index = authority.indexOf(':', index1 != -1 ? index1 : 0);
			if (index != -1)
				try {
					result = Integer.parseInt(authority.substring(index + 1));
				} catch (NumberFormatException nfe) {
					TreeContext.getCurrentLogger().log(Level.WARNING, (new StringBuilder()).append("Can't parse hostPort : [hostRef,requestUri]=[").append(getBaseRef()).append(",").append(internalRef).append("]").toString());
				}
		}
		return result;
	}

	public String getIdentifier() {
		if (hasFragment())
			return internalRef.substring(0, fragmentIndex);
		else
			return internalRef;
	}

	public String getIdentifier(boolean decode) {
		return decode ? decode(getIdentifier()) : getIdentifier();
	}

	public String getLastSegment() {
		String result = null;
		String path = getPath();
		if (path != null) {
			if (path.endsWith("/"))
				path = path.substring(0, path.length() - 1);
			int lastSlash = path.lastIndexOf('/');
			if (lastSlash != -1)
				result = path.substring(lastSlash + 1);
		}
		return result;
	}

	public String getLastSegment(boolean decode) {
		return getLastSegment(decode, false);
	}

	public String getLastSegment(boolean decode, boolean excludeMatrix) {
		String result = getLastSegment();
		if (excludeMatrix && result != null) {
			int matrixIndex = result.indexOf(';');
			if (matrixIndex != -1)
				result = result.substring(0, matrixIndex);
		}
		return decode ? decode(result) : result;
	}

	public String getMatrix() {
		String lastSegment = getLastSegment();
		if (lastSegment != null) {
			int matrixIndex = lastSegment.indexOf(';');
			if (matrixIndex != -1)
				return lastSegment.substring(matrixIndex + 1);
		}
		return null;
	}

	public String getMatrix(boolean decode) {
		return decode ? decode(getMatrix()) : getMatrix();
	}

	public Form getMatrixAsForm() {
		return new Form(getMatrix(), ';');
	}

	public Form getMatrixAsForm(CharacterSet characterSet) {
		return new Form(getMatrix(), characterSet, ';');
	}

	public Reference getParentRef() {
		Reference result = null;
		if (isHierarchical()) {
			String parentRef = null;
			String path = getPath();
			if (!path.equals("/") && !path.equals("")) {
				if (path.endsWith("/"))
					path = path.substring(0, path.length() - 1);
				parentRef = (new StringBuilder()).append(getHostIdentifier()).append(path.substring(0, path.lastIndexOf('/') + 1)).toString();
			} else {
				parentRef = internalRef;
			}
			result = new Reference(parentRef);
		}
		return result;
	}

	public String getPath() {
		String result = null;
		String part = isRelative() ? getRelativePart() : getSchemeSpecificPart();
		if (part != null)
			if (part.startsWith("//")) {
				int index1 = part.indexOf('/', 2);
				if (index1 != -1) {
					int index2 = part.indexOf('?');
					if (index2 != -1)
						result = part.substring(index1, index2);
					else
						result = part.substring(index1);
				}
			} else {
				int index = part.indexOf('?');
				if (index != -1)
					result = part.substring(0, index);
				else
					result = part;
			}
		return result;
	}

	public String getPath(boolean decode) {
		return decode ? decode(getPath()) : getPath();
	}

	public String getQuery() {
		if (hasQuery()) {
			if (hasFragment()) {
				if (queryIndex < fragmentIndex)
					return internalRef.substring(queryIndex + 1, fragmentIndex);
				else
					return null;
			} else {
				return internalRef.substring(queryIndex + 1);
			}
		} else {
			return null;
		}
	}

	public String getQuery(boolean decode) {
		return decode ? decode(getQuery()) : getQuery();
	}

	public Form getQueryAsForm() {
		return new Form(getQuery());
	}

	public Form getQueryAsForm(boolean decode) {
		return new Form(getQuery(), decode);
	}

	public Form getQueryAsForm(CharacterSet characterSet) {
		return new Form(getQuery(), characterSet);
	}

	public String getRelativePart() {
		return isRelative() ? toString(false, false) : null;
	}

	public String getRelativePart(boolean decode) {
		return decode ? decode(getRelativePart()) : getRelativePart();
	}

	public Reference getRelativeRef() {
		return getRelativeRef(getBaseRef());
	}

	public Reference getRelativeRef(Reference base) {
		Reference result = null;
		if (base == null) {
			result = this;
		} else {
			if (!isAbsolute() || !isHierarchical())
				throw new IllegalArgumentException("The reference must have an absolute hierarchical path component");
			if (!base.isAbsolute() || !base.isHierarchical())
				throw new IllegalArgumentException("The base reference must have an absolute hierarchical path component");
			if (!getHostIdentifier().equals(base.getHostIdentifier())) {
				result = this;
			} else {
				String localPath = getPath();
				String basePath = base.getPath();
				String relativePath = null;
				if (basePath == null || localPath == null) {
					relativePath = localPath;
				} else {
					boolean diffFound = false;
					int lastSlashIndex = -1;
					int i;
					for (i = 0; !diffFound && i < localPath.length() && i < basePath.length();) {
						char current = localPath.charAt(i);
						if (current != basePath.charAt(i)) {
							diffFound = true;
						} else {
							if (current == '/')
								lastSlashIndex = i;
							i++;
						}
					}

					if (!diffFound) {
						if (localPath.length() == basePath.length())
							relativePath = ".";
						else if (i == localPath.length()) {
							if (basePath.charAt(i) == '/') {
								if (i + 1 == basePath.length()) {
									relativePath = ".";
								} else {
									StringBuilder sb = new StringBuilder();
									int segments = 0;
									for (int j = basePath.indexOf('/', i); j != -1; j = basePath.indexOf('/', j + 1))
										segments++;

									for (int j = 0; j < segments; j++)
										sb.append("../");

									int lastLocalSlash = localPath.lastIndexOf('/');
									sb.append(localPath.substring(lastLocalSlash + 1));
									relativePath = sb.toString();
								}
							} else {
								StringBuilder sb = new StringBuilder();
								int segments = 0;
								for (int j = basePath.indexOf('/', i); j != -1; j = basePath.indexOf('/', j + 1))
									segments++;

								for (int j = 0; j < segments; j++)
									if (j > 0)
										sb.append("/..");
									else
										sb.append("..");

								relativePath = sb.toString();
								if (relativePath.equals(""))
									relativePath = ".";
							}
						} else if (i == basePath.length())
							if (localPath.charAt(i) == '/') {
								if (i + 1 == localPath.length())
									relativePath = ".";
								else
									relativePath = localPath.substring(i + 1);
							} else if (lastSlashIndex == i - 1)
								relativePath = localPath.substring(i);
							else
								relativePath = (new StringBuilder()).append("..").append(localPath.substring(lastSlashIndex)).toString();
					} else {
						StringBuilder sb = new StringBuilder();
						int segments = 0;
						for (int j = basePath.indexOf('/', i); j != -1; j = basePath.indexOf('/', j + 1))
							segments++;

						for (int j = 0; j < segments; j++)
							sb.append("../");

						sb.append(localPath.substring(lastSlashIndex + 1));
						relativePath = sb.toString();
					}
				}
				result = new Reference();
				String query = getQuery();
				String fragment = getFragment();
				boolean modified = false;
				if (query != null && !query.equals(base.getQuery())) {
					result.setQuery(query);
					modified = true;
				}
				if (fragment != null && !fragment.equals(base.getFragment())) {
					result.setFragment(fragment);
					modified = true;
				}
				if (!modified || !relativePath.equals("."))
					result.setPath(relativePath);
			}
		}
		return result;
	}

	public String getRemainingPart() {
		return getRemainingPart(false, true);
	}

	public String getRemainingPart(boolean decode) {
		return getRemainingPart(true, true);
	}

	public String getRemainingPart(boolean decode, boolean query) {
		String result = null;
		String all = toString(query, false);
		if (getBaseRef() != null) {
			String base = getBaseRef().toString(query, false);
			if (base != null && all.startsWith(base))
				result = all.substring(base.length());
		} else {
			result = all;
		}
		return decode ? decode(result) : result;
	}

	public String getScheme() {
		if (hasScheme())
			return internalRef.substring(0, schemeIndex);
		else
			return null;
	}

	public String getScheme(boolean decode) {
		return decode ? decode(getScheme()) : getScheme();
	}

	public Protocol getSchemeProtocol() {
		return Protocol.valueOf(getScheme());
	}

	public String getSchemeSpecificPart() {
		String result = null;
		if (hasScheme())
			if (hasFragment())
				result = internalRef.substring(schemeIndex + 1, fragmentIndex);
			else
				result = internalRef.substring(schemeIndex + 1);
		return result;
	}

	public String getSchemeSpecificPart(boolean decode) {
		return decode ? decode(getSchemeSpecificPart()) : getSchemeSpecificPart();
	}

	public List getSegments() {
		List result = new ArrayList();
		String path = getPath();
		int start = -2;
		if (path != null) {
			for (int i = 0; i < path.length(); i++) {
				char current = path.charAt(i);
				if (current == '/') {
					if (start == -2) {
						start = i;
					} else {
						result.add(path.substring(start + 1, i));
						start = i;
					}
					continue;
				}
				if (start == -2)
					start = -1;
			}

			if (start != -2)
				result.add(path.substring(start + 1));
		}
		return result;
	}

	public List getSegments(boolean decode) {
		List result = getSegments();
		if (decode) {
			for (int i = 0; i < result.size(); i++)
				result.set(i, decode((String) result.get(i)));

		}
		return result;
	}

	public Reference getTargetRef() {
		Reference result = null;
		if (isRelative() && baseRef != null) {
			Reference baseReference = null;
			if (baseRef.isAbsolute())
				baseReference = baseRef;
			else
				baseReference = baseRef.getTargetRef();
			if (baseReference.isRelative())
				throw new IllegalArgumentException("The base reference must have an absolute hierarchical path component");
			String authority = getAuthority();
			String path = getPath();
			String query = getQuery();
			String fragment = getFragment();
			result = new Reference();
			result.setScheme(baseReference.getScheme());
			if (authority != null) {
				result.setAuthority(authority);
				result.setPath(path);
				result.setQuery(query);
			} else {
				result.setAuthority(baseReference.getAuthority());
				if (path == null || path.equals("")) {
					result.setPath(baseReference.getPath());
					if (query != null)
						result.setQuery(query);
					else
						result.setQuery(baseReference.getQuery());
				} else {
					if (path.startsWith("/")) {
						result.setPath(path);
					} else {
						String basePath = baseReference.getPath();
						String mergedPath = null;
						if (baseReference.getAuthority() != null && (basePath == null || basePath.equals(""))) {
							mergedPath = (new StringBuilder()).append("/").append(path).toString();
						} else {
							int lastSlash = basePath.lastIndexOf('/');
							if (lastSlash == -1)
								mergedPath = path;
							else
								mergedPath = (new StringBuilder()).append(basePath.substring(0, lastSlash + 1)).append(path).toString();
						}
						result.setPath(mergedPath);
					}
					result.setQuery(query);
				}
			}
			result.setFragment(fragment);
		} else {
			if (isRelative())
				throw new IllegalArgumentException("Relative references are only usable when a base reference is set.");
			result = new Reference(internalRef);
		}
		result.normalize();
		return result;
	}

	public String getUserInfo() {
		String result = null;
		String authority = getAuthority();
		if (authority != null) {
			int index = authority.indexOf('@');
			if (index != -1)
				result = authority.substring(0, index);
		}
		return result;
	}

	public String getUserInfo(boolean decode) {
		return decode ? decode(getUserInfo()) : getUserInfo();
	}

	public boolean hasExtensions() {
		boolean result = false;
		String path = getPath();
		if (path == null || !path.endsWith("/")) {
			String lastSegment = getLastSegment();
			if (lastSegment != null) {
				int extensionsIndex = lastSegment.indexOf('.');
				int matrixIndex = lastSegment.indexOf(';');
				result = extensionsIndex != -1 && (matrixIndex == -1 || extensionsIndex < matrixIndex);
			}
		}
		return result;
	}

	public boolean hasFragment() {
		return fragmentIndex != -1;
	}

	public int hashCode() {
		return internalRef != null ? internalRef.hashCode() : 0;
	}

	public boolean hasMatrix() {
		return getLastSegment().indexOf(';') != -1;
	}

	public boolean hasQuery() {
		return queryIndex != -1;
	}

	public boolean hasScheme() {
		return schemeIndex != -1;
	}

	public boolean isAbsolute() {
		return getScheme() != null;
	}

	public boolean isEquivalentTo(Reference ref) {
		return getTargetRef().equals(ref.getTargetRef());
	}

	public boolean isHierarchical() {
		return isRelative() || getSchemeSpecificPart().charAt(0) == '/';
	}

	public boolean isOpaque() {
		return isAbsolute() && getSchemeSpecificPart().charAt(0) != '/';
	}

	public boolean isParent(Reference childRef) {
		boolean result = false;
		if (childRef != null && childRef.isHierarchical())
			result = childRef.toString(false, false).startsWith(toString(false, false));
		return result;
	}

	public boolean isRelative() {
		return getScheme() == null;
	}

	public Reference normalize() {
		StringBuilder output = new StringBuilder();
		StringBuilder input = new StringBuilder();
		String path = getPath();
		if (path != null)
			input.append(path);
		while (input.length() > 0)
			if (input.length() >= 3 && input.substring(0, 3).equals("../"))
				input.delete(0, 3);
			else if (input.length() >= 2 && input.substring(0, 2).equals("./"))
				input.delete(0, 2);
			else if (input.length() >= 3 && input.substring(0, 3).equals("/./"))
				input.delete(0, 2);
			else if (input.length() == 2 && input.substring(0, 2).equals("/."))
				input.delete(1, 2);
			else if (input.length() >= 4 && input.substring(0, 4).equals("/../")) {
				input.delete(0, 3);
				removeLastSegment(output);
			} else if (input.length() == 3 && input.substring(0, 3).equals("/..")) {
				input.delete(1, 3);
				removeLastSegment(output);
			} else if (input.length() == 1 && input.substring(0, 1).equals("."))
				input.delete(0, 1);
			else if (input.length() == 2 && input.substring(0, 2).equals("..")) {
				input.delete(0, 2);
			} else {
				int max = -1;
				for (int i = 1; max == -1 && i < input.length(); i++)
					if (input.charAt(i) == '/')
						max = i;

				if (max != -1) {
					output.append(input.substring(0, max));
					input.delete(0, max);
				} else {
					output.append(input);
					input.delete(0, input.length());
				}
			}
		setPath(output.toString());
		setScheme(getScheme());
		setHostDomain(getHostDomain());
		int hostPort = getHostPort();
		if (hostPort != -1) {
			int defaultPort = Protocol.valueOf(getScheme()).getDefaultPort();
			if (hostPort == defaultPort)
				setHostPort(null);
		}
		return this;
	}

	private void removeLastSegment(StringBuilder output) {
		int min = -1;
		for (int i = output.length() - 1; min == -1 && i >= 0; i--)
			if (output.charAt(i) == '/')
				min = i;

		if (min != -1)
			output.delete(min, output.length());
		else
			output.delete(0, output.length());
	}

	public void setAuthority(String authority) {
		String oldPart = isRelative() ? getRelativePart() : getSchemeSpecificPart();
		String newAuthority = authority != null ? (new StringBuilder()).append("//").append(authority).toString() : "";
		String newPart;
		if (oldPart == null)
			newPart = newAuthority;
		else if (oldPart.startsWith("//")) {
			int index = oldPart.indexOf('/', 2);
			if (index != -1) {
				newPart = (new StringBuilder()).append(newAuthority).append(oldPart.substring(index)).toString();
			} else {
				index = oldPart.indexOf('?');
				if (index != -1)
					newPart = (new StringBuilder()).append(newAuthority).append(oldPart.substring(index)).toString();
				else
					newPart = newAuthority;
			}
		} else {
			newPart = (new StringBuilder()).append(newAuthority).append(oldPart).toString();
		}
		if (isAbsolute())
			setSchemeSpecificPart(newPart);
		else
			setRelativePart(newPart);
	}

	public void setBaseRef(Reference baseRef) {
		this.baseRef = baseRef;
	}

	public void setBaseRef(String baseUri) {
		setBaseRef(new Reference(baseUri));
	}

	public void setExtensions(String extensions) {
		String lastSegment = getLastSegment();
		if (lastSegment != null) {
			int extensionIndex = lastSegment.indexOf('.');
			int matrixIndex = lastSegment.indexOf(';');
			StringBuilder sb = new StringBuilder();
			if (extensionIndex != -1) {
				sb.append(lastSegment.substring(0, extensionIndex));
				if (extensions != null && extensions.length() > 0)
					sb.append('.').append(extensions);
				if (matrixIndex != -1)
					sb.append(lastSegment.substring(matrixIndex));
			} else if (extensions != null && extensions.length() > 0) {
				if (matrixIndex != -1)
					sb.append(lastSegment.substring(0, matrixIndex)).append('.').append(extensions).append(lastSegment.substring(matrixIndex));
				else
					sb.append(lastSegment).append('.').append(extensions);
			} else {
				sb.append(lastSegment);
			}
			setLastSegment(sb.toString());
		} else {
			setLastSegment((new StringBuilder()).append('.').append(extensions).toString());
		}
	}

	public void setExtensions(String extensions[]) {
		String exts = null;
		if (extensions != null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < extensions.length; i++) {
				if (i > 0)
					sb.append('.');
				sb.append(extensions[i]);
			}

			exts = sb.toString();
		}
		setExtensions(exts);
	}

	public void setFragment(String fragment) {
		fragment = encodeInvalidCharacters(fragment);
		if (fragment != null && fragment.indexOf('#') != -1)
			throw new IllegalArgumentException("Illegal '#' character detected in parameter");
		if (hasFragment()) {
			if (fragment != null)
				internalRef = (new StringBuilder()).append(internalRef.substring(0, fragmentIndex + 1)).append(fragment).toString();
			else
				internalRef = internalRef.substring(0, fragmentIndex);
		} else if (fragment != null)
			if (internalRef != null)
				internalRef = (new StringBuilder()).append(internalRef).append('#').append(fragment).toString();
			else
				internalRef = (new StringBuilder()).append('#').append(fragment).toString();
		updateIndexes();
	}

	public void setHostDomain(String domain) {
		String authority = getAuthority();
		if (authority == null) {
			setAuthority(domain);
		} else {
			if (domain == null)
				domain = "";
			else
				domain = domain.toLowerCase();
			int index1 = authority.indexOf('@');
			int index2 = authority.indexOf(':', index1 != -1 ? index1 : 0);
			if (index1 != -1) {
				if (index2 != -1)
					setAuthority((new StringBuilder()).append(authority.substring(0, index1 + 1)).append(domain).append(authority.substring(index2)).toString());
				else
					setAuthority((new StringBuilder()).append(authority.substring(0, index1 + 1)).append(domain).toString());
			} else if (index2 != -1)
				setAuthority((new StringBuilder()).append(domain).append(authority.substring(index2)).toString());
			else
				setAuthority(domain);
		}
	}

	public void setHostPort(Integer port) {
		String authority = getAuthority();
		if (authority != null) {
			int index1 = authority.indexOf('@');
			int index = authority.indexOf(':', index1 != -1 ? index1 : 0);
			String newPort = port != null ? (new StringBuilder()).append(":").append(port).toString() : "";
			if (index != -1)
				setAuthority((new StringBuilder()).append(authority.substring(0, index)).append(newPort).toString());
			else
				setAuthority((new StringBuilder()).append(authority).append(newPort).toString());
		} else {
			throw new IllegalArgumentException("No authority defined, please define a host name first");
		}
	}

	public void setIdentifier(String identifier) {
		identifier = encodeInvalidCharacters(identifier);
		if (identifier == null)
			identifier = "";
		if (identifier.indexOf('#') != -1)
			throw new IllegalArgumentException("Illegal '#' character detected in parameter");
		if (hasFragment())
			internalRef = (new StringBuilder()).append(identifier).append(internalRef.substring(fragmentIndex)).toString();
		else
			internalRef = identifier;
		updateIndexes();
	}

	public void setLastSegment(String lastSegment) {
		String path = getPath();
		int lastSlashIndex = -1;
		if (path != null)
			lastSlashIndex = path.lastIndexOf('/');
		if (lastSlashIndex != -1)
			setPath((new StringBuilder()).append(path.substring(0, lastSlashIndex + 1)).append(lastSegment).toString());
		else
			setPath((new StringBuilder()).append('/').append(lastSegment).toString());
	}

	public void setPath(String path) {
		String oldPart = isRelative() ? getRelativePart() : getSchemeSpecificPart();
		String newPart = null;
		if (oldPart != null) {
			if (path == null)
				path = "";
			if (oldPart.startsWith("//")) {
				int index1 = oldPart.indexOf('/', 2);
				if (index1 != -1) {
					int index2 = oldPart.indexOf('?');
					if (index2 != -1)
						newPart = (new StringBuilder()).append(oldPart.substring(0, index1)).append(path).append(oldPart.substring(index2)).toString();
					else
						newPart = (new StringBuilder()).append(oldPart.substring(0, index1)).append(path).toString();
				} else {
					int index2 = oldPart.indexOf('?');
					if (index2 != -1)
						newPart = (new StringBuilder()).append(oldPart.substring(0, index2)).append(path).append(oldPart.substring(index2)).toString();
					else
						newPart = (new StringBuilder()).append(oldPart).append(path).toString();
				}
			} else {
				int index = oldPart.indexOf('?');
				if (index != -1)
					newPart = (new StringBuilder()).append(path).append(oldPart.substring(index)).toString();
				else
					newPart = path;
			}
		} else {
			newPart = path;
		}
		if (isAbsolute())
			setSchemeSpecificPart(newPart);
		else
			setRelativePart(newPart);
	}

	public void setProtocol(Protocol protocol) {
		setScheme(protocol.getSchemeName());
	}

	public void setQuery(String query) {
		query = encodeInvalidCharacters(query);
		boolean emptyQueryString = query == null || query.length() <= 0;
		if (hasQuery()) {
			if (hasFragment()) {
				if (!emptyQueryString)
					internalRef = (new StringBuilder()).append(internalRef.substring(0, queryIndex + 1)).append(query).append(internalRef.substring(fragmentIndex)).toString();
				else
					internalRef = (new StringBuilder()).append(internalRef.substring(0, queryIndex)).append(internalRef.substring(fragmentIndex)).toString();
			} else if (!emptyQueryString)
				internalRef = (new StringBuilder()).append(internalRef.substring(0, queryIndex + 1)).append(query).toString();
			else
				internalRef = internalRef.substring(0, queryIndex);
		} else if (hasFragment()) {
			if (!emptyQueryString)
				internalRef = (new StringBuilder()).append(internalRef.substring(0, fragmentIndex)).append('?').append(query).append(internalRef.substring(fragmentIndex)).toString();
		} else if (!emptyQueryString)
			if (internalRef != null)
				internalRef = (new StringBuilder()).append(internalRef).append('?').append(query).toString();
			else
				internalRef = (new StringBuilder()).append('?').append(query).toString();
		updateIndexes();
	}

	public void setRelativePart(String relativePart) {
		relativePart = encodeInvalidCharacters(relativePart);
		if (relativePart == null)
			relativePart = "";
		if (!hasScheme())
			if (hasQuery())
				internalRef = (new StringBuilder()).append(relativePart).append(internalRef.substring(queryIndex)).toString();
			else if (hasFragment())
				internalRef = (new StringBuilder()).append(relativePart).append(internalRef.substring(fragmentIndex)).toString();
			else
				internalRef = relativePart;
		updateIndexes();
	}

	public void setScheme(String scheme) {
		scheme = encodeInvalidCharacters(scheme);
		if (scheme != null)
			scheme = scheme.toLowerCase();
		if (hasScheme()) {
			if (scheme != null)
				internalRef = (new StringBuilder()).append(scheme).append(internalRef.substring(schemeIndex)).toString();
			else
				internalRef = internalRef.substring(schemeIndex + 1);
		} else if (scheme != null)
			if (internalRef == null)
				internalRef = (new StringBuilder()).append(scheme).append(':').toString();
			else
				internalRef = (new StringBuilder()).append(scheme).append(':').append(internalRef).toString();
		updateIndexes();
	}

	public void setSchemeSpecificPart(String schemeSpecificPart) {
		schemeSpecificPart = encodeInvalidCharacters(schemeSpecificPart);
		if (schemeSpecificPart == null)
			schemeSpecificPart = "";
		if (hasScheme()) {
			if (hasFragment())
				internalRef = (new StringBuilder()).append(internalRef.substring(0, schemeIndex + 1)).append(schemeSpecificPart).append(internalRef.substring(fragmentIndex)).toString();
			else
				internalRef = (new StringBuilder()).append(internalRef.substring(0, schemeIndex + 1)).append(schemeSpecificPart).toString();
		} else if (hasFragment())
			internalRef = (new StringBuilder()).append(schemeSpecificPart).append(internalRef.substring(fragmentIndex)).toString();
		else
			internalRef = schemeSpecificPart;
		updateIndexes();
	}

	public void setSegments(List segments) {
		StringBuilder sb = new StringBuilder();
		String segment;
		for (Iterator i$ = segments.iterator(); i$.hasNext(); sb.append('/').append(segment))
			segment = (String) i$.next();

		setPath(sb.toString());
	}

	public void setUserInfo(String userInfo) {
		String authority = getAuthority();
		if (authority != null) {
			int index = authority.indexOf('@');
			String newUserInfo = userInfo != null ? (new StringBuilder()).append(userInfo).append('@').toString() : "";
			if (index != -1)
				setAuthority((new StringBuilder()).append(newUserInfo).append(authority.substring(index + 1)).toString());
			else
				setAuthority((new StringBuilder()).append(newUserInfo).append(authority).toString());
		} else {
			throw new IllegalArgumentException("No authority defined, please define a host name first");
		}
	}

	public String toString() {
		return internalRef;
	}

	public String toString(boolean query, boolean fragment) {
		if (query) {
			if (fragment)
				return internalRef;
			if (hasFragment())
				return internalRef.substring(0, fragmentIndex);
			else
				return internalRef;
		}
		if (fragment)
			if (hasQuery()) {
				if (hasFragment())
					return (new StringBuilder()).append(internalRef.substring(0, queryIndex)).append("#").append(getFragment()).toString();
				else
					return internalRef.substring(0, queryIndex);
			} else {
				return internalRef;
			}
		if (hasQuery())
			return internalRef.substring(0, queryIndex);
		if (hasFragment())
			return internalRef.substring(0, fragmentIndex);
		else
			return internalRef;
	}

	public URI toUri() {
		return URI.create(getTargetRef().toString());
	}

	public URL toUrl() {
		URL result = null;
		try {
			result = new URL(getTargetRef().toString());
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed URL exception", e);
		}
		return result;
	}

	private void updateIndexes() {
		if (internalRef != null) {
			int firstSlashIndex = internalRef.indexOf('/');
			schemeIndex = internalRef.indexOf(':');
			if (firstSlashIndex != -1 && schemeIndex > firstSlashIndex)
				schemeIndex = -1;
			queryIndex = internalRef.indexOf('?');
			fragmentIndex = internalRef.indexOf('#');
			if (hasQuery() && hasFragment() && queryIndex > fragmentIndex)
				queryIndex = -1;
			if (hasQuery() && schemeIndex > queryIndex)
				schemeIndex = -1;
			if (hasFragment() && schemeIndex > fragmentIndex)
				schemeIndex = -1;
		} else {
			schemeIndex = -1;
			queryIndex = -1;
			fragmentIndex = -1;
		}
	}

	private static final boolean charValidityMap[];
	private volatile Reference baseRef;
	private volatile int fragmentIndex;
	private volatile String internalRef;
	private volatile int queryIndex;
	private volatile int schemeIndex;

	static {
		charValidityMap = new boolean[127];
		for (int character = 0; character < 127; character++)
			charValidityMap[character] = isReserved(character) || isUnreserved(character) || character == 37 || character == 123 || character == 125;

	}
}
