package net.ion.radon.core.let;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;

import net.ion.framework.util.Debug;
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


	public static PathHandler reload(Class... resources) throws Exception {
		final MyPathClassLoader ploader = new MyPathClassLoader(PathHandler.class.getClassLoader().getParent());

		final List<String> clzNames = ListUtil.newList();
		for (Class clz : resources) {
			clzNames.add(clz.getCanonicalName());
		}

		List<Class> newClz = ListUtil.newList();
		for (String clz : clzNames) {
			newClz.add(ploader.loadClass(clz));
		}

		final PathHandler result = new PathHandler(newClz.toArray(new Class[0]));

		ploader.start(new AfterReLoader() {
			@Override
			public void onReload(ClassLoader classloader) {
				try {
					List<Class> newClz = ListUtil.newList();
					for (String clz : clzNames) {
						newClz.add(ploader.loadClass(clz));
					}
					result.init(new URL[0], newClz.toArray(new Class[0]));
				} catch (ClassNotFoundException ex) {
					ex.printStackTrace();
				}
			}
		});

		return result;
	}

	// @Deprecated
	// PathHandler(URLClassLoader cl, Class... resources) {
	// init(cl != null ? cl.getURLs() : new URL[0], resources);
	// }

	private void init(URL[] scanningUrls, Class... resources) {

		ConfigurationBootstrap bootstrap = new PathBootstrap(scanningUrls, this.cloader, resources);
		ResteasyDeployment deployment = bootstrap.createDeployment();
		deployment.setInjectorFactoryClass(RadonInjectorFactory.class.getCanonicalName());
		// deployment.getResources().addAll(Arrays.asList(resources));
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
					Debug.line(file + " changed !");
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