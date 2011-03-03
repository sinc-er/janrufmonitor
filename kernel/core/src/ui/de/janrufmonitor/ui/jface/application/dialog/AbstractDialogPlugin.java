package de.janrufmonitor.ui.jface.application.dialog;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public abstract class AbstractDialogPlugin implements IDialogPlugin {

	protected IDialog m_dialog;
	protected Logger m_logger;
	protected String ID;
	
	private IRuntime m_runtime;
	private II18nManager m_i18n;
	private String m_language;

	public AbstractDialogPlugin() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
	}
	
	public void setDialog(IDialog d) {
		this.m_dialog = d;
	}
	
	protected II18nManager getI18nManager() {
		if (this.m_i18n==null) {
			this.m_i18n = this.getRuntime().getI18nManagerFactory().getI18nManager();
		}
		return this.m_i18n;
	}

	protected String getLanguage() {
		if (this.m_language==null) {
			this.m_language = 
				this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(
					IJAMConst.GLOBAL_NAMESPACE,
					IJAMConst.GLOBAL_LANGUAGE
				);
		}
		return this.m_language;
	}
	
	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}
	
	public void setID(String id) {
		this.ID = id;
	}
	
	public abstract String getLabel();

	public abstract void run();

	public abstract boolean isEnabled();

}
