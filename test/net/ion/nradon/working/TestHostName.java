package net.ion.nradon.working;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nradon.config.RadonConfiguration;

public class TestHostName extends TestCase {

	
	public void testHostNameSet() throws Exception {
		RadonConfiguration config = RadonConfiguration.newBuilder(9000).build() ;
		
		Debug.line(config.publicUri());
	}
	
}
