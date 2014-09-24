package net.ion.radon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.core.PathSegment;

import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.Types;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContextParamInjector implements ValueInjector {
	private String paramName;
	private boolean encode;
	private Class type;
	private Logger logger = Logger.getLogger(ContextParam.class.getCanonicalName()) ;

	public ContextParamInjector(Class type, Type genericType, AccessibleObject target, String paramName, String defaultValue, boolean encode, Annotation[] annotations, ResteasyProviderFactory factory) {
		this.type = type;
		this.paramName = paramName ;
		this.encode = encode;
	}

	private boolean isPathSegmentArray(Class type) {
		return type.isArray() && type.getComponentType().equals(PathSegment.class);
	}

	private boolean isPathSegmentList(Class type, Type genericType) {
		Class collectionBaseType = Types.getCollectionBaseType(type, genericType);
		return List.class.equals(type) && collectionBaseType != null && collectionBaseType.equals(PathSegment.class);
	}

	public Object inject(HttpRequest request, HttpResponse response) {
		TreeContext rcontext = (TreeContext) request.getAttribute(TreeContext.class.getCanonicalName()) ;
		if (rcontext == null) {
			throw new InternalServerErrorException("Unknown @ContextParam: " + paramName + " for request context: ");
		}
		
		if (TreeContext.class.getCanonicalName().equals(paramName)){
			return rcontext ;
		}
		
		Object result = rcontext.getAttributeObject(paramName, type) ;
		if (result != null) return result ;
		logger.warning("Unknown @ContextParam: " + paramName + " for request context: ");
		
		return null ;
	}

	public Object inject() {
		throw new RuntimeException("It is illegal to inject a @PathParam into a singleton");
	}

}
