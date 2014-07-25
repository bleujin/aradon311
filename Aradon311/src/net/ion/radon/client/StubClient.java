package net.ion.radon.client;

import net.ion.nradon.stub.StubHttpRequest;
import net.ion.radon.core.let.PathHandler;

public class StubClient {

	private PathHandler shandler;
	StubClient() {
	}
	
	public static StubClient create(Class... clazz) {
		StubClient result = new StubClient();
		result.shandler = new PathHandler(clazz)  ; 
		return result ;
	}

	public FakeRequest request(String path) {
		return new FakeRequest(shandler, new StubHttpRequest(path));
	}
}
