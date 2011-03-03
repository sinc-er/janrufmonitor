package de.janrufmonitor.ui.jface.wizards.pages;

import java.util.ArrayList;
import java.util.List;

import de.janrufmonitor.framework.CallerListComparator;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IMultiPhoneCaller;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.filter.IFilter;
import de.janrufmonitor.repository.types.IReadCallerRepository;
import de.janrufmonitor.runtime.PIMRuntime;

public class CallerReaderFactory {
	private static CallerReaderFactory m_instance = null;
	private List m_list = null; 
	private ICallerManager m_currentCM;
	
	private CallerReaderFactory() {
		
	}
	
    public static synchronized CallerReaderFactory getInstance() {
        if (CallerReaderFactory.m_instance == null) {
        	CallerReaderFactory.m_instance = new CallerReaderFactory();
        }
        return CallerReaderFactory.m_instance;
    }
    
    public static synchronized void invalidate() {
    	if (m_instance!=null) {
    		m_instance = null;
    	}
    }
    
    public ICallerManager getCurrent() {
    	return m_currentCM;
    }
    
	public List getAllCallers() {
		if (m_list==null) {
			List managers = this.getActiveCallerManagers();
			ICallerList list = PIMRuntime.getInstance().getCallerFactory().createCallerList();
			
			ICallerList manList = null;
			ICallerManager man = null;
			for (int i=0;i<managers.size();i++) {
				man = (ICallerManager) managers.get(i);				
				if (man!=null && man.isActive() && man.isSupported(IReadCallerRepository.class)) {
					this.m_currentCM = man;
					manList = ((IReadCallerRepository)man).getCallers((IFilter)null);					
					list.add(unMultiPhoneCaller(manList, managers));
				}
			}
			
			list.sort(CallerListComparator.ORDER_CALLERNAME, false);
			
			m_list = new ArrayList();
			m_list.add(PIMRuntime.getInstance().getCallerFactory().createCaller(
				PIMRuntime.getInstance().getCallerFactory().createName("", ""),
				PIMRuntime.getInstance().getCallerFactory().createPhonenumber(true)
			));
			
			// added 2009/04/18: added internal numbers
			m_list.add(PIMRuntime.getInstance().getCallerFactory().createCaller(
				PIMRuntime.getInstance().getCallerFactory().createName("", ""),
				PIMRuntime.getInstance().getCallerFactory().createPhonenumber(IJAMConst.INTERNAL_CALL, "", IJAMConst.INTERNAL_CALL_NUMBER_SYMBOL)
			));
			
			for (int i=list.size()-1;i>=0;i--) {
				m_list.add(list.get(i));
			}
		}
		this.m_currentCM = null;
		return m_list;
	}

	private ICallerList unMultiPhoneCaller(ICallerList cl, List managers) {
		ICallerList l = PIMRuntime.getInstance().getCallerFactory().createCallerList(cl.size());
		ICaller c = null;
		ICaller nc = null;
		for (int i=0,j=cl.size();i<j;i++) {
			c = cl.get(i);
			if (c instanceof IMultiPhoneCaller) {
				List phones = ((IMultiPhoneCaller)c).getPhonenumbers();
				IPhonenumber pn = null;				
				for (int k=0;k<phones.size();k++) {
					pn = (IPhonenumber) phones.get(k);
					//removed due to performance issues: c = this.getCaller(pn, managers);
					nc = PIMRuntime.getInstance().getCallerFactory().createCaller(null, pn, c.getAttributes());
					if (nc!=null) {
						l.add(nc);
					}
				}
			} else
				l.add(c);
		}
		
		return l;
	}
	
    private List getActiveCallerManagers() {
		List managers = PIMRuntime.getInstance().getCallerManagerFactory().getAllCallerManagers();
		
		ICallerManager man = null;
		for (int i=managers.size()-1;i>=0;i--) {
			man = (ICallerManager) managers.get(i);
			// removed: 2009/04/30: if (!man.isActive() || !(man.isSupported(IReadCallerRepository.class) && man.isSupported(IWriteCallerRepository.class))){
			if (!man.isActive() || !man.isSupported(IReadCallerRepository.class)){
				managers.remove(i);
			}
		}
		return managers;
	}
	
}
