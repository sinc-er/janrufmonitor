package de.janrufmonitor.ui.jface.configuration.pages;

import java.util.Date;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractServiceFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;
import de.janrufmonitor.ui.jface.configuration.controls.FileDialogFieldEditor;
import de.janrufmonitor.util.formatter.Formatter;

public class ArchiveJournal extends AbstractServiceFieldEditorConfigPage {
	
    private String NAMESPACE = "ui.jface.configuration.pages.ArchiveJournal";
    private String CONFIG_NAMESPACE = "repository.ArchiveJournal";
    
	private IRuntime m_runtime;
	
	public String getParentNodeID() {
		return IConfigPage.ADVANCED_NODE;
	}
	
	public String getNodeID() {
		return "ArchiveJournal".toLowerCase();
	}

	public int getNodePosition() {
		return 51;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null) 
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	public String getConfigNamespace() {
		return this.CONFIG_NAMESPACE;
	}
	
	protected void createFieldEditors() {
		super.createFieldEditors();
		//this.setTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));
		
		if (isExpertMode()) {
			FileDialogFieldEditor dfe = new FileDialogFieldEditor(
				this.CONFIG_NAMESPACE+SEPARATOR+"db",
				this.m_i18n.getString(this.getNamespace(), "db", "label", this.m_language),
				this.getFieldEditorParent()
			);
			dfe.setFileExtensions(new String[]{"*.archive"});
			addField(dfe);
			
			ComboFieldEditor cfe = new ComboFieldEditor(
					getConfigNamespace()+SEPARATOR+"timeframe",
					this.m_i18n.getString(this.getNamespace(), "timeframe", "label", this.m_language),
					new String[][] { 
						{this.m_i18n.getString(this.getNamespace(), "7", "label", this.m_language), "7"}, 
						{this.m_i18n.getString(this.getNamespace(), "14", "label", this.m_language), "14"},					
						{this.m_i18n.getString(this.getNamespace(), "30", "label", this.m_language), "30"},
						{this.m_i18n.getString(this.getNamespace(), "60", "label", this.m_language), "60"},
						{this.m_i18n.getString(this.getNamespace(), "90", "label", this.m_language), "90"},
						{this.m_i18n.getString(this.getNamespace(), "180", "label", this.m_language), "180"},
						{this.m_i18n.getString(this.getNamespace(), "365", "label", this.m_language), "365"},
					},
				this.getFieldEditorParent()
			);
			addField(cfe);	
			String lastrun = getRuntime().getConfigManagerFactory().getConfigManager().getProperty("repository.ArchiveJournal", "lastrun");
			if (lastrun!=null && lastrun.length()>1) {
				long time = Long.parseLong(lastrun);
				StringBuffer text = new StringBuffer();
				text.append(this.m_i18n.getString(this.getNamespace(), "lastrun", "label", this.m_language));
				text.append(Formatter.getInstance(getRuntime()).parse(IJAMConst.GLOBAL_VARIABLE_CALLTIME, new Date(time)));
							
				new Label(this.getFieldEditorParent(), SWT.NONE).setText(text.toString());
			}
		}
	}

}
