package net.ion.radon.core.let.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "movie")
public class Product {

	private String name;
	private int qty;
 
	public Product(String name, int qty){
		this.name = name ;
		this.qty = qty ;
	}
	
	@XmlElement
	public String getName() {
		return name;
	}
 
	public void setName(String name) {
		this.name = name;
	}
 
	@XmlAttribute
	public int getQty() {
		return qty;
	}
 
	public void setQty(int qty) {
		this.qty = qty;
	}
}
