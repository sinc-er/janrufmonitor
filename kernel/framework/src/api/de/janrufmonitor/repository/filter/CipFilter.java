package de.janrufmonitor.repository.filter;

import de.janrufmonitor.framework.ICip;


/**
 * This class is a CIP filter.
 * 
 *@author     Thilo Brandt
 *@created    2004/07/17
 */
public class CipFilter extends AbstractFilter {

	/**
	 * Creates a new CIP filter object for the specified CIP.
	 * @param cip a valid CIP
	 */
	public CipFilter(ICip cip) {
		super();
		this.m_filter = cip;
		this.m_type = FilterType.CIP;
	}
	
	/**
	 * Gets the CIP to be filtered.
	 * 
	 * @return a valid CIP object.
	 */
	public ICip getCip() {
		return (ICip)this.m_filter;
	}

	public String toString() {
		return CipFilter.class.getName()+"#"+this.m_filter;
	}
}
