package net.ion.radon.core.config;

import java.util.List;
import java.util.Map;

import net.ion.nradon.filter.XRadonFilter;
import net.ion.radon.core.IService;

public abstract class LetConfiguration<T> {

	private final Map<String, AttributeValue> attributes;
	
	private final List<XRadonFilter> filters ;

	private IService attachedService ;
	protected LetConfiguration(Map<String, AttributeValue> attributes, List<XRadonFilter> filters){
		this.attributes = attributes ;
		this.filters = filters ;
	}
	
	public void initFilter(IService service) {
		this.attachedService = service ;
		for(XRadonFilter filter : filters){
			filter.init(service) ;
		}
	}
	
	public Map<String, AttributeValue> attributes(){
		return attributes ;
	}

	public T addFilter(XRadonFilter filter) {
		filter.init(attachedService) ;
		filters.add(filter) ;
		return (T) this ;
	}

	
	public List<XRadonFilter> filters(){
		return filters ;
	}

}
