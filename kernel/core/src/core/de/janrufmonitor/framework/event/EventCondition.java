package de.janrufmonitor.framework.event;

import java.util.Properties;
import java.util.Enumeration;
import java.util.Iterator;

public class EventCondition implements IEventCondition {
    
    Properties m_conditions;
        
    public EventCondition() {
        this.m_conditions = new Properties();
    }
    
    public void addCondition(String name, String value) {
        if (name!=null && value!=null) {
            this.m_conditions.setProperty(name, value);
        }
    }
    
    public void clear() {
        this.m_conditions.clear();
    }
    
    public String getCondition(String name) {
        return (this.m_conditions.getProperty(name)==null? "" : this.m_conditions.getProperty(name));
    }
    
    public Enumeration getConditionKeys() {
        return this.m_conditions.keys();
    }
    
    public void removeCondition(String name) {
        this.m_conditions.remove(name);
    }
    
    public boolean isEmpty() {
        return this.m_conditions.isEmpty();
    }
    
    public int size() {
        return this.m_conditions.size();
    }
    
    public Iterator getConditionIterator() {
        return this.m_conditions.keySet().iterator();
    }
    
    public boolean equals(IEventCondition eventCond) {
        if (this.m_conditions.size() != eventCond.size()) {
            return false;
        }
        Iterator iter = this.m_conditions.keySet().iterator();
        while (iter.hasNext()){
            String key = (String)iter.next();
            String comp_value = eventCond.getCondition(key);
            if (!comp_value.equalsIgnoreCase(this.getCondition(key))){
                return false;
            }
        }
        
        return true;
    }
    
    public String toString() {
        String condS = "";
        Iterator iter = this.getConditionIterator();
        while (iter.hasNext()){
            String key = (String) iter.next();
            condS += "{"+key+"="+this.m_conditions.getProperty(key)+"} ";
        }
        return condS;
    }
    
}
