package de.janrufmonitor.service.server;

public class ClientHistoryItem {

	private long m_timestamp;
	private String m_event;

	public ClientHistoryItem(long timestamp, String event) {
		this.m_event = event;
		this.m_timestamp = timestamp;
	}
	
	public long getTimestamp() {
		return this.m_timestamp;
	}
	
	public String getEvent() {
		return this.m_event;
	}

}
