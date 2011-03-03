package de.janrufmonitor.repository.filter;

import de.janrufmonitor.framework.IPhonenumber;

/**
 * This class is a phonenumber filter.
 * 
 *@author     Thilo Brandt
 *@created    2004/07/17
 */
public class PhonenumberFilter extends AbstractFilter {

	/**
	 * Creates a new phonenumber filter object.
	 * @param c a valid phonenumber
	 */
	public PhonenumberFilter(IPhonenumber c) {
		super();
		this.m_filter = c;
		this.m_type = FilterType.PHONENUMBER;
	}
	
	/**
	 * Gets the phonenumber to be filtered.
	 * 
	 * @return a valid phonenumber object.
	 */
	public IPhonenumber getPhonenumber() {
		return (IPhonenumber)this.m_filter;
	}

	public String toString() {
		return PhonenumberFilter.class.getName()+"#"+this.m_filter;
	}
}
