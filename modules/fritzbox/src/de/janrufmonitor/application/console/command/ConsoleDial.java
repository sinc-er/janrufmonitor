package de.janrufmonitor.application.console.command;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.framework.command.AbstractConsoleCommand;
import de.janrufmonitor.fritzbox.firmware.FirmwareManager;

public class ConsoleDial extends AbstractConsoleCommand {
	
	private String ID = "dial";
	private String NAMESPACE = "application.console.command.ConsoleDial";
	
	private IRuntime m_runtime;
	private boolean isExecuting; 

	public String getLabel() {     
		return "Dial a number       - DIAL <number> <extension> + <ENTER>";
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
		
		if (this.getExecuteParams().length!=2) {
			System.out.println("ERROR: Parameters are invalid. Please specify a valid number e.g. +4930123456 or 030123456 and a fritzbox extension");
			System.out.println();
			System.out.println("Valid extensions:");
			System.out.println("-----------------");
			System.out.println("1 for Fon1, 2 for Fon2, 3 for Fon3");
			System.out.println("51 for ISDN1 ... 59 for ISDN 6, 50 for all ISDN devices");
			
			this.isExecuting = false;
			return;
		}
		
		String dial = this.getExecuteParams()[0];
		if (dial.length()<5) {
			System.out.println("ERROR: Invalid number length. Number must contain at least be 5 digits and start with 0.");
			this.isExecuting = false;
			return;
		}
		if (!dial.startsWith("0")) dial = "0"+dial;
		
		String ext = this.getExecuteParams()[1];
		int i = Integer.parseInt(ext);
		if ((i>=0 && i<=9) || (i>=50 && i<=59)) {
			FirmwareManager fwm = FirmwareManager.getInstance();
			try {
				fwm.login();
				fwm.doCall(dial + "#", ext);				
			} catch (Exception e) {
				System.out.println("ERROR: Dialing fritzbox failed: "+e.getMessage());
			}
		} else {
			System.out.println("ERROR: Invalid fritzbox extension: "+i+"; allowed values 1 for Fon1, 2 for Fon2, 3 for Fon3, 51 for ISDN1 ... 59 for ISDN 6, 50 for all ISDN devices.");
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
