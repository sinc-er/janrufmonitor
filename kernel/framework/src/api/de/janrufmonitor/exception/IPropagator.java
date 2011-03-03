package de.janrufmonitor.exception;

/**
 *  This interface must be implemented by a propagator object
 *  which would be notified for exception handling.
 *
 *@author     Thilo Brandt
 *@created    2004/11/20
 */
public interface IPropagator {
	
	/**
	 * Propagates a Message object to a propagator
	 * 
	 * @param m
	 */
	public void propagate(Message m);
	
	/**
	 * Returns the ID of the propagator
	 * 
	 * @return
	 */
	public String getID();
}
