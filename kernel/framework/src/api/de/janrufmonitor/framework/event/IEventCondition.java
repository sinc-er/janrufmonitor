package de.janrufmonitor.framework.event;

import java.util.Enumeration;
import java.util.Iterator;

public interface IEventCondition {

    public void addCondition(String name, String value);

    public void removeCondition(String name);

    public void clear();

    public String getCondition(String name);

    public Enumeration getConditionKeys();

    public Iterator getConditionIterator();

    public boolean isEmpty();

    public int size();

    public boolean equals(IEventCondition eventCond);
}
