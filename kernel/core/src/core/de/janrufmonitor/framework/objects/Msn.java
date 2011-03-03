package de.janrufmonitor.framework.objects;

import java.io.Serializable;

import de.janrufmonitor.framework.IMsn;

public class Msn implements IMsn, Serializable {

	private static final long serialVersionUID = -3388726991561916470L;
	
	private String m_msn;
	private String m_additional;
	
	public Msn(String msn, String additional) {
		this.m_msn = msn;
		this.m_additional = additional;
	}

	public void setMSN(String msn) {
		this.m_msn = msn;
	}

	public void setAdditional(String additional) {
		this.m_additional = additional;
	}

	public String getMSN() {
		return (this.m_msn==null ? "" : this.m_msn);
	}

	public String getAdditional() {
		return (this.m_additional==null ? "" : this.m_additional);
	}

	public boolean equals(Object msn) {
		if (msn instanceof Msn) {
			if (this.getMSN().equalsIgnoreCase(((Msn)msn).getMSN())){
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		return this.getMSN().hashCode();
	}

	public String toString() {
		if (this.getAdditional().length()>0){
			return this.getMSN() + " ("+this.getAdditional()+")";
		}
		return this.getMSN();
	}

}
