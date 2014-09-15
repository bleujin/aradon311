package net.ion.radon.cload.cloader;


import java.io.File;
import java.util.concurrent.Executors;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.radon.cload.monitor.AbstractListener;
import net.ion.radon.cload.monitor.FileAlterationMonitor;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class TestReloadClassLoad extends TestCase {

	
	
	
	
	public void testReloadSourceLoader() throws Exception {

		final ClassLoader classloader = PathClassLoader.createClassLoader(getClass().getClassLoader().getParent()) ;
		

		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
						Object o = classloader.loadClass("net.ion.radon.cload.cloader.Main").newInstance();
						((Runnable) o).run();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		

		new InfinityThread().startNJoin();

	}
	
	public void testMonitor() throws Exception {
		FileAlterationObserver fo = new FileAlterationObserver(new File("./test"), FileFilterUtils.suffixFileFilter("java")) ;
		fo.addListener(new AbstractListener() {
			@Override
			public void onFileChange(File file) {
				Debug.line(file); 
			}
		});
		FileAlterationMonitor fam = new FileAlterationMonitor(2000, Executors.newScheduledThreadPool(1), fo);
		fam.start();
		
		new InfinityThread().startNJoin(); 
	}
	
}
