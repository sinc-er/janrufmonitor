package de.janrufmonitor.runtime.launcher;

/**
 * This interface can be implemented by a launcher component which 
 * will be registered at the LauncherFactory. A launcher can add or remove java
 * depended information, e.g. jar file to a launcher control file (Batch file or shell script).
 * 
 *@author     Thilo Brandt
 *@created    2004/02/02
 */
public interface ILauncher {

	/**
	 * Adds the given library to the launchers control file.
	 * 
	 * @param lib library name to be added
	 */
	public void addLibrary(String lib);
	
	/**
	 * Removes the given library name from the launchers control file.
	 * 
	 * @param lib library name to be removed
	 */
	public void removeLibrary(String lib);
	
	/**
	 * Gets a unique ID of the launcher.
	 * 
	 * @return unique ID
	 */
	public String getID();
	
}
