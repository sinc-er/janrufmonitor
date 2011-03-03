package de.janrufmonitor.framework.monitor;

/**
 *  This interface must be implemented by a monitor object.
 * 	A monitor can observe different media types, e.g., the CAPI line or
 *  a network line. It reports the information to a monitor listener object.
 * 
 *@author     Thilo Brandt
 *@created    2003/08/24
 */
public interface IMonitor {

	/**
	 * Starts the monitor for a certain line.
	 */
    public void start();

	/**
	 * Stops the monitor for a certain line.
	 */
    public void stop();

	/**
	 * Sets the monitor listener object to report to.
	 * 
	 * @param jml the monitor listener
	 */
    public void setListener(IMonitorListener jml);

	/**
	 * Rejects a call.
	 * 
	 * @param cause the cause for rejection.
	 */
    public void reject(short cause);

	/**
	 * Releases the monitor.
	 */
    public void release();
    
    /**
     * Status of the monitor.
     * 
     * @return true if started, false if not.
     */
    public boolean isStarted();
    
    /**
     * Availability status of the monitor.
     * 
     * @return true if avaiable, false if not.
     */
    public boolean isAvailable();

	/**
	 * Get a textual description of the monitor implementation, e.g.
	 * a CAPI information object.
	 * 
	 * @return textual information
	 */
    public String[] getDescription();
    
	/**
	 * Gets a unique ID of the monitor object.
	 * 
	 * @return textual information
	 */
    public String getID();

}
