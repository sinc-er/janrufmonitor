package de.janrufmonitor.ui.jface.application;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.action.IAction;

public class ActionRegistry implements IConfigurable {

	private static String NAMESPACE = "ui.jface.application.ActionRegistry";
	private static String PARAMETERNAME = "action_";
	
	private Map m_actions;
    private Logger m_logger;
    private Properties m_configuration;
    
    private static ActionRegistry m_instance = null;
	
	private ActionRegistry() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		PIMRuntime.getInstance().getConfigurableNotifier().register(this);
	}

    public static synchronized ActionRegistry getInstance() {
        if (ActionRegistry.m_instance == null) {
        	ActionRegistry.m_instance = new ActionRegistry();
        }
        return ActionRegistry.m_instance;
    }

	public String getNamespace() {
		return ActionRegistry.NAMESPACE;
	}

	public String getConfigurableID() {
		return "ActionRegistry";
	}

	public void setConfiguration(Properties configuration) {
		this.m_configuration = configuration;
		this.m_actions = Collections.synchronizedMap(new HashMap(this.m_configuration.size()));
	}
	
	public synchronized IAction getAction(String id, IApplication app) {
		String key = id+app.getID();
		if (this.m_actions.containsKey(key)) {
			IAction action = (IAction)this.m_actions.get(key);
			action.setApplication(app);
			return action;
		}
		
		String className = this.m_configuration.getProperty(PARAMETERNAME+id, "");
		if (className.length()>0) {
			try {
				Class classObject = Thread.currentThread().getContextClassLoader().loadClass(className);
				IAction action = (IAction)classObject.newInstance();
				action.setApplication(app);
				action.setID(id);
				this.m_actions.put(key, action);
				return action;
			} catch (ClassNotFoundException ex) {
				this.m_logger.severe("Could not find class: " + className);
			} catch (InstantiationException ex) {
				this.m_logger.severe("Could not instantiate class: " + className);
			} catch (IllegalAccessException ex) {
				this.m_logger.severe("Could not access class: " + className);
			} catch (Exception e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			}		
		}
		return null;
	}
}
