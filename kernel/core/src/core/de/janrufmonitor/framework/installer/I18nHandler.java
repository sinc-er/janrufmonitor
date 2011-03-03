package de.janrufmonitor.framework.installer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.PIMRuntime;

public class I18nHandler {

	private Logger m_logger;
	private String NAMESPACE_SEPARATOR = ":";
	
	public I18nHandler() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
	}

	public void removeI18nData(Properties i18nData) {
	    try {
            II18nManager i18n = PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager();
            Iterator iter = i18nData.keySet().iterator();
            String key = null;
            while (iter.hasNext()) {
                key = (String) iter.next();
                StringTokenizer st = new StringTokenizer(key, this.NAMESPACE_SEPARATOR);
                String namespace = st.nextToken();
                i18n.removeNamespace(namespace);
            }
            i18n.saveData();
        } catch (NullPointerException ex) {
            this.m_logger.severe("I18n entry is invalid: " + ex.getMessage());
        } catch (NoSuchElementException ex) {
            this.m_logger.severe("I18n entry is invalid: " + ex.getMessage());
        }
	}
	
	public void addI18nData(Properties i18nData) {
	    try {
            II18nManager i18n = PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager();
            Iterator iter = i18nData.keySet().iterator();
            String key = null;
            while (iter.hasNext()) {
                key = (String) iter.next();
                StringTokenizer st = new StringTokenizer(key, this.NAMESPACE_SEPARATOR);
                String namespace = st.nextToken();
                String parameter = st.nextToken();
                String identifier = st.nextToken();
                String language = st.nextToken();
                String value = i18nData.getProperty(key);
                i18n.setString(namespace, parameter, identifier, language, value);
            }
            i18n.saveData();
        } catch (NullPointerException ex) {
            this.m_logger.severe("I18n entry is invalid: " + ex.getMessage());
        } catch (NoSuchElementException ex) {
            this.m_logger.severe("I18n entry is invalid: " + ex.getMessage());
        }
	}
	
	public void addI18nData(File f) {
        if (!f.getName().toLowerCase().endsWith(InstallerConst.EXTENSION_I18N)) {
            this.m_logger.severe("File is not an I18N file: " + f.getName());
            return;
        }
        try {
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream(f);
            props.load(fis);
            fis.close();
            addI18nData(props);
        } catch (IOException ex) {
            this.m_logger.warning(ex.getMessage());
        } catch (NullPointerException ex) {
            this.m_logger.severe("I18n entry is invalid: " + ex.getMessage());
        }
    }

}
