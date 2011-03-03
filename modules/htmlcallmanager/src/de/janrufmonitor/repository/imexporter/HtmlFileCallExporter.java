package de.janrufmonitor.repository.imexporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.CallListComparator;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.JournalBuilder;
import de.janrufmonitor.repository.imexport.ICallExporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class HtmlFileCallExporter implements ICallExporter {

	private String ID = "HtmlFileCallExporter";
	private String NAMESPACE = "repository.HtmlFileCallExporter";

	Logger m_logger;
	ICallList m_callList;
	II18nManager m_i18n;
	String m_language;
	String m_filename;

	public HtmlFileCallExporter() {
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
		return "*.html";
	}

	public void setFilename(String filename) {
		this.m_filename = filename;
	}

	public void setCallList(ICallList callList) {
		this.m_callList = callList;
	}

	public boolean doExport() {
		this.m_callList.sort(CallListComparator.ORDER_DATE, false);
		File db = new File(this.m_filename);
		return this.generateHtml(db);
	}

	private boolean generateHtml(File htmlFile) {
		this.m_logger.info("Writing " + this.m_callList.size()
				+ " calls to html format");

		if (!htmlFile.exists()) {
			try {
				if (!htmlFile.getParentFile().exists())
					htmlFile.getParentFile().mkdirs();

				FileOutputStream fos = new FileOutputStream(htmlFile);
				fos.write("".getBytes());
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				this.m_logger.severe("File not found: " + htmlFile.getAbsolutePath());
				return false;
			} catch (IOException e) {
				this.m_logger.severe("Can't create new file " + htmlFile.getAbsolutePath()
						+ ": " + e.getMessage());
				return false;
			}
		}

		try {
			FileWriter dbWriter = new FileWriter(htmlFile);
			BufferedWriter bufWriter = new BufferedWriter(dbWriter);

			// added 2005/10/03: sorting by date
			this.m_callList.sort(CallListComparator.ORDER_DATE, false);
			StringBuffer sb = JournalBuilder.parseFromTemplate(this.m_callList, getRuntime().getConfigManagerFactory().getConfigManager().getProperties("repository.HtmlCallManager"));
			if (sb!=null)
				bufWriter.write(sb.toString());

			bufWriter.flush();
			bufWriter.close();
			dbWriter.close();
		} catch (FileNotFoundException ex) {
			this.m_logger.severe("File not found: " + htmlFile.getAbsolutePath());
			return false;
		} catch (IOException ex) {
			this.m_logger.severe("IO Error on file " + htmlFile.getAbsolutePath());
			return false;
		}
		return true;
	}

	private IRuntime getRuntime() {
		return PIMRuntime.getInstance();
	}


	


}
