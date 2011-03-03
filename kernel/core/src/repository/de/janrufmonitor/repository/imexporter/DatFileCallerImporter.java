package de.janrufmonitor.repository.imexporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.imexport.ICallerImporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.io.Serializer;
import de.janrufmonitor.util.io.SerializerException;

public class DatFileCallerImporter implements ICallerImporter {

	private String ID = "DatFileCallerImporter";
	private String NAMESPACE = "repository.DatFileCallerImporter";

	private Logger m_logger;
	private ICallerList m_callerList;
	private II18nManager m_i18n;
	private String m_language;
	private String m_filename;
	
	public DatFileCallerImporter() {
		m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		m_i18n = PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager();
		m_language = PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE);
	}
	
	public ICallerList doImport() {
		m_callerList = PIMRuntime.getInstance().getCallerFactory().createCallerList();
		Date startDate = new Date();
		File db = new File(m_filename);
		try {
			FileReader dbReader = new FileReader(db);
			BufferedReader bufReader = new BufferedReader(dbReader);
			ICaller aCaller = null;
			while (bufReader.ready()) {
				try {
					aCaller =
						Serializer.toCaller(
							bufReader.readLine().getBytes(),
							PIMRuntime.getInstance());
					if (aCaller==null) {
						// file format is corrupted
						return this.m_callerList;
					}
					this.m_callerList.add(aCaller);
				} catch (SerializerException e) {
					this.m_logger.warning("Caller was skipped: "+e.getMessage());
				}
			}
			bufReader.close();
			dbReader.close();
		} catch (FileNotFoundException ex) {
			this.m_logger.warning("Cannot find caller backup file " + m_filename);
			return this.m_callerList;
		} catch (IOException ex) {
			this.m_logger.severe("IOException on file " + m_filename);
			return this.m_callerList;
		}
		Date endDate = new Date();
		this.m_logger.info("Successfully imported caller file " + m_filename);
		this.m_logger.info("Found " + new Integer(this.m_callerList.size()).toString() + " caller items in " + new Float((endDate.getTime() - startDate.getTime()) / 1000).toString() + " secs.");
    	
		return this.m_callerList;
	}

	public String getID() {
		return this.ID;
	}

	public int getMode() {
		return IImExporter.CALLER_MODE;
	}

	public int getType() {
		return IImExporter.IMPORT_TYPE;
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
