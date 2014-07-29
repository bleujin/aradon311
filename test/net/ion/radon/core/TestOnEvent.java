package net.ion.radon.core;

import junit.framework.TestCase;
import net.ion.nradon.Radon;
import net.ion.nradon.config.OnEventObject;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.handler.event.ServerEvent.EventType;

public class TestOnEvent extends TestCase {

	public void testInit() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9000).rootContext("ni", new NeedINIT()).start().get() ;
		
		NeedINIT ni = (NeedINIT) radon.getConfig().getServiceContext().getAttributeObject("ni") ;
		assertEquals(true, ni.inited);
		
		radon.stop().get() ;
		assertEquals(false, ni.inited);
	}
}

class NeedINIT implements OnEventObject{

	boolean inited = false ;
	
	@Override
	public void onEvent(EventType event, Radon service) {
		if (event == EventType.START) this.inited = true ;
		else if (event == EventType.STOP) this.inited = false ;
	}
	
}
