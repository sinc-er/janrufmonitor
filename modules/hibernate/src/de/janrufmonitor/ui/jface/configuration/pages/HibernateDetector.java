package de.janrufmonitor.ui.jface.configuration.pages;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractServiceFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;

public class HibernateDetector extends AbstractServiceFieldEditorConfigPage {
	
    private String NAMESPACE = "ui.jface.configuration.pages.HibernateDetector";
    private String CONFIG_NAMESPACE = "service.HibernateDetector";
    
	private IRuntime m_runtime;
	
	public String getParentNodeID() {
		return IConfigPage.SERVICE_NODE;
	}
	
	public String getNodeID() {
		return "HibernateDetector".toLowerCase();
	}

	public int getNodePosition() {
		return 0;
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
	}

}
