package de.janrufmonitor.repository.filter;

import de.janrufmonitor.framework.IMsn;

/**
 * This class is a MSN filter.
 * 
 *@author     Thilo Brandt
 *@created    2004/07/17
 */
public class MsnFilter extends AbstractFilter {

	/**
	 * Creates a new MSN filter object for the specified MSNs.
	 * @param msn valid MSNs
	 */
	public MsnFilter(IMsn[] msn) {
		super();
		this.m_filter = msn;
		this.m_type = FilterType.MSN;
	}
	
	/**
	 * Creates a new MSN filter object for the specified MSN.
	 * @param a msn valid MSN
	 */
	public MsnFilter(IMsn msn) {
		this(new IMsn[] {msn});
	}
	
	/**
	 * Gets the MSN to be filtered.
	 * 
	 * @return a valid MSN object.
	 */
	public IMsn[] getMsn() {
		return (IMsn[])this.m_filter;
	}

	public String toString() {
		return MsnFilter.class.getName()+"#"+this.m_filter;
	}
}
