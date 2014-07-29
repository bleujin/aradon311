package net.ion.nradon.filter;

import net.ion.nradon.EventSourceConnection;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.WebSocketConnection;
import net.ion.radon.core.IService;

public interface XRadonFilter {

	public class DefaultFilter implements XRadonFilter{

		public void esClose(IService iservice, EventSourceConnection conn) {
		}

		public void esOpen(IService iservice, EventSourceConnection conn) {
		}

		public void httpEnd(IService iservice, HttpRequest hreq, HttpResponse hres) {
		}

		public void httpStart(IService iservice, HttpRequest hreq, HttpResponse hres) {
		}

		public void init(IService service) {
		}

		public void wsClose(IService iservice, WebSocketConnection conn) {
		}

		public void wsInbound(IService iservice, WebSocketConnection conn, String msg) {
		}

		public void wsInbound(IService iservice, WebSocketConnection conn, byte[] msg) {
		}

		public void wsInboundPing(IService iservice, WebSocketConnection conn, byte[] msg) {
		}

		public void wsInboundPong(IService iservice, WebSocketConnection conn, byte[] msg) {
		}

		public void wsOpen(IService iservice, WebSocketConnection conn) {
		}
		
	};
	
	public abstract void init(IService service) ;
	
	public abstract void httpEnd(IService iservice, HttpRequest hreq, HttpResponse hres) ;

	public abstract void httpStart(IService iservice, HttpRequest hreq, HttpResponse hres) ;

	public abstract void wsOpen(IService iservice, WebSocketConnection conn);

	public abstract void wsClose(IService iservice, WebSocketConnection conn);

	public abstract void wsInbound(IService iservice, WebSocketConnection conn, String msg);

	public abstract void wsInbound(IService iservice, WebSocketConnection conn, byte[] msg);

	public abstract void wsInboundPong(IService iservice, WebSocketConnection conn, byte[] msg);

	public abstract void wsInboundPing(IService iservice, WebSocketConnection conn, byte[] msg);

//	public abstract void error(HttpRequest request, Throwable error);
//	public abstract void custom(HttpRequest request, String action, String data);

	public abstract void esOpen(IService iservice, EventSourceConnection conn);

	public abstract void esClose(IService iservice, EventSourceConnection conn);


}
