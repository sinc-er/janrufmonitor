package de.janrufmonitor.framework;

/**
 *  This interface must be implemented by a AttributeList object, which should be
 *  used in the framework.
 *
 *@author     Thilo Brandt
 *@created    2003/03/01
 *@deprecated since 4.4, use IAttrbuteMap instead !
 */
public interface IAttributeList extends IAttributeMap {

    /**
     *  Updates the IAttribute object in the list. If not contained it should be added.
     *
     *@param  att  The IAttribute object to be updated.
     */
    public void update(IAttribute att);


    /**
     *  Gets the IAttribute object by its position
     *
     *@param  position  Position of an IAttribute object
     *@return           IAttribute object
     */
    public IAttribute get(int position);

}
