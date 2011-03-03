package de.janrufmonitor.framework;

/**
 *  This interface must be implemented by a call list object, which should be
 *  used in the framework. 
 *
 *@author     Thilo Brandt
 *@created    2003/08/10
 */
public interface ICallList {

	/**
	 *  Checks if a call object is in this list.
	 *
	 *@param  call  call to be checked in this list.
	 *@return       true if call is in the list, if not false.
	 */
    public boolean contains(ICall call);

	/**
	 *  Removes a call from the list.
	 *
	 *@param  call  call to be removed from the list.
	 */
    public void remove(ICall call);

	/**
	 *  Add a call to the list.
	 *
	 *@param  call  call to be added to the list.
	 */
    public void add(ICall call);

	/**
	 *  Add a call list to the list.
	 *
	 *@param  callList  call list to be added to the list.
	 */
	public void add(ICallList callList);
	
	/**
	 *  Gets a call with a certain position from the list.
	 *
	 *@param  position  position of the call. 
	 *@return 		a call object or <code>null</code> if none was found.
	 */
    public ICall get(int position);

	/**
	 *  Gets the size of the list.
	 *
	 *@return 		size of the list.
	 */
    public int size();

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
