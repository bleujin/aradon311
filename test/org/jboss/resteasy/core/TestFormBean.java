package org.jboss.resteasy.core;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.nradon.stub.StubHttpResponse;
import net.ion.radon.client.StubServer;

import org.jboss.resteasy.annotations.Form;

@Path("/form")
public class TestFormBean extends TestCase {

	
	@POST
	public String registerUser(@Form UserBean user){
		

		Debug.line(user.getName(), user.getPwd(), user.getAge());
		
		
		return "register" ;
	}
	
	
	public void testCreateFormBean() throws Exception {
		
		StubServer ss = StubServer.create(getClass()) ;
		StubHttpResponse response = ss.request("/form").postParam("name", "bleujin").postParam("pwd", "1234").postParam("age", "20").post() ;
	}
	
}

