package net.ion.nradon.restlet;

import java.io.IOException;
import java.io.OutputStream;

public class HeaderUtils {
	
	public static final long UNKNOWN_SIZE = -1L;
	
	
	public static long getContentLength(Series<Header> headers) {
		long contentLength = UNKNOWN_SIZE;

		if (headers != null) {
			// Extract the content length header
			for (Header header : headers) {
				if (header.getName().equalsIgnoreCase(HeaderConstant.HEADER_CONTENT_LENGTH)) {
					try {
						contentLength = Long.parseLong(header.getValue());
					} catch (NumberFormatException e) {
						contentLength = UNKNOWN_SIZE;
					}
				}
			}
		}

		return contentLength;
	}

	/**
	 * Indicates if the given character is alphabetical (a-z or A-Z).
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is alphabetical (a-z or A-Z).
	 */
	public static boolean isAlpha(int character) {
		return isUpperCase(character) || isLowerCase(character);
	}

	/**
	 * Indicates if the given character is in ASCII range.
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is in ASCII range.
	 */
	public static boolean isAsciiChar(int character) {
		return (character >= 0) && (character <= 127);
	}

	/**
	 * Indicates if the given character is a carriage return.
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is a carriage return.
	 */
	public static boolean isCarriageReturn(int character) {
		return (character == 13);
	}

	/**
	 * Indicates if the entity is chunked.
	 * 
	 * @return True if the entity is chunked.
	 */
	public static boolean isChunkedEncoding(Series<Header> headers) {
		boolean result = false;

		if (headers != null) {
			final String header = headers.getFirstValue(HeaderConstant.HEADER_TRANSFER_ENCODING, true);
			result = "chunked".equalsIgnoreCase(header);
		}

		return result;
	}

	/**
	 * Indicates if the given character is a comma, the character used as header value separator.
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is a comma.
	 */
	public static boolean isComma(int character) {
		return (character == ',');
	}

	/**
	 * Indicates if the given character is a comment text. It means {@link #isText(int)} returns true and the character is not '(' or ')'.
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is a quoted text.
	 */
	public static boolean isCommentText(int character) {
		return isText(character) && (character != '(') && (character != ')');
	}

	/**
	 * Indicates if the connection must be closed.
	 * 
	 * @param headers
	 *            The headers to test.
	 * @return True if the connection must be closed.
	 */
	public static boolean isConnectionClose(Series<Header> headers) {
		boolean result = false;

		if (headers != null) {
			String header = headers.getFirstValue(HeaderConstant.HEADER_CONNECTION, true);
			result = "close".equalsIgnoreCase(header);
		}

		return result;
	}

	/**
	 * Indicates if the given character is a control character.
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is a control character.
	 */
	public static boolean isControlChar(int character) {
		return ((character >= 0) && (character <= 31)) || (character == 127);
	}

	/**
	 * Indicates if the given character is a digit (0-9).
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is a digit (0-9).
	 */
	public static boolean isDigit(int character) {
		return (character >= '0') && (character <= '9');
	}

	/**
	 * Indicates if the given character is a double quote.
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is a double quote.
	 */
	public static boolean isDoubleQuote(int character) {
		return (character == 34);
	}

	/**
	 * Indicates if the given character is an horizontal tab.
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is an horizontal tab.
	 */
	public static boolean isHorizontalTab(int character) {
		return (character == 9);
	}

	/**
	 * Indicates if the given character is in ISO Latin 1 (8859-1) range. Note that this range is a superset of ASCII and a subrange of Unicode (UTF-8).
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is in ISO Latin 1 range.
	 */
	public static boolean isLatin1Char(int character) {
		return (character >= 0) && (character <= 255);
	}

	/**
	 * Indicates if the given character is a value separator.
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is a value separator.
	 */
	public static boolean isLinearWhiteSpace(int character) {
		return (isCarriageReturn(character) || isSpace(character) || isLineFeed(character) || HeaderUtils.isHorizontalTab(character));
	}

	/**
	 * Indicates if the given character is a line feed.
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is a line feed.
	 */
	public static boolean isLineFeed(int character) {
		return (character == 10);
	}

	/**
	 * Indicates if the given character is lower case (a-z).
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is lower case (a-z).
	 */
	public static boolean isLowerCase(int character) {
		return (character >= 'a') && (character <= 'z');
	}

	/**
	 * Indicates if the given character marks the start of a quoted pair.
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character marks the start of a quoted pair.
	 */
	public static boolean isQuoteCharacter(int character) {
		return (character == '\\');
	}

	/**
	 * Indicates if the given character is a quoted text. It means {@link #isText(int)} returns true and {@link #isDoubleQuote(int)} returns false.
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is a quoted text.
	 */
	public static boolean isQuotedText(int character) {
		return isText(character) && !isDoubleQuote(character);
	}

	/**
	 * Indicates if the given character is a semicolon, the character used as header parameter separator.
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is a semicolon.
	 */
	public static boolean isSemiColon(int character) {
		return (character == ';');
	}

	/**
	 * Indicates if the given character is a separator.
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is a separator.
	 */
	public static boolean isSeparator(int character) {
		switch (character) {
		case '(':
		case ')':
		case '<':
		case '>':
		case '@':
		case ',':
		case ';':
		case ':':
		case '\\':
		case '"':
		case '/':
		case '[':
		case ']':
		case '?':
		case '=':
		case '{':
		case '}':
		case ' ':
		case '\t':
			return true;

		default:
			return false;
		}
	}

	/**
	 * Indicates if the given character is a space.
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is a space.
	 */
	public static boolean isSpace(int character) {
		return (character == 32);
	}

	/**
	 * Indicates if the given character is textual (ISO Latin 1 and not a control character).
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is textual.
	 */
	public static boolean isText(int character) {
		return isLatin1Char(character) && !isControlChar(character);
	}

	/**
	 * Indicates if the token is valid.<br>
	 * Only contains valid token characters.
	 * 
	 * @param token
	 *            The token to check
	 * @return True if the token is valid.
	 */
	public static boolean isToken(CharSequence token) {
		for (int i = 0; i < token.length(); i++) {
			if (!isTokenChar(token.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Indicates if the given character is a token character (text and not a separator).
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is a token character (text and not a separator).
	 */
	public static boolean isTokenChar(int character) {
		return isAsciiChar(character) && !isSeparator(character);
	}

	/**
	 * Indicates if the given character is upper case (A-Z).
	 * 
	 * @param character
	 *            The character to test.
	 * @return True if the given character is upper case (A-Z).
	 */
	public static boolean isUpperCase(int character) {
		return (character >= 'A') && (character <= 'Z');
	}

	// [ifndef gwt] method
	/**
	 * Writes a new line.
	 * 
	 * @param os
	 *            The output stream.
	 * @throws IOException
	 */
	public static void writeCRLF(OutputStream os) throws IOException {
		os.write(13); // CR
		os.write(10); // LF
	}

	// [ifndef gwt] method
	/**
	 * Writes a header line.
	 * 
	 * @param header
	 *            The header to write.
	 * @param os
	 *            The output stream.
	 * @throws IOException
	 */
	public static void writeHeaderLine(Header header, OutputStream os) throws IOException {
		os.write(SysUtils.getAsciiBytes(header.getName()));
		os.write(':');
		os.write(' ');

		if (header.getValue() != null) {
			os.write(SysUtils.getLatin1Bytes(header.getValue()));
		}

		os.write(13); // CR
		os.write(10); // LF
	}

	/**
	 * Private constructor to ensure that the class acts as a true utility class i.e. it isn't instantiable and extensible.
	 */
	private HeaderUtils() {
	}
}
