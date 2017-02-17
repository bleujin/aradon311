package net.ion.bleujin.problem;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

import javax.ws.rs.Path;

import junit.framework.TestCase;
import net.ion.framework.cloader.AfterReLoader;
import net.ion.framework.cloader.DynamicClassLoader;
import net.ion.framework.cloader.OuterClassLoader;
import net.ion.framework.util.Debug;
import net.ion.radon.cload.monitor.AbstractListener;
import net.ion.radon.cload.monitor.FileAlterationMonitor;

import org.apache.commons.io.monitor.FileAlterationObserver;

public class TestAnnotation extends TestCase {

	public void testIsAnno() throws Exception {
		Debug.line(HelloName.class.isAnnotationPresent(Path.class)) ;
		Debug.line(getClass().getClassLoader().loadClass(HelloName.class.getCanonicalName()).isAnnotationPresent(Path.class)) ;
	}
	

	public void testWhenReload() throws Exception {
		String name = HelloName.class.getCanonicalName() ;
		
		MyPathClassLoader cl = new MyPathClassLoader(getClass().getClassLoader().getParent()) ;
		Debug.line(cl.loadClass(name).isAnnotationPresent(Path.class)) ;
		
	}
	
	
}



class MyPathClassLoader {

	private OuterClassLoader classloader;
	private File srcDir;
	
	public MyPathClassLoader(ClassLoader parent) throws Exception {
		this.srcDir = new File("./bin");

		DynamicClassLoader inner = new DynamicClassLoader("./bin", parent);
		inner.addDirectory("./lib/ref");
		inner.addDirectory("./lib/reflib");
		inner.addDirectory("./lib/option");

		this.classloader = new OuterClassLoader(inner);
	}

	public void start(final AfterReLoader loader) throws Exception {
		FileAlterationObserver fo = new FileAlterationObserver(srcDir);
		fo.addListener(new AbstractListener() {
			@Override
			public void onFileChange(File file) {
				try {
					DynamicClassLoader newLoader = new DynamicClassLoader("./bin", getClass().getClassLoader().getParent());
					newLoader.addDirectory("./lib/ref");
					newLoader.addDirectory("./lib/reflib");
					newLoader.addDirectory("./lib/option");

					classloader.change(newLoader);
					loader.onReload(classloader);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		FileAlterationMonitor fam = new FileAlterationMonitor(1000, Executors.newScheduledThreadPool(1), fo);
		fam.start();
	}

	public Class loadClass(String clzName) throws ClassNotFoundException {
		return classloader.loadClass(clzName);
	}
}