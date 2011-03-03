package de.janrufmonitor.framework;

import java.util.Date;

/**
 *  This interface must be implemented by a Call object, which should be
 *  used in the framework.
 *
 *@author     Thilo Brandt
 *@created    2003/08/10
 */
public interface ICall extends Cloneable {

	/**
	 *  Sets a unique identifier of a call.
	 *
	 *@param  uuid  UUID of the call to be set
	 */
    public void setUUID(String uuid);

	/**
	 *  Sets a caller object of a call.
	 *
	 *@param  caller  caller of the call to be set
	 */
    public void setCaller(ICaller caller);

	/**
	 *  Sets a MSN (multiple subscriber number) object  of a call.
	 *
	 *@param  msn  MSN of the call to be set
	 */
    public void setMSN(IMsn msn);

	/**
	 *  Sets the date of a call.
	 *
	 *@param  date  date of the call to be set
	 */
    public void setDate(Date date);

	/**
	 *  Sets the CIP (common ISDN access profile) of a call.
	 *
	 *@param  cip  CIP of the call to be set
	 */
    public void setCIP(ICip cip);

	/**
	 *  Sets an attribute of a call.
	 *
	 *@param  att  attribute of the call to be set
	 */
    public void setAttribute(IAttribute att);

	/**
	 *  Sets multiple attributes with an attribute list of a call.
	 *
	 *@param  attMap  map of attributes to be set
	 */
    public void setAttributes(IAttributeMap attMap);

	/**
	 *  Gets the UUID of the call.
	 *
	 *@return       UUID of the call.
	 */
    public String getUUID();

	/**
	 *  Gets the CIP (common ISDN access profile) of the call.
	 *
	 *@return       CIP of the call.
	 */
    public ICip getCIP();

	/**
	 *  Gets the date of the call.
	 *
	 *@return       date of the call.
	 */
    public Date getDate();

	/**
	 *  Gets the caller object of the call.
	 *
	 *@return       caller of the call.
	 */
    public ICaller getCaller();

	/**
	 *  Gets the MSN (multiple subscriber number) of the call.
	 *
	 *@return       MSN of the call.
	 */
    public IMsn getMSN();

	/**
	 *  Gets a specific attribute the call.
	 *
	 *@param  name  name of the attribute
	 *@return      the requested attribute
	 */
    public IAttribute getAttribute(String name);

	/**
	 *  Gets the map with all attributes of the call.
	 *
	 *@return      attribute map of the call.
	 */
    public IAttributeMap getAttributes();

	/**
	 *  Clones this call to one-to-one copied object
	 *
	 *@return      a one-to-one copy of this call.
	 */
    public Object clone() throws CloneNotSupportedException;
}
