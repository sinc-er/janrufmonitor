package de.janrufmonitor.framework.command;

import de.janrufmonitor.framework.command.AbstractCommand;

/**
 * This class is an abstract implementation of a console command.
 * 
 * @author Thilo Brandt
 * @created 2004/09/06
 */
public abstract class AbstractConsoleCommand
	extends AbstractCommand
	implements IConsoleCommand {

	private String[] m_params; 
	
	public void setExecuteParams(String[] args) {
		this.m_params = args;
	}

	public String[] getExecuteParams() {
		return (this.m_params==null ? new String[] {} : this.m_params);
	}


}
