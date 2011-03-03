package de.janrufmonitor.service.comment.api;

import java.util.List;

import de.janrufmonitor.framework.ICaller;

public interface ICommentCaller {

	public void setCaller(ICaller caller);
	
	public ICaller getCaller();
	
	public void addComment(IComment comment);
	
	public void removeComment(String id);
	
	public void removeComments();
	
	public List getComments();
	
}
