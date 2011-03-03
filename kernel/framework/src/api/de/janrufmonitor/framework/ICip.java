package de.janrufmonitor.framework;

/**
 *  This interface must be implemented by a CIP object, which should be
 *  used in the framework.
 *
 *@author     Thilo Brandt
 *@created    2003/08/10
 */
public interface ICip {

    /**
     *  Sets the CIP value of the cip object
     *
     *@param  cip  CIP to be set.
     */
    public void setCIP(String cip);


    /**
     *  Sets the additional information of the cip object
     *
     *@param  additional  additional information to be set.
     */
    public void setAdditional(String additional);


    /**
     *  Gets the CIP value of the cip object
     *
     *@return    CIP value
     */
    public String getCIP();


    /**
     *  Gets the additional information of the cip object
     *
     *@return    additional information
     */
    public String getAdditional();

}
