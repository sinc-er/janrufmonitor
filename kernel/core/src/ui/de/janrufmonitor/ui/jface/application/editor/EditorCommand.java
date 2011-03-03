package de.janrufmonitor.ui.jface.application.editor;

import java.util.Properties;

import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAsyncDisplayCommand;

public class EditorCommand extends AbstractAsyncDisplayCommand implements IConfigurable {

	private static String NAMESPACE = "ui.jface.application.editor.Editor";
	
	private IRuntime m_runtime;
	
	private Editor j;
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getNamespace() {
		return EditorCommand.NAMESPACE;
	}

	public boolean isExecutable() {
		return true;
	}

	public String getID() {
		return "EditorCommand";
	}

	public String getConfigurableID() {
		return this.getID();
	}

	public void setConfiguration(Properties configuration) {
	}

	public void asyncExecute() {
		if (this.j==null) {
			this.j = new Editor();
			this.j.open();
			this.j=null;
		} else {
			this.j.focus();
		}
	}

}
