package org.jboss.resteasy.core;

import javax.ws.rs.FormParam;


public class UserBean {
	@FormParam("name") private String name;
	@FormParam("pwd") private String pwd;
	@FormParam("age") private int age;

	public UserBean() {
		this.name = name ;
	}

	public String getName() {
		return name;
	}

	public String getPwd() {
		return pwd;
	}

	public int getAge() {
		return age;
	}
}
