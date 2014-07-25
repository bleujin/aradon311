package net.ion.nradon.rest.let;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import net.ion.framework.util.SetUtil;

@ApplicationPath("/apps/.*")
public class GreetingApp extends Application {

	private Set<Class> letClz = SetUtil.<Class>create(NamasteLet.class) ;
	private Set<Object> lets = SetUtil.<Object>create(new NamasteLet()) ;
	
	public GreetingApp() {
	}

	public Set getClasses() {
		return letClz;
	}

	public Set getSingletons() {
		return lets ;
	}

}
