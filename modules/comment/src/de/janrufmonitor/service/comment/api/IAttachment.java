package de.janrufmonitor.service.comment.api;

import java.io.InputStream;
import java.io.Serializable;

public interface IAttachment extends Serializable {

	public void setName(String name);
	
	public String getName();
	
	public void setContent(InputStream is);
	
	public InputStream getConent();

}
