package de.janrufmonitor.repository;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.PIMRuntime;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Iterator;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class CallManagerFactory implements ICallManagerFactory, IConfigurable {

    private String ID = "CallManagerFactory";
    private String NAMESPACE = "repository.CallManagerFactory";
    private String PARAMETERNAME = "manager_";
	private String DEFAULT_PARAMETERNAME = "default";
    private static CallManagerFactory m_instance = null;
    
    private Logger m_logger;
    //Vector m_callManagerList;
    private Map m_callManagers;
	private String defaultManager;
	private Properties m_configuration;
    
    private CallManagerFactory() {
        this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
    }
    
    public static synchronized CallManagerFactory getInstance() {
        if (CallManagerFactory.m_instance == null) {
            CallManagerFactory.m_instance = new CallManagerFactory();
        }
        return CallManagerFactory.m_instance;
    }
    
    public String[] getAllCallManagerIDs() {
		String[] list = new String[this.m_callManagers.size()];
    	
		synchronized(this.m_callManagers) {
			Iterator iter = this.m_callManagers.keySet().iterator();
			int i=0;
			String key = null;
			while(iter.hasNext()) {
				key = (String) iter.next();
				list[i] = key;
				i++;
			}
		}
		return list;
    }
    
    public List getAllCallManagers() {
		List list = new ArrayList();
    	
		synchronized(this.m_callManagers) {
			Iterator iter = this.m_callManagers.keySet().iterator();
			String key = null;
			ICallManager mgr = null;
			while(iter.hasNext()) {
				key = (String) iter.next();
				mgr = (ICallManager)this.m_callManagers.get(key);
				if (mgr!=null) {
					list.add(mgr);
				}
			}
		}
		Collections.sort(list, new RepositoryManagerComparator());
		return list;
    }
    
    public List getTypedCallManagers(Class type) {
		List list = new ArrayList();
    	
		synchronized(this.m_callManagers) {
			Iterator iter = this.m_callManagers.keySet().iterator();
			String key = null;
			ICallManager mgr = null;
			while(iter.hasNext()) {
				key = (String) iter.next();
				mgr = (ICallManager)this.m_callManagers.get(key);
				if (mgr!=null && mgr.isSupported(type)) {
					list.add(mgr);
				}
			}
		}
		Collections.sort(list, new RepositoryManagerComparator());
		return list;
    }
    
    public ICallManager getCallManager(String id) {
		if (id.equalsIgnoreCase(""))
			id = this.defaultManager;
    	
		if (this.m_callManagers!=null && this.m_callManagers.containsKey(id)) {
			return (ICallManager)this.m_callManagers.get(id);
		}
		this.m_logger.warning("no CallManager found for ID: " + id);  
		return null;
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

	public String[] getCallManagerIDs(Class type) {
		List list = new ArrayList();
    	
		synchronized(this.m_callManagers) {
			Iterator iter = this.m_callManagers.keySet().iterator();
			String key = null;
			ICallManager mgr = null;
			while(iter.hasNext()) {
				key = (String) iter.next();
				mgr = (ICallManager)this.m_callManagers.get(key);
				if (mgr!=null && mgr.isSupported(type)) {
					list.add(mgr.getManagerID());
				}
			}
		}
		
		this.m_logger.info("Call manager IDs with type <"+type+">: " + list);  
		
		String[] managerIDs = new String[list.size()];
		for (int i=0,n=list.size();i<n;i++)
			managerIDs[i]=(String) list.get(i);
        
		return managerIDs;
	}

	public ICallManager getDefaultCallManager() {
		return this.getCallManager(this.defaultManager);
	}

	public void startup() {
		this.m_logger.entering(CallManagerFactory.class.getName(), "startup");
		PIMRuntime.getInstance().getConfigurableNotifier().register(this);  
		
		this.m_callManagers = Collections.synchronizedMap(new HashMap());
        
		Iterator iter = this.m_configuration.keySet().iterator();
		String key = null;
		while (iter.hasNext()) {
			key = (String) iter.next();
			if (key.startsWith(PARAMETERNAME)) {
				String className = this.m_configuration.getProperty(key);
				try {
					Class classObject = Thread.currentThread().getContextClassLoader().loadClass(className);
					ICallManager cpm = null;
					try {
						Constructor cons = classObject.getConstructor(new Class[] {String.class});
						cpm = (ICallManager) cons.newInstance(new Object[] {key.substring(PARAMETERNAME.length())});
					} catch (SecurityException e) {
					} catch (NoSuchMethodException e) {
					} catch (IllegalArgumentException e) {
						this.m_logger.log(Level.SEVERE, e.getMessage(), e);
					} catch (InvocationTargetException e) {
						this.m_logger.log(Level.SEVERE, e.getMessage(), e);
					}
					if (cpm==null)
						cpm = (ICallManager) classObject.newInstance();
					
					//cpm.startup();
					cpm.setManagerID(key.substring(PARAMETERNAME.length()));
					this.m_callManagers.put(cpm.getManagerID(), cpm);
					this.m_logger.info("Registered new call manager <" + cpm.getManagerID()+">.");
				} catch (ClassNotFoundException ex) {
					this.m_logger.warning("Could not find class: " + className);
				} catch (InstantiationException ex) {
					this.m_logger.severe("Could not instantiate class: " + className);
				} catch (IllegalAccessException ex) {
					this.m_logger.severe("Could not access class: " + className);
				} catch (NoClassDefFoundError ex) {
					this.m_logger.warning("Could not find class definition: " + className);
				}
			}
		}
		
		String[] managers = this.getAllCallManagerIDs();
		for (int i = 0; i < managers.length; i++) {
			this.startManager(managers[i]);
		}
		
		defaultManager = this.m_configuration.getProperty(DEFAULT_PARAMETERNAME, "");
		this.m_logger.info("Set default call manager to <" + defaultManager +">.");
		this.m_logger.exiting(CallManagerFactory.class.getName(), "startup");
	}

	private void startManager(String managerID) {
		this.m_logger.entering(CallManagerFactory.class.getName(), "startManager");

		ICallManager man = this.getCallManager(managerID);
		if (man==null) {
			this.m_logger.severe("CallManager <" + managerID + "> has null reference.");
			this.m_logger.exiting(CallManagerFactory.class.getName(), "startManager");
			return;
		}
		
		try {
			if (man.isActive()) {
				man.startup();
			}
		} catch (Exception ex) {
			this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);		
		}

		this.m_logger.exiting(CallManagerFactory.class.getName(), "startManager");

	}

	public void shutdown() {
		this.m_logger.entering(CallManagerFactory.class.getName(), "shutdown");
		List mgrs = this.getAllCallManagers();
		ICallManager mgr = null;
		for (int i=0;i<mgrs.size();i++) {
			mgr = (ICallManager) mgrs.get(i);
			mgr.shutdown();
			if (mgr instanceof IConfigurable)
				PIMRuntime.getInstance().getConfigurableNotifier().unregister((IConfigurable) mgr);	
		}
		
		this.m_callManagers.clear();

		PIMRuntime.getInstance().getConfigurableNotifier().unregister(this);
		m_instance = null;
		this.m_logger.exiting(CallManagerFactory.class.getName(), "shutdown");
	}

	public boolean isManagerAvailable(String id) {
		return this.m_callManagers.containsKey(id);
	}
    
}
