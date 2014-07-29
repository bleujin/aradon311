package net.ion.bleujin;

import java.io.IOException;

import net.ion.framework.util.InfinityThread;

public class TestTinyWebServer {
	
	
	public static void main(String[] args) throws Exception {

		final MyServer srv = new MyServer();
		// setting aliases, for an optional file servlet
		Acme.Serve.Serve.PathTreeDictionary aliases = new Acme.Serve.Serve.PathTreeDictionary();
		aliases.put("/*", new java.io.File("./resource"));
		// note cast name will depend on the class name, since it is anonymous class
		srv.setMappingTable(aliases);
		// setting properties for the server, and exchangeable Acceptors
		java.util.Properties properties = new java.util.Properties();
		properties.put("port", 9080);
		properties.setProperty(Acme.Serve.Serve.ARG_NOHUP, "nohup");
		srv.arguments = properties;
		srv.addDefaultServlets(null); // optional file servlet
		// srv.addServlet("/myservlet", new MyServlet()); // optional
		// the pattern above is exact match, use /myservlet/* for mapping any path startting with /myservlet (Since 1.93)
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				try {
					srv.notifyStop();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				srv.destroyAllServlets();
			}
		}));
		srv.serve();
		
		new InfinityThread().startNJoin(); 
	}
}

class MyServer extends Acme.Serve.Serve {
	private static final long serialVersionUID = -8821575997336903554L;

	public void setMappingTable(PathTreeDictionary mappingtable) {
		super.setMappingTable(mappingtable);
	}

	// add the method below when .war deployment is needed
	public void addWarDeployer(String deployerFactory, String throttles) {
		super.addWarDeployer(deployerFactory, throttles);
	}
}