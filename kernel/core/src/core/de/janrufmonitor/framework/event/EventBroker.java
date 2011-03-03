package de.janrufmonitor.framework.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Collections;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;

public class EventBroker implements IEventBroker {
    
    private static EventBroker m_instance = null;
    
    private Map m_senders;
    private Map m_receivers;

    private Logger m_logger;
    
    private EventBroker() {
        this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
    }
    
    public static synchronized EventBroker getInstance() {
        if (EventBroker.m_instance == null) {
            EventBroker.m_instance = new EventBroker();
            new EventQueueThread();
        }
        return EventBroker.m_instance;
    }     
    
    public IEvent createEvent(int type) {
        return createEvent(type, null, new EventCondition());
    }
    
    public IEvent createEvent(int type, Object data) {
        return createEvent(type, data, new EventCondition());
    }
    
    public IEvent createEvent(int type, Object data, IEventCondition cond) {
        return new Event(type, data, cond);
    }
    
    public IEventCondition createEventCondition() {
        return new EventCondition();
    }

    public void send(IEventSender sender, IEvent event) {
		this.m_logger.entering(EventBroker.class.getName(), "send");
		this.m_logger.info("Total registered EventSenders: "+this.m_senders.size());      
		this.m_logger.info("Total registered EventReceivers: "+this.m_receivers.size());

        // check if sender is registered
        if (this.m_senders.containsKey(sender.getSenderID())) {
        	List r = this.getReceivers(event);
			this.m_logger.info("EventReceivers for event #"+event.getType()+": "+r.size());
			EventMapper em = null;
			EventQueueItem eqi = null;
			for (int i=0,n=r.size();i<n; i++) {
				 em = (EventMapper) r.get(i);
				 if (em.getEventType() == event.getType()) {
					 // removed 2008/02/11: never used anywhere, concept seems to be obsolete...
					 // check if all properties are equals e.g. MSN, CIP etc.
					 //if (this.equalityConditions(event.getConditions(), em.getEventCondition())) {
						 this.m_logger.info("New event #" + new Integer(event.getType()).toString() + " queued for receiver <" + em.getReceiver().getReceiverID() + ">.");
						 eqi = new EventQueueItem(event, em.getReceiver());
						 EventQueue.getInstance().enqueue(eqi);
					 //}
				 }
			 }
        }
		this.m_logger.exiting(EventBroker.class.getName(), "send");
    }    	
    
    public void register(IEventSender sender) {
        synchronized(this.m_senders) {
        	if (!this.m_senders.containsKey(sender.getSenderID())) {
        		this.m_senders.put(sender.getSenderID(), sender);
				this.m_logger.info("Registered new EventSender <" + sender.getSenderID() + ">");
        	}
        }  
    }
    
    public void register(IEventReceiver receiver, IEvent event) {
       synchronized(this.m_receivers) {
            EventMapper em = new EventMapper(receiver, event);
            if (!this.m_receivers.containsKey(em.toString())) {
            	this.m_receivers.put(em.toString(), em);
				this.m_logger.info("Registered new EventReceiver <" + em.toString() + ">");
            }
         }
    }
    
    public void unregister(IEventSender sender) {
		synchronized(this.m_senders) {
            if (this.m_senders.containsKey(sender.getSenderID())) {
				this.m_senders.remove(sender.getSenderID());
                this.m_logger.info("Unregistered EventSender <" + sender.getSenderID() + ">");
            }
        }
    }
    
    public void unregister(IEventReceiver receiver, IEvent event) {
		synchronized(this.m_receivers) {
            EventMapper em = new EventMapper(receiver, event);
			if (this.m_receivers.containsKey(em.toString())) {
				this.m_receivers.remove(em.toString());
				this.m_logger.info("Unregistered EventReceiver <" + em.toString() + ">");
			}
        }   
    }
    
	private List getReceivers(IEvent event) {
		List rList = new ArrayList();
    	
		synchronized(this.m_receivers) {
			Iterator iter = this.m_receivers.keySet().iterator();
			String key = null;
			while (iter.hasNext()) {
				key = (String)iter.next();
				if (key.endsWith(Integer.toString(event.getType()))) {
					rList.add(this.m_receivers.get(key));
				}
			}
		}
    	
		return rList;
	}   
    
// removed 2008/02/11: never used anywhere, concept seems to be obsolete...	
//    private boolean equalityConditions(IEventCondition condEvent, IEventCondition condReceiver) {
//
//        // means: receiver accpets all events for which he is registered
//        if (condReceiver.isEmpty()) {
//            this.m_logger.info("Receiver is accepting all EventConditions.");
//            return true;
//        }
//
//        // means: event has no properties but receiver is restricted -> no equality
//        if (condEvent.isEmpty() && !condReceiver.isEmpty()) {
//            this.m_logger.info("Receiver is restricted but event has no properties.");
//            return false;
//        }
//
//        // the counter should at least be 1 or higher ...
//        int propMaxCount = Math.min(condEvent.size(), condReceiver.size());
//        int propCount = 0;
//
//        Iterator eventPropsIter = condEvent.getConditionIterator();
//        String eventPropKey = null;
//        while (eventPropsIter.hasNext()) {
//            eventPropKey = (String) eventPropsIter.next();
//            Iterator receiverPropsIter = condReceiver.getConditionIterator();
//            while (receiverPropsIter.hasNext()) {
//                String receiverPropKey = (String) receiverPropsIter.next();
//                // the same property key was found
//                if (eventPropKey.equalsIgnoreCase(receiverPropKey)) {
//                    // the same value was found
//                    if (condEvent.getCondition(eventPropKey).equalsIgnoreCase(condReceiver.getCondition(receiverPropKey)) ||
//                            condReceiver.getCondition(receiverPropKey).equalsIgnoreCase("*")) {
//
//                        propCount++;
//                        if (propCount == propMaxCount) {
//                            this.m_logger.info("EventCondition match of event ("+condEvent+") and receiver ("+condReceiver+") successful.");
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        this.m_logger.info("EventCondition of event ("+condEvent+") and receiver ("+condReceiver+") does not match.");
//        return false;
//    }

	public void startup() {
		this.m_senders = Collections.synchronizedMap(new HashMap());
		this.m_receivers = Collections.synchronizedMap(new HashMap());
	}

	public void shutdown() {
		this.m_senders.clear();
		this.m_receivers.clear();
		m_instance = null;
	}

}
