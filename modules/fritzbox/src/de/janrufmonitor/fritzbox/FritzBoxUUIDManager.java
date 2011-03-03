package de.janrufmonitor.fritzbox;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;

public class FritzBoxUUIDManager {

	private static FritzBoxUUIDManager m_instance = null;
	
	private Logger m_logger;
	private String m_uuid;
	private String m_prevuuid;
	private long m_lastcheck;
    
    private FritzBoxUUIDManager() {
        this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
        this.m_uuid = "0";
        this.m_prevuuid = "0";
        this.m_lastcheck = System.currentTimeMillis();
    }
    
    public static synchronized FritzBoxUUIDManager getInstance() {
        if (FritzBoxUUIDManager.m_instance == null) {
        	FritzBoxUUIDManager.m_instance = new FritzBoxUUIDManager();
        }
        return FritzBoxUUIDManager.m_instance;
    }
    
    public String calculateUUID(String uuid) {
    	// check wether 1 minute is already over
    	if (this.isTimeElapsed()) {
    		this.m_uuid = "0";
    		this.m_prevuuid = "0";
    	}
    	
    	if (uuid.equalsIgnoreCase(this.m_uuid) && !uuid.equalsIgnoreCase(this.m_prevuuid)) {
    		this.m_prevuuid = uuid;
    		uuid += "-1";
    		this.m_logger.info("New 1st UUID calculated: "+uuid);
    	}
    	if (uuid.equalsIgnoreCase(this.m_prevuuid)) {
    		uuid += "-1-1";
    		this.m_logger.info("New 2nd UUID calculated: "+uuid);
    	}    	
    	this.m_uuid = uuid;
    	
    	this.m_lastcheck = System.currentTimeMillis();
    	return uuid;
    }
    
    private boolean isTimeElapsed() {
    	return (System.currentTimeMillis() - this.m_lastcheck > 60000); 
    }
    
    public void init() {
    	this.m_uuid = "0";
    	this.m_prevuuid = "0";
        this.m_lastcheck = System.currentTimeMillis();
    }
}
