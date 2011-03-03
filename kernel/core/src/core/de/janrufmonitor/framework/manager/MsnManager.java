package de.janrufmonitor.framework.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.PIMRuntime;

public class MsnManager implements IMsnManager, IConfigurable {

    private String ID = "MsnManager";
    private String NAMESPACE = "manager.MsnManager";
    private static MsnManager m_instance = null;
    
    private Logger m_logger;
    private Properties m_configuration;
    private List m_msns;
    
    private String CONFIG_LABEL = "_label";
    private String CONFIG_MSN = "_msn";
    private String CONFIG_MSNLIST = "list";
    
    private MsnManager() {
        this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
    }
    
    public static synchronized MsnManager getInstance() {
        if (MsnManager.m_instance == null) {
            MsnManager.m_instance = new MsnManager();
        }
        return MsnManager.m_instance;
    }
    
    public IMsn createMsn(String msn) {
        IMsn aMsn = PIMRuntime.getInstance().getCallFactory().createMsn(msn, "");
        aMsn.setAdditional(this.getMsnLabel(aMsn));
        return aMsn;
    }
    
    public String getManagerID() {
        return this.ID;
    }
    
    public String getMsnLabel(IMsn msn) {
        return this.getMsnLabel(msn.getMSN());      
    }
    
    public String[] getMsnList() {
    	this.buildMsnList();
        
        this.m_logger.info("MsnList: "+this.m_msns.toString());
        
        String[] msns = new String[this.m_msns.size()];
        for (int i=0;i<this.m_msns.size();i++) {
        	msns[i] = (String) this.m_msns.get(i);
        }
        
        return msns;        
    }
    
    private void buildMsnList() {
    	if (this.m_msns==null) {
    		this.m_msns = new ArrayList();

    		List validMsns = new ArrayList();
    		StringTokenizer st = new StringTokenizer(m_configuration.getProperty(CONFIG_MSNLIST, ""), ",");
    		while (st.hasMoreTokens()) {
    			validMsns.add(st.nextToken());
    		}
    		
            Iterator iter = this.m_configuration.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                String msn = m_configuration.getProperty(key, "");
                if (key.endsWith(this.CONFIG_MSN) && msn.length()>0 && validMsns.contains(msn)) {
                	this.m_msns.add(msn);
                }
            }
            Collections.sort(this.m_msns);
    	}
    }
    
    public int getPriority() {
        return 999;
    }
    
    public String getConfigurableID() {
        return this.ID;
    }
    
    public String getNamespace() {
        return this.NAMESPACE;
    }
    
    public void setConfiguration(Properties configuration) {
        this.m_configuration = configuration;
        if (this.m_msns!=null)
        	this.m_msns.clear();
        this.m_msns = null;
    }
    
    public String getMsnLabel(String msn) {
        Iterator iter = this.m_configuration.keySet().iterator();
        String key = null;
        while (iter.hasNext()) {
            key = (String) iter.next();
            if (key.equalsIgnoreCase(msn + this.CONFIG_LABEL)) {
                return (this.m_configuration.getProperty(key) != null ? this.m_configuration.getProperty(key) : "");
            }
        }
        return "";          
    }

	public void startup() {
		PIMRuntime.getInstance().getConfigurableNotifier().register(this);
	}

	public void shutdown() {
		PIMRuntime.getInstance().getConfigurableNotifier().unregister(this);
		m_instance = null;
	}

	public boolean existMsn(IMsn msn) {
		this.buildMsnList();
		return this.m_msns.contains(msn.getMSN());
	}

	public void restart() {
		this.shutdown();
		this.startup();
	}
	
	public boolean isMsnMonitored(IMsn msn) {
		String detectMsnValue = PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(
			IJAMConst.GLOBAL_NAMESPACE,
			IJAMConst.GLOBAL_DETECT_ALL_MSN
		);
	
		if (detectMsnValue.equalsIgnoreCase("true"))
			return true;
	
		this.m_logger.info("Detect all MSNs option is disabled.");
		return this.existMsn(msn);
	}

	public void setManagerID(String id) {
	}
    
}
