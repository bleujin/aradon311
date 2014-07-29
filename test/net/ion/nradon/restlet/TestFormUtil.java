package net.ion.nradon.restlet;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nradon.restlet.data.CharacterSet;

public class TestFormUtil extends TestCase {
	
	public void testParse() throws Exception {
		String s = "a=한글&b=c" ;
		
		Form form = new Form(s, CharacterSet.UTF_8) ;
		
		Debug.line(form.getFirstValue("a"));
		
		
		FormUtils.parse(form, s, CharacterSet.UTF_8, true, '&');
		
		Debug.line(form.getFirstValue("a"));
		
	}

}
