package de.janrufmonitor.framework.command;

/**
 * This interface must be implemented by a command factory instance.
 * 
 * @author Thilo Brandt
 * @created 2004/04/04
 */
public interface ICommandFactory {
	
	/** 
	 * Gets the command with the specified ID, or null of the command does not exist.
	 * 
	 * @param id ID of the command to get
	 * @return a ICommand obejct or null if command with ID does not exist.
	 */
	public ICommand getCommand(String id);
	
	/**
	 * Adds a new command to the factory.
	 * 
	 * @param c the command to add.
	 */
	public void addCommand(ICommand c);
	
	/**
	 * Gets a list with all registered command of this factory.
	 * 
	 * @return a list of command IDs
	 */
	public String[] getCommandIDs();
	
	/**
	 * This method is called on startup time by the runtime object.
	 */
	public void startup();
    
	/**
	 * This method is called on shutdown time by the runtime object.
	 */
	public void shutdown();
}
