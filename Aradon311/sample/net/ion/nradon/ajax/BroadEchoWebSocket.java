package net.ion.nradon.ajax;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.ion.nradon.WebSocketConnection;
import net.ion.nradon.WebSocketHandler;

public class BroadEchoWebSocket implements WebSocketHandler {

	private CopyOnWriteArrayList<WebSocketConnection> conns = new CopyOnWriteArrayList<WebSocketConnection>() ;
	public void onOpen(WebSocketConnection connection) {
		connection.send("Hello! There are " + conns.size() + " other connections active");
		conns.add(connection) ;
	}

	public void onClose(WebSocketConnection connection) {
		conns.remove(connection) ;
	}

	public void onMessage(WebSocketConnection connection, String message) {
		for (final WebSocketConnection conn : conns) {
			conn.send(message.toUpperCase() + "(conns :" + conns.size() + ")") ;
		}
	}

	public void onMessage(WebSocketConnection connection, byte[] message) {
	}

	public void onPong(WebSocketConnection connection, byte[] message) {
	}
	public void onPing(WebSocketConnection connection, byte[] message) {
	}

	List<WebSocketConnection> getConnList(){
		return conns ;
	}
}