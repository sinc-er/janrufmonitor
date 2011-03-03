package de.janrufmonitor.service.comment.api;

import java.util.Date;
import java.util.List;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IAttributeMap;

public interface IComment {
	
	public final static String COMMENT_ATTRIBUTE_STATUS = "cmt.status";
	public final static String COMMENT_ATTRIBUTE_FOLLOWUP = "cmt.followup";
	public final static String COMMENT_ATTRIBUTE_MODIFIED = "cmt.modified";
	public final static String COMMENT_ATTRIBUTE_CREATED = "cmt.created";
	public final static String COMMENT_ATTRIBUTE_SUBJECT = "cmt.subject";
	
	public void setID(String ID);
	
	public String getID();
	
	public void setText(String data);
	
	public String getText();
	
	public Date getDate();
	
	public void setDate(Date date);

	public void addAttachment(IAttachment atm);
	
	public void removeAttachment(String name);
	
	public IAttachment getAttachment(String name);
	
	public List getAttachments();
	
	public int getAttachmentCount();
	
	public IAttributeMap getAttributes();
	
	public void setAttrbutes(IAttributeMap m);
	
	public void addAttribute(IAttribute a);
	
}
