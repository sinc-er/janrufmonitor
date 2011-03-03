package de.janrufmonitor.framework;

/**
 *  This interface must be implemented by a name object, which should be
 *  used in the framework.
 *
 *@author     Thilo Brandt
 *@created    2003/08/10
 */
public interface IName {

	/**
	 *  Gets the firstname value of the name object
	 *
	 *@return    firstname value
	 */
    public String getFirstname();


	/**
	 *  Gets the lastname value of the name object
	 *
	 *@return    lastname value
	 */
    public String getLastname();


	/**
	 *  Gets the additional information of the name object
	 *
	 *@return    additional information
	 */
    public String getAdditional();


	/**
	 *  Gets the fullname value of the name object
	 *
	 *@return    fullname value
	 */
    public String getFullname();


	/**
	 *  Sets the firstname of the name object
	 *
	 *@param  firstname  firstname to be set.
	 */
    public void setFirstname(String firstname);


	/**
	 *  Sets the lastname of the name object
	 *
	 *@param  lastname  lastname to be set.
	 */
    public void setLastname(String lastname);


	/**
	 *  Sets the additional information of the name object
	 *
	 *@param  additional  additional information to be set.
	 */
    public void setAdditional(String additional);

}
