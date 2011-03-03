package de.janrufmonitor.framework.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.janrufmonitor.framework.CallListComparator;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICallList;

public class CallList implements ICallList, Serializable {

	private static final long serialVersionUID = -4962746884423829505L;

	private final static int m_capacity = 10;

	private List m_list;

	public CallList() {
		this(m_capacity);
	}
	
	public CallList(int capacity) {
		this.m_list = new ArrayList(capacity);
	}

	public boolean contains(ICall call) {
		return this.m_list.contains(call);
	}

	public void remove(ICall call) {
		synchronized(this.m_list) {
			this.m_list.remove(call);
		}
	}

	public void add(ICall call) {
		this.m_list.add(call);
	}

	public ICall get(int position) {
		return (ICall)this.m_list.get(position);
	}

	public int size() {
		return this.m_list.size();
	}

	public void sort() {
		Collections.sort(this.m_list, new CallListComparator(CallListComparator.ORDER_DATE));
	}

	public void sort(int order, boolean direction) {
		Collections.sort(this.m_list, new CallListComparator(order));
		if (!direction) {
			Collections.reverse(this.m_list);
		}  
	}

	public void clear() {
		this.m_list.clear();
	}

	public void add(ICallList callList) {
		if (callList instanceof CallList) {
			this.m_list.addAll(((CallList)callList).m_list);
		} else {
			for (int i=0;i<callList.size();i++)
				this.add(callList.get(i));
		}
	}

	public Object[] toArray() {
		return this.m_list.toArray();
	}
}
