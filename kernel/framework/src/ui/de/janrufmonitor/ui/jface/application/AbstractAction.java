package de.janrufmonitor.ui.jface.application;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.ui.jface.application.action.IAction;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.util.string.StringUtils;

public abstract class AbstractAction extends Action implements de.janrufmonitor.ui.jface.application.action.IAction {

	private II18nManager m_i18n;
	private String m_language;
	private String m_id;
	
	protected IApplication m_app;
	protected Logger m_logger;
	
	public AbstractAction() {
		super();
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);	
	}
	
	public AbstractAction(String s) {
		super(s);
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);	
	}

	public AbstractAction(String s, int i) {
		super(s, i);
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);	
	}
	
	public AbstractAction(String s, ImageDescriptor id) {
		super(s, id);
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);	
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

	public void setApplication(IApplication app) {
		this.m_app = app;
	}
	
	public void run() {	
		MessageDialog.openInformation(
				new Shell(DisplayManager.getDefaultDisplay()),
				"",
				this.getI18nManager().getString("ui.jface.application.AbstractAction", "message", "label", this.getLanguage()+this.getID())			
		);
	}
	
	public String getID() {
		return this.m_id;
	}
	
	public void setID(String id) {
		this.m_id = id;
	}
	
	public String getShortText() {
		String t = this.getI18nManager().getString(
				this.getNamespace(),
				"shorttext",
				"label",
				this.getLanguage()
		);
		
		if (!t.equalsIgnoreCase("shorttext")) return t;
		
		t = StringUtils.replaceString(getText(), ".", "");
		if (t.length()>13) {
			return t.substring(0,13)+"...";
		}
		return t;
	}

	public void setData(Object data) {

	}

	public IAction[] getSubActions() {
		return null;
	}

	public boolean hasSubActions() {
		return false;
	}

	public abstract IRuntime getRuntime();

}
