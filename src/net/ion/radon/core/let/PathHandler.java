package net.ion.radon.core.let;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import net.ion.framework.logging.LogBroker;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.HttpControl;
import net.ion.nradon.HttpHandler;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.Radon;
import net.ion.nradon.handler.event.ServerEvent.EventType;
import net.ion.radon.cload.cloader.AfterReLoader;
import net.ion.radon.cload.cloader.DynamicClassLoader;
import net.ion.radon.cload.cloader.OuterClassLoader;
import net.ion.radon.cload.monitor.AbstractListener;
import net.ion.radon.cload.monitor.FileAlterationMonitor;
import net.ion.radon.core.RadonInjectorFactory;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ConfigurationBootstrap;
import org.jboss.resteasy.spi.ResteasyDeployment;

public class PathHandler implements HttpHandler {

	private Dispatcher dispatcher;
	private String prefixURI = "";
	private ClassLoader cloader = PathHandler.class.getClassLoader();

	public PathHandler(Class... resources) {
		init(new URL[0], resources);
	}
	

	public static HttpHandler create(Class... resources) {
		return new PathHandler(resources);
	}
	
	// run with debug mode
	public static PathHandler reload(final Class... resources) throws Exception {
		final Logger logger = LogBroker.getLogger(PathHandler.class) ;
		final PathHandler result = new PathHandler(resources) ;

//		final List<String> clzNames = ListUtil.newList() ;
//		for (Class clz : resources) {
//			clzNames.add(clz.getCanonicalName()) ;
//		}
//		
//		MyPathClassLoader mcl = new MyPathClassLoader(PathHandler.class.getClassLoader()) ;
//		mcl.start(new AfterReLoader() {
//			@Override
//			public void onReload(ClassLoader classloader) {
//				logger.warning(" reloaded !");
//				try {
//					List<Class> clzz = ListUtil.newList();
//					for (String clzName : clzNames) {
//						Class clz = classloader.loadClass(clzName);
//						clzz.add(clz);
//						
//						
//					}
//					result.init(new URL[0], clzz.toArray(new Class[0]));
//				} catch (ClassNotFoundException e) {
//					Debug.line(e.getMessage());
////					e.printStackTrace();
//				}
//			}
//		});
		return result;
	}

	private void init(URL[] scanningUrls, Class... resources) {

		ConfigurationBootstrap bootstrap = new PathBootstrap(scanningUrls, this.cloader);
		ResteasyDeployment deployment = bootstrap.createDeployment();
		deployment.setInjectorFactoryClass(RadonInjectorFactory.class.getCanonicalName());
		deployment.getActualResourceClasses().clear(); 
		deployment.getActualResourceClasses().addAll(ListUtil.toList(resources)) ;
		deployment.start();
		dispatcher = deployment.getDispatcher();
	}

	public void handleHttpRequest(final HttpRequest request, final HttpResponse response, final HttpControl control) throws Exception {

		if (StringUtil.isBlank(prefixURI) || request.uri().startsWith(prefixURI)) {

			RestRequest req = RestRequest.wrap(request, this.prefixURI);
			RestResponse res = new RestResponse(request, response, dispatcher, control);
			dispatcher.invoke(req, res);
			if (res.wasHandled()) {
				response.status(200).end();
				// control.nextHandler(request, response, control);
			} else {
				// control.nextHandler();
			}
		} else {
			// control.nextHandler();
		}
	}

	public int order() {
		return 5;
	}

	public void onEvent(EventType event, Radon radon) {

	}

	public PathHandler prefixURI(String prefixURI) {
		this.prefixURI = prefixURI.startsWith("/") ? prefixURI : "/" + prefixURI;
		return this;
	}

}


class MyPathClassLoader {

	private OuterClassLoader classloader;
	private File srcDir;
	
	public MyPathClassLoader(ClassLoader parent) throws Exception {
		this.srcDir = new File("./bin");

		DynamicClassLoader inner = new DynamicClassLoader("./bin", parent);
		inner.addDirectory("./lib/ref");
		inner.addDirectory("./lib/reflib");
		inner.addDirectory("./lib/option");

		this.classloader = new OuterClassLoader(inner);
	}

	public void start(final AfterReLoader loader) throws Exception {
		FileAlterationObserver fo = new FileAlterationObserver(srcDir);
		fo.addListener(new AbstractListener() {
			@Override
			public void onFileChange(File file) {
				try {
					DynamicClassLoader newLoader = new DynamicClassLoader("./bin", getClass().getClassLoader().getParent());
					newLoader.addDirectory("./lib/ref");
					newLoader.addDirectory("./lib/reflib");
					newLoader.addDirectory("./lib/option");

					classloader.change(newLoader);
					loader.onReload(classloader);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		FileAlterationMonitor fam = new FileAlterationMonitor(1000, Executors.newScheduledThreadPool(1), fo);
		fam.start();
	}

	public Class loadClass(String clzName) throws ClassNotFoundException {
		return classloader.loadClass(clzName);
	}
}