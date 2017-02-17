package net.ion.bleujin.problem;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

import junit.framework.TestCase;
import net.ion.framework.cloader.DynamicClassLoader;
import net.ion.framework.cloader.OuterClassLoader;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.handler.logging.LoggingHandler;
import net.ion.nradon.handler.logging.SimpleLogSink;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.cload.monitor.AbstractListener;
import net.ion.radon.cload.monitor.FileAlterationMonitor;
import net.ion.radon.core.let.PathHandler;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class TestReloadRadon extends TestCase {

	public void testStart() throws Exception {
		
		RadonConfiguration.newBuilder(9500)
			.add(new LoggingHandler(new SimpleLogSink()))
			.add(PathHandler.reload(HelloName.class))
			.start().get() ;
		
		new InfinityThread().startNJoin(); 
	}
	
	public void testStru() throws Exception {
		
		Radon radon = RadonConfiguration.newBuilder(9500)
			.add(new PathHandler(HelloName.class))
			.start().get() ;
		
		NewClient nc = NewClient.create() ;
		Response response = nc.prepareGet("http://localhost:9500/hello/bleuin").execute().get() ;
		Debug.line(response.getTextBody());
		
		nc.prepareGet("http://localhost:9500/hello/bleuin").execute().get() ;
		
		nc.close(); 
		radon.stop() ;
	}
	
	public void testReloadStart() throws Exception {
		final File srcDir = new File("./test") ;
		
		DynamicClassLoader inner = new DynamicClassLoader("./bin", getClass().getClassLoader().getParent()) ;
		inner.addDirectory("./lib/ref") ;
		inner.addDirectory("./lib/reflib") ;
		inner.addDirectory("./lib/option") ;
		
		final OuterClassLoader classloader = new OuterClassLoader(inner);

		FileAlterationObserver fo = new FileAlterationObserver(srcDir) ;
		fo.addListener(new AbstractListener() {
			@Override
			public void onFileChange(File file) {
				try {
					Debug.line(file + " changed !");
					DynamicClassLoader newLoader = new DynamicClassLoader("./test");
					newLoader.addDirectory("./lib/ref") ;
					newLoader.addDirectory("./lib/reflib") ;
					newLoader.addDirectory("./lib/option") ;
					
					classloader.change(newLoader);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		FileAlterationMonitor fam = new FileAlterationMonitor(1000, Executors.newScheduledThreadPool(1), fo);
		fam.start();


		Object o = classloader.loadClass("net.ion.bleujin.problem.TestReloadRadon").newInstance();
		MethodUtils.invokeMethod(o, "testStart", new Object[0]) ;
	}
}

