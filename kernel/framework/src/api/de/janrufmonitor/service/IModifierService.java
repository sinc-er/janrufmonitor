package de.janrufmonitor.service;

/**
 *  This interface must be implemented by a modifier service object, which should be
 *  used in the framework. Modifier services could change or modify JAM objects like 
 *  ICaller or ICall
 *
 *@author     Thilo Brandt
 *@created    2010/04/28
 */
public interface IModifierService extends IService {

	/**
	 * Modifies the specified object, e.g. a ICall or ICaller
	 * 
	 */
    public void modifyObject(Object o);



}
