package net.ion.nradon.ajax;

import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

import net.ion.framework.util.Debug;
import net.ion.nradon.Radon;
import net.ion.nradon.client.websocket.TextMessageHandler;
import net.ion.nradon.client.websocket.WebSocketClient;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.handler.HttpToWebSocketHandler;
import net.ion.nradon.handler.SimpleStaticFileHandler;
import net.ion.nradon.handler.URIPathMatchHandler;
import net.ion.nradon.netty.codec.http.websocketx.TextWebSocketFrame;

import org.junit.Test;

public class WebSocketToAradon {

	@Test
	public void helloSocket() throws Exception {

		Radon webServer = RadonConfiguration.newBuilder(8080)
			.add(new URIPathMatchHandler("/a/{p1}/{p2}", new HttpToWebSocketHandler(new BroadEchoWebSocket())))
			.add(new SimpleStaticFileHandler("./resource/web/client"))
			.uncaughtExceptionHandler(new UncaughtExceptionHandler() {
				public void uncaughtException(Thread t, Throwable e) {
					e.printStackTrace(); 
				}
			}).start().get()  ;
		
		System.out.println("Server running at " + webServer.getConfig().publicUri());
		
		// Desktop.getDesktop().browse(new URI("http://" + InetAddress.getLocalHost().getHostAddress() + ":8080/client/sample.html")) ;

		final CountDownLatch openLatch = new CountDownLatch(1) ;
		final CountDownLatch messageLatch = new CountDownLatch(10) ;
		WebSocketClient client = WebSocketClient.create(new TextMessageHandler() {
			@Override
			public void onOpen() {
				openLatch.countDown(); 
			}
			@Override
			public void onMessage(TextWebSocketFrame tframe) {
				messageLatch.countDown(); 
				Debug.line(tframe.getText());
			}
			
			@Override
			public void onClosed() {
			}
		});
		
		client.connect(new URI("ws://127.0.0.1:8080/a/b/c")) ;
		openLatch.await(); 
		int i = 0 ;
		while(i++ < 10){
			client.sendMessage("Hi bleujin " + i) ;
		}
		messageLatch.await(); 
		client.disconnect() ;

		webServer.stop().get() ;
	}

}

