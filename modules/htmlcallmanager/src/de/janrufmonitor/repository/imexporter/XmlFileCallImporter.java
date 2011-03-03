package de.janrufmonitor.repository.imexporter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.imexport.ICallImporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.repository.xml.XMLSerializer;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.io.Stream;

public class XmlFileCallImporter implements ICallImporter {
	
	private String ID = "XmlFileCallImporter";
	private String NAMESPACE = "repository.XmlFileCallImporter";

	private Logger m_logger;
	private ICallList m_callList;
	private II18nManager m_i18n;
	private String m_language;
	private String m_filename;

	public XmlFileCallImporter() {
		m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		m_i18n = PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager();
		m_language = PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE);
	}

	public String getID() {
		return this.ID;
	}

	public int getMode() {
		return IImExporter.CALL_MODE;
	}

	public String getFilterName() {
		return this.m_i18n.getString(this.NAMESPACE, "filtername", "label", this.m_language);
	}

	public String getExtension() {
		return "*.xml";
	}

	public void setFilename(String filename) {
		this.m_filename = filename;
	}

	public ICallList doImport() {
		m_callList = PIMRuntime.getInstance().getCallFactory().createCallList();
		try {
			FileInputStream fin = new FileInputStream(m_filename);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Stream.copy(fin, out, true);
			this.m_callList.add(XMLSerializer.toCallList(out.toString()));
		} catch (FileNotFoundException e) {
			this.m_logger.severe("File not found: " + m_filename);
		} catch (IOException e) {
			this.m_logger.severe("IO Error on file " + m_filename
					+ ": " + e.getMessage());
		}
		return m_callList;
	}

	public int getType() {
		return IImExporter.IMPORT_TYPE;
	}
}
