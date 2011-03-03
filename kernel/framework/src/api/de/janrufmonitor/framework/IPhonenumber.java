package de.janrufmonitor.framework;

/**
 *  This interface should be implemented by a phone number object. A phone number object should store 
 * a phone number in two different ways:
 * <ol><li>As an complete number representation: 072657110 
 *     <li>As splitted number representation:<br>
 *         internation area code: 49<br>
 *         area code: 7265<br>
 *         party number: 7110<br><br>
 * </ol>
 * Both representations at the same time are allowed and must be handled by the application. 
 *
 *
 *@author     Thilo Brandt
 *@created    2003/03/01
 */
public interface IPhonenumber {

    /**
     *  Sets the international area code of the phone number object
     *
     *@param  internationalAreaCode  the international area code to be set.
     */
    public void setIntAreaCode(String internationalAreaCode);


	/**
	 *  Sets the local area code of the phone number object
	 *
	 *@param  areaCode  the local area code to be set.
	 */
    public void setAreaCode(String areaCode);


    /**
     *  Sets the call number attribute of the phone number object
     *
     *@param  callNumber  the call number value to be set.
     */
    public void setCallNumber(String callNumber);


    /**
     *  Sets the complete telephone number of the phone number object
     *
     *@param  telephoneNumber  The complete telephone number to be set.
     */
    public void setTelephoneNumber(String telephoneNumber);


    /**
     *  Gets the international area code of the phone number object
     *
     *@return    the international area code
     */
    public String getIntAreaCode();


    /**
     *  Gets the local area code of the phone number object
     *
     *@return    the the local area code
     */
    public String getAreaCode();


    /**
     *  Gets the call number of the phone number object
     *
     *@return    the call number 
     */
    public String getCallNumber();


    /**
     *  Gets the complete telephone number of the phone number object
     *
     *@return    the complete telephone number
     */
    public String getTelephoneNumber();


    /**
     *  Checks if the phone number object is marked as clired (CLIR).
     *
     *@return    true if phone number object is clired, false if not.
     */
    public boolean isClired();


    /**
     *  Sets the clired status of the phone number object
     *
     *@param  isClir  true if phone number object is clired
     */
    public void setClired(boolean isClir);


}
