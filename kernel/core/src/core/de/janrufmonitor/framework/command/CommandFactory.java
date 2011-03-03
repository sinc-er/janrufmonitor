package de.janrufmonitor.framework.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.PIMRuntime;

public class CommandFactory implements ICommandFactory, IConfigurable {

	private String ID = "CommandFactory";
	private String NAMESPACE = "command.CommandFactory";
	private String COMMAND = "command_";
	
	private static CommandFactory m_instance = null;

	private Logger m_logger;
	private Map m_commands;
	private Properties m_configuration;

	private CommandFactory() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
	}
	
	public static synchronized CommandFactory getInstance() {
		if (CommandFactory.m_instance == null) {
			CommandFactory.m_instance = new CommandFactory();
		}
		return CommandFactory.m_instance;
	} 

	public ICommand getCommand(String id) {
		if (id==null) return null;
		if (this.m_commands.containsKey(id)) {
			return (ICommand)this.m_commands.get(id);
		}
		this.m_logger.warning("no command found for ID: " + id);  
		return null;
	}

	public void addCommand(ICommand c) {
		if (c!=null)
			this.m_commands.put(c.getID(), c);
	}

	public String[] getCommandIDs() {
		String[] ids = new String[this.m_commands.size()];
		synchronized(this.m_commands) {
			Iterator iter = this.m_commands.keySet().iterator();
			int i=0;
			while(iter.hasNext()) {
				ids[i] = (String) iter.next();
				i++;
			}
		}
		return ids;
	}

	public void startup() {
		this.m_logger.entering(CommandFactory.class.getName(), "startup");

		PIMRuntime.getInstance().getConfigurableNotifier().register(this);

		this.m_commands = Collections.synchronizedMap(new HashMap());

		Iterator iter = this.m_configuration.keySet().iterator();
		String key = null;
		while (iter.hasNext()) {
			key = (String) iter.next();
			if (key.startsWith(this.COMMAND)) {
				String className = this.m_configuration.getProperty(key);
				try {
					Class classObject = Thread.currentThread().getContextClassLoader().loadClass(className);
					ICommand c = (ICommand) classObject.newInstance();
					this.m_commands.put(c.getID(), c);
					this.m_logger.info("Registered <"+c.getID()+"> as command.");
				} catch (ClassNotFoundException ex) {
					this.m_logger.warning("Could not find class: " + className);
				} catch (InstantiationException ex) {
					this.m_logger.severe("Could not instantiate class: " + className);
				} catch (IllegalAccessException ex) {
					this.m_logger.severe("Could not access class: " + className);
				} catch (NoClassDefFoundError ex) {
					this.m_logger.warning("Could not find class definition: " + className);
				}
			}
		}
		this.m_logger.exiting(CommandFactory.class.getName(), "startup");
	}

	public void shutdown() {
		this.m_logger.entering(CommandFactory.class.getName(), "shutdown");
		PIMRuntime.getInstance().getConfigurableNotifier().unregister(this);
		m_instance = null;
		this.m_logger.exiting(CommandFactory.class.getName(), "shutdown");
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public String getConfigurableID() {
		return this.ID;
	}

	public void setConfiguration(Properties configuration) {
		this.m_logger.entering(CommandFactory.class.getName(), "setConfiguration");
		this.m_configuration = configuration;
		this.m_logger.exiting(CommandFactory.class.getName(), "setConfiguration");
	}

}
