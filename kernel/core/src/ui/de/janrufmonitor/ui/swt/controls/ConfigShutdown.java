package de.janrufmonitor.ui.swt.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.command.AbstractCommand;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTExecuter;

public class ConfigShutdown extends AbstractCommand {
	
	private String NAMESPACE = "ui.swt.controls.ConfigShutdown";

	public IRuntime getRuntime() {
		return PIMRuntime.getInstance();
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public void execute() {
		new SWTExecuter(false, getID()) {
			protected void execute() {
				// create shell
				Shell shell = new Shell(DisplayManager.getDefaultDisplay());
		
				shell.setSize(0,0);
		
				int style = SWT.APPLICATION_MODAL | SWT.OK;
				MessageBox messageBox = new MessageBox (shell, style);
				messageBox.setMessage (m_i18n.getString(getNamespace(), "shutdown", "label", m_language));
				messageBox.setText(m_i18n.getString(getNamespace(), "shutdowntitle", "label", m_language));
				if (messageBox.open () == SWT.OK) {
					m_logger.info("Shutting down jAnrufmonitor by ConfigShutdown Command. Please restart manually.");
					PIMRuntime.getInstance().shutdown();
					System.exit(0);
				}
			}
		}.start();
	}

	public boolean isExecutable() {
		return true;
	}

	public boolean isExecuting() {
		return false;
	}

	public String getID() {
		return "ConfigShutdown";
	}

}
