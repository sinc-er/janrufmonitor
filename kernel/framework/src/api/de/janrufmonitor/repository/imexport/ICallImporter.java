package de.janrufmonitor.repository.imexport;

import de.janrufmonitor.framework.ICallList;

/**
 *  This interface must be implemented by a call importer.
 * 	A call importer takes care about the storage of calls
 *  from outside the framework, e.g. for restore reasons from a file or database.
 * 
 *@author     Thilo Brandt
 *@created    2003/10/17
 */
public interface ICallImporter extends IImExporter {
		
	/**
	 * Triggers the import of external calls.
	 * 
	 * @return a list of valid call objects.
	 */
	public ICallList doImport();
	
}
