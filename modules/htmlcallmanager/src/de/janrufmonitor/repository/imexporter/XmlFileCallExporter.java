package de.janrufmonitor.repository.imexporter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.CallListComparator;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.imexport.ICallExporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.repository.xml.XMLSerializer;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.io.Stream;

public class XmlFileCallExporter implements ICallExporter {

	private String ID = "XmlFileCallExporter";
	private String NAMESPACE = "repository.XmlFileCallExporter";

	Logger m_logger;
	ICallList m_callList;
	II18nManager m_i18n;
	String m_language;
	String m_filename;

	public XmlFileCallExporter() {
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

	public int getType() {
		return IImExporter.EXPORT_TYPE;
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

	public void setCallList(ICallList callList) {
		this.m_callList = callList;
	}

	public boolean doExport() {
		this.m_callList.sort(CallListComparator.ORDER_DATE, false);
		File xml = new File(this.m_filename);
		if (!xml.exists()) {
			if (!xml.getParentFile().exists()) xml.getParentFile().mkdirs();

			try {
				FileOutputStream fos = new FileOutputStream(xml);
				ByteArrayInputStream in = new ByteArrayInputStream(XMLSerializer.toXML(this.m_callList, false).getBytes());
				Stream.copy(in, fos, true);
				return true;
			} catch (FileNotFoundException e) {
				this.m_logger.severe("File not found: " + xml.getAbsolutePath());
			} catch (IOException e) {
				this.m_logger.severe("Can't create new file " + xml.getAbsolutePath()
						+ ": " + e.getMessage());
			}
		}
		return false;
	}

}
