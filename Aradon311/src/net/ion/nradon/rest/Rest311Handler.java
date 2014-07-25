package net.ion.nradon.rest;

import static net.ion.nradon.rest.RestRequest.wrap;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Set;

import net.ion.framework.util.SetUtil;
import net.ion.nradon.HttpControl;
import net.ion.nradon.HttpHandler;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.Radon;
import net.ion.nradon.handler.event.ServerEvent.EventType;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ConfigurationBootstrap;
import org.jboss.resteasy.spi.ResteasyDeployment;

public class Rest311Handler implements HttpHandler {
    private Dispatcher dispatcher;

    public Rest311Handler() {
        ClassLoader cl = getClass().getClassLoader();
        if (cl instanceof URLClassLoader) {
            init(((URLClassLoader) cl).getURLs());
        } else {
            throw new RuntimeException("The empty constructor can only be used when the class loader is an URLClassLoader. Use one of the other constructors.");
        }
    }
    
    public Rest311Handler(URL[] scanningUrls) {
        init(scanningUrls);
    }

    public Rest311Handler(Class clazz, Class... more) {
    	Set<URL> urls = SetUtil.newSet() ;
    	urls.add(clazz.getProtectionDomain().getCodeSource().getLocation()) ;
    	for (Class clz : more) {
			urls.add(clz.getProtectionDomain().getCodeSource().getLocation()) ;
		}
    	
   		init(urls.toArray(new URL[0])) ;
    }

    public Rest311Handler(Object... resources) {
        init(new URL[0], resources);
    }

    private void init(URL[] scanningUrls, Object... resources) {
        ConfigurationBootstrap bootstrap = new RadonBootstrap(scanningUrls);
        ResteasyDeployment deployment = bootstrap.createDeployment();
        deployment.getResources().addAll(Arrays.asList(resources));
        deployment.start();
        dispatcher = deployment.getDispatcher();
    }

    public void handleHttpRequest(final HttpRequest request, final HttpResponse response, final HttpControl control) throws Exception {
        RestRequest req = wrap(request);
        RestResponse res = new RestResponse(request, response, dispatcher, control);
        dispatcher.invoke(req, res);
        if (res.wasHandled()) {
            response.end();
        } else {
        	control.nextHandler();
        }
    }
    
    public int order(){
    	return 1 ;
    }

	public void onEvent(EventType event, Radon radon) {
		
	}
}
