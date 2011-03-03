package de.janrufmonitor.repository.filter;

/**
 * This class is a item count filter, whcih limits the number of
 * elements in a list.
 * 
 *@author     Thilo Brandt
 *@created    2006/04/20
 */
public class ItemCountFilter extends AbstractFilter {

	private int m_limit;
	

	/**
	 * Creates a new item count filter object for the specified limit value.
	 * @param limit a valid limit
	 */
	public ItemCountFilter(int limit) {
		super();
		this.m_limit = limit;
		this.m_type = FilterType.ITEMCOUNT;
	}
	
	/**
	 * Gets the limit to be filtered.
	 * 
	 * @return a valid limit integer.
	 */
	public int getLimit() {
		return m_limit;
	}

	public String toString() {
		return ItemCountFilter.class.getName()+"#"+this.m_limit;
	}

}
