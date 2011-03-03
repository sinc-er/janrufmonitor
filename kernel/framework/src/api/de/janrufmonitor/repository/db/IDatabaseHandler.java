package de.janrufmonitor.repository.db;

import java.sql.SQLException;

public interface IDatabaseHandler {
	public boolean isConnected() throws SQLException;
	
	public void connect() throws SQLException, ClassNotFoundException;
	
	public void commit() throws SQLException;
	
	public void rollback() throws SQLException;
	
	public void disconnect() throws SQLException;
	
	public void setCommitCount(int c);
	
	public boolean isKeepAlive();
	
	public void setKeepAlive(boolean keep);
}
