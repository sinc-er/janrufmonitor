package de.janrufmonitor.ui.jface.application.journal;

import java.util.Properties;

import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAsyncDisplayCommand;

public class JournalCommand extends AbstractAsyncDisplayCommand implements IConfigurable {

	private static String NAMESPACE = "ui.jface.application.journal.Journal";
	
	private IRuntime m_runtime;
	
	private Journal j;

	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getNamespace() {
		return JournalCommand.NAMESPACE;
	}

	public boolean isExecutable() {
		return true;
	}

	public String getID() {
		return "JournalCommand";
	}

	public String getConfigurableID() {
		return this.getID();
	}

	public void setConfiguration(Properties configuration) {
	}

	public void asyncExecute() {
		if (this.j==null) {
			// check if blocking or not
			//boolean isBlocking = (getRuntime().getConfigManagerFactory().getConfigManager().getProperty(Journal.NAMESPACE, "blocking").equalsIgnoreCase("true"));
			this.j = new Journal();
			this.j.open();
			this.j=null;
		} else {
			this.j.focus();
		}
	}

}
