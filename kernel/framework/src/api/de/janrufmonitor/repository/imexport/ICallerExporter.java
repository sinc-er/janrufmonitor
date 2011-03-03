package de.janrufmonitor.repository.imexport;

import de.janrufmonitor.framework.ICallerList;

/**
 *  This interface must be implemented by a caller exporter.
 * 	A caller exporter takes care about the storage of callers
 *  outside the framework, e.g. for backup reasons in a file or database.
 * 
 *@author     Thilo Brandt
 *@created    2003/10/17
 */
public interface ICallerExporter extends IImExporter {

	/**
	 * Sets the caller list which should be exported.
	 * 
	 * @param callerList the list to be exported.
	 */
	public void setCallerList(ICallerList callerList);
	
	/**
	 * Triggers the export to the external data source.
	 * 
	 * @return true if export was completed successfully, false if not
	 */
	public boolean doExport();
	
}
