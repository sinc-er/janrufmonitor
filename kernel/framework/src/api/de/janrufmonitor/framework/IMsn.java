package de.janrufmonitor.framework;

/**
 *  This interface must be implemented by a MSN object, which should be
 *  used in the framework.
 *
 *@author     Thilo Brandt
 *@created    2003/08/10
 */
public interface IMsn {

	/**
	 *  Sets the MSN value of the cip object
	 *
	 *@param  cip  MSN to be set.
	 */
    public void setMSN(String msn);


	/**
	 *  Sets the additional information of the msn object
	 *
	 *@param  additional  additional information to be set.
	 */
    public void setAdditional(String additional);


	/**
	 *  Gets the MSN value of the msn object
	 *
	 *@return    MSN value
	 */
    public String getMSN();


	/**
	 *  Gets the additional information of the msn object
	 *
	 *@return    additional information
	 */
    public String getAdditional();

}
