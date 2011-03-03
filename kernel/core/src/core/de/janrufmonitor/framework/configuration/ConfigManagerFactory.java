package de.janrufmonitor.framework.configuration;

import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.framework.IJAMConst;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.io.FileInputStream;
import java.io.IOException;

public class ConfigManagerFactory implements IConfigManagerFactory {
    
    private static String CLASSCONST = "configmanager.class";
    private static ConfigManagerFactory m_instance = null;
    private static IConfigManager m_configManager = null;
    private Properties m_props;
    private Logger m_logger;
    
    private ConfigManagerFactory() {
        this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);    
    }
    
    public static synchronized ConfigManagerFactory getInstance() {
        if (ConfigManagerFactory.m_instance == null) {
            ConfigManagerFactory.m_instance = new ConfigManagerFactory();
        }
        return ConfigManagerFactory.m_instance;
    }
    
    public IConfigManager getConfigManager() {
        if (m_configManager == null) {
			this.m_logger.severe("No configuration manager defined. Please check file "+IJAMConst.CONFIGURATION_BASE_FILE+" for valid configuration manager entry.");
			this.m_logger.severe("Closing application ...");
			System.exit(0);
        }
        return m_configManager;
    }
    
    private void loadBaseConfiguration() {
		this.m_logger.entering(ConfigManagerFactory.class.getName(), "loadBaseConfiguration");
        this.m_props = new Properties();
        try {
            FileInputStream fi = new FileInputStream(PathResolver.getInstance(PIMRuntime.getInstance()).getConfigDirectory() + IJAMConst.CONFIGURATION_BASE_FILE);
            this.m_props.load(fi);
            fi.close();
        } catch (IOException ex) {
            this.m_logger.severe("IOException occured: " + ex.getMessage());
        }
		this.m_logger.exiting(ConfigManagerFactory.class.getName(), "loadBaseConfiguration");
    }    

	public void shutdown() {
		this.m_logger.entering(ConfigManagerFactory.class.getName(), "shutdown");
		m_configManager.shutdown();
		m_instance = null;
		this.m_logger.exiting(ConfigManagerFactory.class.getName(), "shutdown");
	}

	public void startup() {
		this.m_logger.entering(ConfigManagerFactory.class.getName(), "startup");
		this.loadBaseConfiguration();
        
		String className = m_props.getProperty(CLASSCONST);
		try {
			Class classObject = Thread.currentThread().getContextClassLoader().loadClass(className);
			m_configManager = (IConfigManager) classObject.newInstance();
			m_configManager.startup();			
		} catch (ClassNotFoundException ex) {
			m_logger.severe("Could not find class for IConfigManager: " + className);
		} catch (InstantiationException ex) {
			m_logger.severe("Could not instantiate class for IConfigManager: " + className);
		} catch (IllegalAccessException ex) {
			m_logger.severe("Could not access class for IConfigManager: " + className);
		} catch (NullPointerException ex) {
			this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);
		}  
		this.m_logger.exiting(ConfigManagerFactory.class.getName(), "startup");
	}

}
