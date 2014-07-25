package net.ion.nradon.restlet.util;

import java.nio.channels.Channel;

/**
 * NIO channel that is based on a selectable channel.
 * 
 * @author Jerome Louvel
 */
public interface SelectionChannel extends Channel, BlockableChannel {

	/**
	 * Returns the NIO registration.
	 * 
	 * @return The NIO registration.
	 */
	public SelectionRegistration getRegistration();

}
