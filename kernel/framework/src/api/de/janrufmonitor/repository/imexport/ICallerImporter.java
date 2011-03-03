package de.janrufmonitor.repository.imexport;

import de.janrufmonitor.framework.ICallerList;

/**
 *  This interface must be implemented by a caller importer.
 * 	A caller importer takes care about the storage of callers
 *  from outside the framework, e.g. for restore reasons from a file or database.
 * 
 *@author     Thilo Brandt
 *@created    2003/10/17
 */
public interface ICallerImporter extends IImExporter {
	
	/**
	 * Triggers the import of external callers.
	 * 
	 * @return a list of valid caller objects.
	 */
	public ICallerList doImport();
	
}
