package de.janrufmonitor.framework.i18n;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.PIMRuntime;

import java.util.Properties;
import java.util.Iterator;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class I18nManagerFactory implements II18nManagerFactory, IConfigurable {
    
    private String ID = "I18nManagerFactory";
    private String NAMESPACE = "i18n.I18nManagerFactory";
    private String PARAMETERNAME = "manager";
    private static I18nManagerFactory m_instance = null;
    
    Logger m_logger;    
    Properties m_configuration;
    II18nManager m_i18nManager;
    
    private I18nManagerFactory() {
        this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
    }
    
    public static synchronized I18nManagerFactory getInstance() {
        if (I18nManagerFactory.m_instance == null) {
            I18nManagerFactory.m_instance = new I18nManagerFactory();
        }
        return I18nManagerFactory.m_instance;
    }
    
    public II18nManager getI18nManager() {
        if (this.m_i18nManager==null){
            this.m_logger.severe("I18nManager is not available. Please check configuration.");
        }
        return this.m_i18nManager;
    }
    
    public String getConfigurableID() {
        return this.ID;
    }
    
    public String getNamespace() {
        return this.NAMESPACE;
    }
    
    public void setConfiguration(Properties configuration) {
        this.m_configuration = configuration;
    }

	public void startup() {
		PIMRuntime.getInstance().getConfigurableNotifier().register(this);
		
		Iterator iter = this.m_configuration.keySet().iterator();
		String key = null; 
		while (iter.hasNext()) {
			 key = (String) iter.next();

			 if (key.startsWith(PARAMETERNAME)) {
				 String className = this.m_configuration.getProperty(key);
				 try {
					 Class classObject = Thread.currentThread().getContextClassLoader().loadClass(className);
					 this.m_i18nManager = (II18nManager) classObject.newInstance();
				 } catch (ClassNotFoundException ex) {
					 this.m_logger.severe("Could not find class: " + className);
				 } catch (InstantiationException ex) {
					 this.m_logger.severe("Could not instantiate class: " + className);
				 } catch (IllegalAccessException ex) {
					 this.m_logger.severe("Could not access class: " + className);
				 }
			 }
		 }

		this.m_i18nManager.startup();
	}

	public void shutdown() {
		this.m_i18nManager.shutdown();
		PIMRuntime.getInstance().getConfigurableNotifier().unregister(this);
		m_instance = null;
	}
    
}
