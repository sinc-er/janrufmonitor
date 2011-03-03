package de.janrufmonitor.runtime;

import de.janrufmonitor.framework.command.ICommandFactory;
import de.janrufmonitor.framework.ICallFactory;
import de.janrufmonitor.framework.ICallerFactory;
import de.janrufmonitor.framework.configuration.IConfigManagerFactory;
import de.janrufmonitor.framework.configuration.IConfigurableNotifier;
import de.janrufmonitor.framework.event.IEventBroker;
import de.janrufmonitor.framework.i18n.II18nManagerFactory;
import de.janrufmonitor.framework.manager.ICipManager;
import de.janrufmonitor.framework.manager.IMsnManager;
import de.janrufmonitor.framework.monitor.IMonitorListener;
import de.janrufmonitor.framework.rules.IRuleEngine;
import de.janrufmonitor.repository.ICallManagerFactory;
import de.janrufmonitor.repository.ICallerManagerFactory;
import de.janrufmonitor.service.IServiceFactory;

/**
 *  This interface can be implemented by any runtime instance. It is also used for accessing
 *  the default implementation PIMRuntime.<br><br>
 *  Example:<br>
 *  <code>IRuntime runtime = PIMRuntime.getInstance();</code><br><br>
 *  The runtime instance should be a singleton and acts as a central
 *  entry point for all jAnrufmonitor developments.<br><br>
 *  With the runtime instance you can access, handle and manipulate all objects
 *  which are handled by the jAnrufmonitor framework.
 * 
 *@author     Thilo Brandt
 *@created    2003/10/17
 */
public interface IRuntime {

	/**
	 * Gets the configurable notifier implementation
	 * 
	 * @return the configurable notifier implementation.
	 */
    public IConfigurableNotifier getConfigurableNotifier();

	/**
	 * Gets the config manager implementation.
	 * 
	 * @return the config manager implementation
	 */
    public IConfigManagerFactory getConfigManagerFactory();

	/**
	 * Gets the call manager factory implementation.
	 * 
	 * @return the call manager factory implementation.
	 */
    public ICallManagerFactory getCallManagerFactory();

	/**
	 * Gets the caller manager factory implementation.
	 * 
	 * @return the caller manager factory implementation.
	 */
    public ICallerManagerFactory getCallerManagerFactory();

	/**
	 * Gets the event broker implementation.
	 * 
	 * @return the event broker implementation.
	 */
    public IEventBroker getEventBroker();

	/**
	 * Gets the call factory implementation.
	 * 
	 * @return the call factory implementation.
	 */
    public ICallFactory getCallFactory();

	/**
	 * Gets the caller factory implementation. 
	 * 
	 * @return the caller factory implementation.
	 */
    public ICallerFactory getCallerFactory();

	/**
	 * Gets the service factory implementation.
	 * 
	 * @return the service factory implementation.
	 */
    public IServiceFactory getServiceFactory();

	/**
	 * Gets the msn manager implementation.
	 * 
	 * @return the msn manager implementation.
	 */
    public IMsnManager getMsnManager();

	/**
	 * Gets the cip manager implementation.
	 * 
	 * @return the cip manager implementation.
	 */
    public ICipManager getCipManager();

	/**
	 * Gets the monitor listener implementation.
	 * 
	 * @return the monitor listener implementation.
	 */
    public IMonitorListener getMonitorListener();

	/**
	 * Gets the command factory implementation.
	 * 
	 * @return the command factory implementation.
	 */
	public ICommandFactory getCommandFactory();

	/**
	 * Gets the i18n manager factory implementation.
	 * 
	 * @return the i18n manager factory implementation.
	 */
    public II18nManagerFactory getI18nManagerFactory();

	/**
	 * Gets the rule engine implementation.
	 * 
	 * @return the rule engine implementation.
	 */
    public IRuleEngine getRuleEngine();

	/**
	 * Starts up a runtime instance.
	 */
    public void startup();

	/**
	 * Shuts down a runtime instance.
	 */
    public void shutdown();

	/**
	 * Cleans up a runtime instance.
	 */
    public void cleanUp();

    /**
     * Explicitly enable/disable the monitoring component
     * @param enable
     */
    public void enableMonitorListener(boolean enable);
}
