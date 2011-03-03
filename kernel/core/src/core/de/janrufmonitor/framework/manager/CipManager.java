package de.janrufmonitor.framework.manager;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.ICip;
import de.janrufmonitor.runtime.PIMRuntime;

public class CipManager implements ICipManager {

    private String ID = "CipManager";
    private String NAMESPACE = "manager.CipManager";
    
    private static CipManager m_instance = null;
    
    private CipManager() { }
    
    public static synchronized CipManager getInstance() {
        if (CipManager.m_instance == null) {
            CipManager.m_instance = new CipManager();
        }
        return CipManager.m_instance;
    }    
    
    public ICip createCip(String cip) {
        return PIMRuntime.getInstance().getCallFactory().createCip(cip, "");
    }
    
    public String getCipLabel(ICip cip, String language) {
        String l_cip = cip.getCIP();
        return this.getCipLabel(l_cip, language);
    }
    
    public String getCipLabel(String cip, String language) {
        if (language.equalsIgnoreCase("")) {
            language = PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE);
        }
        return PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager().getString(this.NAMESPACE, cip, "label", language);
    }
    
    public String getManagerID() {
        return this.ID;
    }
    
    public int getPriority() {
        return 999;
    }
    
    public String[] getCipList() {
        String[] cipList = new String[] {
        	"1",
        	"2",
        	"3",
        	"4",
        	"5",
        	"6",
			"7",
			"8",
			"9",
			"16",
			"17",
			"18",
			"19",
			"20",
			"21",
			"22",
			"23",
			"24",
			"25",
			"26",
			"27",
			"28",
			"100"
        };
        return cipList;
    }

	public void startup() {	}

	public void shutdown() {
		m_instance = null;
	}

	public void restart() {
		this.shutdown();
		this.startup();
	}

	public void setManagerID(String id) {
	}
    
}
