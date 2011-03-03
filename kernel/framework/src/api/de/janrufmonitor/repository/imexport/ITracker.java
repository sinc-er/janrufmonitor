package de.janrufmonitor.repository.imexport;

/**
 *  This interface allows to track the status of an import or export filter.
 * 
 *@author     Thilo Brandt
 *@created    2008/04/24
 */
public interface ITracker {

	public int getCurrent();
	
	public int getTotal();
	
}
