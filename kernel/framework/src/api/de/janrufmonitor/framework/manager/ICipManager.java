package de.janrufmonitor.framework.manager;

import de.janrufmonitor.framework.ICip;

/**
 *  This interface must be implemented by a CipManager object, which should be
 *  used in the framework. A CipManager must handle CIP information for calls.
 *
 *@author     Thilo Brandt
 *@created    2003/08/24
 */
public interface ICipManager extends IManager {

	/**
	 * Gets the CIP label for the given Cip object in a specific language.
	 * @param cip the Cip object to label
	 * @param language the requested language
	 * @return a language dependend String
	 */
    public String getCipLabel(ICip cip, String language);

	/**
	 * Gets the CIP label for the given Cip String in a specific language.
	 * @param cip the Cip String to label
	 * @param language the requested language
	 * @return a language dependend String
	 */
    public String getCipLabel(String cip, String language);

	/**
	 * Creates a new Cip object for the given Cip String.
	 * @param cip the Cip string to create
	 * @return an ICip object for the given Cip String
	 */
    public ICip createCip(String cip);

	/**
	 * Gets a list with all Cip Strings supported by the Cip manager.
	 * @return Array of Cip Strings
	 */
    public String[] getCipList();
}
