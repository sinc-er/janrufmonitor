package de.janrufmonitor.classloader;

import java.io.File;
import java.io.FileFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.repository.zip.ZipArchive;
import de.janrufmonitor.repository.zip.ZipArchiveException;
import de.janrufmonitor.util.io.PathResolver;


public class JamMasterClassLoader extends ClassLoader {

	private class JamJarHandler {

		private File m_jar;

		private Map m_classesCache;

		private List m_namespaces;

		public JamJarHandler(File jar) {
			this.m_jar = jar;
			this.m_classesCache = new HashMap();
			this.m_namespaces = new ArrayList();
		}

		public String toString() {
			return "JamJarHandler-" + this.m_jar.getName();
		}

		public boolean containsNamespace(String ns) {
			if (m_logger.isLoggable(Level.FINE))
				m_logger.fine("Namespaces handled: " + this.m_namespaces);
			return this.m_namespaces.contains(ns);
		}

		public void prepareNamespace() {
			ZipArchive jr = new ZipArchive(this.m_jar, false);

			try {
				if (!jr.available())
					jr.open();
				List entries = jr.list();
				String entry = null;
				for (int i=0,j=entries.size();i<j;i++) {
					entry = (String) entries.get(i);
					if (entry.length()>6)
						entry = entry.substring(0, entry.length()-6);
					
					if (classNameReplacementChar == '\u0000') {
						entry = entry.replace('/', '.');
					} else {
						// Replace '.' with custom char, such as '_'
						entry = entry.replace(classNameReplacementChar, '.');
					}
					entry = getNamespace(entry);
					if (!this.m_namespaces.contains(entry)) 
						this.m_namespaces.add(entry);
				}
			} catch (ZipArchiveException e) {
				m_logger.log(Level.SEVERE, e.toString(), e);
			} finally {
				try {
					jr.close();
				} catch (ZipArchiveException e) {
					m_logger.log(Level.SEVERE, e.toString(), e);
				}
			}
		}

		public Class loadClass(String className, boolean resolveIt)
				throws ClassNotFoundException {
			if (this.m_jar == null || !this.m_jar.exists())
				throw new ClassNotFoundException();

			Class result;
			byte[] classBytes;

			// ----- Check our local cache of classes
			result = (Class) this.m_classesCache.get(className);
			if (result != null) {
				if (m_logger.isLoggable(Level.FINE))
					m_logger.fine("Loaded class " + className
							+ " from local cache of " + this.toString());
				return result;
			}

			// ----- Try to load it from preferred source
			classBytes = loadClassBytes(className);
			if (classBytes == null) {
				if (m_logger.isLoggable(Level.FINE))
					m_logger.fine("Class " + className + " not found in "
							+ this.toString());
				throw new ClassNotFoundException();
			}

			// ----- Define it (parse the class file)
			result = defineClass(className, classBytes, 0, classBytes.length);
			if (result == null) {
				throw new ClassFormatError();
			}

			// ----- Resolve if necessary
			if (resolveIt)
				resolveClass(result);

			this.m_classesCache.put(className, result);
			if (!this.m_namespaces.contains(getNamespace(className)))
				this.m_namespaces.add(getNamespace(className));

			if (m_logger.isLoggable(Level.FINE))
				m_logger.fine("Loaded class " + className + " from JAR file "
						+ this.m_jar.getName());
			return result;
		}

		public byte[] loadClassBytes(String className) {
			// Support the JamMasterClassLoader's class name munging facility.
			className = formatClassName(className);

			if (this.m_jar == null || !this.m_jar.exists())
				return null;

			byte[] c = null;

			ZipArchive jr = new ZipArchive(this.m_jar, false);

			try {
				if (!jr.available())
					jr.open();
				c = jr.getContent(className);
				if (c != null) {
					return c;
				}
			} catch (ZipArchiveException e) {
				m_logger.log(Level.SEVERE, e.toString(), e);
			} finally {
				try {
					jr.close();
				} catch (ZipArchiveException e) {
					m_logger.log(Level.SEVERE, e.toString(), e);
				}
			}
			return null;
		}

	}

	private static JamMasterClassLoader m_instance = null;

	private Logger m_logger;

	private char classNameReplacementChar;

	private Map m_handlerMap;
	
	private JamMasterClassLoader() {
		super();
		this.m_logger = LogManager.getLogManager().getLogger(
				IJAMConst.DEFAULT_LOGGER);
		this.m_handlerMap = new HashMap();
	}

	private String getHandlerKey(File jar) {
		StringBuffer key = new StringBuffer();
		key.append(jar.getName().toLowerCase().substring(0, jar.getName().length() - 4));
		key.append("%");
		key.append(jar.lastModified());
		return key.toString();
	}

