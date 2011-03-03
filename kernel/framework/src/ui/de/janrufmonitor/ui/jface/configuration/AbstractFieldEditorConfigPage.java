
package de.janrufmonitor.ui.jface.configuration;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.IRuntime;

public abstract class AbstractFieldEditorConfigPage extends FieldEditorPreferencePage implements IConfigPage {

	protected Logger m_logger;
	
	protected String m_externalId;
	protected String m_language;
	protected II18nManager m_i18n;
	
	public AbstractFieldEditorConfigPage() {
		super(GRID);
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
        this.m_i18n = getRuntime().getI18nManagerFactory().getI18nManager();
        this.m_language = getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE);
	}
	
	public abstract String getNamespace();
	
	public abstract IRuntime getRuntime();

	public abstract String getConfigNamespace();
	
	protected Label createDescriptionLabel(Composite parent) {
		Label description = new Label(parent, SWT.WRAP);
		String desc = this.m_i18n.getString(this.getNamespace(), "description", "label", this.m_language);
		description.setText((desc.equalsIgnoreCase("description") ? "" : desc));
		return description;
	}
	
	public boolean performOk() {
		boolean ok = super.performOk();
		
		if (ok && this.getConfigNamespace().length()>0)
			this.getRuntime().getConfigurableNotifier().notifyByNamespace(this.getConfigNamespace());
		
		return ok;
	}
	
	public boolean isExpertMode() {
		return getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_CONFIG_EXPERT_MODE).equalsIgnoreCase("true");
	}
	
	public void setNodeID(String id) {
		this.m_externalId = id;
	}
}
