package de.janrufmonitor.framework.command;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.IRuntime;

/**
 * This class is an abstract implementation of a command.
 * 
 * @author Thilo Brandt
 * @created 2004/04/04
 */
public abstract class AbstractCommand implements ICommand {

	protected Logger m_logger;
	
	protected String m_language;
	protected II18nManager m_i18n;

	public AbstractCommand() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		this.m_language = 
			this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(
				IJAMConst.GLOBAL_NAMESPACE,
				IJAMConst.GLOBAL_LANGUAGE
			);
		this.m_i18n = this.getRuntime().getI18nManagerFactory().getI18nManager();
	}

	public String getLabel() {
		return this.m_i18n.getString(this.getNamespace(), "label", "label", this.m_language);
	}
	
	/**
	 * Gets the current runtime object.
	 * 
	 * @return an instance of the runtime.
	 */
	public abstract IRuntime getRuntime();
	
	/**
	 * Gets the namespace of this command implementation.
	 * 
	 * @return a valid namespace.
	 */
	public abstract String getNamespace();

	public Map getParameters() {
		return new HashMap();
	}

	public void setParameters(Map m) {

	}

}
