package de.janrufmonitor.ui.jface.application.last10calls;

import java.util.Properties;

import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAsyncDisplayCommand;

public class Last10CallsCommand extends AbstractAsyncDisplayCommand implements IConfigurable {

	private IRuntime m_runtime;
	
	private Last10Calls j;

	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getNamespace() {
		return Last10Calls.NAMESPACE;
	}

	public boolean isExecutable() {
		return true;
	}

	public String getID() {
		return "Last10CallsCommand";
	}

	public String getConfigurableID() {
		return this.getID();
	}

	public void setConfiguration(Properties configuration) {
	}

	public void asyncExecute() {
		if (this.j==null) {
			this.j = new Last10Calls();
			this.j.open();
			this.j=null;
		} else {
			this.j.focus();
		}
	}

}
