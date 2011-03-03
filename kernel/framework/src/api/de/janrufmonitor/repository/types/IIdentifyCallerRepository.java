package de.janrufmonitor.repository.types;

import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.repository.CallerNotFoundException;

/**
 * This type is used for identification repositories.
 * 
 * @author brandt
 *
 */
public interface IIdentifyCallerRepository {

	/**
	 * Gets a caller object from a repository by the specified phone number object.
	 *  
	 * @param number number of the requested caller object.
	 * @return caller object from a repository
	 * @throws CallerNotFoundException is thrown if a caller with specified phone number is not found.
	 */
    public ICaller getCaller(IPhonenumber number) throws CallerNotFoundException;

	
}
