package de.janrufmonitor.framework.objects;

import java.io.Serializable;

import de.janrufmonitor.framework.IName;

public class Name implements IName, Serializable {

	private static final long serialVersionUID = 99666099717470999L;
	
	private String m_firstname;
	private String m_lastname;
	private String m_additional;
	
	public Name(String firstname, String lastname) {
		this(firstname, lastname, "");
	}

	public Name(String firstname, String lastname, String additional) {
		this.m_firstname = firstname;
		this.m_lastname = lastname;
		this.m_additional = additional;
	}

	public String getFirstname() {
		return (this.m_firstname==null ? "" : this.m_firstname);
	}

	public String getLastname() {
		return (this.m_lastname==null ? "" : this.m_lastname);
	}

	public String getAdditional() {
		return (this.m_additional==null ? "" : this.m_additional);
	}

	public String getFullname() {
		return (this.getFirstname()+" "+this.getLastname()).trim();
	}

	public void setFirstname(String firstname) {
		this.m_firstname = firstname;
	}

	public void setLastname(String lastname) {
		this.m_lastname = lastname;
	}

	public void setAdditional(String additional) {
		this.m_additional = additional;
	}

	public boolean equals(Object name) {
		if (name instanceof Name) {
			Name o = (Name)name;
			if (o.getFirstname().equalsIgnoreCase(this.getFirstname()) && 
				o.getLastname().equalsIgnoreCase(this.getLastname()) &&
				o.getAdditional().equalsIgnoreCase(this.getAdditional())
			) {
				return true; 
			}
		}
		return false;
	}

	public int hashCode() {
		return this.getFirstname().hashCode() + this.getLastname().hashCode() + this.getAdditional().hashCode();
	}

	public String toString() {
		if (this.getFullname().length()==0) {
			return this.getAdditional();
		}
		return (this.getFullname() + " - " + this.getAdditional()).trim();
	}

}
