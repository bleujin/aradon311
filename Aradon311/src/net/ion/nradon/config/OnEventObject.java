package net.ion.nradon.config;

import net.ion.nradon.Radon;
import net.ion.nradon.handler.event.ServerEvent.EventType;

public interface OnEventObject {

	public void onEvent(EventType event, Radon service) ;
}
