package de.janrufmonitor.repository.types;

import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.repository.filter.IFilter;

/**
 * This type is used for repositories which allow read access to their call information.
 * 
 * @author brandt
 *
 */
public interface IReadCallRepository {

	/**
	 * Gets a list with all calls of a repository filtered by the
	 * specified filter implementation.
	 * 
	 * @param f a valid filter object
	 * @return list with calls
	 */
	public ICallList getCalls(IFilter filter);

	/**
	 * Gets a list with all calls of a repository filtered by the
	 * specified filter array. The order of the filtering is done
	 * by the order of the array object.
	 * 
	 * @param f a valid filter array. Array elements must not be null.
	 * @return list with calls
	 */
	public ICallList getCalls(IFilter[] filters);
	
	
	/**
	 * Gets a list with all calls of a repository filtered by the
	 * specified filter array. The order of the filtering is done
	 * by the order of the array object.
	 * 
	 * @param f a valid filter array. Array elements must not be null.
	 * @param count number off entries from repository
	 * @param offset starting point in repository
	 * @return list with calls
	 */
	public ICallList getCalls(IFilter[] filters, int count, int offset);
	
	/**
	 * Gets the number of call entries in the repository for a specific
	 * filter object.
	 * 
	 * @param f a valid filter array. Array elements must not be null.
	 * @return number of calls applied on this filter
	 */
	public int getCallCount(IFilter[] filters);
	
	
}
