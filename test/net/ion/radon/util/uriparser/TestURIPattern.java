package net.ion.radon.util.uriparser;

import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;

public class TestURIPattern extends TestCase {

	
	public void testBestMatch() throws Exception {
		List<URIPattern> patterns = ListUtil.toList(new URIPattern("/article/123.{format}"), new URIPattern("/article/123.htm"), new URIPattern("/article/123.txt"), new URIPattern("/article/{name}")) ;
		
		for(URIPattern pattern : patterns){
			Debug.line(pattern.match("/article/123.htm"), pattern.score()) ;
		}
		
	}
}
