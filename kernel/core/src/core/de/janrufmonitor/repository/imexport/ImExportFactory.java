package de.janrufmonitor.repository.imexport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.PIMRuntime;

public class ImExportFactory implements IConfigurable {

	private String ID = "ImExportFactory";
	private String NAMESPACE = "repository.ImExportFactory";
	private static ImExportFactory m_instance = null;
	private String IMPORTER = "import_";
	private String EXPORTER = "export_";
	
	Logger m_logger;
	List m_ImExporterList;
	
	private ImExportFactory() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		PIMRuntime.getInstance().getConfigurableNotifier().register(this);  
	}
    
	public static synchronized ImExportFactory getInstance() {
		if (ImExportFactory.m_instance == null) {
			ImExportFactory.m_instance = new ImExportFactory();
		}
		return ImExportFactory.m_instance;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public String getConfigurableID() {
		return this.ID;
	}

	public void setConfiguration(Properties configuration) {
		this.m_ImExporterList = new ArrayList();
        
		Iterator iter = configuration.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if (key.startsWith(EXPORTER)) {
				String className = configuration.getProperty(key);
				try {
					Class classObject = Thread.currentThread().getContextClassLoader().loadClass(className);
					IImExporter exp = (IImExporter) classObject.newInstance();
					this.m_ImExporterList.add(exp);
				} catch (ClassNotFoundException ex) {
					this.m_logger.severe("Could not find class: " + className);
				} catch (InstantiationException ex) {
					this.m_logger.severe("Could not instantiate class: " + className);
				} catch (IllegalAccessException ex) {
					this.m_logger.severe("Could not access class: " + className);
				}
			}
			if (key.startsWith(IMPORTER)) {
				String className = configuration.getProperty(key);
				try {
					Class classObject = Thread.currentThread().getContextClassLoader().loadClass(className);
					IImExporter imp = (IImExporter) classObject.newInstance();
					this.m_ImExporterList.add(imp);
				} catch (ClassNotFoundException ex) {
					this.m_logger.severe("Could not find class: " + className);
				} catch (InstantiationException ex) {
					this.m_logger.severe("Could not instantiate class: " + className);
				} catch (IllegalAccessException ex) {
					this.m_logger.severe("Could not access class: " + className);
				}
			}
		}
	}

	public IImExporter getImporterByExtension(String ext) {
		for (int i = 0; i < this.m_ImExporterList.size(); i++) {
			IImExporter obj = (IImExporter) this.m_ImExporterList.get(i);
			if (obj.getExtension().equalsIgnoreCase(ext) && obj.getType()==IImExporter.IMPORT_TYPE) {
				return obj;
			}
		}
		this.m_logger.warning("no Importer found for extension: " + ext);
		return null;
	}
	
	public IImExporter getImporter(String id) {
		for (int i = 0; i < this.m_ImExporterList.size(); i++) {
			IImExporter obj = (IImExporter) this.m_ImExporterList.get(i);
			if (obj.getID().equalsIgnoreCase(id) && obj.getType()==IImExporter.IMPORT_TYPE) {
				return obj;
			}
		}
		this.m_logger.warning("no Importer found for ID: " + id);
		return null;
	}
	
	public List getAllImporterIds(int mode) {
		List ids = new ArrayList();
		for (int i = 0; i < this.m_ImExporterList.size(); i++) {
			IImExporter obj = (IImExporter)this.m_ImExporterList.get(i);
			if (obj.getType()==IImExporter.IMPORT_TYPE && obj.getMode() == mode) {
				ids.add(obj.getID());
			}
		}
		return ids;	
	}
	
	public IImExporter getExporter(String id) {
		for (int i = 0; i < this.m_ImExporterList.size(); i++) {
			IImExporter obj = (IImExporter) this.m_ImExporterList.get(i);
			if (obj.getID().equalsIgnoreCase(id) && obj.getType()==IImExporter.EXPORT_TYPE) {
				return obj;
			}
		}
		this.m_logger.warning("no Importer found for ID: " + id);
		return null;
	}
	
	public List getAllExporterIds(int mode) {
		List ids = new ArrayList();
		for (int i = 0; i < this.m_ImExporterList.size(); i++) {
			IImExporter obj = (IImExporter)this.m_ImExporterList.get(i);
			if (obj.getType()==IImExporter.EXPORT_TYPE && obj.getMode() == mode) {
				ids.add(obj.getID());
			}
		}
		return ids;	
	}
			
}
