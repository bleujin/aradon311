package net.ion.radon.cload.cloader;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ion.radon.cload.listeners.ReloadNotificationListener;

import org.codehaus.commons.compiler.AbstractJavaSourceClassLoader;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.ErrorHandler;
import org.codehaus.commons.compiler.ICookable;
import org.codehaus.commons.compiler.WarningHandler;
import org.codehaus.janino.ClassLoaderIClassLoader;
import org.codehaus.janino.Descriptor;
import org.codehaus.janino.JaninoRuntimeException;
import org.codehaus.janino.JavaSourceIClassLoader;
import org.codehaus.janino.UnitCompiler;
import org.codehaus.janino.util.ClassFile;
import org.codehaus.janino.util.resource.DirectoryResourceFinder;
import org.codehaus.janino.util.resource.PathResourceFinder;
import org.codehaus.janino.util.resource.ResourceFinder;

public class ReloadSourceClassLoader extends AbstractJavaSourceClassLoader implements ReloadNotificationListener {

	private final JavaSourceIClassLoader innerLoader;
	private boolean debugSource = Boolean.getBoolean(ICookable.SYSTEM_PROPERTY_SOURCE_DEBUGGING_ENABLE);
	private boolean debugLines = this.debugSource;
	private boolean debugVars = this.debugSource;
	private final Map<String, byte[]> precompiledClasses = new HashMap(); // name, bytecode

	/** Collection of parsed, but still uncompiled compilation units. */
	private final Set<UnitCompiler> unitCompilers = new HashSet();

	public ReloadSourceClassLoader(ClassLoader parentClassLoader, File[] optionalSourcePath, String optionalCharacterEncoding) {
		this(parentClassLoader, (optionalSourcePath == null ? (ResourceFinder) new DirectoryResourceFinder(new File(".")) : (ResourceFinder) new PathResourceFinder(optionalSourcePath)), optionalCharacterEncoding); // optionalCharacterEncoding
	}

	public ReloadSourceClassLoader(ClassLoader parentClassLoader, ResourceFinder sourceFinder, String optionalCharacterEncoding) {
		super(parentClassLoader);
		this.innerLoader = new JavaSourceIClassLoader(sourceFinder, // sourceFinder
				optionalCharacterEncoding, // optionalCharacterEncoding
				this.unitCompilers, // unitCompilers
				new ClassLoaderIClassLoader(parentClassLoader) // optionalParentIClassLoader
		);
	}

	@Override
	public void setSourcePath(File[] sourcePath) {
		this.innerLoader.setSourceFinder(new PathResourceFinder(sourcePath));
	}

	@Override
	public void setSourceFileCharacterEncoding(String optionalCharacterEncoding) {
		this.innerLoader.setCharacterEncoding(optionalCharacterEncoding);
	}

	@Override
	public void setDebuggingInfo(boolean debugSource, boolean debugLines, boolean debugVars) {
		this.debugSource = debugSource;
		this.debugLines = debugLines;
		this.debugVars = debugVars;
	}

	public void setCompileErrorHandler(ErrorHandler optionalCompileErrorHandler) {
		this.innerLoader.setCompileErrorHandler(optionalCompileErrorHandler);
	}

	public void setWarningHandler(WarningHandler optionalWarningHandler) {
		this.innerLoader.setWarningHandler(optionalWarningHandler);
	}

	@Override
	public Class findClass(String name) throws ClassNotFoundException { /* No need to synchronize, because 'loadClass()' is synchronized */

		// Check if the bytecode for that class was generated already.
		byte[] bytecode = (byte[]) this.precompiledClasses.remove(name);
		if (bytecode == null) {

			// Read, scan, parse and compile the right compilation unit.
			{
				Map<String, byte[]> bytecodes = this.generateBytecodes(name);
				if (bytecodes == null)
					throw new ClassNotFoundException(name);
				this.precompiledClasses.putAll(bytecodes);
			}

			// Now the bytecode for our class should be available.
			bytecode = (byte[]) this.precompiledClasses.remove(name);
			if (bytecode == null) {
				throw new JaninoRuntimeException("SNO: Scanning, parsing and compiling class \"" + name + "\" did not create a class file!?");
			}
		}

		return this.defineBytecode(name, bytecode);
	}

	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		// First, check if the class has already been loaded
		Class c = findLoadedClass(name);
		if (c == null) {
			ClassLoader parent = name.startsWith("net.ion.radon") ? getParent().getParent() : getParent();
			try {
				c = parent.loadClass(name);
			} catch (ClassNotFoundException e) {
				// ClassNotFoundException thrown if class not found
				// from the non-null parent class loader
			}
			if (c == null) {
				// If still not found, then invoke findClass in order
				// to find the class.
				c = findClass(name);
			}
		}
		if (resolve) {
			resolveClass(c);
		}
		return c;
	}

	protected Map<String, byte[]> generateBytecodes(String name) throws ClassNotFoundException {
		if (this.innerLoader.loadIClass(Descriptor.fromClassName(name)) == null)
			return null;

		Map<String, byte[]> bytecodes = new HashMap();
		Set<UnitCompiler> compiledUnitCompilers = new HashSet();
		COMPILE_UNITS: for (;;) {
			for (UnitCompiler uc : this.unitCompilers) {
				if (!compiledUnitCompilers.contains(uc)) {
					ClassFile[] cfs;
					try {
						cfs = uc.compileUnit(this.debugSource, this.debugLines, this.debugVars);
					} catch (CompileException ex) {
						throw new ClassNotFoundException(ex.getMessage(), ex);
					}
					for (ClassFile cf : cfs)
						bytecodes.put(cf.getThisClassName(), cf.toByteArray());
					compiledUnitCompilers.add(uc);
					continue COMPILE_UNITS;
				}
			}
			return bytecodes;
		}
	}

	private Class defineBytecode(String className, byte[] ba) {
		return this.defineClass(className, ba, 0, ba.length, (this.optionalProtectionDomainFactory == null ? null : this.optionalProtectionDomainFactory.getProtectionDomain(ClassFile.getSourceResourceName(className))));
	}

	@Override
	public void handleNotification() {
		precompiledClasses.clear();
	}

}
