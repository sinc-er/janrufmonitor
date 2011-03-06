package de.janrufmonitor.service.commons.http.simple;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import simple.http.connect.Connection;
import simple.http.connect.ConnectionFactory;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.service.commons.http.handler.Handler;
import de.janrufmonitor.service.commons.http.simple.handler.GenericHandler;

public class SimplePortListener {

	private class ServerThread implements Runnable {

		private ServerSocket m_socket;
		
		public void run() {
			if (m_h!=null) {
				Connection connection = ConnectionFactory.getConnection(m_h);
		        try {
		        	this.m_socket = new ServerSocket(getPort());
		        	//this.m_socket.setSoTimeout(15000);
		        	connection.connect(this.m_socket);
				} catch (IOException e) {
					m_logger.severe(e.getMessage());
				}
			}
		}
		
		public void close() {
			if (this. m_socket!=null)
				try {
					m_socket.close();
				} catch (IOException e) {
					m_logger.severe(e.getMessage());
				}
		}
		
	}
		
	private Handler m_h;
	private int m_port;
	private String m_address;
    private Logger m_logger;
    private ServerThread m_t;
	
	public SimplePortListener(Handler h, int port) {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		if (this.m_logger==null)
			this.m_logger = Logger.getAnonymousLogger();
		this.m_h = h;
		this.m_port = port;
		try {
			this.m_address = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			this.m_logger.severe(e.getMessage());
		}		
	}

	public void start() {
		if (this.m_t==null) {
			this.m_t = new ServerThread();
			Thread t = new Thread(this.m_t);
			t.setName("SimplePortListener-"+this.getServerIP()+":"+this.getPort());
			t.setDaemon(false);
			t.start();
		} else {
			this.m_logger.warning("Server is already started.");
		}
	}
	
	public void stop() {
		if (this.m_t!=null) {
			this.m_t.close();
		}
		this.m_t = null;
	}
	
	public int getPort() {
		return this.m_port;
	}
	
	public String getServerIP() {
		return (this.m_address==null ? "0.0.0.0" : this.m_address);
	}
	
	public static void main(String[] args) {
		SimplePortListener spl = new SimplePortListener(
		   new GenericHandler(),
		   80
		);
		spl.start();
	}
}
