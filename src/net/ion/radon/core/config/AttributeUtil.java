package net.ion.radon.core.config;

import java.util.List;
import java.util.Map;

import net.ion.framework.util.Debug;
import net.ion.framework.util.InstanceCreationException;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.EnumClass.Scope;
import net.ion.radon.core.IService;
import net.ion.radon.core.TreeContext;

import org.apache.commons.configuration.ConfigurationException;

public class AttributeUtil {

	public static void load(IService service, XMLConfig contextConfig) throws InstanceCreationException, ConfigurationException {
		//Debug.line(context.getZone(), parentConfig);
		
		
		if (! contextConfig.getTagName().equals("context")) return ;
		
		// XMLConfig contextConfig = parentConfig.firstChild("context") ;

		final String name = StringUtil.toString(contextConfig.getAttributeValue("name"), "aradon");
		
		
		setStringAttribute(service, contextConfig);
		setObjectAttribute(service, contextConfig) ;
	}
	
	private static void setObjectAttribute(IService service, XMLConfig contextConfig) throws InstanceCreationException {
		TreeContext context = service.getServiceContext() ;
		List<XMLConfig> configs =  contextConfig.children("configured-object") ;
		for (XMLConfig config : configs) {
//			Object created = ConfigCreator.createConfiguredInstance(objOfConfig) ;
			String id = config.getAttributeValue("id");
			Scope scope = Scope.valueOf(StringUtil.capitalize(config.getAttributeValue("scope")));
			ReferencedObject refObj = ReferencedObject.create(context, id, scope, config) ;

			context.putAttribute(id, refObj) ;
		}
	}


	private static void setStringAttribute(IService service, XMLConfig config) {
		TreeContext context = service.getServiceContext() ;
		List<XMLConfig> children = config.children("attribute") ;
		for (XMLConfig child : children) {
			final String id = child.getAttributeValue("id");
			// final String type = child.getString("[@type]"); 
			if (StringUtil.isBlank(id)) {
				Debug.warn("not found attribute id : blank id") ;
				continue ;
			}
			if (context.contains(id)) {
				Debug.warn("duplicate attribute id : " + id + " ignored") ;
				continue ;
			}
			
			
			Object attrValue = ObjectUtil.coalesce(child.getElementValue(), ObjectUtil.NULL) ;
			context.putAttribute(id, attrValue) ;
		}
	}


	public static Attribute create(Map<String, String> attrMap, String elementValue) {
		return new Attribute(attrMap, elementValue);
	}

}
