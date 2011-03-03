package de.janrufmonitor.framework.command;

import java.util.Map;

/**
 * This interface must be implemented by a command component.
 * A command is an executable unit within the framework.
 * 
 * @author Thilo Brandt
 * @created 2004/04/04
 */
public interface ICommand {
	
	/**
	 * This method executes the command.
	 */
	public void execute() throws Exception;
	
	/**
	 * Checks wether the command is executable or not.
	 * 
	 * @return true if command is executable, false if not
	 */
	public boolean isExecutable();
	
	/**
	 * Checks wether the command is currently executing or not.
	 * 
	 * @return true if command is currently executing, false if not.
	 */
	public boolean isExecuting();
	
	/**
	 * Gets e unique ID of this command.
	 * 
	 * @return a unique ID
	 */
	public String getID();
	
	/**
	 * Gets the label of the command.
	 * 
	 * @return a valid label
	 */
	public String getLabel();
	
	/**
	 * Sets the parameters which can be evaluated during 
	 * execution time. The map contains key value pairs
	 * of parameters.
	 * 
	 * @param p a parameter map
	 */
	public void setParameters(Map p);
	
	/**
	 * Gets the parameters which can be evaluated during 
	 * execution time.
	 * 
	 * @return a valid parameter map.
	 */
	public Map getParameters();
	
}
