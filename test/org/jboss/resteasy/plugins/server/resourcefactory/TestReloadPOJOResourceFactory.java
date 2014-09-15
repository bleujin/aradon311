package org.jboss.resteasy.plugins.server.resourcefactory;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.cload.cloader.PathClassLoader;
import net.ion.radon.core.let.PathHandler;

import org.apache.commons.beanutils.MethodUtils;

public class TestReloadPOJOResourceFactory extends TestCase {

	
	public void testRun() throws Exception {
		Radon radon = RadonConfiguration.newBuilder(9000).add(new PathHandler(Hallo.class)).start().get() ;
		
		new InfinityThread().startNJoin();
	}
	
	public void testCloader() throws Exception {
		ClassLoader cloader = PathClassLoader.createClassLoader(getClass().getClassLoader().getParent()) ;
		
		Class<?> clz = cloader.loadClass(Hallo.class.getCanonicalName()) ;
		
		Object hallo = clz.newInstance() ;
		Object result = MethodUtils.invokeMethod(hallo, "hello", "bleujin") ;
		Debug.line(result);
	}
}

