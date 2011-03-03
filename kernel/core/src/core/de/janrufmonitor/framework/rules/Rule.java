package de.janrufmonitor.framework.rules;

import java.util.StringTokenizer;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.framework.ICip;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.runtime.PIMRuntime;

public class Rule implements IRule {
    
	static int counter = 1;
	
	public static String GENERIC_SIGN = "*";
	public static String TOKEN_SEPARATOR_SIGN = "%";
	public static String PHONE_SEPARATOR_SIGN = ";";
	
	private String NAMESPACE = "rules.RuleEngine";
	
	private String m_name;
    private IMsn m_msn;
    private ICip m_cip;
    private String m_serviceID;
    private boolean m_active;
    private IPhonenumber[] m_phones;
    private IPhonenumber[] m_exphones;
    private String m_timeslot;
   
    private Logger m_logger;
    
    
	public Rule() { 
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
	}
	
	public Rule(String rule) {
		this();
		if (rule!=null)
			this.buildRule(rule);
	}
	
	public void setName(String name) {
		this.m_name = name;
	}
	
	public String getName() {
		if (this.m_name==null || this.m_name.trim().length()==0) {
			this.m_name = PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager().getString(
				this.NAMESPACE,
				"rulelabel",
				"label",
				PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE)
			);
			this.m_name += counter++;
		}
		return this.m_name;
	}
	
    public ICip getCip() {
        return this.m_cip;
    }
    
    public IMsn getMsn() {
        return this.m_msn;
    }
    
    public String getServiceID() {
        return this.m_serviceID;
    }
    
    public boolean isValid() {
        if (this.m_cip!=null && this.m_msn!=null && this.m_serviceID!=null) {
            return true;
        }
        return false;
    }
    
    public void setCip(ICip cip) {
        this.m_cip = cip;
    }
    
    public void setMsn(IMsn msn) {
        this.m_msn = msn;
    }
    
    public void setService(String serviceID) {
        this.m_serviceID = serviceID;
    }
    
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(this.getName());
		sb.append(Rule.TOKEN_SEPARATOR_SIGN);
    	sb.append(this.m_serviceID);
		sb.append(Rule.TOKEN_SEPARATOR_SIGN);
		sb.append((this.m_msn.getMSN().length()==0 ? GENERIC_SIGN : this.m_msn.getMSN()));
		sb.append(Rule.TOKEN_SEPARATOR_SIGN);
		sb.append((this.m_cip.getCIP().length()==0 ? GENERIC_SIGN : this.m_cip.getCIP()));
		sb.append(Rule.TOKEN_SEPARATOR_SIGN);
		sb.append(this.m_active);
		sb.append(Rule.TOKEN_SEPARATOR_SIGN);
    	if (this.m_phones==null || this.m_phones.length==0)
			sb.append(Rule.GENERIC_SIGN);
		else {
			for (int i=0;i<this.m_phones.length;i++) {
				if (this.m_phones[i].isClired()) {
					sb.append("[clired]");
				} else {
					sb.append("[");
					sb.append((this.m_phones[i].getIntAreaCode().length()==0 ? " " : this.m_phones[i].getIntAreaCode())+Rule.PHONE_SEPARATOR_SIGN);
					sb.append((this.m_phones[i].getAreaCode().length()==0 ? " " : this.m_phones[i].getAreaCode())+Rule.PHONE_SEPARATOR_SIGN);
					sb.append((this.m_phones[i].getCallNumber().length()==0 ? " " : this.m_phones[i].getCallNumber())+"]");
				}
			}
		}
		sb.append(Rule.TOKEN_SEPARATOR_SIGN);
		if (this.m_exphones==null || this.m_exphones.length==0)
			sb.append(Rule.GENERIC_SIGN);
		else {
			for (int i=0;i<this.m_exphones.length;i++) {
				if (this.m_exphones[i].isClired()) {
					sb.append("[clired]");
				} else {
					sb.append("[");
					sb.append((this.m_exphones[i].getIntAreaCode().length()==0 ? " " : this.m_exphones[i].getIntAreaCode())+Rule.PHONE_SEPARATOR_SIGN);
					sb.append((this.m_exphones[i].getAreaCode().length()==0 ? " " : this.m_exphones[i].getAreaCode())+Rule.PHONE_SEPARATOR_SIGN);
					sb.append((this.m_exphones[i].getCallNumber().length()==0 ? " " : this.m_exphones[i].getCallNumber())+"]");
				}
			}
		}
		sb.append(Rule.TOKEN_SEPARATOR_SIGN);
		if (this.m_timeslot==null || this.m_timeslot.length()==0)
			sb.append(Rule.GENERIC_SIGN);
		else {
			sb.append(this.m_timeslot);
		}
    	return sb.toString();
    }

	public void setActive(boolean active) {
		this.m_active = active;		
	}

	public boolean isActive() {
		return this.m_active;
	}

	public void setPhonenumbers(IPhonenumber[] phones) {
		this.m_phones = phones;
	}

	public IPhonenumber[] getPhonenumbers() {
		return this.m_phones;
	}

	public IPhonenumber[] getExcludePhonenumbers() {
		return this.m_exphones;
	}

	public void setExcludePhonenumbers(IPhonenumber[] phones) {
		this.m_exphones = phones;
	}
	
	public String getTimeslot() {
		return (this.m_timeslot==null||this.m_timeslot.length()==0 ? GENERIC_SIGN : this.m_timeslot);
	}
	
	public void setTimeslot(String ts){
		this.m_timeslot = ts;
	}
	
    private void buildRule(String rule) {
		this.m_logger.entering(Rule.class.getName(), "buildRule");
		StringTokenizer st = new StringTokenizer(rule, "%");
		
		if (st.countTokens()<5 &&  st.countTokens()>8) {
			this.m_logger.severe("Invalid rule detected: "+rule);
			return;
		}
		
		// old rules till 4.0.2
		if (st.countTokens()==3) {
			this.m_logger.info("Detected Rule Type 4.0.2");

			this.m_serviceID = st.nextToken();
			this.m_msn = PIMRuntime.getInstance().getCallFactory().createMsn(st.nextToken(), "");
			this.m_cip = PIMRuntime.getInstance().getCallFactory().createCip(st.nextToken(), "");
			this.m_active = true;
		}
		
		// new rules since 4.0.3
		if (st.countTokens()==5) {
			this.m_logger.info("Detected Rule Type 4.0.3");

			this.m_serviceID = st.nextToken();
			this.m_msn = PIMRuntime.getInstance().getCallFactory().createMsn(st.nextToken(), "");
			this.m_cip = PIMRuntime.getInstance().getCallFactory().createCip(st.nextToken(), "");
			this.m_active = (st.nextToken().trim().equalsIgnoreCase("true") ? true : false);
			this.m_phones = this.getPhonenumbers(st.nextToken());
		}
		
		// new rules since 4.2
		if (st.countTokens()==6) {
			this.m_logger.info("Detected Rule Type 4.2.0");
			
			this.m_serviceID = st.nextToken();
			this.m_msn = PIMRuntime.getInstance().getCallFactory().createMsn(st.nextToken(), "");
			this.m_cip = PIMRuntime.getInstance().getCallFactory().createCip(st.nextToken(), "");
			this.m_active = (st.nextToken().trim().equalsIgnoreCase("true") ? true : false);
			this.m_phones = this.getPhonenumbers(st.nextToken());
			this.m_exphones = this.getPhonenumbers(st.nextToken());
		}
		
		// new rules since 4.3
		if (st.countTokens()==7) {
			this.m_logger.info("Detected Rule Type 4.3.0");
			
			this.m_name = st.nextToken();
			this.m_serviceID = st.nextToken();
			this.m_msn = PIMRuntime.getInstance().getCallFactory().createMsn(st.nextToken(), "");
			this.m_cip = PIMRuntime.getInstance().getCallFactory().createCip(st.nextToken(), "");
			this.m_active = (st.nextToken().trim().equalsIgnoreCase("true") ? true : false);
			this.m_phones = this.getPhonenumbers(st.nextToken());
			this.m_exphones = this.getPhonenumbers(st.nextToken());
		}	
		
		// new rules since 4.4
		if (st.countTokens()==8) {
			this.m_logger.info("Detected Rule Type 4.4.0");
			
			this.m_name = st.nextToken();
			this.m_serviceID = st.nextToken();
			this.m_msn = PIMRuntime.getInstance().getCallFactory().createMsn(st.nextToken(), "");
			this.m_cip = PIMRuntime.getInstance().getCallFactory().createCip(st.nextToken(), "");
			this.m_active = (st.nextToken().trim().equalsIgnoreCase("true") ? true : false);
			this.m_phones = this.getPhonenumbers(st.nextToken());
			this.m_exphones = this.getPhonenumbers(st.nextToken());
			this.m_timeslot = st.nextToken();
		}			
		
		this.m_logger.exiting(Rule.class.getName(), "buildRule");
    }
    
    private IPhonenumber[] getPhonenumbers(String phones) {
    	
    	if (phones.equalsIgnoreCase(Rule.GENERIC_SIGN))
    		return null;
    	
		StringTokenizer st = new StringTokenizer(phones, "[");
		IPhonenumber[] pns = new IPhonenumber[st.countTokens()];
		
		int i=0;
		while (st.hasMoreTokens()) {
			String pn = st.nextToken();
			pn = pn.substring(0, pn.length()-1);
			if (pn.equalsIgnoreCase("clired")) {
				pns[i] = PIMRuntime.getInstance().getCallerFactory().createPhonenumber(true);
			} else {
				StringTokenizer pt = new StringTokenizer(pn, Rule.PHONE_SEPARATOR_SIGN);
				if (pt.countTokens()==3) {
					pns[i]=PIMRuntime.getInstance().getCallerFactory().createPhonenumber(
						pt.nextToken().trim(),
						pt.nextToken().trim(),
						pt.nextToken().trim()
					);
				} else {
					this.m_logger.severe("Phonenumber in rule is not valid: "+pn);
				}
			}
			i++;
		}
		
		return pns;
    }
}
