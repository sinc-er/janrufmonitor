package de.janrufmonitor.framework.objects;

import java.io.Serializable;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.formatter.Formatter;

public class Phonenumber implements IPhonenumber, Serializable {

	private static final long serialVersionUID = -3020735189698018392L;
	
	private String m_telephoneNumber;
	private String m_areaCode;
	private String m_internationalAreaCode;
	private String m_callNumber;
	boolean m_clir;
	
	public Phonenumber(String telephoneNumber) {
		if (telephoneNumber==null || telephoneNumber.length()==0) {
			this.m_clir = true;
		}
		this.m_telephoneNumber = telephoneNumber;
	}
    
	public Phonenumber(boolean isClir) {
		this.m_clir = isClir;
	}

	public Phonenumber(String intAreaCode, String areaCode, String callNumber) {
		if (intAreaCode!=null) { this.m_internationalAreaCode = intAreaCode; }
		if (areaCode!=null) { this.m_areaCode = areaCode; }
		if (callNumber!=null) { this.m_callNumber = callNumber; }
		this.buildTelephoneNumber();
	}
	
	private void buildTelephoneNumber() {
		if (this.getCallNumber().length()>0) {
			this.setTelephoneNumber(this.getAreaCode() + this.getCallNumber());
		}
		if (this.getAreaCode().length()==0 && 
			this.getCallNumber().length()==0) {
			this.setClired(true);
		} else {
			this.setClired(false);
		}
	}
	
	public void setAreaCode(String areaCode) {
		this.m_areaCode = areaCode;
		this.buildTelephoneNumber();
	}
    
	public void setCallNumber(String callNumber) {
		this.m_callNumber = callNumber;
		this.buildTelephoneNumber();
	}
    
	public void setClired(boolean isClir) {
		this.m_clir = isClir;
	}
    
	public void setIntAreaCode(String internationalAreaCode) {
		this.m_internationalAreaCode = internationalAreaCode;
	}
    
	public void setTelephoneNumber(String telephoneNumber) {
		this.m_telephoneNumber = telephoneNumber;
	}

	public String getIntAreaCode() {
		return (this.m_internationalAreaCode==null ? "" : this.m_internationalAreaCode);
	}

	public String getAreaCode() {
		return (this.m_areaCode==null ? "" : this.m_areaCode);
	}

	public String getCallNumber() {
		return (this.m_callNumber==null ? "" : this.m_callNumber);
	}

	public String getTelephoneNumber() {
		return (this.m_telephoneNumber==null ? "" : this.m_telephoneNumber);
	}

	public boolean isClired() {
		return this.m_clir;
	}

	public boolean equals(Object pn) {
		if (pn instanceof Phonenumber) {
			if (this.getIntAreaCode().equalsIgnoreCase(((Phonenumber)pn).getIntAreaCode()) &&
				this.getTelephoneNumber().equalsIgnoreCase(((Phonenumber)pn).getTelephoneNumber())
				) {
					return true;
			}
		}
		return false;
	}

	public int hashCode() {
		return this.getTelephoneNumber().hashCode() + this.getIntAreaCode().hashCode();
	}

	public String toString() {
		if (this.getCallNumber().length()==0) {
			return this.getTelephoneNumber();
		}
		return Formatter.getInstance(PIMRuntime.getInstance()).parse(IJAMConst.GLOBAL_VARIABLE_CALLERNUMBER, this);
	}

}
