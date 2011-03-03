package de.janrufmonitor.ui.jface.configuration.pages;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractEmptyConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;

public class Advanced extends AbstractEmptyConfigPage {
	
    private String NAMESPACE = "ui.jface.configuration.pages.Advanced";
    
	private IRuntime m_runtime;
	
	public String getParentNodeID() {
		return IConfigPage.ROOT_NODE;
	}
	
	public String getNodeID() {
		return IConfigPage.ADVANCED_NODE;
	}

	public int getNodePosition() {
		return 5;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null) 
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}
	
}
