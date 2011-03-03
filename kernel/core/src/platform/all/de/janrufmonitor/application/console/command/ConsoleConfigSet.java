package de.janrufmonitor.application.console.command;

import de.janrufmonitor.framework.command.AbstractConsoleCommand;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;


public class ConsoleConfigSet extends AbstractConsoleCommand {
	
	private String ID = "cfg_set";
	private String NAMESPACE = "application.console.command.ConsoleConfigSet";
	
	private IRuntime m_runtime;
	private boolean isExecuting; 

	public String getLabel() {
		return null;
	}
	
	public IRuntime getRuntime() {
		if (m_runtime==null)
			m_runtime = PIMRuntime.getInstance();
		return m_runtime;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public void execute() {
		this.isExecuting = true;
		
		if (this.getExecuteParams().length<2) {
			System.out.println("ERROR: Parameters are invalid. Please enter syntax: CFG_SET <namespace>:<parameter>:<type> <value>");
			this.isExecuting = false;
			return;
		}
		
		String parameter = this.getExecuteParams()[0];
		String value = this.getExecuteParams()[1];
		
		if (this.getExecuteParams().length>2) {
			parameter = this.getExecuteParams()[0];
			for (int i=1;i<this.getExecuteParams().length;i++) {
				value = this.getExecuteParams()[i];
				if (i<this.getExecuteParams().length-1)
					value += " ";
			}
		}
		
		String[] parameter_data = parameter.split(":");
		if (parameter_data.length!=3) {
			System.out.println("ERROR: Parameter data is invalid. Please enter syntax: CFG_SET <namespace>:<parameter>:<type> <value>");
			this.isExecuting = false;
			return;
		}
		
		PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().setProperty(parameter_data[0], parameter_data[1], parameter_data[2], value);
		PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().saveConfiguration();

		System.out.println("Configuration successfully set for parameter "+parameter+", value="+value);
		
		System.out.println();
		this.isExecuting = false;
	}

	public boolean isExecutable() {
		return true;
	}

	public boolean isExecuting() {
		return this.isExecuting;
	}

	public String getID() {
		return this.ID;
	}

}
