package net.ion.nradon.restlet;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

public class TestFileMetaType extends TestCase{

	
	public void testInit() throws Exception {
//		FileMetaType.init(); 
		
		Debug.line(FileMetaType.mediaType("1.htm")) ;
		Debug.line(FileMetaType.mediaType2("1.htm").getName()) ;
	}
}
