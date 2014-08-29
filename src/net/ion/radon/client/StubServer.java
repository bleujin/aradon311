package net.ion.radon.client;

import java.io.Closeable;
import java.io.IOException;

import net.ion.nradon.stub.StubHttpRequest;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.let.PathHandler;

public class StubServer {

	private PathHandler shandler;
	private TreeContext rootContext;
	private StubServer() {
		this.rootContext = TreeContext.createRootContext() ;
	}
	
	public static StubServer create(Class... clazz) {
		System.setProperty("log4j.configuration", "file:./resource/log4j.properties") ;
		
		StubServer result = new StubServer();
		result.shandler = new PathHandler(clazz)  ; 
		return result ;
	}

	public FakeRequest request(String path) {
		StubHttpRequest stub = new StubHttpRequest(path);
		stub.data(TreeContext.class.getCanonicalName(), rootContext.createChildContext()) ;
		
		FakeRequest result = new FakeRequest(shandler, stub);
		return result;
	}
	
	public TreeContext treeContext() {
		return rootContext ;
	}

	public void shutdown() throws IOException {
		for(String key : rootContext.keys()) {
			Object obj = rootContext.getAttributeObject(key) ;
			if (obj instanceof Closeable) ((Closeable)obj).close(); 
		}
	}
}
