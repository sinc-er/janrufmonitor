package de.janrufmonitor.repository;

import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.IRuntime;

/**
 *  This abstract class can be used as base class for a new call manager implementation which
 *  is supporting configuration.
 *
 *@author     Thilo Brandt
 *@created    2006/05/27
 */
public abstract class AbstractConfigurableCallManager implements ICallManager, IConfigurable {

	protected String CFG_PRIO = "priority";
	protected String CFG_ENABLED = "enabled";
	protected Logger m_logger;

	protected String m_externalID; 
	protected Properties m_configuration;
	private boolean isStarted;
	
	public AbstractConfigurableCallManager() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
	}
	
	/**
	 * Gets the ID of the new call manager. The ID is taken for registration at 
	 * call manager factory and at the configurable notifier.
	 * 
	 * @return call manager ID
	 */
	public abstract String getID();
	
	/**
	 * Gets the runtime objects.
	 * 
	 * @return the current runtime object.
	 */
	public abstract IRuntime getRuntime();

	public boolean isActive() {
		return this.m_configuration.getProperty(CFG_ENABLED, "").equalsIgnoreCase("true");
	}

	public int getPriority() {
		String prio = this.m_configuration.getProperty(CFG_PRIO, "0");
		int value = 0;
		try {
			value = new Integer(prio).intValue();
		} catch (Exception ex) {
			this.m_logger.warning("priority could not be read: " + ex.getMessage());
		}
		return value;  
	}
	
	public void setConfiguration(Properties configuration) {
		this.m_configuration = configuration;
		
		if (this.isActive() && this.getRuntime().getCallManagerFactory().isManagerAvailable(this.getID()))
			this.restart();
	}
	
	public String getConfigurableID() {
		return this.getID();
	}
	
	public void setManagerID(String id) { 
		this.m_externalID = id;
	}

	public String getManagerID() {
		return this.getID();
	}

	public void shutdown() {
		this.isStarted = false;
	}

	public void startup() { 
		this.isStarted = true;
	}
	
	public void restart() { 
		if (this.isStarted)
			this.shutdown();
			
		this.startup();
	}
	
	public String toString() {
		return this.getID();
	}
	
}
