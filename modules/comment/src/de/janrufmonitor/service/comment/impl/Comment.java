package de.janrufmonitor.service.comment.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.comment.api.IAttachment;
import de.janrufmonitor.service.comment.api.IComment;

public class Comment implements IComment {

	String m_id;
	String m_data;
	Date m_date;
	Map m_attachments;
	IAttributeMap m_attributes;

	public Comment() {
		this.m_id = Long.toString(System.currentTimeMillis());
		this.m_date = new Date(System.currentTimeMillis());
		this.m_attachments = new HashMap();
		this.m_attributes = PIMRuntime.getInstance().getCallerFactory().createAttributeMap();
		this.m_attributes.add(
			PIMRuntime.getInstance().getCallerFactory().createAttribute(IComment.COMMENT_ATTRIBUTE_CREATED, Long.toString(this.m_date.getTime()))
		);
	}
	
	public Comment(String ID) {
		this.m_id = ID;
		this.m_date = new Date();
		this.m_attachments = new HashMap();
	}

	public void setID(String ID) {
		this.m_id = ID;
	}

	public String getID() {
		return this.m_id;
	}

	public void setText(String data) {
		this.m_data = data;
	}

	public String getText() {
		return (this.m_data!=null ? this.m_data : "");
	}

	public void addAttachment(IAttachment atm) {
		this.m_attachments.put(atm.getName(), atm);
	}

	public void removeAttachment(String name) {
		this.m_attachments.remove(name);
	}

	public IAttachment getAttachment(String name) {
		return (IAttachment)this.m_attachments.get(name);
	}

	public List getAttachments() {
		List attachments = new ArrayList();
		Iterator iter = this.m_attachments.keySet().iterator();
		while(iter.hasNext()) {
			attachments.add(this.m_attachments.get(iter.next()));
		}
		
		return attachments;
	}

	public int getAttachmentCount() {
		return this.m_attachments.size();
	}

	public Date getDate() {
		return this.m_date;
	}

	public void setDate(Date date) {
		this.m_date = date;
	}

	public void addAttribute(IAttribute a) {
		this.m_attributes.add(a);
	}

	public IAttributeMap getAttributes() {
		return this.m_attributes;
	}

	public void setAttrbutes(IAttributeMap m) {
		this.m_attributes = m;		
	}
	
	public String toString(){
		return this.m_id + ": " + this.m_date + " @ " +this.m_data;
	}
	
	public int hashCode() {
		return this.m_id.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof Comment) {
			return ((Comment)o).getID().equalsIgnoreCase(this.m_id);
		}
		return false;
	}
}
