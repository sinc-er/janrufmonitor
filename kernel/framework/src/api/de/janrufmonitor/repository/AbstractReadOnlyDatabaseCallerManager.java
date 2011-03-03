package de.janrufmonitor.repository;

import java.sql.SQLException;
import java.util.logging.Level;

import de.janrufmonitor.repository.db.ICallerDatabaseHandler;

public abstract class AbstractReadOnlyDatabaseCallerManager extends AbstractReadOnlyCallerManager {
	
	protected ICallerDatabaseHandler m_dbh;
	
	public AbstractReadOnlyDatabaseCallerManager() {
		super();
	}

	public boolean isSupported(Class c) {
		return c.isInstance(this);
	}
	
	public void shutdown() {
		if (this.m_dbh!=null)
			try {
				getDatabaseHandler().commit();
				if (getDatabaseHandler().isConnected())
					getDatabaseHandler().disconnect();
				this.m_dbh = null;
			} catch (SQLException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			}
		super.shutdown();
	}
	
	/**
	 * Creates a new instance of a specific database handler.
	 * 
	 * @return a valid database handler, must not be null.
	 */
	protected abstract ICallerDatabaseHandler getDatabaseHandler();

}
