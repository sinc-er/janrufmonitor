package de.janrufmonitor.framework.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.IJAMConst;

/**
 *  This abstract class can be used as base class for a new monitor listener component.
 *	It delivers default implementations for the CAPI event methods.
 *
 *@author     Thilo Brandt
 *@created    2003/12/11
 */
public abstract class AbstractMonitorListener implements IMonitorListener {

	private String ID = "AbstractMonitorListener";

	protected Logger m_logger; 
	protected List m_monitors;

	public AbstractMonitorListener() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		this.m_monitors = new ArrayList(1);
	}

	public void doCallConnect(ICall call) {
		this.m_logger.info("Method doCallConnect() not implemented.");
	}

	public void doCallDisconnect(ICall call) {
		this.m_logger.info("Method doCallDisconnect() not implemented.");
	}
	
	public String[] getCapiInformation() {
		return new String[] {
			this.ID
		};
	}
	
	public IMonitor getMonitor(String id) {
		IMonitor m = null;
		for (int i=0;i<this.m_monitors.size();i++) {
			m = (IMonitor) this.m_monitors.get(i);
			if (m.getID().equalsIgnoreCase(id)) return m;
		}
		return null;
	}
	
	public List getMonitors() {
		return this.m_monitors;
	}

	abstract public boolean isEnabled();
	
	abstract public boolean isRunning();
	
	abstract public IMonitor getDefaultMonitor();
	
	/**
	 * Gets a unique ID of this listener
	 *
	 * @return a unique ID
	 */
	abstract public String getID();

	public void shutdown() { }

	public void startup() { }

}
