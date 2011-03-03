package de.janrufmonitor.framework.rules;

import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.framework.ICip;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.PIMRuntime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class RuleEngine implements IRuleEngine, IConfigurable {
    
    private String ID = "RuleEngine";
    private String NAMESPACE = "rules.RuleEngine";
    private static RuleEngine m_instance = null;
    
    Logger m_logger;
    Properties m_configuration;
    List m_rules;
    
    String GENERIC_SIGN = "*";
    String CONFIG_RULE = "_rule";
    
    private RuleEngine() {
        this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
    }
    
    public static synchronized RuleEngine getInstance() {
        if (RuleEngine.m_instance == null) {
            RuleEngine.m_instance = new RuleEngine();
        }
        return RuleEngine.m_instance;
    }  
    
    public void addRule(IRule rule) {
        this.m_rules.add(rule);
    }
    
	public IRule createRule(String serviceID, IMsn msn, ICip cip, boolean active, IPhonenumber[] phones, IPhonenumber[] exphones) {
		return this.createRule(
			serviceID, 
			msn,
			cip,
			active,
			phones,
			exphones,
			GENERIC_SIGN
		);
	}
	
	public IRule createRule(String serviceID, IMsn msn, ICip cip, boolean active, IPhonenumber[] phones, IPhonenumber[] exphones, String timeslot) {
		Rule rule = new Rule();
		rule.setCip(cip);
		rule.setMsn(msn);
		rule.setService(serviceID);
		rule.setActive(active);
		rule.setPhonenumbers(phones);
		rule.setExcludePhonenumbers(exphones);
		rule.setTimeslot(timeslot);
		return rule;
	}
	
	public IRule createRule(String rule) {
		IRule r = new Rule(rule);
		if (r.isValid()) return r;
		return null;
	}

	public IRule createRule(String serviceID, IMsn msn, ICip cip, boolean active, IPhonenumber[] phones) {
		return this.createRule(
			serviceID, 
			msn,
			cip,
			active,
			phones,
			null
		);
	}
	
	public IRule createRule(String serviceID, String msn, String cip, boolean active, IPhonenumber[] phones) {
		return this.createRule(
			serviceID,
			PIMRuntime.getInstance().getCallFactory().createMsn(msn, ""),
			PIMRuntime.getInstance().getCallFactory().createCip(cip, ""),
			active,
			phones
		);
	}

	public IRule createRule(String serviceID, String msn, String cip, IPhonenumber phone) {
		IPhonenumber[] phones = new IPhonenumber[1];
		phones[0] = phone;
		return this.createRule(serviceID, msn, cip, true, phones);
	}

    
    public List getRules() {
        return this.m_rules;
    }
    
    private List getRulesForService(String serviceID) {
        List serviceRules = new ArrayList();
        for (int i=0;i<this.m_rules.size();i++) {
            if (((IRule)this.m_rules.get(i)).getServiceID().equalsIgnoreCase(serviceID)) {
                serviceRules.add(this.m_rules.get(i));
            }
        }
        return serviceRules;
    }
    
    private List getRulesForMSN(List filteredList, IMsn msn) {
    	String msns = msn.getMSN();
    	
    	if (filteredList==null) {
    		this.m_logger.warning("Filtered list is null for MSN "+msns+". Taking all rules.");
			filteredList = this.getRules();
    	}
    	
		if (msns.equalsIgnoreCase(this.GENERIC_SIGN)) {
			return filteredList;
		}
			    	
    	List msnList = new ArrayList();
    	for (int i=0,n=filteredList.size();i<n;i++) {
			if (((IRule)filteredList.get(i)).getMsn().getMSN().equalsIgnoreCase(msns)) {
				msnList.add(filteredList.get(i));
    		}
			if (((IRule)filteredList.get(i)).getMsn().getMSN().equalsIgnoreCase(this.GENERIC_SIGN)) {
				msnList.add(filteredList.get(i));
			}
    	}
    	
    	return msnList;
    }
    
    private List getRulesForTimeslot(List filteredList) {
    	if (filteredList==null) {
    		this.m_logger.warning("Filtered list is null for date "+new Date().toString()+". Taking all rules.");
			filteredList = this.getRules();
    	}
	
    	List timeList = new ArrayList();
    	IRule r = null;
    	String ts = null;
    	for (int i=0,n=filteredList.size();i<n;i++) {
    		r = ((IRule)filteredList.get(i));
    		ts = r.getTimeslot();
    		if (ts.equalsIgnoreCase(GENERIC_SIGN)) {
    			timeList.add(r);
    		} else {
    			// Format: Day1,Day2,Day3;hh,mm,hh,mm
    			StringTokenizer st = new StringTokenizer(ts, ";");
    			if (st.countTokens()==2) {
    				StringTokenizer days = new StringTokenizer(st.nextToken(), ",");
    				while (days.hasMoreTokens()) {
    					// check if day matches
    					if (days.nextToken().equalsIgnoreCase(Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)))) {
    						// check if time matches
    						StringTokenizer hours = new StringTokenizer(st.nextToken(), ",");
    						if (hours.countTokens()==4) {
    							this.m_logger.info("Current timeslot calculation: "+new Integer(""+Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + (Calendar.getInstance().get(Calendar.MINUTE)<10 ? "0"+Calendar.getInstance().get(Calendar.MINUTE) : ""+Calendar.getInstance().get(Calendar.MINUTE))).intValue());
    							if (new Integer(hours.nextToken()+hours.nextToken()).intValue() <= new Integer(
    									""+
										Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 
										(Calendar.getInstance().get(Calendar.MINUTE)<10 ? "0"+Calendar.getInstance().get(Calendar.MINUTE) : ""+Calendar.getInstance().get(Calendar.MINUTE))
										).intValue()
									) {    								
    								if (new Integer(hours.nextToken()+hours.nextToken()).intValue() >= new Integer(""+Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + (Calendar.getInstance().get(Calendar.MINUTE)<10 ? "0"+Calendar.getInstance().get(Calendar.MINUTE) : ""+Calendar.getInstance().get(Calendar.MINUTE))).intValue()) {
    									timeList.add(r);
    								}
    							}
    						}
    					}
    				}   				
    			} else {
    				this.m_logger.warning("Invalid timeslot string: "+ts);
    			}
    		}
    	}
    	
    	return timeList;
    }    
    
	private List getRulesForCIP(List filteredList, ICip cip) {
		String cips = cip.getCIP();
    	
		if (filteredList==null) {
			this.m_logger.warning("Filtered list is null for CIP "+cips+". Taking all rules.");
			filteredList = this.getRules();
		}
    	
		if (cips.equalsIgnoreCase(this.GENERIC_SIGN)) {
			return filteredList;
		}
			    	
		List cipList = new ArrayList();
		for (int i=0,n=filteredList.size();i<n;i++) {
			if (((IRule)filteredList.get(i)).getCip().getCIP().equalsIgnoreCase(cips)) {
				cipList.add(filteredList.get(i));
			}
			if (((IRule)filteredList.get(i)).getCip().getCIP().equalsIgnoreCase(this.GENERIC_SIGN)) {
				cipList.add(filteredList.get(i));
			}
		}
    	
		return cipList;
	}
	
	private List getRulesForPhone(List filteredList, IPhonenumber pn) {
		    	
		if (filteredList==null) {
			this.m_logger.warning("Filtered list is null for Phonenumber "+pn+". Taking all rules.");
			filteredList = this.getRules();
		}
    				    	
		List pnsList = new ArrayList();
		IPhonenumber[] phones = null;
		for (int i=0,n=filteredList.size();i<n;i++) {
			phones = ((IRule)filteredList.get(i)).getPhonenumbers();
			
			if (phones!=null) {
				for (int j=0;j<phones.length;j++) {
					if (phones[j]!=null && pn!=null) {
						// added 2009/04/18 added support for internal telefon system calls, accept internal calls
						if (phones[j].getIntAreaCode().equalsIgnoreCase(IJAMConst.INTERNAL_CALL) && phones[j].getCallNumber().equalsIgnoreCase(IJAMConst.INTERNAL_CALL_NUMBER_SYMBOL) && pn.getIntAreaCode().equalsIgnoreCase(IJAMConst.INTERNAL_CALL)) {
							pnsList.add(filteredList.get(i));
						}
						if (phones[j].isClired() && pn.isClired()) {
							pnsList.add(filteredList.get(i));
						}
						if (phones[j].getIntAreaCode().equalsIgnoreCase(pn.getIntAreaCode()) &&
							phones[j].getAreaCode().equalsIgnoreCase(pn.getAreaCode()) &&
							pn.getCallNumber().startsWith(phones[j].getCallNumber())
							) {
							pnsList.add(filteredList.get(i));
						} 
						// added 2008/04/04: 
						else if(pn.getTelephoneNumber().equalsIgnoreCase(phones[j].getTelephoneNumber())) {
							pnsList.add(filteredList.get(i));
						}
					} 
				}
			} else {
				// all phones are allowed --> phones == null
				pnsList.add(filteredList.get(i));
			}

		}
    	
		return pnsList;
	}
	
	private List getRulesForExcludePhone(List filteredList, IPhonenumber pn) {
		    	
		if (filteredList==null) {
			this.m_logger.warning("Filtered list is null for Phonenumber "+pn+". Taking all rules.");
			filteredList = this.getRules();
		}
    				    	
		List pnsList = new ArrayList();
		IPhonenumber[] phones = null;
		for (int i=0,n=filteredList.size();i<n;i++) {
			phones = ((IRule)filteredList.get(i)).getExcludePhonenumbers();
			
			if (phones!=null) {
				for (int j=0;j<phones.length;j++) {
					if (phones[j]!=null && pn!=null) {
						// added 2009/04/18 added support for internal telefon system calls, accept internal calls
						if (phones[j].getIntAreaCode().equalsIgnoreCase(IJAMConst.INTERNAL_CALL) && phones[j].getCallNumber().equalsIgnoreCase(IJAMConst.INTERNAL_CALL_NUMBER_SYMBOL) && pn.getIntAreaCode().equalsIgnoreCase(IJAMConst.INTERNAL_CALL)) {
							return new ArrayList();
						}
						if (phones[j].isClired() && pn.isClired()) {
							return new ArrayList();
						}
						if (phones[j].getIntAreaCode().equalsIgnoreCase(pn.getIntAreaCode()) &&
							phones[j].getAreaCode().equalsIgnoreCase(pn.getAreaCode()) &&
							pn.getCallNumber().startsWith(phones[j].getCallNumber())
							) {
								return new ArrayList();
						}
						pnsList.add(filteredList.get(i));
					} 
				}
			} else {
				// all phones are allowed --> phones == null
				pnsList.add(filteredList.get(i));
			}
		}
		return pnsList;
	}
	
	private List getActiveRules(List filteredList) {
		if (filteredList==null) {
			this.m_logger.warning("Filtered list is null. Taking all rules.");
			filteredList = this.getRules();
		}
		
		List activeList = new ArrayList();
		for (int i=0,n=filteredList.size();i<n;i++) {
			if (((IRule)filteredList.get(i)).isActive()) {
				activeList.add(filteredList.get(i));
			}
		}
    	
		return activeList;
	}
    
    public void removeRule(IRule rule) {
        this.m_rules.remove(rule);
    }
    
    public synchronized boolean validate(IRule rule) {
		this.m_logger.entering(RuleEngine.class.getName(), "validate");
        if (!rule.isValid()) { 
            this.m_logger.severe("Rule is invalid.");
            return false; 
        }
        
        List rules = this.getRulesForService(rule.getServiceID());
        if (rules.size()<1) {
            this.m_logger.info("no rules found for service: " + rule.getServiceID());
            return false; 
        }
        
        List filtered = this.getActiveRules(rules);
		if (filtered.size()<1) {
			this.m_logger.info("no active rules found for service: " + rule.getServiceID());
			return false; 
		}
		
		filtered = this.getRulesForMSN(filtered, rule.getMsn());
		if (filtered.size()<1) {
			this.m_logger.info("no active rules found for service " + rule.getServiceID()+" and MSN "+rule.getMsn());
			return false; 
		}
        
		filtered = this.getRulesForCIP(filtered, rule.getCip());
		if (filtered.size()<1) {
			this.m_logger.info("no active rules found for service " + rule.getServiceID()+", MSN "+rule.getMsn() + " and CIP "+rule.getCip());
			return false; 
		}
		
		filtered = this.getRulesForTimeslot(filtered);
		if (filtered.size()<1) {
			this.m_logger.info("no active rules found for service " + rule.getServiceID()+", MSN "+rule.getMsn() + ", CIP "+rule.getCip() +" and Date "+new Date().toString());
			return false; 
		}
		
		IPhonenumber pn = null;
		if (rule.getPhonenumbers()!=null && rule.getPhonenumbers().length>0) {
			pn = rule.getPhonenumbers()[0];
		}
		
		filtered = this.getRulesForPhone(filtered, pn);
		if (filtered.size()<1) {
			this.m_logger.info("no active rules found for service " + rule.getServiceID()+", MSN "+rule.getMsn() + ", CIP "+rule.getCip()+" and number "+pn);
			return false; 
		}

		filtered = this.getRulesForExcludePhone(filtered, pn);
		if (filtered.size()<1) {
			this.m_logger.info("caller "+pn.toString()+" is exluded in rule for service " + rule.getServiceID()+", MSN "+rule.getMsn() + ", CIP "+rule.getCip()+" and number "+pn);
			return false; 
		}
		
		if (filtered.size()>0) {
			this.m_logger.info("found "+filtered.size()+" rule(s) for service "+rule.getServiceID()+" which match with check rule.");
			return true;
		}
		
        this.m_logger.info("no rules found for service "+rule.getServiceID()+", which match with check rule.");
		this.m_logger.exiting(RuleEngine.class.getName(), "validate");
        return false;
    }
    
	public boolean validate(String serviceID, String msn, String cip, IPhonenumber phone) {
		return this.validate(this.createRule(serviceID, msn, cip, phone));
	}
	

	public boolean validate(String serviceID, IMsn msn, ICip cip, IPhonenumber phone) {
		IPhonenumber[] pns = new IPhonenumber[1];
		pns[0] = phone;
		return this.validate(this.createRule(serviceID, msn, cip, true, pns));
	}
    
    public String getConfigurableID() {
        return this.ID;
    }
    
    public String getNamespace() {
        return this.NAMESPACE;
    }
    
    public void setConfiguration(Properties configuration) {
        this.m_configuration = configuration;

		this.m_rules = new ArrayList();
		this.buildRules();
    }
    
    private void buildRules() {  
        Iterator iter = this.m_configuration.keySet().iterator();
        String key = null;
        while (iter.hasNext()) {
            key = (String) iter.next();
            if (key.endsWith(this.CONFIG_RULE)) {
                String rule = this.m_configuration.getProperty(key);
                IRule aRule = this.createRule(rule);
                if (aRule!=null) {
					this.m_logger.info("Updating rule from configuration: " + aRule.toString());
					this.m_rules.add(aRule);
                }
            }
        }       
        this.m_logger.info(new Integer(this.m_rules.size()).toString() + " rules available.");
    }
    
    public void startup() {
		PIMRuntime.getInstance().getConfigurableNotifier().register(this);
	}

	public void shutdown() {
		this.m_rules = null;
		PIMRuntime.getInstance().getConfigurableNotifier().unregister(this);
		m_instance = null;
	}
    
}
