package de.janrufmonitor.framework;

/**
 *  This interface must be implemented by a Caller object, which should be
 *  used in the framework.
 *
 *@author     Thilo Brandt
 *@created    2003/08/10
 */
public interface ICaller extends Cloneable {

	/**
	 *  Sets a unique identifier of a caller.
	 *
	 *@param  uuid  UUID of the caller to be set
	 */
    public void setUUID(String uuid);

	/**
	 *  Sets a phone number object of a caller.
	 *
	 *@param  phonenumber  phone number object of the caller to be set
	 */
    public void setPhoneNumber(IPhonenumber phonenumber);

	/**
	 *  Sets a name object of a caller.
	 *
	 *@param  name  name object of the caller to be set
	 */
    public void setName(IName name);

	/**
	 *  Sets a attribute of a caller.
	 *
	 *@param  att  attribute of the caller to be set
	 */
    public void setAttribute(IAttribute att);

	/**
	 *  Sets a attribute map of a caller.
	 *
	 *@param  attMap  attribute map of the caller to be set
	 */
    public void setAttributes(IAttributeMap attMap);

	/**
	 *  Gets the UUID of the caller.
	 *
	 *@return       UUID of the caller.
	 */
    public String getUUID();

	/**
	 *  Gets the phone number of the caller.
	 *
	 *@return       phone number of the caller.
	 */
    public IPhonenumber getPhoneNumber();

	/**
	 *  Gets the name object of the caller.
	 *
	 *@return       name object of the caller.
	 */
    public IName getName();

	/**
	 *  Gets a specific attribute of the caller.
	 *
	 *@param  attName  name of the attribute
	 *@return       attribute of the caller.
	 */
    public IAttribute getAttribute(String attName);

	/**
	 *  Gets the attribute map with all attributes of the caller.
	 *
	 *@return       attribute map of the caller.
	 */
    public IAttributeMap getAttributes();

	/**
	 *  Clones this caller to one-to-one copied object
	 *
	 *@return      a one-to-one copy of this caller.
	 */
    public Object clone() throws CloneNotSupportedException;

}
