package de.janrufmonitor.framework;

/**
 *  This interface must be implemented by a Attribute object, which should be
 *  used in the framework.
 *
 *@author     Thilo Brandt
 *@created    2003/08/10
 */
public interface IAttribute {

	/**
	 *  Sets the name of an attribute.
	 *
	 *@param  name  name of the attribute to be set
	 */
    public void setName(String name);

	/**
	 *  Sets the value of an attribute.
	 *
	 *@param  value  value of the attribute to be set
	 */
    public void setValue(String value);

	/**
	 *  Gets the name of the attribute.
	 *
	 *@return       name of the attribute
	 */
    public String getName();

	/**
	 *  Gets the value of the attribute.
	 *
	 *@return       value of the attribute
	 */
    public String getValue();

}
