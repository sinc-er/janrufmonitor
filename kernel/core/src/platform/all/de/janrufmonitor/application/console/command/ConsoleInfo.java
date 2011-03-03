package de.janrufmonitor.application.console.command;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.command.AbstractConsoleCommand;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class ConsoleInfo extends AbstractConsoleCommand {

	private String ID = "info";
	private String NAMESPACE = "application.console.command.ConsoleInfo";
	
	private IRuntime m_runtime;

	public IRuntime getRuntime() {
		if (m_runtime==null)
			m_runtime = PIMRuntime.getInstance();
		return m_runtime;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public void execute() {
		System.out.println("jAnrufmonitor "+IJAMConst.VERSION_DISPLAY);
		System.out.println("Build: "+IJAMConst.VERSION_BUILD);
	}

	public boolean isExecutable() {
		return true;
	}

	public boolean isExecuting() {
		return false;
	}

	public String getID() {
		return this.ID;
	}

	public String getLabel() {
		return "Info                - INFO + <ENTER>";
	}

}
