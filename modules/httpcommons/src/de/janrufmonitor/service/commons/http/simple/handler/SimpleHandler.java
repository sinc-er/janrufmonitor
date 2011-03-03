package de.janrufmonitor.service.commons.http.simple.handler;

import simple.http.Request;
import simple.http.Response;

public interface SimpleHandler {
	
	public void handle(Request req, Response resp, int errorcode);
	
}
