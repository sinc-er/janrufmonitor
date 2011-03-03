package de.janrufmonitor.framework.i18n;

import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.PIMRuntime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

public class TextFileI18nManager implements II18nManager, IConfigurable {
    
    private String ID = "TextFileI18nManager";
    private String NAMESPACE = "i18n.TextFileI18nManager";
    private static String FILEHEADER = "created by jAnrufmonitor i18n manager";
    
    Logger m_logger;
    Properties m_configuration;    
    Properties m_i18nObjects;
    List m_identifiers;
    
    String CONFIG_KEY = "database";
    String CONFIG_SEPARATOR = "separator";
    String CONFIG_IDENTIFIER = "identifier";
    String CONFIG_LANG = "defaultlanguage";    
    
    public TextFileI18nManager() {
        this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
    }
    
    public String[] getIdentifiers() {
    	this.buildIdentifiers();
        String[] identifiers = new String[m_identifiers.size()];
        for (int i=0;i<m_identifiers.size();i++)
			identifiers[i] = (String)m_identifiers.get(i);
        
        return identifiers;        
    }
    
    public String getString(String namespace, String parameter, String identifier, String language) {
        if (this.isIdentifier(identifier)) {
            String aProp = this.m_i18nObjects.getProperty(namespace + this.getSeparator() + parameter + this.getSeparator() + identifier + this.getSeparator() + language);
            if (aProp == null) {
                aProp = this.m_i18nObjects.getProperty(namespace + this.getSeparator() + parameter + this.getSeparator() + identifier + this.getSeparator() + this.m_configuration.getProperty(this.CONFIG_LANG));
            }
            return (aProp == null ? parameter : aProp);
        }
        this.m_logger.warning("Identifier {" + identifier + "} is not valid.");
        return "";        
    }
    
    public void loadData() {
        this.m_i18nObjects = new Properties();
        try {
            FileInputStream fi = new FileInputStream(PathResolver.getInstance(PIMRuntime.getInstance()).resolve(this.getDatabase()));
            this.m_i18nObjects.load(fi);
            fi.close();
        } catch (FileNotFoundException ex) {
            this.m_logger.info("Cannot find file: " + PathResolver.getInstance(PIMRuntime.getInstance()).resolve(this.getDatabase()));
            File newFile = new File(PathResolver.getInstance(PIMRuntime.getInstance()).resolve(this.getDatabase()));
            try {
                newFile.createNewFile();
                this.m_logger.info("Created new file: " + newFile.getAbsolutePath());
            } catch (IOException e) {
            	this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        } catch (IOException ex) {
        	this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);
        }        
        this.m_logger.info("I18n data successfully loaded.");
    }
    
    public void saveData() {
        try {
            FileOutputStream fo = new FileOutputStream(PathResolver.getInstance(PIMRuntime.getInstance()).resolve(this.getDatabase()));
            this.m_i18nObjects.store(fo, FILEHEADER);
            fo.close();
        } catch (FileNotFoundException ex) {
            this.m_logger.severe("Cannot find file: " + PathResolver.getInstance(PIMRuntime.getInstance()).resolve(this.getDatabase()));
        } catch (IOException ex) {
        	this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);
        }        
    }
    
    public void setString(String namespace, String parameter, String identifier, String language, String value) {
        if (this.isIdentifier(identifier)) {
            this.m_i18nObjects.setProperty(namespace + this.getSeparator() + parameter + this.getSeparator() + identifier + this.getSeparator() + language, value);
        } else {
            this.m_logger.severe("Identifier {" + identifier + "} not valid.");
        }        
    }
    
    public String getConfigurableID() {
        return this.ID;
    }
    
    public String getNamespace() {
        return this.NAMESPACE;
    }
    
    public void setConfiguration(Properties configuration) {
        this.m_configuration = configuration;    
    }
    
    private String getSeparator(){
        String sep = this.m_configuration.getProperty(this.CONFIG_SEPARATOR);
        if (sep==null) {
            sep = "";
            this.m_logger.severe("Attribute separator was not set in configuration.");
        }
        return sep;
    }
    
    private String getDatabase(){
        String database = this.m_configuration.getProperty(this.CONFIG_KEY);
        if (database==null) {
            database = "";
            this.m_logger.severe("Attribute database was not set in configuration. Usage of <"+this.ID+"> not possible.");
        }
        return database;
    }
    
    private boolean isIdentifier(String identifier){
        this.buildIdentifiers();
        return this.m_identifiers.contains(identifier);
    }

	public void removeNamespace(String namespace) {
		Iterator iter = this.m_i18nObjects.keySet().iterator();
		String key = null;
		while (iter.hasNext()){
			key = (String)iter.next();
			if (namespace.length()>0 && key.startsWith(namespace)) {
				this.m_i18nObjects.remove(key);
			}
		}
	}
	
	private void buildIdentifiers() {
		if (this.m_identifiers!=null) return;
		
        String ident = this.m_configuration.getProperty(this.CONFIG_IDENTIFIER, "label,description");
        if (ident != null) {
            StringTokenizer st = new StringTokenizer(ident, ",");
            
            this.m_identifiers = new ArrayList(st.countTokens());            
            
            while (st.hasMoreTokens()) {
            	this.m_identifiers.add(st.nextToken().trim());
            }
            Collections.sort(m_identifiers);
        }
	}

	public void startup() {
		PIMRuntime.getInstance().getConfigurableNotifier().register(this);
		this.loadData();   
	}

	public void shutdown() {
		this.m_i18nObjects.clear();
		PIMRuntime.getInstance().getConfigurableNotifier().unregister(this);
	}
    
}
