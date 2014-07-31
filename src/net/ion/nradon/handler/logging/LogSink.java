package net.ion.nradon.handler.logging;

import net.ion.nradon.EventSourceConnection;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.WebSocketConnection;

public interface LogSink {

	void httpStart(HttpRequest request);

	void httpEnd(HttpRequest request, HttpResponse response);

	void webSocketConnectionOpen(WebSocketConnection connection);

	void webSocketConnectionClose(WebSocketConnection connection);

	void webSocketInboundData(WebSocketConnection connection, String data);

	void webSocketInboundData(WebSocketConnection connection, byte[] msg);

	void webSocketInboundPing(WebSocketConnection connection, byte[] msg);

	void webSocketInboundPong(WebSocketConnection connection, byte[] msg);

	void webSocketOutboundData(WebSocketConnection connection, String data);

	void webSocketOutboundData(WebSocketConnection connection, byte[] data);

	void webSocketOutboundPing(WebSocketConnection connection, byte[] msg);
	
	void webSocketOutboundPong(WebSocketConnection connection, byte[] msg);

	void error(HttpRequest request, Throwable error);

	void custom(HttpRequest request, String action, String data);

	void eventSourceConnectionOpen(EventSourceConnection connection);

	void eventSourceConnectionClose(EventSourceConnection connection);

	void eventSourceOutboundData(EventSourceConnection connection, String data);
}
