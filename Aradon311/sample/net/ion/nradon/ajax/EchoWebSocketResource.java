package net.ion.nradon.ajax;

import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.nradon.WebSocketConnection;
import net.ion.nradon.WebSocketHandler;

public class EchoWebSocketResource implements WebSocketHandler{

	private List<WebSocketConnection> conns = ListUtil.newList();

	public void onMessage(WebSocketConnection conn, String msg) throws Throwable {
		conn.send(msg) ;
	}

	public void onMessage(WebSocketConnection conn, byte[] msg) throws Throwable {
		;
	}

	public void onOpen(WebSocketConnection conn) throws Exception {
		conns.add(conn);
	}

	public void onClose(WebSocketConnection conn) throws Exception {
		conns.remove(conn);
	}

	public void onPong(WebSocketConnection conn, byte[] msg) throws Throwable {

	}

	public void onPing(WebSocketConnection conn, byte[] msg) throws Throwable {

	}

	public List<WebSocketConnection> getConnections(){
		return conns ;
	}
	
}
