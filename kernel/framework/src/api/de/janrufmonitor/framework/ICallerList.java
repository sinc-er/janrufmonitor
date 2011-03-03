package de.janrufmonitor.framework;

/**
 *  This interface must be implemented by a caller list object, which should be
 *  used in the framework. 
 *
 *@author     Thilo Brandt
 *@created    2003/08/10
 */
public interface ICallerList {

	/**
	 *  Checks if a caller object is in this list.
	 *
	 *@param  caller  caller to be checked in this list.
	 *@return       true if caller is in the list, if not false.
	 */
    public boolean contains(ICaller caller);

	/**
	 *  Removes a caller from the list.
	 *
	 *@param  caller  caller to be removed from the list.
	 */
    public void remove(ICaller caller);

	/**
	 *  Add a caller to the list.
	 *
	 *@param  caller  caller to be added to the list.
	 */
    public void add(ICaller caller);
    
	/**
	 *  Add a caller list to the list.
	 *
	 *@param  callerList  caller list to be added to the list.
	 */
	public void add(ICallerList callerList);

	/**
	 *  Gets a caller with a certain position from the list.
	 *
	 *@param  position  position of the caller. 
	 *@return 		a caller object or <code>null</code> if none was found.
	 */
    public ICaller get(int position);

	/**
	 *  Sorts the list with a default sort order.
	 */
    public void sort();

	/**
	 *  Sorts the list with a defined sort order.
	 * 
	 *@param  order  the sort order.
	 *@param  direction  the direction, true ascending, false descending.
	 */
    public void sort(int order, boolean direction);

	/**
	 *  Gets the size of the list.
	 *
	 *@return 		size of the list.
	 */
    public int size();

	/**
	 *  Clears the list, means removes all elements.
	 */
    public void clear();
    
    /**
     * Returns an object array of this list.
     * 
     * @return an object array of the list
     */
    public Object[] toArray();
}
