package de.janrufmonitor.application;

import java.util.logging.LogManager;

import org.tanukisoftware.wrapper.*;

import de.janrufmonitor.framework.IJAMConst;

public class RunWin32Service
	implements WrapperListener {
	
	static boolean isStarted;
	static int count;

	public Integer start(String[] args) {
		count ++;
		if (!isStarted) {			
			isStarted = true;
			RunUI.go();
		} else {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).warning("Tried to start jAnrufmonitor service "+count+" times.");
		}
	
		LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).info("Started as Service.");
		return null;
	}

	public int stop(int exitCode) {
		RunUI.quit();
		LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).info("Stopped as Service.");
		isStarted = false;
		return exitCode;
	}

	public void controlEvent(int event) {
		if ((event == WrapperManager.WRAPPER_CTRL_LOGOFF_EVENT)
			&& WrapperManager.isLaunchedAsService()) {
				LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).info("Service received LOGOFF_EVENT.");
		} else {
			WrapperManager.stop(0);
		}
	}

	public static void main(String[] args) {
		WrapperManager.start(new RunWin32Service(), args);
	}  
	
}
