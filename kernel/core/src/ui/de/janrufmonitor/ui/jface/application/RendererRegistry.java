package de.janrufmonitor.ui.jface.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.rendering.ITableCellRenderer;

public class RendererRegistry implements IConfigurable {

	private static String NAMESPACE = "ui.jface.application.RendererRegistry";
	private static String PARAMETERNAME = "renderer_";
	
	private Map m_renderer;
    private Logger m_logger;
    private Properties m_configuration;
    
    private static RendererRegistry m_instance = null;
	
	private RendererRegistry() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		PIMRuntime.getInstance().getConfigurableNotifier().register(this);
	}

    public static synchronized RendererRegistry getInstance() {
        if (RendererRegistry.m_instance == null) {
        	RendererRegistry.m_instance = new RendererRegistry();
        }
        return RendererRegistry.m_instance;
    }

	public String getNamespace() {
		return RendererRegistry.NAMESPACE;
	}

	public String getConfigurableID() {
		return "RendererRegistry";
	}

	public void setConfiguration(Properties configuration) {
		this.m_configuration = configuration;
		
		this.m_renderer = Collections.synchronizedMap(new HashMap(this.m_configuration.size()));
		Iterator iter = this.m_configuration.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			// check for .class attribute in properties file
			if (key.startsWith(PARAMETERNAME)) {
				String className = this.m_configuration.getProperty(key);
				try {
					Class classObject = Thread.currentThread().getContextClassLoader().loadClass(className);
					// add a renderer to renderer list
					ITableCellRenderer is = (ITableCellRenderer) classObject.newInstance();
					// check for external IDs
					String id = key.substring(PARAMETERNAME.length());
					if (this.m_configuration.getProperty(id+"_id")!=null) {
						is.setID(this.m_configuration.getProperty(id+"_id"));
					}
					
					this.m_renderer.put(is.getID().toLowerCase(), is);
					this.m_logger.info("Registered new renderer <" + is.getID().toLowerCase()+">.");
				} catch (ClassNotFoundException ex) {
					this.m_logger.severe("Could not find class: " + className);
				} catch (InstantiationException ex) {
					this.m_logger.severe("Could not instantiate class: " + className);
				} catch (IllegalAccessException ex) {
					this.m_logger.severe("Could not access class: " + className);
				} catch (Exception ex) {
					this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);				}
			}
		}
	}
	
	public ITableCellRenderer getRenderer(String id) {
		if (id!=null && this.m_renderer.containsKey(id)) {
			return (ITableCellRenderer)this.m_renderer.get(id);
		}
		return null;
	}
	
	public List getAllRendererIDs() {
		List l = new ArrayList();
		
		Iterator iter = this.m_renderer.keySet().iterator();
		while (iter.hasNext()) {
			l.add(iter.next());
		}
		
		return l;
	}
	
	public List getAllRendererIDsForApplication(AbstractApplication a) {
		List l = new ArrayList();
		
		Iterator iter = this.m_renderer.keySet().iterator();
		String key = null;
		ITableCellRenderer tr = null;
		while (iter.hasNext()) {
			key = (String) iter.next();
			tr = this.getRenderer(key);
			if (tr!=null && a.isSupportingRenderer(tr))
				l.add(key);
		}
		
		return l;
	}
}
