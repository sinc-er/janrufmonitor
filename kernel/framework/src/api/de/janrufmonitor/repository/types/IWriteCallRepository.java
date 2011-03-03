package de.janrufmonitor.repository.types;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICallList;

/**
 * This type is used for repositories which allow write access to their call information.
 * 
 * @author brandt
 *
 */
public interface IWriteCallRepository {

	/**
	 * Sets a call object to a repository.
	 * 
	 * @param call call to be set
	 */
    public void setCall(ICall call);

	/**
	 * Sets a list of call objects to a repository.
	 * 
	 * @param list call list to be set
	 */
	public void setCalls(ICallList list);

	/**
	 * Updates a call with the new data. The call to be updated has to
	 * be detremined through its UUID.
	 * 
	 * @param call call to be updated
	 */
    public void updateCall(ICall call);

	/**
	 * Updates a call list with the new data. The calls to be updated have to
	 * be detremined through its UUID.
	 * 
	 * @param list call list to be updated
	 */
	public void updateCalls(ICallList list);


	/**
	 * Removes a certain call from a repository.
	 * 
	 * @param call call to be removed
	 */
    public void removeCall(ICall call);

	/**
	 * Removes a list of calls objects from a repository.
	 * 
	 * @param callList list of calls to be removed.
	 */
    public void removeCalls(ICallList callList);
    
}
