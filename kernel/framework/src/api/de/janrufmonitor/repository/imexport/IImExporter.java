package de.janrufmonitor.repository.imexport;

/**
 *  This base interface must be implemented by all importer
 *  and exporter instances.
 * 
 *@author     Thilo Brandt
 *@created    2003/10/17
 */
public interface IImExporter {
	
	/**
	 * Constant for call mode
	 */
	public final int CALL_MODE = 1;
	
	/**
	 * Constant for caller mode
	 */
	public final int CALLER_MODE = 2;
	
	/**
	 * Constant for export type
	 */
	public final int EXPORT_TYPE = 1;
	
	/**
	 * Constant for import type
	 */
	public final int IMPORT_TYPE = 2;
 
 	/**
 	 * Gets the ID of the importer/exporter
 	 * @return a valid ID
 	 */
	public String getID();

	/**
	 * Gets the mode of the importer/exporter
	 * 
	 * @return a valid mode value
	 */
	public int getMode();
	
	/**
	 * Gets the type of the importer/exporter
	 * 
	 * @return a valid type value
	 */
	public int getType();
	
	/**
	 * Gets the name of the importer/exporter
	 * 
	 * @return a filter name
	 */
	public String getFilterName();
	
	/**
	 * Gets the extension of the importer/exporter
	 * 
	 * @return a file extension
	 */
	public String getExtension();
	
	/**
	 * Sets the filename od the external data source
	 * 
	 * @param filename a valid filename
	 */
	public void setFilename(String filename);
	
			
}
