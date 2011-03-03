package de.janrufmonitor.ui.jface.configuration;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class AbstractEmptyConfigPage extends AbstractConfigPage {

	protected Control createContents(Composite parent) {
		this.setTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));
		this.noDefaultAndApplyButton();
		Composite c = new Composite(parent, 0);
		return c;
	}

	public String getConfigNamespace() {
		return "";
	}
}
