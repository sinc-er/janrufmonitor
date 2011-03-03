package de.janrufmonitor.ui.jface.service.server;

import java.util.Properties;

import de.janrufmonitor.framework.command.AbstractCommand;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.swt.DisplayManager;

public class StatusMonitorCommand extends AbstractCommand implements IConfigurable {

	private static String NAMESPACE = "ui.jface.application.server.StatusMonitor";
	
	private IRuntime m_runtime;
	private boolean isExecuting;
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getNamespace() {
		return StatusMonitorCommand.NAMESPACE;
	}

	public boolean isExecutable() {
		return true;
	}

	public String getID() {
		return "StatusMonitorCommand";
	}

	public String getConfigurableID() {
		return this.getID();
	}

	public void setConfiguration(Properties configuration) {
	}

	public void execute() {
		this.isExecuting = true;
		Thread thread = new Thread () {
			public void run () {
				DisplayManager.getDefaultDisplay().asyncExec(
					new Runnable() {
						public void run() {
							StatusMonitor b = new StatusMonitor();
							b.open();
							b = null;
						}
					}
				);
			}
		};
		thread.setName("JAM-"+this.getID()+"-Thread-(non-deamon)");
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			this.m_logger.severe(e.getMessage());
		}
		this.isExecuting = false;
	}

	public boolean isExecuting() {
		return this.isExecuting;
	}

}
