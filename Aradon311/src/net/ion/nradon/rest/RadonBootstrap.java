package net.ion.nradon.rest;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.ion.framework.util.Debug;
import net.ion.nradon.rest.let.HiLet;
import net.ion.nradon.rest.let.NamasteLet;

import org.jboss.resteasy.plugins.server.servlet.ConfigurationBootstrap;

public class RadonBootstrap extends ConfigurationBootstrap {
	private static final Map<String, String> PARAMS = new HashMap<String, String>() {
		{
//			put("resteasy.scan", "true");
			put("resteasy.resources", HiLet.class.getCanonicalName() + "," + NamasteLet.class.getCanonicalName());
		}
	};
	private final URL[] scanningUrls;

	public RadonBootstrap(URL[] scanningUrls) {
		this.scanningUrls = scanningUrls;
	}

	@Override
	public String getParameter(String key) {
		Debug.line(key);

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
}
