package de.janrufmonitor.repository.imexporter;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.imexport.ICallerImporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.repository.imexport.ITracker;
import de.janrufmonitor.runtime.PIMRuntime;

public class VcfFileCallerImporter implements ICallerImporter, ITracker {

	private String ID = "VcfFileCallerImporter";

	private String NAMESPACE = "repository.VcfFileCallerImporter";

	Logger m_logger;
	II18nManager m_i18n;
	String m_language;
	String m_filename;
	
	VcfParser30 m_vcf;

	public VcfFileCallerImporter() {
		m_logger = LogManager.getLogManager().getLogger(
				IJAMConst.DEFAULT_LOGGER);
		m_i18n = PIMRuntime.getInstance().getI18nManagerFactory()
				.getI18nManager();
		m_language = PIMRuntime.getInstance().getConfigManagerFactory()
				.getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE,
						IJAMConst.GLOBAL_LANGUAGE);
	}

	public ICallerList doImport() {
		ICallerList cl = PIMRuntime.getInstance().getCallerFactory().createCallerList();;
		m_vcf = new VcfParser30(m_filename);
		try {
			cl = m_vcf.parse();
		} catch (VcfParserException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return cl;
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
		return this.m_i18n.getString(this.NAMESPACE, "filtername", "label",
				this.m_language);
	}

	public String getExtension() {
		return "*.vcf";
	}

	public void setFilename(String filename) {
		this.m_filename = filename;
		this.m_vcf = null;
	}

	public int getCurrent() {
		if (this.m_vcf!=null) {
			return this.m_vcf.getCurrent();
		}
		return 1;
	}

	public int getTotal() {
		if (this.m_vcf!=null) {
			return this.m_vcf.getTotal();
		}
		return 1;
	}

}
