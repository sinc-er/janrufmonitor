package de.janrufmonitor.ui.jface.configuration.pages;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Button;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractEmptyConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;

public class BasicIsdnSettings extends AbstractEmptyConfigPage {
	
    private String NAMESPACE = "ui.jface.configuration.pages.BasicIsdnSettings";

	private IRuntime m_runtime;

	StringFieldEditor clir;
	StringFieldEditor intarea;
	StringFieldEditor truncate;
	StringFieldEditor intareaprefix;
	Button autodetectnational;
	Button autodetectinternational;
	Button ad;
	
	public String getParentNodeID() {
		return IConfigPage.ROOT_NODE;
	}
	
	public String getNodeID() {
		return "BasicIsdnSettings".toLowerCase();
	}

	public int getNodePosition() {
		return 1;
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
