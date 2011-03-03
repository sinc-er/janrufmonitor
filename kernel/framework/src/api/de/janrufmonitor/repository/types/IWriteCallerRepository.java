package de.janrufmonitor.repository.types;

import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;

/**
 * This type is used for repositories which allow write access to their caller information.
 * 
 * @author brandt
 *
 */
public interface IWriteCallerRepository {
	
	/**
	 * Sets a caller object to a repository.
	 * 
	 * @param caller caller to be set
	 */
    public void setCaller(ICaller caller);

	/**
	 * Sets a list of caller objects to a repository.
	 * 
	 * @param callerList list of caller objects
	 */
    public void setCaller(ICallerList callerList);

	/**
	 * Updates a caller with the new data. The caller to be updated has to
	 * be detremined through its UUID.
	 * 
	 * @param caller caller to be updated
	 */
    public void updateCaller(ICaller caller);

	/**
	 * Removes a certain caller from a repository.
	 * 
	 * @param caller caller to be removed
	 */
    public void removeCaller(ICaller caller);

	/**
	 * Removes a list of caller objects from a repository.
	 * 
	 * @param callerList list of callers to be removed.
	 */
    public void removeCaller(ICallerList callerList);
}
