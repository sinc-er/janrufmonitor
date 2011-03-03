package de.janrufmonitor.framework;

import java.util.List;


/**
 *  This interface must be implemented by a Caller object, which should be
 *  used in the framework. The IMultiPhoneCaller interface must support multiple 
 *  IPhonenumber objects per ICaller instance.
 *
 *@author     Thilo Brandt
 *@created    2007/11/20
 */
public interface IMultiPhoneCaller extends ICaller {

	/**
	 *  Adds a new phonenumber to this caller instance
	 *
	 *@param  n IPhonenumber object
	 */
	public void addPhonenumber(IPhonenumber n);
	
	/**
	 *  Sets a list of new phonenumber to this caller instance
	 *
	 *@param  pns List of IPhonenumber objects
	 */
	public void setPhonenumbers(List pns);
	
	/**
	 *  Returns the list with all IPhonenumber objects of this caller.
	 *
	 *@return    List of IPhonenumber objects.
	 */
	public List getPhonenumbers();
		
}
