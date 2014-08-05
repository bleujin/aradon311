package net.ion.radon.core.let;

import java.net.URL;
import java.net.URLClassLoader;

import net.ion.framework.util.StringUtil;
import net.ion.nradon.HttpControl;
import net.ion.nradon.HttpHandler;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.Radon;
import net.ion.nradon.handler.event.ServerEvent.EventType;
import net.ion.radon.core.RadonInjectorFactory;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ConfigurationBootstrap;
import org.jboss.resteasy.spi.ResteasyDeployment;

public class PathHandler implements HttpHandler {

	private Dispatcher dispatcher;
	private String prefixURI = "";

	
	public PathHandler(Class... resources) {
		init(new URL[0], resources);
	}

	public PathHandler(URLClassLoader cl, Class... resources) {
		init(cl != null ? cl.getURLs() : new URL[0], resources);
	}

	private void init(URL[] scanningUrls, Class... resources) {
		ConfigurationBootstrap bootstrap = new PathBootstrap(scanningUrls, resources);
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
//				control.nextHandler(request, response, control);
			} else {
//				control.nextHandler(); 
			}
		} else {
//			control.nextHandler(); 
		}
	}

	public int order() {
		return 1;
	}

	public void onEvent(EventType event, Radon radon) {

	}

	public PathHandler prefixURI(String prefixURI) {
		this.prefixURI = prefixURI.startsWith("/") ? prefixURI : "/" + prefixURI;
		return this;
	}
}
