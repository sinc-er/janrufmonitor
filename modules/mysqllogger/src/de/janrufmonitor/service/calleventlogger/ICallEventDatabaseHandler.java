package de.janrufmonitor.service.calleventlogger;

import java.sql.SQLException;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.repository.db.IDatabaseHandler;

public interface ICallEventDatabaseHandler extends IDatabaseHandler {

	public void insertCall(ICall c) throws SQLException ;
	
	public void insertEvent(ICall c, int event) throws SQLException;
	
}
