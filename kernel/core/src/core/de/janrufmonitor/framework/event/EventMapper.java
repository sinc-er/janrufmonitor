package de.janrufmonitor.framework.event;

public class EventMapper {

    private IEventReceiver m_receiver = null;
    private IEvent m_event = null;

    public EventMapper(IEventReceiver receiver, IEvent event) {
        this.m_receiver = receiver;
        this.m_event = event;
    }

    public IEventReceiver getReceiver() {
        return this.m_receiver;
    }


    public IEventCondition getEventCondition() {
        return this.m_event.getConditions();
    }


    public int getEventType() {
        return this.m_event.getType();
    }


    public String toString() {
        return this.m_receiver.getReceiverID() + "#" + this.m_event.getType();
    }
    
    public boolean equals(EventMapper em) {
        if (!this.m_receiver.getReceiverID().equalsIgnoreCase(em.getReceiver().getReceiverID())){
            return false;
        }
        
        if (this.m_event.getType()!=em.getEventType()){
            return false;
        }
        
        if (this.m_event.getConditions().getConditionKeys().toString().equalsIgnoreCase(em.m_event.getConditions().getConditionKeys().toString())){
            return false;
        }
        
        return true;
    }

}
