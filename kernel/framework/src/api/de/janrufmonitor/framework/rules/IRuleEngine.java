package de.janrufmonitor.framework.rules;

import java.util.List;

import de.janrufmonitor.framework.ICip;
import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.framework.IPhonenumber;

/**
 *  This interface must be implemented by a rule engine object.
 *  A rule engine takes care about all rules in the framework. It should be
 *  singleton implementation.
 * 
 *@author     Thilo Brandt
 *@created    2003/10/12
 */
public interface IRuleEngine {

	/**
	 * Checks if a certain rule is validated
	 * 
	 * @param rule rule to be validated
	 * @return true if the rule is validated, false if not
	 */
    public boolean validate(IRule rule);

	/**
	 * Checks a service ID, MSN obejct and a CIP object are based in a rule
	 * stored in the rule engine.
	 * 
	 * @param serviceID service ID of the service to be checked
	 * @param msn MSN object
	 * @param cip CIP object
	 * @param phone Phonenumber object
	 * @return true if the rule is validated, false if not
	 */
    public boolean validate(String serviceID, IMsn msn, ICip cip, IPhonenumber phone);

	/**
	 * Checks a service ID, MSN obejct and a CIP object are based in a rule
	 * stored in the rule engine.
	 * 
	 * @param serviceID service ID of the service to be checked
	 * @param msn MSN string
	 * @param cip CIP string
	 * @param phone phonenumber object
	 * @return true if the rule is validated, false if not
	 */
	public boolean validate(String serviceID, String msn, String cip, IPhonenumber phone);

	/**
	 * Creates new rule object.
	 * 
	 * @param rule string representation of a rule
	 * @return e new rule object
	 */
	public IRule createRule(String rule);
	
	/**
	 * Creates new rule object.
	 * 
	 * @param serviceID service ID of the service to be checked
	 * @param msn MSN object
	 * @param cip CIP object
	 * @param active current status of the rule
	 * @param phones phonenumber objects
	 * @return e new rule object
	 */
	public IRule createRule(String serviceID, IMsn msn, ICip cip, boolean active, IPhonenumber[] phones);

	/**
	 * Creates new rule object.
	 * 
	 * @param serviceID service ID of the service to be checked
	 * @param msn MSN object
	 * @param cip CIP object
	 * @param active current status of the rule
	 * @param phones phonenumber objects
	 * @param exphones phonenumber objects for exclusion
	 * @return e new rule object
	 */
	public IRule createRule(String serviceID, IMsn msn, ICip cip, boolean active, IPhonenumber[] phones, IPhonenumber[] exphones);

	/**
	 * Creates new rule object.
	 * 
	 * @param serviceID service ID of the service to be checked
	 * @param msn MSN object
	 * @param cip CIP object
	 * @param active current status of the rule
	 * @param phones phonenumber objects
	 * @param exphones phonenumber objects for exclusion
	 * @param timeslot encoded timeslot data
	 * @return e new rule object
	 */
	public IRule createRule(String serviceID, IMsn msn, ICip cip, boolean active, IPhonenumber[] phones, IPhonenumber[] exphones, String timeslot);

	/**
	 * Creates new rule object.
	 * 
	 * @param serviceID service ID of the service to be checked
	 * @param msn MSN string
	 * @param cip CIP string
	 * @param active current status of the rule
	 * @param phones phonenumber objects
	 * @return e new rule object
	 */
	public IRule createRule(String serviceID, String msn, String cip, boolean active, IPhonenumber[] phones);

	/**
	 * Creates new rule object.
	 * 
	 * @param serviceID service ID of the service to be checked
	 * @param msn MSN string
	 * @param cip CIP string
	 * @param phone phonenumber object
	 * @return e new rule object
	 */
	public IRule createRule(String serviceID, String msn, String cip, IPhonenumber phone);

	/**
	 * Gets a list with all rules handled by the rule engine.
	 * 
	 * @return a list with all rules
	 */
    public List getRules();

	/**
	 * Adds a new rule to the rule engine.
	 * 
	 * @param rule rule to be added.
	 */
    public void addRule(IRule rule);

	/**
	 * Removes a rule from the rule engine.
	 * 
	 * @param rule rule to be removed.
	 */
    public void removeRule(IRule rule);
    
    /**
     * This method is called on startup time by the runtime object.
     */
    public void startup();
    
    /**
     * This method is called on shutdown time by the runtime object.
     */
    public void shutdown();

}
