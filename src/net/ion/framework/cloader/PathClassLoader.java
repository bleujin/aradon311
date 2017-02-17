package net.ion.framework.cloader;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

import net.ion.framework.util.Debug;
import net.ion.radon.cload.monitor.AbstractListener;
import net.ion.radon.cload.monitor.FileAlterationMonitor;

import org.apache.commons.io.monitor.FileAlterationObserver;

public class PathClassLoader {

	public static ClassLoader createClassLoader(ClassLoader parent) throws Exception{
		return createClassLoader(parent, AfterReLoader.BLANK) ;
	}
	

	public static ClassLoader createClassLoader(ClassLoader parent, final AfterReLoader loader) throws Exception{
		final File srcDir = new File("./bin") ;
		
		DynamicClassLoader inner = new DynamicClassLoader("./bin", parent) ;
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
					DynamicClassLoader newLoader = new DynamicClassLoader("./bin", getClass().getClassLoader().getParent());
					newLoader.addDirectory("./lib/ref") ;
					newLoader.addDirectory("./lib/reflib") ;
					newLoader.addDirectory("./lib/option") ;
					
					classloader.change(newLoader);
					loader.onReload(classloader); 
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		FileAlterationMonitor fam = new FileAlterationMonitor(1000, Executors.newScheduledThreadPool(1), fo);
		fam.start();
		
		return classloader ;
	}
	
	
}
