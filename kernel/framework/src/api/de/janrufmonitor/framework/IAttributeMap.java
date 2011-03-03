package de.janrufmonitor.framework;

import java.util.Iterator;

/**
 *  This interface must be implemented by a AttributeMap object, which should be
 *  used in the framework. The Map implementation contains IAttrubute instances as values 
 *  and the name (String) of the IAttribute as key.
 *
 *@author     Thilo Brandt
 *@created    2005/11/11
 */
public interface IAttributeMap {
    /**
     *  Checks if the given attribute is already in the map.
     *
     *@param  att  The IAttribute to be checked
     *@return      true if it is contained, false if not.
     */
    public boolean contains(IAttribute att);


    /**
     *  Checks if the given attribute name is already in the map.
     *
     *@param  attName  The name attribute of the IAttribute object.
     *@return          true if it is contained, false if not.
     */
    public boolean contains(String attName);
    

    /**
     *  Adds the IAttribute object to the map.
     *
     *@param  att  The IAttribute object to be added.
     */
    public void add(IAttribute att);

    
    /**
     *  Adds the IAttributeMap object to the map.
     *
     *@param  map  The IAttributeMap object to be added.
     */
    public void addAll(IAttributeMap map);
    
    
    /**
     *  Removes the IAttribute object from the map.
     *
     *@param  att  The IAttribute object to be removed.
     */
    public void remove(IAttribute att);


    /**
     *  Removed the IAttribute object with the given name.
     *
     *@param  attName  The name attribute of the IAttribute object to be removed.
     */
    public void remove(String attName);


    /**
     *  Gets the IAttribute object for the given name.
     *
     *@param  name  The name attribute of the requested IAttribute object.
     *@return       IAttribute object or <code>null</code>, if not contained.
     */
    public IAttribute get(String name);

 
    /**
     *  Gets the size of the IAttributeMap object.
     *
     *@return    The size as int
     */
    public int size();
    
    
    /**
     *  Gets an iterator of the map.
     *
     *@return    iterator of the map. Contains IAttribute instances.
     */
    public Iterator iterator();
    
}
