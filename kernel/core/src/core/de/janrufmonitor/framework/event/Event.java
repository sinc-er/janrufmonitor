package de.janrufmonitor.framework.event;

public class Event implements IEvent {
    
    IEventCondition m_conditions;
    Object m_data;
    int m_type;
    
    public Event() {
        this.m_type = IEventConst.EVENT_TYPE_UNKNOWN;
    }
    
    public Event(int type, Object data, IEventCondition condition){
        this.m_type = type;
        this.m_conditions = condition;
        this.m_data = data;
    }
    
    public IEventCondition getConditions() {
        return this.m_conditions;
    }
    
    public Object getData() {
        return this.m_data;
    }
    
    public int getType() {
        return this.m_type;
    }
    
    public void setConditions(IEventCondition cond) {
        this.m_conditions = cond;
    }
    
    public void setData(Object obj) {
        this.m_data = obj;
    }
    
    public void setType(int type) {
        this.m_type = type;
    }
    
    public String toString() {
    	return "Event - type #"+this.m_type + ", data : "+this.m_data;
    }
    
}
