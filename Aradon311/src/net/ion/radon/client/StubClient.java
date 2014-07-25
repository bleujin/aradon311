package net.ion.radon.client;

import net.ion.nradon.stub.StubHttpRequest;
import net.ion.radon.core.let.SectionHandler;

public class StubClient {

	private SectionHandler shandler;
	StubClient() {
	}
	
	public static StubClient create(Object... lets) {
		StubClient result = new StubClient();
		result.shandler = new SectionHandler(lets)  ; 
		return result ;
	}

	public FakeRequest request(String path) {
		return new FakeRequest(shandler, new StubHttpRequest(path));
	}
}
