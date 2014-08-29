package net.ion.radon.cload.cloader;

import net.ion.framework.util.Debug;

public class Main implements Runnable{

	public void run(){
		Debug.line("nnnn", new Hello().hello(), getClass().getClassLoader());  
	}
}


class Hello {
	
	public String hello(){
		return new Out().hello("jin") ; 
	}
}