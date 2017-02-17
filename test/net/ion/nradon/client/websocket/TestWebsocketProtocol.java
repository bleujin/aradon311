package net.ion.nradon.client.websocket;

import java.io.File;

import junit.framework.TestCase;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.handler.SimpleStaticFileHandler;

public class TestWebsocketProtocol extends TestCase {

	public void testRun() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9000)
					.add("/client.htm", new SimpleStaticFileHandler(new File("./resource/web")))
					.add("/websocket", new DebugHandler()).startRadon() ;
		
		new InfinityThread().startNJoin(); 
	}
}
