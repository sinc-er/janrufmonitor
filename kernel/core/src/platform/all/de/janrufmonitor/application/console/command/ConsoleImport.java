package de.janrufmonitor.application.console.command;

import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.command.AbstractConsoleCommand;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.imexport.ICallerImporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.repository.imexport.ImExportFactory;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class ConsoleImport extends AbstractConsoleCommand {

	private String ID = "import";
	private String NAMESPACE = "application.console.command.ConsoleImport";
	
	private IRuntime m_runtime;
	private boolean isExecuting; 
	
	public String getLabel() {
		return "Import callers      - IMPORT <path-to-DAT-file> + <ENTER>";
	}
	
	public IRuntime getRuntime() {
		if (m_runtime==null)
			m_runtime = PIMRuntime.getInstance();
		return m_runtime;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public void execute() throws Exception {
		this.isExecuting = true;
		
		if (this.getExecuteParams().length!=1) {
			System.out.println("ERROR: Paramters are invalid. Please specify a valid module for installation.");
			this.isExecuting = false;
			return;
		}
		
		String filename = this.getExecuteParams()[0];
		
		IImExporter imp = ImExportFactory.getInstance().getImporter("OldDatFileCallerImporter");
		if (imp!=null && imp instanceof ICallerImporter) {
			((ICallerImporter)imp).setFilename(filename);
			ICallerList cl = ((ICallerImporter)imp).doImport();
			ICallerManager mgr = this.getRuntime().getCallerManagerFactory().getCallerManager("CallerDirectory");
			if (mgr!=null && mgr.isActive() && mgr.isSupported(IWriteCallerRepository.class)) {
				((IWriteCallerRepository)mgr).setCaller(cl);
				System.out.println("INFO: Successfully imported "+cl.size()+" caller entries.");
			} else {
				System.out.println("ERROR: Caller manager is missing.");
			}
		} else {
			System.out.println("ERROR: import filter for DAT files is missing.");
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
