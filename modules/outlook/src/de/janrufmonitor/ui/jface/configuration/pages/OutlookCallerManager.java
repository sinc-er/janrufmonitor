package de.janrufmonitor.ui.jface.configuration.pages;

import org.eclipse.jface.preference.ComboFieldEditor;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractServiceFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;
import de.janrufmonitor.ui.jface.configuration.controls.BooleanFieldEditor;

public class OutlookCallerManager extends AbstractServiceFieldEditorConfigPage {

	private String NAMESPACE = "ui.jface.configuration.pages.OutlookCallerManager";
    private String CONFIG_NAMESPACE = "repository.OutlookCallerManager";

    private IRuntime m_runtime;
    
	public String getConfigNamespace() {
		return this.CONFIG_NAMESPACE;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null) 
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	public String getParentNodeID() {
		return IConfigPage.CALLER_NODE;
	}

	public String getNodeID() {
		return "OutlookCallerManager".toLowerCase();
	}

	public int getNodePosition() {
		return 11;
	}

	protected void createFieldEditors() {
		super.createFieldEditors();
		
		if (isExpertMode()) {
			BooleanFieldEditor bfe = new BooleanFieldEditor(
					getConfigNamespace()+SEPARATOR+"split",
				this.m_i18n.getString(this.getNamespace(), "split", "label", this.m_language),
				this.getFieldEditorParent()
			);
			addField(bfe);	
			
			bfe = new BooleanFieldEditor(
					getConfigNamespace()+SEPARATOR+"keepextension",
				this.m_i18n.getString(this.getNamespace(), "keepextension", "label", this.m_language),
				this.getFieldEditorParent()
			);
			addField(bfe);	
			
			ComboFieldEditor cfe = new ComboFieldEditor(
					getConfigNamespace()+SEPARATOR+"mode",	
					this.m_i18n.getString(this.getNamespace(), "mode", "label", this.m_language),
					new String[][] { 
						{this.m_i18n.getString(this.getNamespace(), "mode1", "label", this.m_language), "1"}, 
						{this.m_i18n.getString(this.getNamespace(), "mode2", "label", this.m_language), "2"},
						{this.m_i18n.getString(this.getNamespace(), "mode3", "label", this.m_language), "3"}
					},	
					this.getFieldEditorParent()
				);
				addField(cfe);
			
			cfe = new ComboFieldEditor(
				getConfigNamespace()+SEPARATOR+"index",	
				this.m_i18n.getString(this.getNamespace(), "index", "label", this.m_language),
				new String[][] { 
					{"1 min", "1"}, 
					{"5 min", "5"},
					{"30 min", "30"},
					{"60 min", "60"},
					{"120 min", "120"}
				},	
				this.getFieldEditorParent()
			);
			addField(cfe);
			
//			OutlookTransformer ot = new OutlookTransformer();
//			
//			List folders = ot.getAllContactFolders();
//			if (folders.size()>0) {
//				Label header = new Label(this.getFieldEditorParent(), SWT.NONE);
//				header.setText(this.m_i18n.getString(this.getNamespace(), "import", "label", this.m_language));
//				
//
//				int itemCount = 0;
//				String folder = null;
//				String label = null;
//				for (int i=0,j=folders.size();i<j;i++) {
//					folder = (String)folders.get(i);
//					itemCount = ot.getContactCount(folder);
//					if (itemCount>0) {
//						label = this.m_i18n.getString(this.getNamespace(), "sf", "label", this.m_language);
//						label = StringUtils.replaceString(label, "{%1}", folder);
//						label = StringUtils.replaceString(label, "{%2}", Integer.toString(itemCount));
//						bfe = new BooleanFieldEditor(
//							getConfigNamespace()+SEPARATOR+"subfolder_"+folder,
//							label,
//							this.getFieldEditorParent()
//						);
//						addField(bfe);	
//					}
//				}
//			}
		}
	}
}
