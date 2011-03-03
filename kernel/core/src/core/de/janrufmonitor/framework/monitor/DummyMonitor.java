package de.janrufmonitor.framework.monitor;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;

public class DummyMonitor implements IMonitor {

	private String ID = "DummyMonitor";
	
	Logger m_logger;

	public DummyMonitor() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
	}

	public void start() {
		this.m_logger.info("DummyMonitor started.");
	}
	
	public void stop() {
		this.m_logger.info("DummyMonitor stopped.");
	}

	public void setListener(IMonitorListener jml) {
		this.m_logger.info("DummyMonitor set with listener <"+jml.getClass().toString()+">");
	}

	public void reject(short cause) {
		this.m_logger.info("DummyMonitor reject with cause #"+new Short(cause).toString()+".");
	}

	public void release() {
		this.m_logger.info("DummyMonitor released.");
	}

	public String[] getDescription () {
		String[] info = new String[]{"", "", "", "" ,""};
		info[0] = "CAPI not found.";
		return info;
	}

	public boolean isStarted() {
		return false;
	}

	public String getID() {
		return this.ID;
	}

	public boolean isAvailable() {
		return true;
	}
}
