package net.ion.radon.core.let;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import net.ion.nradon.HttpControl;
import net.ion.nradon.HttpHandler;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.Radon;
import net.ion.nradon.handler.event.ServerEvent.EventType;
import net.ion.nradon.rest.RestRequest;
import net.ion.nradon.rest.RestResponse;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ConfigurationBootstrap;
import org.jboss.resteasy.spi.ResteasyDeployment;

public class SectionHandler implements HttpHandler {

	private Dispatcher dispatcher;

	public SectionHandler(Object... resources) {
		init(new URL[0], resources);
	}

	public SectionHandler(URLClassLoader cl, Object... resources) {
		init( cl != null ? cl.getURLs() : new URL[0], resources);
	}

	private void init(URL[] scanningUrls, Object... resources) {
		ConfigurationBootstrap bootstrap = new SectionBootstrap(scanningUrls, resources);
		ResteasyDeployment deployment = bootstrap.createDeployment();
		deployment.getResources().addAll(Arrays.asList(resources));
		deployment.start();
		dispatcher = deployment.getDispatcher();
	}

	public void handleHttpRequest(final HttpRequest request, final HttpResponse response, final HttpControl control) throws Exception {
		RestRequest req = RestRequest.wrap(request);
		RestResponse res = new RestResponse(request, response, dispatcher, control);
		dispatcher.invoke(req, res);
		if (res.wasHandled()) {
			response.end();
		} else {
			control.nextHandler();
		}
	}
	
	public int order() {
		return 1;
	}

	public void onEvent(EventType event, Radon radon) {

	}
}
