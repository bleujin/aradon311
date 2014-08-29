package net.ion.radon.cload.cloader;


import java.io.File;
import java.io.IOException;
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

		final File srcDir = new File("./test") ;
		
		DirClassLoader inner = new DirClassLoader("./test") ;
		final OuterClassLoader classloader = new OuterClassLoader(inner);

		FileAlterationObserver fo = new FileAlterationObserver(srcDir) ;
		fo.addListener(new AbstractListener() {
			@Override
			public void onFileChange(File file) {
				try {
					Debug.line(file + " changed !");
					classloader.change(new DirClassLoader("./test"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		FileAlterationMonitor fam = new FileAlterationMonitor(1000, Executors.newScheduledThreadPool(1), fo);
		fam.start();

		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
						Object o = classloader.loadClass("net.ion.radon.cload.cloader.Main").newInstance();
						((Runnable) o).run();
					} catch (Exception e) {
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
