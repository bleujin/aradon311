package net.ion.radon.core;

import static org.jboss.resteasy.util.FindAnnotation.findAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.core.ContextParameterInjector;
import org.jboss.resteasy.core.CookieParamInjector;
import org.jboss.resteasy.core.FormInjector;
import org.jboss.resteasy.core.FormParamInjector;
import org.jboss.resteasy.core.HeaderParamInjector;
import org.jboss.resteasy.core.ListFormInjector;
import org.jboss.resteasy.core.MapFormInjector;
import org.jboss.resteasy.core.MatrixParamInjector;
import org.jboss.resteasy.core.MessageBodyParameterInjector;
import org.jboss.resteasy.core.MethodInjectorImpl;
import org.jboss.resteasy.core.PathParamInjector;
import org.jboss.resteasy.core.PrefixedFormInjector;
import org.jboss.resteasy.core.PropertyInjectorImpl;
import org.jboss.resteasy.core.QueryParamInjector;
import org.jboss.resteasy.core.SuspendInjector;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.Types;

@SuppressWarnings("unchecked")
public class RadonInjectorFactory implements InjectorFactory {
	private ResteasyProviderFactory providerFactory;

	public RadonInjectorFactory(ResteasyProviderFactory factory) {
		this.providerFactory = factory;
	}

	public ConstructorInjector createConstructor(Constructor constructor) {
		return new RadonInjectorImpl(constructor, providerFactory);
	}

	public PropertyInjector createPropertyInjector(Class resourceClass) {
		return new PropertyInjectorImpl(resourceClass, providerFactory);
	}

	public MethodInjector createMethodInjector(Class root, Method method) {
		return new MethodInjectorImpl(root, method, providerFactory);
	}

	public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations) {
		return createParameterExtractor(injectTargetClass, injectTarget, type, genericType, annotations, true);
	}

	public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations, boolean useDefault) {
		DefaultValue defaultValue = findAnnotation(annotations, DefaultValue.class);
		boolean encode = findAnnotation(annotations, Encoded.class) != null || injectTarget.isAnnotationPresent(Encoded.class) || type.isAnnotationPresent(Encoded.class);
		String defaultVal = null;
		if (defaultValue != null)
			defaultVal = defaultValue.value();

		QueryParam query;
		HeaderParam header;
		MatrixParam matrix;
		PathParam uriParam;
		Form form;
		CookieParam cookie;
		FormParam formParam;
		Suspend suspend;
		ContextParam cparam ;
		
		if ((query = findAnnotation(annotations, QueryParam.class)) != null) {
			return new QueryParamInjector(type, genericType, injectTarget, query.value(), defaultVal, encode, annotations, providerFactory);
		} else if ((header = findAnnotation(annotations, HeaderParam.class)) != null) {
			return new HeaderParamInjector(type, genericType, injectTarget, header.value(), defaultVal, annotations, providerFactory);
		} else if ((formParam = findAnnotation(annotations, FormParam.class)) != null) {
			return new FormParamInjector(type, genericType, injectTarget, formParam.value(), defaultVal, encode, annotations, providerFactory);
		} else if ((cookie = findAnnotation(annotations, CookieParam.class)) != null) {
			return new CookieParamInjector(type, genericType, injectTarget, cookie.value(), defaultVal, annotations, providerFactory);
		} else if ((uriParam = findAnnotation(annotations, PathParam.class)) != null) {
			return new PathParamInjector(type, genericType, injectTarget, uriParam.value(), defaultVal, encode, annotations, providerFactory);
		} else if ((cparam = findAnnotation(annotations, ContextParam.class)) != null) {
			return new ContextParamInjector(type, genericType, injectTarget, cparam.value(), defaultVal, encode, annotations, providerFactory);
		} else if ((form = findAnnotation(annotations, Form.class)) != null) {
			String prefix = form.prefix();
			if (prefix.length() > 0) {
				if (genericType instanceof ParameterizedType) {
					ParameterizedType pType = (ParameterizedType) genericType;
					if (Types.isA(List.class, pType)) {
						return new ListFormInjector(type, Types.getArgumentType(pType, 0), prefix, providerFactory);
					}
					if (Types.isA(Map.class, pType)) {
						return new MapFormInjector(type, Types.getArgumentType(pType, 0), Types.getArgumentType(pType, 1), prefix, providerFactory);
					}
				}
				return new PrefixedFormInjector(type, prefix, providerFactory);
			}
			return new FormInjector(type, providerFactory);
		} else if ((matrix = findAnnotation(annotations, MatrixParam.class)) != null) {
			return new MatrixParamInjector(type, genericType, injectTarget, matrix.value(), defaultVal, encode, annotations, providerFactory);
		} else if ((suspend = findAnnotation(annotations, Suspend.class)) != null) {
			return new SuspendInjector(suspend, type);
		} else if (findAnnotation(annotations, Context.class) != null) {
			return new ContextParameterInjector(type, providerFactory);
		} else if (useDefault) {
			return new MessageBodyParameterInjector(injectTargetClass, injectTarget, type, genericType, annotations, providerFactory);
		} else {
			return null;
		}
	}

}
