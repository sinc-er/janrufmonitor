package de.janrufmonitor.framework.objects;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICip;
import de.janrufmonitor.framework.IMsn;

public class Call implements ICall, Serializable {

	private static final long serialVersionUID = -3170103563310248309L;
	
	private ICaller m_caller;
	private IMsn m_msn;
	private Date m_date;
	private ICip m_cip;
	private String m_uuid;
	private IAttributeMap m_attributeMap;
	
	public Call(String uuid, ICaller caller, IMsn msn, ICip cip, Date date, IAttributeMap attributeMap) {
		this.m_uuid = uuid;
		this.m_caller = caller;
		this.m_msn = msn;
		this.m_cip = cip;
		this.m_date = date;
		this.m_attributeMap = attributeMap;
	}

	public void setAttribute(IAttribute att) {
		this.m_attributeMap.add(att);
	}
    
	public void setAttributes(IAttributeMap attMap) {
		this.m_attributeMap = attMap;
	}
    
	public void setCIP(ICip cip) {
		this.m_cip = cip;
	}
    
	public void setCaller(ICaller caller) {
		this.m_caller = caller;
	}
    
	public void setDate(Date date) {
		this.m_date = date;
	}
    
	public void setMSN(IMsn msn) {
		this.m_msn = msn;
	}
    
	public void setUUID(String uuid) {
		this.m_uuid = uuid;
	}

	public IAttribute getAttribute(String name) {
		return this.m_attributeMap.get(name);
	}
    
	public IAttributeMap getAttributes() {
		return this.m_attributeMap;
	}
    
	public ICip getCIP() {
		return this.m_cip;
	}
    
	public ICaller getCaller() {
		return this.m_caller;
	}
    
	public Date getDate() {
		return this.m_date;
	}
    
	public IMsn getMSN() {
		return this.m_msn;
	}
    
	public String getUUID() {
		return this.m_uuid;
	}
		
	public Object clone() throws CloneNotSupportedException {
		Caller cloneCaller = (Caller)((Caller)this.m_caller).clone();
		String cloneUuid = this.m_uuid;
		Date cloneDate = this.m_date;
		AttributeMap cloneAttribs = new AttributeMap();
		Iterator i = this.m_attributeMap.iterator();
		IAttribute att = null;
		while(i.hasNext()) {
			att = (IAttribute)i.next();
			Attribute cloneAttrib =
					new Attribute(att.getName(),
								  att.getValue());
			cloneAttribs.add(cloneAttrib);
		}
		Cip cloneCip = new Cip(this.m_cip.getCIP(), this.m_cip.getAdditional());
		Msn cloneMsn = new Msn(this.m_msn.getMSN(),this.m_msn.getAdditional());

		return new Call(cloneUuid, cloneCaller, cloneMsn, cloneCip, cloneDate, cloneAttribs);
	}    

	public boolean equals(Object c) {
		if (c instanceof Call) {
			if (((Call)c).getCaller().equals(this.getCaller()) &&
				((Call)c).getDate().equals(this.getDate())&&
				((Call)c).getCIP().equals(this.getCIP()) &&
				((Call)c).getMSN().equals(this.getMSN())) {
					return true;
				}
		}
		return false;
	}

	public int hashCode() {
		return this.getDate().hashCode() + this.getCaller().hashCode() + this.getMSN().hashCode() + this.getCIP().hashCode();
	}

	public String toString() {
		StringBuffer callToString = new StringBuffer();
		callToString.append("{CALL: ");
		callToString.append("[UUID: " + this.getUUID() + "]");
		callToString.append("[" + this.getCaller() + "]");
		callToString.append("[MSN: " + this.getMSN() + "]");
		callToString.append("[CIP: " + this.getCIP() + "]");
		callToString.append("[DATE: " + this.getDate() + "]");
		callToString.append(this.m_attributeMap.toString());
		callToString.append("}");
		return callToString.toString();
	}

}
