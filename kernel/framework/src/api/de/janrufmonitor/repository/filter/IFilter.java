package de.janrufmonitor.repository.filter;

/**
 *  This interface must be implemented by a filter object.
 *  A repository manager can call a filter in its getXXX() method to
 * determine a subset of its repository objects.
 * 
 *@author     Thilo Brandt
 *@created    2004/07/17
 */
public interface IFilter {

	/**
	 * Gets the objects the filter is defined for.
	 * 
	 * @return a valid filter object or null.
	 */
	public Object getFilterObject();
	
	/**
	 * Gets the type of the filter.
	 * 
	 * @return a vlaid filter type
	 */
	public FilterType getType();
	
}
