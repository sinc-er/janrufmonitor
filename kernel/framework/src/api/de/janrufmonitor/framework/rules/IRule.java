package de.janrufmonitor.framework.rules;

import de.janrufmonitor.framework.ICip;
import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.framework.IPhonenumber;

/**
 *  This interface must be implemented by a rule object.
 *  A rule simply consists of a service name (the service the rule is assigned to,
 *  a MSN and a CIP.
 * 
 *@author     Thilo Brandt
 *@created    2003/10/12
 *@changed	  2004/12/27
 */
public interface IRule {

	/**
	 * Sets the name of this rule.
	 * 
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * Gets the name of this rule.
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Checks if the rule is valid
	 * 
	 * @return true if it is valid, false if not.
	 */
    public boolean isValid();

	/**
	 * Sets the ID of the service the rule is assigned to.
	 * 
	 * @param serviceID the servcie ID of the assigned service
	 */
    public void setService(String serviceID);

	/**
	 * Sets the MSN object of the rule
	 * 
	 * @param msn MSN to be set
	 */
    public void setMsn(IMsn msn);

	/**
	 * Sets the CIP object of the rule
	 * 
	 * @param msn CIP to be set
	 */
    public void setCip(ICip cip);

	/**
	 * Gets the ID of the service the rule is assigned to.
	 * 
	 * @return service ID
	 */
    public String getServiceID();

	/**
	 * Gets the MSN object of the rule
	 * 
	 * @return MSN object
	 */
    public IMsn getMsn();

	/**
	 * Gets the CIP object of the rule
	 * 
	 * @return CIP object
	 */
    public ICip getCip();
    
    /**
     * Set the status of this rule
     * 
     * @param active falg to determine if rule is active
     */
    public void setActive(boolean active);
    
    /**
     * Gets the status of this rule
     * 
     * @return true if rule is active, false if not
     */
    public boolean isActive();
    
    /**
     * Sets the phonenumbers to which this rule applies to
     * 
     * @param phones phonenumbers to which  this rule applies to
     */
    public void setPhonenumbers(IPhonenumber[] phones);
    
    /**
     * Gets the phonenumbers to which this rule applies to
     * 
     * @return phonenumbers
     */
	public IPhonenumber[] getPhonenumbers();
	
	/**
	 * Gets the excluded phonenumbers to which this rule applies to
	 * 
	 * @return phonenumbers
	 */
	public IPhonenumber[] getExcludePhonenumbers();

	/**
	 * Sets the excluded phonenumbers to which this rule applies to
	 * 
	 * @param phones phonenumbers to which  this rule applies to
	 */
	public void setExcludePhonenumbers(IPhonenumber[] phones);
    
	/**
	 * Gets the timeslot this rule is valid in.
	 * 
	 * @return string containing the encoded timeslots
	 */
	public String getTimeslot();
	
	/**
	 * Sets the timeslot this rule is valid in.
	 * 
	 * @param ts encoded timeslot data
	 */
	public void setTimeslot(String ts);
	
}
