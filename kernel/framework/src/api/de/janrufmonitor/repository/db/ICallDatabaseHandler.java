package de.janrufmonitor.repository.db;

import java.sql.SQLException;

import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.repository.filter.IFilter;

public interface ICallDatabaseHandler extends IDatabaseHandler {

	public void setCallList(ICallList cl) throws SQLException;
	
	public void updateCallList(ICallList cl) throws SQLException;
	
	public void deleteCallList(ICallList cl) throws SQLException;
	
	public ICallList getCallList(IFilter[] filters) throws SQLException;
	
	public ICallList getCallList(IFilter[] filters, int count, int offset) throws SQLException;
	
	public int getCallCount(IFilter[] filters) throws SQLException;
	
}
