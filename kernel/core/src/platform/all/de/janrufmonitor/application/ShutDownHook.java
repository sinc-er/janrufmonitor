package de.janrufmonitor.application;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.PIMRuntime;

public class ShutDownHook extends Thread {

	Logger m_logger;
	
	public void run() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		this.m_logger.info("Hook program termination: jAnrufmonitor is shutting down ...");
		
		// added 2005/03/14: for shutdown of OS level - problem with some telephone systems
		PIMRuntime.getInstance().enableMonitorListener(false);
		PIMRuntime.getInstance().shutdown();
		//System.exit(0);
	}

}
