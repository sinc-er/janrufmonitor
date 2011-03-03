package de.janrufmonitor.framework.objects;

import java.io.Serializable;

import de.janrufmonitor.framework.ICip;

public class Cip implements ICip, Serializable {

	private static final long serialVersionUID = 3830630939330437509L;
	
	private String m_cip;
	private String m_additional;
	
	public Cip(String cip, String additional) {
		this.m_cip = cip;
		this.m_additional = additional;
	}

	public void setCIP(String cip) {
		this.m_cip = cip;
	}

	public void setAdditional(String additional) {
		this.m_additional = additional;
	}

	public String getCIP() {
		return (this.m_cip==null ? "" : this.m_cip);
	}

	public String getAdditional() {
		return (this.m_additional==null ? "" : this.m_additional);
	}

	public boolean equals(Object cip) {
		if (cip instanceof Cip) {
			if (this.getCIP().equalsIgnoreCase(((Cip)cip).getCIP())){
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		return this.getCIP().hashCode();
	}

	public String toString() {
		if (this.getAdditional().length()>0){
			return this.getCIP() + " (" + this.getAdditional() + ")";
		}
		return this.getCIP();
	}

}
