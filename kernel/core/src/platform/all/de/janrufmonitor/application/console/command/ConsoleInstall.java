package de.janrufmonitor.application.console.command;

import java.io.File;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.framework.command.AbstractConsoleCommand;
import de.janrufmonitor.framework.installer.InstallerEngine;


public class ConsoleInstall extends AbstractConsoleCommand {
	
	private String ID = "install";
	private String NAMESPACE = "application.console.command.ConsoleInstall";
	
	private IRuntime m_runtime;
	private boolean isExecuting; 

	public String getLabel() {
		return "Module installation - INSTALL <path-to-jam.zip-file> + <ENTER>";
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
		
		if (this.getExecuteParams().length!=1) {
			System.out.println("ERROR: Parameters are invalid. Please specify a valid module for installation.");
			this.isExecuting = false;
			return;
		}
		
		String filename = this.getExecuteParams()[0];
		
		File installFile = new File(filename);
		if (installFile.exists()) {
			if (InstallerEngine.getInstance().install(installFile)) {
				System.out.println("INFO: Installation successfully finished. Changes take effect after restart.");
			} else {
				System.out.println("ERROR: Corrupted jam.zip file. Installation failed.");
			}
		} else {
			System.out.println("ERROR: Cannot execute command INSTALL because the file <"+filename+"> could not be found.");
		}
		
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
