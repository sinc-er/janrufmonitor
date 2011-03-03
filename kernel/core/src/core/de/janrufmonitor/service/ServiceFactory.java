package de.janrufmonitor.service;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.PIMRuntime;

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

public class ServiceFactory implements IServiceFactory, IConfigurable {
    
    private String ID = "ServiceFactory";
    private String NAMESPACE = "service.ServiceFactory";
    private static String PARAMETERNAME = "service_";
    private static ServiceFactory m_instance = null;

    private Map m_services;
    private Logger m_logger;
    private Properties m_configuration;
    
    private ServiceFactory() {
        this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
    }
    
    public static synchronized ServiceFactory getInstance() {
        if (ServiceFactory.m_instance == null) {
            ServiceFactory.m_instance = new ServiceFactory();
        }
        return ServiceFactory.m_instance;
    }
    
    public String[] getAllServiceIDs() {
		String[] list = new String[this.m_services.size()];
    	
		synchronized(this.m_services) {
			Iterator iter = this.m_services.keySet().iterator();
			int i=0;
			while(iter.hasNext()) {
				String key = (String) iter.next();
				list[i] = key;
				i++;
			}
		}
		return list;
    }
    
    public List getAllServices() {
		List list = new ArrayList();
    	
		synchronized(this.m_services) {
			Iterator iter = this.m_services.keySet().iterator();
			while(iter.hasNext()) {
				String key = (String) iter.next();
				IService service = (IService)this.m_services.get(key);
				if (service!=null) {
					list.add(service);
				}
			}
		}
		Collections.sort(list, new ServiceComparator());        
		return list;
    }
    
    public IService getService(String id) {
		if (this.m_services.containsKey(id)) {
			return (IService)this.m_services.get(id);
		}
		return null;
    }
    
