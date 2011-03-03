package de.janrufmonitor.ui.jface.wizards;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.jface.wizard.Wizard;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.IRuntime;

public abstract class AbstractWizard extends Wizard {

	protected Logger m_logger;
	protected II18nManager m_i18n;
	protected String m_language;
	
	public AbstractWizard() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		this.m_i18n = getRuntime().getI18nManagerFactory().getI18nManager();
		this.m_language = getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE);
	}

	protected abstract IRuntime getRuntime();

	public abstract String getID();
	
	public abstract String getNamespace();

}
