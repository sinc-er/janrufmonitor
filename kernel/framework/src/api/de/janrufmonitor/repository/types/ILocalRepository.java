package de.janrufmonitor.repository.types;

/**
 * This type is used for local storage repositories. These kind of repositories store their 
 * information on the local filesystem.
 * 
 * @author brandt
 *
 */
public interface ILocalRepository {

	public String getNamespace();
	
	/**
	 * Gets the full qualified file path to the local file
	 * 
	 * @return a filepath string
	 */
	public String getFile();
	
	/**
	 * Sets the full qualified file path for the local file.
	 * 
	 * @param filename
	 */
	public void setFile(String filename);
	
	/**
	 * Gets the file type as extension, e.g. *.db
	 * 
	 * @return a valid filetype
	 */
	public String getFileType();
}
