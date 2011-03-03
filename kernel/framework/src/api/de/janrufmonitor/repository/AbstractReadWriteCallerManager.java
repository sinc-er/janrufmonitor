package de.janrufmonitor.repository;

import java.net.InetAddress;
import java.net.UnknownHostException;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.repository.filter.IFilter;
import de.janrufmonitor.repository.types.IIdentifyCallerRepository;
import de.janrufmonitor.repository.types.IReadCallerRepository;
import de.janrufmonitor.repository.types.IWriteCallerRepository;

/**
 *  This abstract class can be used as base class for a new caller manager implementation which
 *  is supporting configuration.
 *
 *@author     Thilo Brandt
 *@created    2003/11/02
 */
public abstract class AbstractReadWriteCallerManager extends AbstractConfigurableCallerManager implements IIdentifyCallerRepository, IReadCallerRepository, IWriteCallerRepository {

	public AbstractReadWriteCallerManager() {
		super();
	}

	public ICallerList getCallers(IFilter[] filters) {
		if (filters!=null && filters.length>0)
			return this.getCallers(filters[0]);
		return this.getCallers((IFilter)null);
	}

	
	/**
	 * Updates a caller with the new data. The caller to be updated has to
	 * be detremined through its UUID. In this abstract implementation
	 * the updateCaller() method calls the setCaller() method.
	 * 
	 * @param caller caller to be updated
	 */
	public void updateCaller(ICaller caller) {
		this.setCaller(caller);
	}

	public boolean isSupported(Class c) {
		return c.isInstance(this);
	}

	public void setCaller(ICallerList callerList) {
		ICaller c = null;
		for (int i=0,n=callerList.size();i<n;i++) {
			c = callerList.get(i);
			this.addCreationAttributes(c);
			this.addSystemAttributes(c);
			this.setCaller(c);	
		}
	}

	public void removeCaller(ICallerList callerList) {
		for (int i=0,n=callerList.size();i<n;i++) {
			this.removeCaller(callerList.get(i));	
		}
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
