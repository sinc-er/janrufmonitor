package de.janrufmonitor.repository.imexporter;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.ThunderbirdTransformer;
import de.janrufmonitor.repository.imexport.ICallerImporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.repository.imexport.ITracker;
import de.janrufmonitor.runtime.PIMRuntime;

public class ThunderbirdCallerImporter implements ICallerImporter, ITracker {

	private String ID = "ThunderbirdCallerImporter";

	private String NAMESPACE = "repository.ThunderbirdCallerImporter";

	Logger m_logger;
	II18nManager m_i18n;
	String m_language;
	String m_filename;
	
	private ThunderbirdTransformer tbt;

	public ThunderbirdCallerImporter() {
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
		tbt = new ThunderbirdTransformer(this.m_filename, false);
		cl.add(tbt.getCallers());
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
		return "*.mab";
	}

	public void setFilename(String filename) {
		this.m_filename = filename;
		this.tbt = null;
	}

	public int getCurrent() {
		if (this.tbt!=null) {
			return this.tbt.getCurrent();
		}
		return 1;
	}

	public int getTotal() {
		if (this.tbt!=null) {
			return this.tbt.getTotal();
		}
		return 1;
	}

}
