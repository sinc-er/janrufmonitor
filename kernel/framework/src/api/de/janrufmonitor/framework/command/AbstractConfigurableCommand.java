package de.janrufmonitor.framework.command;

import java.util.Properties;

import de.janrufmonitor.framework.configuration.IConfigurable;

/**
 * This class is an abstract implementation of a command with 
 * configuration capabilities.
 * 
 * @author Thilo Brandt
 * @created 2004/04/04
 */
public abstract class AbstractConfigurableCommand extends AbstractCommand implements IConfigurable {

	protected Properties m_configuration;

	public AbstractConfigurableCommand() {
		super();
	}

	public String getConfigurableID() {
		return this.getID();
	}

	public void setConfiguration(Properties configuration) {
		this.m_configuration = configuration;
	}


}
