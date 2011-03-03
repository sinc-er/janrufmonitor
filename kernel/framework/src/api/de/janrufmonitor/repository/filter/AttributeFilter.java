package de.janrufmonitor.repository.filter;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IAttributeMap;

/**
 * This class is an attribute filter.
 * 
 *@author     Thilo Brandt
 *@created    2004/07/17
 */
public class AttributeFilter extends AbstractFilter {

	private IAttribute m_a;
	private IAttributeMap m_map;
	
	/**
	 * Creates a new attribute filter object.
	 * @param c a valid attribute
	 */
	public AttributeFilter(IAttribute c) {
		super();
		this.m_filter = c;
		this.m_a = c;
		this.m_type = FilterType.ATTRIBUTE;
	}
	
	/**
	 * Creates a new attribute filter object.
	 * @param c a valid attribute
	 */
	public AttributeFilter(IAttributeMap map) {
		super();
		this.m_filter = map;
		this.m_map = map;
		this.m_type = FilterType.ATTRIBUTE;
	}
	
	/**
	 * Gets the attribute to be filtered.
	 * 
	 * @return a valid attribute object.
	 */
	public IAttribute getAttribute() {
		return this.m_a;
	}
	
	/**
	 * Gets the attribute map to be filtered.
	 * 
	 * @return a valid attribute map object.
	 */
	public IAttributeMap getAttributeMap() {
		return this.m_map;
	}
	
	public String toString() {
		return AttributeFilter.class.getName()+"#"+this.m_filter;
	}

}
