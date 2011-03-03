package de.janrufmonitor.repository.filter;

/**
 * This class is an abstract implementation of the IFilter interface. 
 * It can be used as base class for a concrete filter implementation.
 * 
 *@author     Thilo Brandt
 *@created    2004/07/17
 */
public abstract class AbstractFilter implements IFilter {

	protected Object m_filter;
	protected FilterType m_type;
	
	protected AbstractFilter() {
		this.m_type = FilterType.UNDEFINED;
	}

	public Object getFilterObject() {
		return this.m_filter;
	}
	
	public FilterType getType() {
		return this.m_type;
	}

}
