package org.jboss.resteasy.plugins.server.resourcefactory;

import java.lang.reflect.Constructor;

import net.ion.framework.cloader.PathClassLoader;

import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.util.PickConstructor;

public class ReloadPOJOResourceFactory implements ResourceFactory {
	private Class<?> clzName;
	private ConstructorInjector constructorInjector;
	private PropertyInjector propertyInjector;

	public ReloadPOJOResourceFactory(Class<?> scannableClass) {
		this.clzName = scannableClass;
	}

	public void registered(InjectorFactory factory) {
	}

	public Object createResource(HttpRequest request, HttpResponse response, InjectorFactory factory) {
//		Constructor constructor = PickConstructor.pickPerRequestConstructor(clzName);
		try {
			ClassLoader cloader = PathClassLoader.createClassLoader(getClass().getClassLoader().getParent()) ;
			clzName = cloader.loadClass(clzName.getCanonicalName()) ;
			Constructor constructor = PickConstructor.pickPerRequestConstructor(clzName);
			
			if (constructor == null) {
				throw new RuntimeException("Unable to find a public constructor for class " + clzName.getName());
			}
			this.constructorInjector = factory.createConstructor(constructor);
			this.propertyInjector = factory.createPropertyInjector(clzName);
			
			
			Object obj = constructorInjector.construct(request, response);
			propertyInjector.inject(request, response, obj);
			return obj;
		} catch(Exception ex){
			throw new IllegalArgumentException(ex) ;
		}
	}

	public void unregistered() {
	}

	public Class<?> getScannableClass() {
		try {
//			ClassLoader cloader = PathClassLoader.createClassLoader(getClass().getClassLoader().getParent()) ;
//			return cloader.loadClass(clzName.getCanonicalName()) ;
			
			return clzName;
		} catch (Exception e) {
			throw new IllegalStateException(e) ;
		}
	}

	public void requestFinished(HttpRequest request, HttpResponse response, Object resource) {
	}
}
