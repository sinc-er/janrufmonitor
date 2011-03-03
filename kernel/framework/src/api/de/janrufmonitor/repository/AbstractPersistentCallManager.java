package de.janrufmonitor.repository;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.repository.filter.IFilter;
import de.janrufmonitor.repository.types.IWriteCallRepository;

/**
 *  This abstract class can be used as base class for a new call manager implementation
 * using a local persistence.
 *
 *@author     Thilo Brandt
 *@created    2003/11/02
 *@changed	  2004/01/17
 */
public abstract class AbstractPersistentCallManager extends AbstractFilterCallManager implements IWriteCallRepository {

    protected ICallList m_callList;

    public AbstractPersistentCallManager() { 
    	super();
    }

    public void removeCall(ICall call) {
		if (this.m_callList==null) {
			this.m_logger.warning("call list not initialized: "+this.getID());
			return;
		}
		
		this.m_callList.remove(call);

        this.saveDatabase();
    }

    public void removeCalls(ICallList callList) {
		if (this.m_callList==null) {
			this.m_logger.warning("call list not initialized: "+this.getID());
			return;
		}    	
    	for (int i = 0, n = callList.size(); i < n; i++) {
            this.m_callList.remove(callList.get(i));
        }
        this.saveDatabase();
    }

    public void setCall(ICall call) {
    	ICallList cl = this.getRuntime().getCallFactory().createCallList(1);
    	cl.add(call);
    	this.setCalls(cl);
    }
    
	public void setCalls(ICallList list) {
		if (this.m_callList==null) {
			this.m_logger.warning("call list not initialized: "+this.getID());
			return;
		}
		
		ICall call = null;
		for (int i=0,n=list.size();i<n;i++) {
			call = list.get(i);
	        if (!this.m_callList.contains(call)) {
	            this.m_callList.add(call);
	        }
		}
		this.saveDatabase();
	}

    public void updateCall(ICall call) {
		if (this.m_callList==null) {
			this.m_logger.warning("call list not initialized: "+this.getID());
			return;
		}
		
    	if (this.m_callList.contains(call)) {
    		this.m_callList.remove(call);
    	}
        this.setCall(call); 
    }
    
	public void updateCalls(ICallList list) {
		if (this.m_callList==null) {
			this.m_logger.warning("call list not initialized: "+this.getID());
			return;
		}
		
		this.removeCalls(list);
		this.setCalls(list);
	}
	
	public boolean isSupported(Class c) {
		return c.isInstance(this);
	}

	/**
	 * Capabilities for database loading.
	 */
    public abstract void loadDatabase();

	/**
	 * Capabilities for database saving.
	 */
    public abstract void saveDatabase();


	protected ICallList getInitialCallList(IFilter f) {
		ICallList cl = this.getRuntime().getCallFactory().createCallList();
		cl.add(this.m_callList);
		return cl;
	}
}
