package net.ion.nradon.restlet.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.util.concurrent.ExecutorService;

import net.ion.nradon.restlet.data.CharacterSet;
import net.ion.nradon.restlet.data.Range;
import net.ion.nradon.restlet.representation.PipeStream;
import net.ion.nradon.restlet.representation.Representation;

public class IoUtils {

	public static final int BUFFER_SIZE = 8192;
	public final static int TIMEOUT_MS = 60000;

	public static long exhaust(InputStream input) throws IOException {
		long result = -1L;

		if (input != null) {
			byte[] buf = new byte[2048];
			int read = input.read(buf);
			result = (read == -1) ? -1 : 0;

			while (read != -1) {
				result += read;
				read = input.read(buf);
			}
		}

		return result;
	}

	public static long getAvailableSize(Representation representation) {
		if (representation.getRange() == null) {
			return representation.getSize();
		} else if (representation.getRange().getSize() != Range.SIZE_MAX) {
			if (representation.hasKnownSize()) {
				return Math.min(representation.getRange().getIndex() + representation.getRange().getSize(), representation.getSize()) - representation.getRange().getIndex();
			} else {
				return Representation.UNKNOWN_SIZE;
			}
		} else if (representation.hasKnownSize()) {
			if (representation.getRange().getIndex() != Range.INDEX_LAST) {
				return representation.getSize() - representation.getRange().getIndex();
			}

			return representation.getSize();
		}

		return Representation.UNKNOWN_SIZE;
	}

	public static java.io.OutputStream getStream(java.io.Writer writer, CharacterSet characterSet) {
		return new WriterOutputStream(writer, characterSet);
	}

	public static boolean isBlocking(Channel channel) {
		boolean result = true;

		if (channel instanceof SelectableChannel) {
			SelectableChannel selectableChannel = (SelectableChannel) channel;
			result = selectableChannel.isBlocking();
		}

		return result;
	}

	public static Reader getReader(InputStream stream, CharacterSet characterSet) throws UnsupportedEncodingException {
		if (characterSet != null) {
			return new InputStreamReader(stream, characterSet.getName());
		}

		return new InputStreamReader(stream);
	}

	public static String toString(InputStream inputStream, CharacterSet characterSet) {
		String result = null;

		if (inputStream != null) {
			// [ifndef gwt]
			try {
				if (characterSet != null) {
					result = IoUtils.toString(new InputStreamReader(inputStream, characterSet.getName()));
				} else {
					result = IoUtils.toString(new InputStreamReader(inputStream));
				}

				inputStream.close();
			} catch (Exception e) {
			}
		}

		return result;
	}

	public static String toString(Reader reader) {
		String result = null;

		if (reader != null) {
			try {
				StringBuilder sb = new StringBuilder();
				BufferedReader br = (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader, BUFFER_SIZE);
				char[] buffer = new char[2048];
				int charsRead = br.read(buffer);

				while (charsRead != -1) {
					sb.append(buffer, 0, charsRead);
					charsRead = br.read(buffer);
				}

				br.close();
				result = sb.toString();
			} catch (Exception e) {
				// Returns an empty string
			}
		}

		return result;
	}

	// [ifndef gwt] method
	/**
	 * Copies characters from a reader to a writer. When the reading is done, the reader is closed.
	 * 
	 * @param reader
	 *            The reader.
	 * @param writer
	 *            The writer.
	 * @throws IOException
	 */
	public static void copy(Reader reader, java.io.Writer writer) throws IOException {
		int charsRead;
		char[] buffer = new char[2048];

		while ((charsRead = reader.read(buffer)) > 0) {
			writer.write(buffer, 0, charsRead);
		}

		writer.flush();
		reader.close();
	}

	public static ReadableByteChannel getChannel(InputStream inputStream) {
		return (inputStream != null) ? Channels.newChannel(inputStream) : null;
	}

	
	public static InputStream getStream(ExecutorService es, final Representation representation) {
		InputStream result = null;
		if (representation == null) {
			return null;
		}

		final PipeStream pipe = new PipeStream();

		Runnable task = new Runnable() {
			public void run() {
				try {
					java.io.OutputStream os = pipe.getOutputStream();
					representation.write(os);
					os.write(-1);
					os.flush();
					os.close();
				} catch (IOException ioe) {
					ioe.printStackTrace(); 
				}
			}
		};

		if (es == null) {
			es.submit(task);
		} else {
			new Thread(task).start();
		}

		result = pipe.getInputStream();

		return result;
	}

}
