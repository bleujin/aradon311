package net.ion.radon.handler;


import java.io.File;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.FileUtil;

import org.apache.commons.io.filefilter.WildcardFileFilter;

public class TestAppCAcheHandler extends TestCase{
	
	public void testFilter() throws Exception {
		File[] files = FileUtil.findFiles(new File("./resource/temp"), new WildcardFileFilter(new String[]{"*.png", "*.js"}), true) ;
		Debug.line(files);
	}
	
	public void testMakeCacheMenifest() throws Exception {
		AppCacheHandler h = new AppCacheHandler(new File("./resource/temp")) ;
		
		Debug.line(h.makeCacheResource());
	}

}
