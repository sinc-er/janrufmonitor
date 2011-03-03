package de.janrufmonitor.application.console.command;

import java.util.List;
import java.util.Properties;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.update.UpdateManager;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.command.AbstractConsoleCommand;
import de.janrufmonitor.framework.installer.InstallerConst;

public class ConsoleSearchUpdates extends AbstractConsoleCommand {

	private String ID = "searchupdates";
	private String NAMESPACE = "application.console.command.ConsoleSearchUpdates";
	
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
		System.out.println("Available updates for jAnrufmonitor:");
		System.out.println("------------------------------------");
		System.out.println("");

		List updates = new UpdateManager().getUpdates();
		this.checkConsoleEnabled(updates);
		if (updates.size()>0) {
			System.out.println("Modules to be updated available on update server: ");
			
			Properties p = null;
			for (int i=0;i<updates.size();i++){
				p = (Properties) updates.get(i);
				System.out.print(PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager().getString(p.getProperty(InstallerConst.DESCRIPTOR_NAMESPACE, "-"), "title", "label", PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE)));
				System.out.print(", ");
				System.out.print(p.getProperty(InstallerConst.DESCRIPTOR_VERSION));
				System.out.print(" -> [ UPDATE ");
				System.out.print(p.getProperty(InstallerConst.DESCRIPTOR_NAME));
				System.out.println(" ]");
			}
		} else {
			System.out.println("No module updates available on update server.");	
		}
		System.out.println("");
	}
	
	private void checkConsoleEnabled(List updates) {
		Properties p = null;
		for (int i=updates.size()-1;i>=0;i--) {
			p = (Properties) updates.get(i);
			if (!Boolean.parseBoolean(p.getProperty(InstallerConst.DESCRIPTOR_CONSOLE_ENABLED, "false"))) {
				updates.remove(i);
			}
		}
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
		return "Search updates      - SEARCHUPDATES + <ENTER>";
	}

}
