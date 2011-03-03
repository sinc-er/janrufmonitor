package de.janrufmonitor.ui.jface.configuration.pages;

import org.eclipse.jface.preference.StringFieldEditor;

import de.janrufmonitor.repository.web.RegExpURLRequester;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractServiceFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;

public class DynamicWebCallerManager extends AbstractServiceFieldEditorConfigPage {

	private IRuntime m_runtime;

	private String ensureNotNull() {
		return (this.m_externalId==null ? this.getTitle() : this.m_externalId);
	}
	
	public String getConfigNamespace() {
		return "repository."+this.ensureNotNull();
	}

	public String getNamespace() {
		return "ui.jface.configuration.pages."+this.ensureNotNull();
	}

	public IRuntime getRuntime() {
		if (this.m_runtime == null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	public String getParentNodeID() {
		return IConfigPage.CALLER_NODE;
	}

	public String getNodeID() {
		return this.m_externalId.toLowerCase();
	}

	public int getNodePosition() {
		return 25;
	}

	protected void createFieldEditors() {
		super.createFieldEditors();

		if (isExpertMode()) {
			StringFieldEditor sfe = new StringFieldEditor(this.getConfigNamespace()
					+ SEPARATOR + "url", this.m_i18n.getString(this
					.getNamespace(), "url", "label", this.m_language), 50, this
					.getFieldEditorParent());
			sfe.setEmptyStringAllowed(false);
			addField(sfe);

			sfe = new StringFieldEditor(this.getConfigNamespace() + SEPARATOR
					+ "skipbytes", this.m_i18n.getString(this.getNamespace(),
					"skipbytes", "label", this.m_language), 6, this
					.getFieldEditorParent());
			sfe.setEmptyStringAllowed(false);
			sfe.setTextLimit(5);
			addField(sfe);

			sfe = new StringFieldEditor(this.getConfigNamespace() + SEPARATOR
					+ RegExpURLRequester.REGEXP_LASTNAME, this.m_i18n
					.getString(this.getNamespace(),
							RegExpURLRequester.REGEXP_LASTNAME, "label",
							this.m_language), 50, this.getFieldEditorParent());
			sfe.setEmptyStringAllowed(true);
			addField(sfe);
			sfe = new StringFieldEditor(this.getConfigNamespace() + SEPARATOR
					+ RegExpURLRequester.REGEXP_FIRSTNAME, this.m_i18n
					.getString(this.getNamespace(),
							RegExpURLRequester.REGEXP_FIRSTNAME, "label",
							this.m_language), 50, this.getFieldEditorParent());
			sfe.setEmptyStringAllowed(true);
			addField(sfe);

			sfe = new StringFieldEditor(this.getConfigNamespace() + SEPARATOR
					+ RegExpURLRequester.REGEXP_STREET, this.m_i18n.getString(
					this.getNamespace(), RegExpURLRequester.REGEXP_STREET,
					"label", this.m_language), 50, this.getFieldEditorParent());
			sfe.setEmptyStringAllowed(true);
			addField(sfe);

			sfe = new StringFieldEditor(this.getConfigNamespace() + SEPARATOR
					+ RegExpURLRequester.REGEXP_STREETNO, this.m_i18n
					.getString(this.getNamespace(),
							RegExpURLRequester.REGEXP_STREETNO, "label",
							this.m_language), 50, this.getFieldEditorParent());
			sfe.setEmptyStringAllowed(true);
			addField(sfe);

			sfe = new StringFieldEditor(this.getConfigNamespace() + SEPARATOR
					+ RegExpURLRequester.REGEXP_POSTALCODE, this.m_i18n
					.getString(this.getNamespace(),
							RegExpURLRequester.REGEXP_POSTALCODE, "label",
							this.m_language), 50, this.getFieldEditorParent());
			sfe.setEmptyStringAllowed(true);
			addField(sfe);
			sfe = new StringFieldEditor(this.getConfigNamespace() + SEPARATOR
					+ RegExpURLRequester.REGEXP_CITY, this.m_i18n.getString(
					this.getNamespace(), RegExpURLRequester.REGEXP_CITY,
					"label", this.m_language), 50, this.getFieldEditorParent());
			sfe.setEmptyStringAllowed(true);
			addField(sfe);
			sfe = new StringFieldEditor(this.getConfigNamespace() + SEPARATOR
					+ RegExpURLRequester.REGEXP_AREACODE, this.m_i18n
					.getString(this.getNamespace(),
							RegExpURLRequester.REGEXP_AREACODE, "label",
							this.m_language), 50, this.getFieldEditorParent());
			sfe.setEmptyStringAllowed(true);
			addField(sfe);
			sfe = new StringFieldEditor(this.getConfigNamespace() + SEPARATOR
					+ RegExpURLRequester.REGEXP_PHONE, this.m_i18n.getString(
					this.getNamespace(), RegExpURLRequester.REGEXP_PHONE,
					"label", this.m_language), 50, this.getFieldEditorParent());
			sfe.setEmptyStringAllowed(true);
			addField(sfe);

			if (!this.m_i18n.getString(this.getNamespace(),"intareacode", "label", this.m_language).equalsIgnoreCase("intareacode")) {
				sfe = new StringFieldEditor(this.getConfigNamespace() + SEPARATOR
					+ "intareacode", this.m_i18n.getString(this.getNamespace(),
					"intareacode", "label", this.m_language), 6, this
					.getFieldEditorParent());
				sfe.setEmptyStringAllowed(false);
				sfe.setTextLimit(4);
				addField(sfe);
			}
		}
	}
}
