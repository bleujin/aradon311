package net.ion.radon.cload.cloader;

import net.ion.framework.util.Debug;


public class Main implements Runnable{

	public void run(){
		Debug.debug("hhh" + new Hello().hello() + new Hi().hi() );  
	}
}


class Hello {
	
	public String hello(){
		return new Out().hello("jin") ; 
	}
}

class Hi {
	
	public String hi(){
		return "hihi @@  " ;
	}
	
}