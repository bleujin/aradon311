package net.ion.nradon.client.websocket;

import net.ion.nradon.netty.codec.http.websocketx.BinaryWebSocketFrame;

public abstract class TextMessageHandler implements IResponseMessageHandler{

	@Override
	public void onBinMessage(BinaryWebSocketFrame bframe) {
	}

	@Override
	public void onPong() {
	}

	@Override
	public void onDisconnected() {
	}

}
