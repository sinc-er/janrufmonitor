package de.janrufmonitor.framework.manager;

import de.janrufmonitor.framework.IMsn;

/**
 *  This interface must be implemented by a MsnManager object, which should be
 *  used in the framework. A MsnManager must handle MSN information for calls.
 *
 *@author     Thilo Brandt
 *@created    2003/08/24
 */
public interface IMsnManager extends IManager {

	/**
	 * Gets the MSN label for the given Msn object
	 * @param msn the Msn object to label
	 * @return the MSN label as String
	 */
    public String getMsnLabel(IMsn msn);

	/**
	 * Gets the MSN label for the given Msn String.
	 * @param msn the Msn String to label
	 * @return the MSN label as String
	 */
    public String getMsnLabel(String msn);

	/**
	 * Creates a new Msn object for the given Msn String.
	 * @param msn the Msn string to create
	 * @return an IMsn object for the given Msn String
	 */
    public IMsn createMsn(String msn);

	/**
	 * Gets a list with all Msn Strings supported by the Msn manager.
	 * @return Array of Msn Strings
	 */
    public String[] getMsnList();
    
 	/**
 	 * Checks wether a Msn object exists.
 	 * @param msn the Msn object to validate
 	 * @return true if Msn object exists, fals if not
 	 */
 	public boolean existMsn(IMsn msn);

 	/**
 	 * Checks wether a Msn object is monitored.
 	 * 
 	 * @param msn the Msn object to validate
 	 * @return true is Msn object is monitored or all Msns are monitored.
 	 */
 	public boolean isMsnMonitored(IMsn msn);
 	
}
