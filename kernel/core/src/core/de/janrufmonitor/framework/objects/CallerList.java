package de.janrufmonitor.framework.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.janrufmonitor.framework.CallerListComparator;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;

public class CallerList implements ICallerList, Serializable {

	private static final long serialVersionUID = -2386787980655948382L;

	private final static int m_capacity = 1;
	
	private List m_list;

	public CallerList() {
		this(m_capacity);
	}
	
	public CallerList(int capacity) {
		this.m_list = new ArrayList(capacity);
	}

	public boolean contains(ICaller caller) {
		return this.m_list.contains(caller);
	}

	public void remove(ICaller caller) {
		synchronized(this.m_list) {
			this.m_list.remove(caller);
		}
	}

	public void add(ICaller caller) {
		this.m_list.add(caller);
	}

	public ICaller get(int position) {
		return (ICaller)this.m_list.get(position);
	}

	public void sort() {
		Collections.sort(this.m_list, new CallerListComparator(CallerListComparator.ORDER_CALLERNAME));
	}

	public void sort(int order, boolean direction) {
		Collections.sort(this.m_list, new CallerListComparator(order));
		if (!direction) {
			Collections.reverse(this.m_list);
		}    
	}

	public int size() {
		return this.m_list.size();
	}

	public void clear() {
		this.m_list.clear();
	}

	public String toString() {
		StringBuffer list = new StringBuffer();
		list.append("<CallerList: ");
		for (int i = 0; i < this.m_list.size(); i++) {
			list.append(m_list.get(i));
		}
		list.append(">");
		return list.toString();
	}

	public void add(ICallerList callerList) {
		if (callerList instanceof CallerList) {
			this.m_list.addAll(((CallerList)callerList).m_list);
		} else {
			for (int i=0;i<callerList.size();i++)
				this.add(callerList.get(i));
		}
	}
	
	public Object[] toArray() {
		return this.m_list.toArray();
	}
}
