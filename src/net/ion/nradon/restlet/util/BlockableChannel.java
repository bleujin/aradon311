package net.ion.nradon.restlet.util;

import java.nio.channels.Channel;

/**
 * NIO channel that can indicate if it is blocking or non blocking.
 * 
 * @author Jerome Louvel
 */
public interface BlockableChannel extends Channel {

	/**
	 * Indicates if the channel is likely to block upon IO operations.
	 * 
	 * @return True if the channel is likely to block upon IO operations.
	 */
	public boolean isBlocking();

}