    public void shutdown() {
		this.m_logger.entering(ServiceFactory.class.getName(), "shutdown");
		List services = this.getAllServices();
		
		for (int i = 0; i < services.size(); i++) {
			IService is = (IService) services.get(i);
			try {
				if (is.isRunning()) {
					is.shutdown();
					this.m_logger.info("Shutdown service: " + is.getServiceID());
				} else {
					this.m_logger.info("Service " + is.getServiceID()+" could not be shut down because it is not running.");
				}
				if (is instanceof IConfigurable)
					PIMRuntime.getInstance().getConfigurableNotifier().unregister((IConfigurable) is);
			} catch (Exception ex) {
				this.m_logger.severe("Service shutdown <"+is+">: " + ex.toString() + ": "+ex.getMessage());
				this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
		PIMRuntime.getInstance().getConfigurableNotifier().unregister(this);
		m_instance = null;
		this.m_logger.exiting(ServiceFactory.class.getName(), "shutdown");
    }
    
    public void startup() {
		this.m_logger.entering(ServiceFactory.class.getName(), "startup");
		PIMRuntime.getInstance().getConfigurableNotifier().register(this);

		this.m_services = Collections.synchronizedMap(new HashMap(this.m_configuration.size()));
		Iterator iter = this.m_configuration.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			// check for .class attribute in properties file
			if (key.startsWith(PARAMETERNAME)) {
				String className = this.m_configuration.getProperty(key);
				try {
					Class classObject = Thread.currentThread().getContextClassLoader().loadClass(className);
					// add a service to service list
					IService is = (IService) classObject.newInstance();
					this.m_services.put(is.getServiceID(), is);
					this.m_logger.info("Registered new service <" + is.getServiceID()+">.");
				} catch (ClassNotFoundException ex) {
					this.m_logger.warning("Could not find class: " + className);
				} catch (InstantiationException ex) {
					this.m_logger.severe("Could not instantiate class: " + className);
				} catch (IllegalAccessException ex) {
					this.m_logger.severe("Could not access class: " + className);
				} catch (Exception e) {
					this.m_logger.log(Level.SEVERE, e.getMessage(), e);
				} catch (NoClassDefFoundError ex) {
					this.m_logger.warning("Could not find class definition: " + className);
				}
			}
		}

		List services = this.getAllServices();
		
        for (int i = 0; i < services.size(); i++) {
            IService is = (IService) services.get(i);
            this.startService(is);
        }
		this.m_logger.exiting(ServiceFactory.class.getName(), "startup");       
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

	public boolean isServiceAvailable(String id) {
		return this.m_services.containsKey(id);
	}
	
	public boolean isServiceEnabled(String id) {
		if (this.isServiceAvailable(id)) {
			IService service = this.getService(id);
			if (service!=null)
				return service.isEnabled();
		}
		return false;
	}
	
	private boolean startService(IService service) {
		if (service==null) {
			this.m_logger.severe("Service reference is null. Service not startable.");
			return false;
		}
		
		if (service.isRunning()) {
			this.m_logger.info("Service <" + service.getServiceID() + "> is already running.");
			return true;
		}
		
		if (!service.isEnabled()) {
			this.m_logger.info("Service <" + service.getServiceID() + "> is not enabled.");
			return false;
		}
		
		try {
			List dependency = service.getDependencyServices();
			boolean isDependencyStarted = true;
			if (dependency!=null && dependency.size()>0) {
				this.m_logger.info("Found dependency list for service <"+service.getServiceID()+">: "+dependency.toString());
				for (int i=0;i<dependency.size();i++){
					String did = (String)dependency.get(i);
					IService dservice = this.getService(did);
					if (dservice!=null) {
						this.m_logger.info("Try to start dependency service <"+did+">.");
						isDependencyStarted &= this.startService(dservice);
					} else {
						this.m_logger.severe("Dependency service <"+did+"> not available for startup.");
					}
				}
			}
			if (isDependencyStarted) {
				service.startup();
			} else {
				this.m_logger.severe("Service <"+service.getServiceID()+"> not started. A dependency service is not available.");
				return false;
			}
		} catch (Exception e) {
			this.m_logger.severe("Service <" + service.getServiceID() + "> startup error: "+e.toString() + ": "+e.getMessage());
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
		return true;
	}

	public void startService(String id) {
		this.m_logger.entering(ServiceFactory.class.getName(), "startService");
		IService srv = this.getService(id);
		if (srv==null)
			this.m_logger.severe("Service <" + id + "> has null reference.");

		this.startService(srv);
		this.m_logger.exiting(ServiceFactory.class.getName(), "startService");
	}

	public void stopService(String id) {
		this.m_logger.entering(ServiceFactory.class.getName(), "stopService");
		IService srv = this.getService(id);
		if (srv!=null) {
			if (!srv.isRunning()) {
				this.m_logger.info("Service <" + id + "> is not running.");
				return;
			}
			try {
				srv.shutdown();
			} catch (Exception ex) {
				this.m_logger.severe("Service <" + id + "> shutdown error: "+ex.toString() + ": "+ex.getMessage());
				this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
		this.m_logger.exiting(ServiceFactory.class.getName(), "stopService");
	}

	public void restartService(String id) {
		this.m_logger.entering(ServiceFactory.class.getName(), "restartService");
		IService srv = this.getService(id);
		if (srv!=null) {
			if (!srv.isEnabled()) {
				this.m_logger.info("Service <" + id + "> is not enabled.");
				return;
			}
			srv.restart();
		}
		this.m_logger.exiting(ServiceFactory.class.getName(), "restartService");
	}

	public List getModifierServices() {
		List list = new ArrayList();
    	
		synchronized(this.m_services) {
			Iterator iter = this.m_services.keySet().iterator();
			while(iter.hasNext()) {
				String key = (String) iter.next();
				IService service = (IService)this.m_services.get(key);
				if (service!=null && service instanceof IModifierService) {
					list.add(service);
				}
			}
		}
		Collections.sort(list, new ServiceComparator());        
		return list;
	}
    
}
