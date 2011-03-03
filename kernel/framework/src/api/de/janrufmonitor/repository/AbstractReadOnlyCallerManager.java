package de.janrufmonitor.repository;

import de.janrufmonitor.repository.types.IIdentifyCallerRepository;

/**
 *  This abstract class can be used as base class for a new call manager implementation
 * using read only access to the caller persistence.
 *
 *@author     Thilo Brandt
 *@created    2004/09/11
 */
public abstract class AbstractReadOnlyCallerManager
	extends AbstractConfigurableCallerManager implements IIdentifyCallerRepository {

	public AbstractReadOnlyCallerManager() {
		super();
	}

	public boolean isSupported(Class c) {
		return c.isInstance(this);
	}
	
}