	private synchronized void buildJamJarHandler() {
		this.m_logger.info("Building classloader handlers...");
		File libDirectory = new File(PathResolver.getInstance()
				.getLibDirectory());
		if (libDirectory != null && libDirectory.exists()
				&& libDirectory.isDirectory()) {
			File[] jars = libDirectory.listFiles(new FileFilter() {

				public boolean accept(File pathname) {
					return pathname.getName().toLowerCase().endsWith(".jar");
				}
			});
			if (m_logger.isLoggable(Level.INFO))
				this.m_logger.info("Handler map: " + this.m_handlerMap);
			// check if jar is already in handler map
			if (jars != null && jars.length > 0) {
				String key = null;
				JamJarHandler jjh = null;
				for (int i = 0; i < jars.length; i++) {
					key = this.getHandlerKey(jars[i]);
					if (!this.m_handlerMap.containsKey(key)) {
						// check if an old key is contained
						Iterator iter = this.m_handlerMap.keySet().iterator();
						String oldKey = null;
						boolean cleanOldKey = false;
						while (iter.hasNext()) {
							oldKey = (String) iter.next();
							if (oldKey.startsWith(key.substring(0, key.indexOf("%")))) {
								if (m_logger.isLoggable(Level.INFO))
									this.m_logger.info("Remove from handler map: " + oldKey);
								cleanOldKey = true;
								break;
							}
						}
						if (cleanOldKey) this.m_handlerMap.remove(oldKey);
						
						if (m_logger.isLoggable(Level.INFO))
							this.m_logger
								.info("Adding classloader handler for JAR file "
										+ jars[i].getName());
						jjh = new JamJarHandler(jars[i]);
						jjh.prepareNamespace();
						
						this.m_handlerMap.put(key, jjh);
					}
					
				}
			}
			if (m_logger.isLoggable(Level.INFO))
				this.m_logger.info("Handler map: " + this.m_handlerMap);
		}
	}

	public static synchronized JamMasterClassLoader getInstance() {
		if (JamMasterClassLoader.m_instance == null) {
			JamMasterClassLoader.m_instance = new JamMasterClassLoader();
			JamMasterClassLoader.m_instance.buildJamJarHandler();
			//JamMasterClassLoader.m_instance.createJamJarHandlerRenewal();
		}
		return JamMasterClassLoader.m_instance;
	}

	public static synchronized void invalidateInstance() {
		if (JamMasterClassLoader.m_instance != null) {
			JamMasterClassLoader.m_instance.invalidate();
			JamMasterClassLoader.m_instance = null;
		}
	}

	public static synchronized void renewInstance() {
		if (JamMasterClassLoader.m_instance != null) {
			JamMasterClassLoader.m_instance.buildJamJarHandler();
		}
	}

	private void invalidate() {
		//this.m_jjhr.setState(false);
		this.m_handlerMap.clear();
	}

	public Class loadClass(String className) throws ClassNotFoundException {
		return (loadClass(className, true));
	}

	public synchronized Class loadClass(String className, boolean resolveIt)
			throws ClassNotFoundException {

		Class result = null;

		// ----- Check with the primordial class loader
		try {
			result = super.findSystemClass(className);
			if (m_logger.isLoggable(Level.FINE))
				this.m_logger.fine("Loaded class " + className
						+ " from system classloader.");
			return result;
		} catch (ClassNotFoundException e) {
			if (m_logger.isLoggable(Level.FINE))
				this.m_logger.fine("Class " + className
						+ " not found in system classloader.");
		}

		Iterator iter = this.m_handlerMap.entrySet().iterator();
		Map.Entry entry = null;
		while (iter.hasNext() && result == null) {
			entry = (Map.Entry) iter.next();
			try {
				if (((JamJarHandler) entry.getValue())
						.containsNamespace(getNamespace(className)))
					return ((JamJarHandler) entry.getValue()).loadClass(
							className, resolveIt);
			} catch (ClassNotFoundException e) {
				// nothing to do...
			}
		}

		// no matching namespace found
		iter = this.m_handlerMap.entrySet().iterator();
		entry = null;
		while (iter.hasNext() && result == null) {
			entry = (Map.Entry) iter.next();
			try {
				return ((JamJarHandler) entry.getValue()).loadClass(className,
						resolveIt);
			} catch (ClassNotFoundException e) {
				// nothing to do...
			}

		}

		throw new ClassNotFoundException();
	}

	public void setClassNameReplacementChar(char replacement) {
		classNameReplacementChar = replacement;
	}

	private String formatClassName(String className) {
		if (classNameReplacementChar == '\u0000') {
			// '/' is used to map the package to the path
			return className.replace('.', '/') + ".class";
		} else {
			// Replace '.' with custom char, such as '_'
			return className.replace('.', classNameReplacementChar) + ".class";
		}
	}

	private String getNamespace(String className) {
		if (className.indexOf(".") > -1)
			return className.substring(0, className.lastIndexOf("."));
		return className;
	}

}
