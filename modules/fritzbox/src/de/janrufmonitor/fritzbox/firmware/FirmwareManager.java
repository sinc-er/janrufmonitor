package de.janrufmonitor.fritzbox.firmware;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.fritzbox.FritzBoxConst;
import de.janrufmonitor.fritzbox.FritzBoxMonitor;
import de.janrufmonitor.fritzbox.firmware.exception.DeleteCallListException;
import de.janrufmonitor.fritzbox.firmware.exception.DoBlockException;
import de.janrufmonitor.fritzbox.firmware.exception.DoCallException;
import de.janrufmonitor.fritzbox.firmware.exception.FritzBoxInitializationException;
import de.janrufmonitor.fritzbox.firmware.exception.FritzBoxLoginException;
import de.janrufmonitor.fritzbox.firmware.exception.GetBlockedListException;
import de.janrufmonitor.fritzbox.firmware.exception.GetCallListException;
import de.janrufmonitor.fritzbox.firmware.exception.GetCallerListException;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class FirmwareManager {
	
	private static FirmwareManager m_instance = null;
	private Logger m_logger;
	private IRuntime m_runtime;
	
	private IFritzBoxFirmware m_fw;
	private Thread m_timeoutThread;
    
    private FirmwareManager() {
        this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
    }
    
    public static synchronized FirmwareManager getInstance() {
        if (FirmwareManager.m_instance == null) {
        	FirmwareManager.m_instance = new FirmwareManager();
        }
        return FirmwareManager.m_instance;
    }
    
    public void startup() {
   		try {
			this.createFirmwareInstance();
		} catch (FritzBoxInitializationException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		}
    }
    
    public void login() throws FritzBoxLoginException {
    	if (this.m_fw==null)
			try {
				this.createFirmwareInstance();
			} catch (FritzBoxInitializationException e) {
				throw new FritzBoxLoginException(e.getMessage());
			}
		this.m_fw.login();
    }
    
    public boolean isInstance(Class c) {
    	return c.isInstance(this.m_fw);
    }
    
    public String getFirmwareDescription() throws FritzBoxLoginException {
    	if (this.m_fw==null)
			return "";
		return this.m_fw.toString();
    }
    
    public List getCallList() throws GetCallListException, IOException {
    	if (this.m_fw==null)
			try {
				this.createFirmwareInstance();
			} catch (FritzBoxInitializationException e) {
				throw new GetCallListException(e.getMessage());
			}
		return this.m_fw.getCallList();
    }
    
    public List getCallerList() throws GetCallerListException, IOException {
    	if (this.m_fw==null)
			try {
				this.createFirmwareInstance();
			} catch (FritzBoxInitializationException e) {
				throw new GetCallerListException(e.getMessage());
			}
		return this.m_fw.getCallerList();
    }
    
    public List getBlockedList() throws GetBlockedListException, IOException {
    	if (this.m_fw==null)
			try {
				this.createFirmwareInstance();
			} catch (FritzBoxInitializationException e) {
				throw new GetBlockedListException(e.getMessage());
			}
		return this.m_fw.getBlockedList();
    }
    
    public void deleteCallList() throws DeleteCallListException, IOException {
    	if (this.m_fw==null)
			try {
				this.createFirmwareInstance();
			} catch (FritzBoxInitializationException e) {
				throw new DeleteCallListException(e.getMessage());
			}
		this.m_fw.deleteCallList();
    }
    
    public void doBlock(String number) throws DoBlockException, IOException {
    	if (this.m_fw==null)
			try {
				this.createFirmwareInstance();
			} catch (FritzBoxInitializationException e) {
				throw new DoBlockException(e.getMessage());
			}
		this.m_fw.doBlock(number);
    }
	
    public void doCall(String number, String extension) throws DoCallException, IOException {
    	if (this.m_fw==null)
			try {
				this.createFirmwareInstance();
			} catch (FritzBoxInitializationException e) {
				throw new DoCallException(e.getMessage());
			}
		this.m_fw.doCall(number, extension);
    }

    
    public void shutdown() {
    	if (m_timeoutThread!=null && m_timeoutThread.isAlive()) {
    		m_timeoutThread.interrupt();
    	}
    	
    	if (this.m_fw!=null) {
    		this.m_fw.destroy();
    	}
    	this.m_fw = null;
    }
    
    private synchronized void createFirmwareInstance() throws FritzBoxInitializationException{
    	if (this.m_fw==null) {
    		this.m_fw = new SessionIDFritzBoxFirmware(getFritzBoxAddress(), getFritzBoxPort(), getFritzBoxPassword());
    		try {
				this.m_fw.init();
				if (this.m_logger.isLoggable(Level.INFO))
					this.m_logger.info("Detected FritzBox Session ID firmware: "+this.m_fw.toString());
			} catch (FritzBoxInitializationException ex) {
				this.m_fw = new PasswordFritzBoxFirmware(getFritzBoxAddress(), getFritzBoxPort(), getFritzBoxPassword());
				try {
					this.m_fw.init();
					if (this.m_logger.isLoggable(Level.INFO))
						this.m_logger.info("Detected FritzBox standard firmware (password protected): "+this.m_fw.toString());
				} catch (FritzBoxInitializationException e) {
					this.m_fw = null;
					throw new FritzBoxInitializationException(e.getMessage());
				}
			}
			if (this.m_fw!=null) {
				if (this.m_fw.getFirmwareTimeout()>0) {
					this.launchTimeoutThread();
				}
			}
    	}
    }
    
    private void launchTimeoutThread() {
    	if (m_timeoutThread!=null && m_timeoutThread.isAlive()) {
    		m_timeoutThread.interrupt();
    	}
    	
    	m_timeoutThread = new Thread() {
			public void run() {
				try {
					m_logger.info("Session ID timeout set to "+(m_fw.getFirmwareTimeout()/1000)+" sec.");
					Thread.sleep(m_fw.getFirmwareTimeout());
					//Thread.sleep(60000);
					
					if (m_fw!=null) m_fw.destroy();
					m_fw = null;
					m_logger.info("Automatic FritzBox timeout for logout reached.");
					if (getFritzBoxAutoReconnect()) {
						m_logger.info("Automatic re-connect to FritzBox...");
						try {
							createFirmwareInstance();
						} catch (FritzBoxInitializationException e) {
							m_logger.log(Level.SEVERE, e.getMessage(), e);
						}
					}
				} catch (InterruptedException e) {
					m_logger.log(Level.WARNING,"JAM-FritzBoxFirmwareTimeout-Thread gets interrupted.: "+e.getMessage(), e);
				}
			}
		};
		m_timeoutThread.setName("JAM-FritzBoxFirmwareTimeout-Thread-(deamon)");
		m_timeoutThread.setDaemon(true);
		m_timeoutThread.start();
    }
    
    private String getFritzBoxAddress() {
    	return getRuntime().getConfigManagerFactory().getConfigManager().getProperty(FritzBoxMonitor.NAMESPACE, FritzBoxConst.CFG_IP);
    }
    
    private String getFritzBoxPassword() {
    	return getRuntime().getConfigManagerFactory().getConfigManager().getProperty(FritzBoxMonitor.NAMESPACE, FritzBoxConst.CFG_PASSWORD);
    }
    
    private String getFritzBoxPort() {
    	return getRuntime().getConfigManagerFactory().getConfigManager().getProperty(FritzBoxMonitor.NAMESPACE, FritzBoxConst.CFG_PORT);
    }
    
    private boolean getFritzBoxAutoReconnect() {
    	return getRuntime().getConfigManagerFactory().getConfigManager().getProperty(FritzBoxMonitor.NAMESPACE, FritzBoxConst.CFG_AUTO_RECONNECT_SESSIONID).equalsIgnoreCase("true");
    }
    
    private IRuntime getRuntime() {
    	if (this.m_runtime==null) {
    		this.m_runtime = PIMRuntime.getInstance();
    	}
    	return this.m_runtime;
    }
    
}
