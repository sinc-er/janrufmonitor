package de.janrufmonitor.application.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.runtime.launcher.ILauncher;
import de.janrufmonitor.util.io.PathResolver;

public class Win32ServiceLauncher implements ILauncher, IConfigurable {

	private String ID = "Win32ServiceLauncher";
	private String NAMESPACE = "application.Win32ServiceLauncher";

	Logger m_logger;
	Properties m_configuration;
	
	private String CFG_FILENAME ="filename";
	
	public Win32ServiceLauncher() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		PIMRuntime.getInstance().getConfigurableNotifier().register(this);
	}

	public void addLibrary(String lib) {
		try{
			String servicefile = PathResolver.getInstance(PIMRuntime.getInstance()).getConfigDirectory()+ this.getFileName();
			Properties ntserviceProps = new Properties();
			Map allLibs = new HashMap();
			File f = new File(servicefile);
			if (f.exists()) {
				FileInputStream fis = new FileInputStream(servicefile);
				ntserviceProps.load(fis);
				fis.close();
				
				Iterator iter = ntserviceProps.keySet().iterator();
				while (iter.hasNext()) {
					String name = (String) iter.next();
					if (name.startsWith("wrapper.java.classpath")) {
						String libo = ntserviceProps.getProperty(name, "");
						iter.remove();
						if (libo.startsWith("-")) {
							allLibs.remove(libo);
							allLibs.remove(libo.substring(1));
						} else{
							allLibs.put(libo, libo);
						}
					}
				}
				
				allLibs.put(lib,lib);
				this.m_logger.info("adding library: "+lib);
			
				int i = 0;
				iter = allLibs.keySet().iterator();
				while (iter.hasNext()) {
					i++;
					ntserviceProps.setProperty("wrapper.java.classpath."+i, (String)allLibs.get(iter.next()));	
				}

				FileOutputStream fo = new FileOutputStream(servicefile);
				ntserviceProps.store(fo, "");
				this.m_logger.info("changed properties file: "+servicefile);
				fo.close();
			}			
		} catch (IOException e) {
			this.m_logger.severe(e.getMessage());
		}
	}

	public void removeLibrary(String lib) {
		try {
			String servicefile = PathResolver.getInstance(PIMRuntime.getInstance()).getConfigDirectory() + this.getFileName();
			Properties ntserviceProps = new Properties();
			Map allLibs = new HashMap();
			File f = new File(servicefile);
			if (f.exists()) {
				FileInputStream fis = new FileInputStream(servicefile);
				ntserviceProps.load(fis);
				fis.close();
				
				Iterator iter = ntserviceProps.keySet().iterator();
				while (iter.hasNext()) {
					String name = (String) iter.next();
					if (name.startsWith("wrapper.java.classpath")) {
						String libo = ntserviceProps.getProperty(name, "");
						iter.remove();
						if (libo.startsWith("-")) {
							allLibs.remove(libo);
							allLibs.remove(libo.substring(1));
						} else{
							allLibs.put(libo, libo);
						}
					}
				}
				
				allLibs.remove(lib);
				this.m_logger.info("removing library: "+lib);
			
				int i = 0;
				iter = allLibs.keySet().iterator();
				while (iter.hasNext()) {
					i++;
					ntserviceProps.setProperty("wrapper.java.classpath."+i, (String)allLibs.get(iter.next()));	
				}

				FileOutputStream fo = new FileOutputStream(servicefile);
				ntserviceProps.store(fo, "");
				this.m_logger.info("changed properties file: "+servicefile);
				fo.close();
			}
		} catch (IOException e) {
			this.m_logger.severe(e.getMessage());
		}
	}

	public String getID() {
		return this.ID;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public String getConfigurableID() {
		return this.getID();
	}

	public void setConfiguration(Properties configuration) {
		this.m_configuration = configuration;
	}
	
	private String getFileName() {
		return (this.m_configuration.getProperty(this.CFG_FILENAME, "ntservice.properties"));
	}

}
