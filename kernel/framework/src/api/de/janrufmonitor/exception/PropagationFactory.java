package de.janrufmonitor.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;

/**
 *  This class is a exception propagation factory object.
 *  Single propagators can be added to the factory and are notified on exception
 *  events.
 *
 *@author     Thilo Brandt
 *@created    2004/11/20
 */
public class PropagationFactory {

    private static PropagationFactory m_instance = null;
    
    private List m_list;
	private Logger m_logger;

    private PropagationFactory() {
        this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
        this.m_list = new ArrayList();
    }
    
    /**
     * Gets a valid instacne of the PropagationFactory.
     * 
     * @return
     */
    public static synchronized PropagationFactory getInstance() {
        if (PropagationFactory.m_instance == null) {
        	PropagationFactory.m_instance = new PropagationFactory();
        }
        return PropagationFactory.m_instance;
    }
    
    /**
     * Adds a new propagator to the factory. 
     * 
     * @param p
     */
	public void add(IPropagator p) {
		if (!this.m_list.contains(p)) {
			this.m_list.add(p);
		} else {
			this.m_logger.warning("Propagator already registered");
		}
	}
	
    /**
     * Removes a propagator from the factory. 
     * 
     * @param p
     */
	public void remove(IPropagator p) {
		if (p!=null)
			this.m_list.remove(p);
	}
		
	/**
	 * Fires a propagator event with an exception object.
	 * 
	 * @param m
	 */
	public void fire(Message m){
		for (int i=0;i<this.m_list.size();i++) {
			IPropagator p = (IPropagator)this.m_list.get(i);
			p.propagate(m);
		}
	}
	
	/**
	 * Fires a propagator event with an exception object for a specific propagator id.
	 * 
	 * @param m
	 * @param id
	 */
	public void fire(Message m, String id){
		for (int i=0;i<this.m_list.size();i++) {
			IPropagator p = (IPropagator)this.m_list.get(i);
			if (id!=null && p.getID().equalsIgnoreCase(id))
				p.propagate(m);
		}
	}
	
}
