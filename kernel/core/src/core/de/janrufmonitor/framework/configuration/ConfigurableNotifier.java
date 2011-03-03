package de.janrufmonitor.framework.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;

public class ConfigurableNotifier implements IConfigurableNotifier {

    private static ConfigurableNotifier m_instance = null;
    
    private Map m_NsIdMap;
    private Map m_configurables;
    private Logger m_logger;
        
    private ConfigurableNotifier() {
        this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
    }
    
    public static synchronized ConfigurableNotifier getInstance() {
        if (ConfigurableNotifier.m_instance == null) {
            ConfigurableNotifier.m_instance = new ConfigurableNotifier();
        }
        return ConfigurableNotifier.m_instance;
    }    
    
    public String[] getAllConfigurableIDs() {
		synchronized(this.m_configurables) {
			String[] ids = new String[this.m_configurables.size()];
        
			Iterator iter = this.m_configurables.keySet().iterator();
			int i = 0;
			while (iter.hasNext()) {
				ids[i] = (String)iter.next();
				i++;
			}
			this.m_logger.info("All registered configurables: "+this.m_configurables.toString());
			return ids; 
		}     
    }
    
    public String[] getAllConfigurableNamespaces() {    	
		synchronized(this.m_NsIdMap) {
        	String[] namespaces = new String[this.m_NsIdMap.size()];
        
			Iterator iter = this.m_NsIdMap.keySet().iterator();
			int i = 0;
			while (iter.hasNext()) {
				namespaces[i] = (String)iter.next();
				i++;
			}
			return namespaces; 
		}               
    }
    
    public void notifyAllConfigurables() {
		synchronized(this.m_configurables) {
			Iterator iter = this.m_configurables.keySet().iterator();
			String id = null;
			while (iter.hasNext()) {
				id = (String)iter.next();
				this.notifyConfigurable(id);
			}
		}     
    }
    
    public void notifyByNamespace(String namespace) {
    	if (namespace!=null && namespace.length()>0) {
            String id = (String)this.m_NsIdMap.get(namespace);
            this.notifyConfigurable(id);
    	}
    }
    
    public void notifyConfigurable(String id) {
		this.m_logger.entering(ConfigurableNotifier.class.getName(), "notifyConfigurable");
		IConfigurable c = this.get(id);
		if (c!=null) {
			Properties config = 
				   ConfigManagerFactory.getInstance().getConfigManager().
				   getProperties(c.getNamespace());
		   c.setConfiguration(config);
		   this.m_logger.info("Configurable <" + c.getConfigurableID() + "> notified (by configurable id).");
		} 
		this.m_logger.exiting(ConfigurableNotifier.class.getName(), "notifyConfigurable");
    }
    
    public void register(IConfigurable c) {
    	if (this.isRegistered(c.getConfigurableID())) {
			this.m_logger.warning("Configurable <"+c.getConfigurableID()+"> already registered.");
    		return;
    	}
    	this.add(c);
		IConfigManager cfgMgr = ConfigManagerFactory.getInstance().getConfigManager();
		if (cfgMgr!=null) {
			Properties config = cfgMgr.getProperties(c.getNamespace());
			c.setConfiguration(config);
			this.m_logger.info("Configurable <" + c.getConfigurableID() + "> registered.");
		} else {
			this.m_logger.severe("Configurable <" + c.getConfigurableID() + "> could not be registered. No ConfigManager available.");
		}  
    }
    
    public void unregister(IConfigurable c) {
        this.remove(c);
		this.m_logger.info("Configurable <" + c.getConfigurableID() + "> unregistered.");
    }
    
    public int getConfigurableCount() {
        return this.m_configurables.size();
    }

	public String getConfigurableNamespace(String id) {
		if (this.contains(id)) {
			IConfigurable c = (IConfigurable)this.get(id);
			if (c!=null)
				return c.getNamespace();
		}
		return "";
	}
	
	private boolean contains(String id) {
		if (this.m_configurables!=null)
			return this.m_configurables.containsKey(id);
		return false;
	}
	
	private void add(IConfigurable c) {
		synchronized(this.m_configurables) {
			if (this.m_configurables.containsKey(c.getConfigurableID()))
				this.m_logger.warning("Configurable <"+c.getConfigurableID()+"> already registered.");
				
			this.m_NsIdMap.put(c.getNamespace(), c.getConfigurableID());
			this.m_configurables.put(c.getConfigurableID(), c);
		}
	}
	
	private void remove(IConfigurable c) {
		synchronized(this.m_configurables) {
			if (!this.m_configurables.containsKey(c.getConfigurableID()))
				this.m_logger.warning("Configurable <"+c.getConfigurableID()+"> is not registered.");
				
			this.m_NsIdMap.remove(c.getNamespace());
			this.m_configurables.remove(c.getConfigurableID());
		}
	}
	
	private IConfigurable get(String id) {
		return (IConfigurable)this.m_configurables.get(id);
	}

	public void startup() {
		this.m_NsIdMap = Collections.synchronizedMap(new HashMap());
		this.m_configurables = Collections.synchronizedMap(new HashMap());
	}

	public void shutdown() {
		String[] allIds = this.getAllConfigurableIDs();
		IConfigurable c = null;
		for (int i=0;i<allIds.length;i++) {
			c = this.get(allIds[i]);
			this.unregister(c);
		}
		this.m_NsIdMap.clear();
		this.m_configurables.clear();
		m_instance = null;
	}

	public boolean isRegistered(String id) {	
		return this.contains(id);
	}
    
}
