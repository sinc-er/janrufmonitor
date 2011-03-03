package de.janrufmonitor.repository.filter;

import de.janrufmonitor.framework.ICaller;

/**
 * This class is a caller filter.
 * 
 *@author     Thilo Brandt
 *@created    2004/07/17
 */
public class CallerFilter extends AbstractFilter {

	/**
	 * Creates a new caller filter object.
	 * @param c a valid caller
	 */
	public CallerFilter(ICaller c) {
		super();
		this.m_filter = c;
		this.m_type = FilterType.CALLER;
	}
	
	/**
	 * Gets the caller to be filtered.
	 * 
	 * @return a valid caller object.
	 */
	public ICaller getCaller() {
		return (ICaller)this.m_filter;
	}

	public String toString() {
		return CallerFilter.class.getName()+"#"+this.m_filter;
	}
}
