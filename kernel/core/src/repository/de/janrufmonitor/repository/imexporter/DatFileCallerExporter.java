package de.janrufmonitor.repository.imexporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.imexport.ICallerExporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.io.Serializer;
import de.janrufmonitor.util.io.SerializerException;

public class DatFileCallerExporter implements ICallerExporter {

	private String ID = "DatFileCallerExporter";
	private String NAMESPACE = "repository.DatFileCallerExporter";

	Logger m_logger;
	ICallerList m_callerList;
	II18nManager m_i18n;
	String m_language;
	String m_filename;
	
	public DatFileCallerExporter() {
		m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		m_i18n = PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager();
		m_language = PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE);
	}


	public void setCallerList(ICallerList callerList) {
		this.m_callerList = callerList;
	}

	public boolean doExport() {
		File db = new File(m_filename);
		try {
			FileWriter dbWriter = new FileWriter(db);
			BufferedWriter bufWriter = new BufferedWriter(dbWriter);
			String aCaller = null;
			for (int i = 0; i < this.m_callerList.size(); i++) {
				try {
					aCaller = 
						new String(
							Serializer.toByteArray(this.m_callerList.get(i), true));
					bufWriter.write(aCaller);
					bufWriter.newLine();
				} catch (SerializerException e) {
					this.m_logger.severe(e.getMessage());
				}
			}
			bufWriter.flush();
			bufWriter.close();
			dbWriter.close();
		} catch (FileNotFoundException ex) {
			this.m_logger.severe("File not found: " + m_filename);
			return false;
		} catch (IOException ex) {
			this.m_logger.severe("IOException on file " + m_filename);
			return false;
		}
		return true;
	}

	public String getID() {
		return this.ID;
	}

	public int getMode() {
		return IImExporter.CALLER_MODE;
	}

	public int getType() {
		return IImExporter.EXPORT_TYPE;
	}

	public String getFilterName() {
		return this.m_i18n.getString(this.NAMESPACE, "filtername", "label", this.m_language);
	}

	public String getExtension() {
		return "*.dat";
	}

	public void setFilename(String filename) {
		this.m_filename = filename;
	}

}
