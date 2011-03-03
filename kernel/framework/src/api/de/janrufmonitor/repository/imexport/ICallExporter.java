package de.janrufmonitor.repository.imexport;

import de.janrufmonitor.framework.ICallList;

/**
 *  This interface must be implemented by a call exporter.
 * 	A call exporter takes care about the storage of calls
 *  outside the framework, e.g. for backup reasons in a file or database.
 * 
 *@author     Thilo Brandt
 *@created    2003/10/17
 */
public interface ICallExporter extends IImExporter {

	/**
	 * Sets the call list which should be exported.
	 * 
	 * @param callList the list to be exported.
	 */
	public void setCallList(ICallList callList);
	
	/**
	 * Triggers the export to the external data source.
	 * 
	 * @return true if export was completed successfully, false if not
	 */
	public boolean doExport();
	
}
