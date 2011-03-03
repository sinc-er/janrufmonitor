package de.janrufmonitor.repository.types;

import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.repository.filter.IFilter;

/**
 * This type is used for repositories which allow read access to their caller information.
 * 
 * @author brandt
 *
 */
public interface IReadCallerRepository {

	/**
	 * Gets a list with all callers of a repository filtered by the
	 * specified filter implementation.
	 * 
	 * @param f a valid filter object
	 * @return list with calls
	 */
	public ICallerList getCallers(IFilter filter);

	/**
	 * Gets a list with all callers of a repository filtered by the
	 * specified filter array. The order of the filtering is done
	 * by the order of the array object.
	 * 
	 * @param f a valid filter array. Array elements must not be null.
	 * @return list with calls
	 */
	public ICallerList getCallers(IFilter[] filters);
	
}
