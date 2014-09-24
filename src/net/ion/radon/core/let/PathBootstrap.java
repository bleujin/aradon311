package net.ion.radon.core.let;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.resteasy.plugins.server.servlet.ConfigurationBootstrap;

public class PathBootstrap extends ConfigurationBootstrap {
	private static final Map<String, String> PARAMS = new HashMap<String, String>() {
		{
			put("resteasy.scan", "false");
//			put("resteasy.resources", AsterikLet.class.getCanonicalName() + "," + NamasteLet.class.getCanonicalName());
		}
	};
	private final URL[] scanningUrls;
	private final ClassLoader cloader;

	public PathBootstrap(URL[] scanningUrls, ClassLoader cloader) {
		this.scanningUrls = scanningUrls;
		this.cloader = cloader ;
	}

	@Override
	public String getParameter(String key) {
		return PARAMS.get(key);
	}

	@Override
	public String getInitParameter(String key) {
		return PARAMS.get(key);
	}

	@Override
	public URL[] getScanningUrls() {
		return scanningUrls;
	}

	@Override
	public Set<String> getParameterNames() {
		return PARAMS.keySet();
	}

	@Override
	public Set<String> getInitParameterNames() {
		return PARAMS.keySet();
	}
	
	public ClassLoader classLoader(){
		return cloader ;
	}
}