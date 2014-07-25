package net.ion.radon.core;

import java.io.Closeable;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

import net.ion.framework.util.IOUtil;
import net.ion.framework.util.InstanceCreationException;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.radon.core.config.AttributeUtil;
import net.ion.radon.core.config.AttributeValue;
import net.ion.radon.core.config.XMLConfig;

import org.apache.commons.configuration.ConfigurationException;

public class TreeContext {

	private TreeContext parent;
	private Map<String, Object> attrs = MapUtil.newMap();
	private Logger logger;

	private TreeContext(TreeContext parent) {
		this.parent = parent;
		this.logger = Logger.getLogger(getClass().getCanonicalName()) ;
	}

	public final static TreeContext createRootContext() {
		return new TreeContext(null);
	}

	public TreeContext createChildContext() {
		TreeContext newChild = new TreeContext(this);
		return newChild;
	}

	public TreeContext getParentContext() {
		return parent;
	}

	public Object getAttributeObject(String key) {
		return getAttributeObject(key, Object.class);
	}

	public <T> T getAttributeObject(String key, Class<T> T) {
		return getAttributeObject(key, null, T);
	}

	public <T> T getAttributeObject(String key, T defaultValue, Class<T> T) {
		TreeContext current = this;
		while (current != null) {
			Object value = current.getSelfAttributeObject(key, Object.class);
			if (value != null && T.isInstance(value)) {
				return (T) value;
			}
			current = current.getParentContext();
		}
		return defaultValue;
	}

	public <T> T getSelfAttributeObject(String key, Class<T> T) {
		return getSelfAttributeObject(key, T, null);
	}

	public <T> T getSelfAttributeObject(String key, Class<T> T, T defaultValue) {
		try {
			Object value = attrs.get(key);
			if (value != null && value instanceof AttributeValue) {
				return (T) ((AttributeValue) value).get(this);
			}
			return (T.isInstance(value)) ? (T) value : defaultValue;
		} catch (InstanceCreationException ex) {
			ex.printStackTrace();
			throw new IllegalStateException(ex);
		}
	}

	public boolean removeAttribute(String key) {
		return attrs.remove(key) != null;
	}

	public Object putAttribute(String key, Object value) {
		return attrs.put(key, value);
	}

	public Map getAttributes() {
		return Collections.unmodifiableMap(attrs);
	}

	public boolean contains(Object key) {
		return getAttributes().get(key) != null;
	}

	public void setAttributes(Map attributes) {
		attrs.putAll(attributes);
	}

	public void loadAttribute(IService service, XMLConfig config) throws ConfigurationException, InstanceCreationException {
		AttributeUtil.load(service, config);
	}

	public void closeAttribute() {
		for (Object key : getAttributes().keySet()) {
			Object value = getAttributeObject(ObjectUtil.toString(key));
			if (value instanceof Closeable) {
				IOUtil.closeQuietly((Closeable) value);
			}
		}
	}

	private static final ThreadLocal<TreeContext> CURRENT = new ThreadLocal<TreeContext>();
	public static TreeContext getCurrent() {
		return CURRENT.get();
	}

	public static Logger getCurrentLogger() {
		return (TreeContext.getCurrent() != null) ? TreeContext.getCurrent().getLogger() : Logger.getLogger("net.ion.aradon");
	}

	private Logger getLogger() {
		return logger;
	}

	public static void setCurrent(TreeContext context) {
		CURRENT.set(context);
	}
}
