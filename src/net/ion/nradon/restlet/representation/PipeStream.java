package net.ion.nradon.restlet.representation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class PipeStream {

	private static final long QUEUE_TIMEOUT = 5;

	private final BlockingQueue<Integer> queue;

	public PipeStream() {
		this.queue = new ArrayBlockingQueue<Integer>(1024);
	}

	public InputStream getInputStream() {
		return new InputStream() {
			private boolean endReached = false;

			@Override
			public int read() throws IOException {
				try {
					if (this.endReached) {
						return -1;
					}

					final Integer value = queue.poll(QUEUE_TIMEOUT, TimeUnit.SECONDS);
					if (value == null) {
						throw new IOException("Timeout while reading from the queue-based input stream");
					}

					this.endReached = (value.intValue() == -1);
					return value;
				} catch (InterruptedException ie) {
					throw new IOException("Interruption occurred while writing in the queue");
				}
			}
		};
	}

	public OutputStream getOutputStream() {
		return new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				try {
					if (!queue.offer(b, QUEUE_TIMEOUT, TimeUnit.SECONDS)) {
						throw new IOException("Timeout while writing to the queue-based output stream");
					}
				} catch (InterruptedException ie) {
					throw new IOException("Interruption occurred while writing in the queue");
				}
			}
		};
	}

}