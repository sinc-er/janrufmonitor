package de.janrufmonitor.service.server;

import java.util.*;

public class Client {

	private int m_port;
	private String m_ip;
	private String m_client;
	private long m_timestamp;
	private List m_events;
	private long m_byteSnd;
	private long m_byteRcv;
	private List m_history;

	public Client(String client, String ip, int port) {
		this.m_client = client;
		this.m_port = port;
		this.m_ip = ip;
		this.m_timestamp = System.currentTimeMillis();
		this.m_history = new ArrayList();
	}
	
	public long getByteSend() {
		return this.m_byteSnd;
	}
	
	public long getByteReceived() {
		return this.m_byteRcv;
	}
	
	public void setByteSend(long bytes) {
		this.m_byteSnd += bytes;
	}
	
	public void setByteReceived(long bytes) {
		this.m_byteRcv += bytes;
	}
	
	public List getEvents() {
		return (this.m_events==null? new ArrayList(1) : this.m_events);
	}
	
	public void setEvents(List events) {
		this.m_events = events;
	}
	
	public void setHistoryItem(ClientHistoryItem item) {
		this.m_history.add(item);
	}
	
	public List getHistory() {
		return this.m_history;
	}
	
	public String getClientName() {
		return (this.m_client==null ? "" : this.m_client);
	}
	
	public String getClientIP() {
		return (this.m_ip==null ? "0.0.0.0" : this.m_ip);
	}
	
	public long getTimestamp() {
		return this.m_timestamp;
	}
	
	public int getClientPort() {
		return this.m_port;
	}

	public boolean equals(Object o) {
		if (o instanceof Client) {
			if (this.m_client.equals(((Client)o).getClientName()) && 
				this.m_port == ((Client)o).getClientPort() &&
				this.m_ip.equals(((Client)o).getClientIP())
				)
			return true;
		}
		return false;
	}

	public int hashCode() {
		return this.m_port + this.m_client.hashCode() + this.m_ip.hashCode();
	}

	public String toString() {
		return this.m_client + " / "+this.m_ip + ":" + this.m_port;
	}

}
