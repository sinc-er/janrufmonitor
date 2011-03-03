package de.janrufmonitor.framework.manager;

/**
 *  This interface must be implemented by a Repository Manager object, which should be
 *  used in the framework. A repository manager has to handle callers or calls.
 *
 *@author     Thilo Brandt
 *@created    2003/08/24
 */
public interface IRepositoryManager extends IManager {

    
    /**
     * Gets the status of the repository manager
     * @return true, if the repository manager is active
     */
    public boolean isActive();
    
    /**
     * Returns wether a repository type (de.janrufmonitor.repository.types.*) 
     * is suppored by a repository manager implementation.
     * 
     * @param c Interface to be supported.
     * @return true is interface is supported, otherwise false.
     */
    public boolean isSupported(Class c);

}
