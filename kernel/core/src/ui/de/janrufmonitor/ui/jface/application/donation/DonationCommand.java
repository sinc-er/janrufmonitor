package de.janrufmonitor.ui.jface.application.donation;

import java.util.Properties;
import java.util.logging.Level;

import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAsyncDisplayCommand;
import de.janrufmonitor.ui.jface.dialogs.DonationDialog;
import de.janrufmonitor.ui.swt.DisplayManager;

public class DonationCommand extends AbstractAsyncDisplayCommand implements IConfigurable {

	private static String NAMESPACE = "ui.jface.dialogs.DonationDialog";
	
	private IRuntime m_runtime;

	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getNamespace() {
		return DonationCommand.NAMESPACE;
	}

	public boolean isExecutable() {
		return true;
	}

	public String getID() {
		return "DonationCommand";
	}

	public String getConfigurableID() {
		return this.getID();
	}

	public void setConfiguration(Properties configuration) {
	}

	public void asyncExecute() {
		try {
			Thread.sleep(30*1000);
		} catch (InterruptedException e) {
			this.m_logger.log(Level.SEVERE, e.toString(), e);
		}
		DonationDialog id = new DonationDialog(new Shell(DisplayManager.getDefaultDisplay()));
		id.open();
	}

}
