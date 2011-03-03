package de.janrufmonitor.framework.objects;

import java.io.Serializable;

import de.janrufmonitor.framework.IAttribute;

public class Attribute implements IAttribute, Serializable {

	private static final long serialVersionUID = -8797985661958758099L;
	
	private String m_name;
	private String m_value;

	public Attribute(String name, String value) {
		this.m_name = name;
		this.m_value = value;
	}

	public void setName(String name) {
		this.m_name = name;
	}

	public void setValue(String value) {
		this.m_value = value;
	}

	public String getName() {
		return (this.m_name==null ? "" : this.m_name);
	}

	public String getValue() {
		return (this.m_value==null ? "" : this.m_value);
	}

	public boolean equals(Object attribute) {
		if (attribute instanceof Attribute) {
			if (this.getName().equalsIgnoreCase(((Attribute)attribute).getName())
				) {
				return true;			
			}
		}
		return false;
	}

	public int hashCode() {
		return this.getName().hashCode();
	}

	public String toString() {
		return this.getName() + " = " + this.getValue();
	}

}
