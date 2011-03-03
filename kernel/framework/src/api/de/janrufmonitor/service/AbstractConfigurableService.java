package de.janrufmonitor.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.IRuntime;

/**
 *  This abstract class can be used as base class for a new service implementation which
 *  is supporting configuration.
 *
 *@author     Thilo Brandt
 *@created    2003/10/17
 */
public abstract class AbstractConfigurableService implements IService, IConfigurable {

	protected String CFG_ENABLED = "enabled";
	protected String CFG_PRIORITY = "priority";
	
	protected Logger m_logger;
	protected Properties m_configuration;
	
	private boolean isStarted;
	
	/**
	 * Default constructor. Creates a new instance and
	 * initializes the logging capabilities.
	 */
	public AbstractConfigurableService() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
	}

	public String getServiceID() {
		return this.getID();
	}

	public String getConfigurableID() {
		return this.getID();
	}
	
	public boolean isEnabled() {
		return (this.m_configuration.getProperty(this.CFG_ENABLED, "false").equalsIgnoreCase("true") ? true : false);
	}

	public void setEnabled(boolean enabled) {
		if (enabled) {
			this.m_configuration.setProperty(this.CFG_ENABLED, "true");
		} else {
			this.m_configuration.setProperty(this.CFG_ENABLED, "false");
		}
	}

	public int getPriority() {
		String prio = this.m_configuration.getProperty(this.CFG_PRIORITY, "0");
		int priority = 999;
		try {
			priority = Integer.parseInt(prio);
		} catch (Exception ex) {
			this.m_logger.severe("Invalid priority property value. No integer value found.");
			return 999;
		}
		return priority;  
	}

	public void setConfiguration(Properties configuration) {
		this.m_configuration = configuration;
		
		// only restart if service is fully loaded by ServiceFactory
		if (this.getRuntime().getServiceFactory().isServiceAvailable(this.getID()))
			this.restart();
	}

	public void restart() {
		if (this.isRunning())
			this.getRuntime().getServiceFactory().stopService(this.getID());

		this.getRuntime().getServiceFactory().startService(this.getID());
	}

	public void startup() {
		this.isStarted = true;
	}

	public void shutdown() {
		this.isStarted = false;
	}
	
	public boolean isRunning() {
		return this.isStarted;
	}
	
	public List getDependencyServices() {
		return new ArrayList(1);
	}
	
	public abstract String getNamespace();
	
	/**
	 * Gets the ID of the new service. The ID is taken for registration at 
	 * service factory and at the configurable notifier.
	 * 
	 * @return service ID
	 */
	public abstract String getID();
	

	/**
	 * Gets the runtime objects.
	 * 
	 * @return the current runtime object.
	 */
	public abstract IRuntime getRuntime();

	public String toString() {
		return this.getID();
	}
}
