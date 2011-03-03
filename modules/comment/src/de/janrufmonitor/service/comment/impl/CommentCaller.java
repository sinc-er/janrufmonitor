package de.janrufmonitor.service.comment.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.service.comment.api.IComment;
import de.janrufmonitor.service.comment.api.ICommentCaller;

public class CommentCaller implements ICommentCaller {

	ICaller m_caller;
	Map m_comments;

	public CommentCaller(ICaller caller) {
		super();
		this.m_comments = new HashMap();
		this.m_caller = caller;
	}

	public void setCaller(ICaller caller) {
		this.m_caller = caller;
	}

	public ICaller getCaller() {
		return this.m_caller;
	}

	public void addComment(IComment comment) {
		this.m_comments.put(comment.getID(), comment);
	}

	public void removeComment(String id) {
		this.m_comments.remove(id);
	}

	public List getComments() {
		List comments = new ArrayList();
		Iterator iter = this.m_comments.keySet().iterator();
		while(iter.hasNext()) {
			comments.add(this.m_comments.get(iter.next()));
		}
		
		return comments;
	}
	
	public String toString() {
		return this.m_caller.toString() + " - " + this.getComments().toString();
	}

	public void removeComments() {
		this.m_comments = null;
		this.m_comments = new HashMap();		
	}

}
