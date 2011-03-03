package de.janrufmonitor.framework.command;

import de.janrufmonitor.framework.command.ICommand;

/**
 * This interface must be implemented by a console command component.
 * A command is an executable unit within the framework. This command allows to 
 * specify some execute parameters.
 * 
 * @author Thilo Brandt
 * @created 2004/09/06
 */
public interface IConsoleCommand extends ICommand {
	
	/**
	 * Sets the execute parameters.
	 * 
	 * @param args execute parameters
	 */
	public void setExecuteParams(String[] args);
	
	/**
	 * Gets the execute parameters.
	 * 
	 * @return array with parameters
	 */
	public String[] getExecuteParams();
	
}
