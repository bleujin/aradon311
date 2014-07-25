package net.ion.nradon.restlet.util;

import java.nio.channels.ReadableByteChannel;

/**
 * Readable byte channel that is based on a selectable channel.
 * 
 * @author Jerome Louvel
 */
public interface ReadableSelectionChannel extends SelectionChannel, ReadableByteChannel {

}