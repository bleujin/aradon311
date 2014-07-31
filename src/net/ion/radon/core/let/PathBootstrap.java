package net.ion.radon.core.let;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.ion.framework.util.SetUtil;
import net.ion.framework.util.StringUtil;

import org.jboss.resteasy.plugins.server.servlet.ConfigurationBootstrap;

public class PathBootstrap extends ConfigurationBootstrap {
	private static final Map<String, String> PARAMS = new HashMap<String, String>() {
		{
			put("resteasy.scan", "false");
//			put("resteasy.resources", AsterikLet.class.getCanonicalName() + "," + NamasteLet.class.getCanonicalName());
		}
	};
	private final URL[] scanningUrls;

	public PathBootstrap(URL[] scanningUrls, Class... resources) {
		this.scanningUrls = scanningUrls;
		Set<String> clzNames = SetUtil.newSet() ;
		for (Class res : resources) {
			clzNames.add(res.getCanonicalName()) ;
		}
		PARAMS.put("resteasy.resources", StringUtil.join(clzNames, ",")) ;
		PARAMS.put("resteasy.servlet.mapping.prefix", "/bleujin") ;
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
}