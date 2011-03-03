package de.janrufmonitor.repository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.IRuntime;

/**
 *  This abstract class can be used as base class for a new caller manager implementation which
 *  is supporting configuration.
 *
 *@author     Thilo Brandt
 *@created    2003/11/02
 */
public abstract class AbstractConfigurableCallerManager implements ICallerManager, IConfigurable {

	protected String CFG_PRIO = "priority";
	protected String CFG_ENABLED = "enabled";

	protected Properties m_configuration;
	protected Logger m_logger;
	protected String m_externalID; 

	private boolean isStarted;

	public AbstractConfigurableCallerManager() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
	}

	public boolean isActive() {
		return this.m_configuration.getProperty(CFG_ENABLED, "").equalsIgnoreCase("true");
	}

	public int getPriority() {
		int value = 0;
		try {
			String prio = this.m_configuration.getProperty(CFG_PRIO, "0");
			value = Integer.parseInt(prio);
		} catch (Exception ex) {
			this.m_logger.warning("Priority for manager <"+this.getID()+"> could not be read: " + ex.toString());
		}
		return value;  
	}
	
	public String getConfigurableID() {
		return this.getID();
	}

	public void setConfiguration(Properties configuration) {
		this.m_configuration = configuration;
		
		if (this.isActive() && this.getRuntime().getCallerManagerFactory().isManagerAvailable(this.getID()))
			this.restart();
	}
		
	/**
	 * Gets the runtime objects.
	 * 
	 * @return the current runtime object.
	 */
	public abstract IRuntime getRuntime();
	
	/**
	 * Gets the ID of the new caller manager. The ID is taken for registration at 
	 * caller manager factory and at the configurable notifier.
	 * 
	 * @return caller manager ID
	 */
	public abstract String getID();

	public abstract String getNamespace();

	/**
	 * Checks wether a IPhonenumber object is an internal number or not.
	 * 
	 * @param pn number to be checked
	 * @return true if number is internal, false if not
	 */
	protected boolean isInternalNumber(IPhonenumber pn) {
		if (pn==null)
			return false;
		
		if (pn.isClired())
			return false;
					
		String number = pn.getTelephoneNumber();
		
		if (number.trim().length()==0) {
			number = pn.getCallNumber();
		}

		if (number.length()<=this.maxInternalNumberLength() || pn.getIntAreaCode().equalsIgnoreCase(IJAMConst.INTERNAL_CALL)) {
			return true;
		}
		return false;
	}
	
	private int maxInternalNumberLength() {
		String value = this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_INTERNAL_LENGTH);
		if (value!=null && value.length()>0) {
			try {
				return Integer.parseInt(value);
			} catch (Exception ex) {
				this.m_logger.warning(ex.getMessage());
			}
		}
		return 0;
	}

	public void setManagerID(String id) { 
		this.m_externalID = id;
	}

	public String getManagerID() {
		return this.getID();
	}

	public void restart() {
		if (this.isStarted)
			this.shutdown();
		this.startup();
	}

	public void shutdown() { 
		this.isStarted = false;
	}

	public void startup() { 
		this.isStarted = true;
	}

	public String toString() {
		return this.getID();
	}
	
	protected void addCreationAttributes(ICaller c) {
	    String value = null;
	    if (!c.getAttributes().contains(IJAMConst.ATTRIBUTE_NAME_MACHINE_NAME)) {
	        try {
				value = InetAddress.getLocalHost().getHostName();
				c.setAttribute(
					this.getRuntime().getCallFactory().createAttribute(
							IJAMConst.ATTRIBUTE_NAME_MACHINE_NAME,
							value
						)	
					);
			} catch (UnknownHostException e) {
				this.m_logger.warning(e.getMessage());
			}
	    }

	    if (!c.getAttributes().contains(IJAMConst.ATTRIBUTE_NAME_MACHINE_IP)) {
	        try {
				value = InetAddress.getLocalHost().getHostAddress();
				c.setAttribute(
					this.getRuntime().getCallFactory().createAttribute(
							IJAMConst.ATTRIBUTE_NAME_MACHINE_IP,
							value
						)	
					);
			} catch (UnknownHostException e) {
				this.m_logger.warning(e.getMessage());
			}
	    }
		
	    if (!c.getAttributes().contains(IJAMConst.ATTRIBUTE_NAME_USER_ACCOUNT)) {
			value = System.getProperty("user.name");
			if (value!=null && value.length()>0) {
				c.setAttribute(
					this.getRuntime().getCallFactory().createAttribute(
							IJAMConst.ATTRIBUTE_NAME_USER_ACCOUNT,
							value
						)	
					);
			}
	    }
	    
	    if (!c.getAttributes().contains(IJAMConst.ATTRIBUTE_NAME_CREATION)) {
			value = Long.toString(System.currentTimeMillis());
			if (value!=null && value.length()>0) {
				c.setAttribute(
					this.getRuntime().getCallFactory().createAttribute(
							IJAMConst.ATTRIBUTE_NAME_CREATION,
							value
						)	
					);
			}
	    }
	}
	
	protected void addSystemAttributes(ICaller c) {
		IAttribute cm = this.getRuntime().getCallerFactory().createAttribute(
			IJAMConst.ATTRIBUTE_NAME_CALLERMANAGER,
			this.getID()
		);
		//c.getAttributes().remove(cm);
		c.getAttributes().add(cm);
	}

}
