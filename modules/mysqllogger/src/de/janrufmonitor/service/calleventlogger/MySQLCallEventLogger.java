package de.janrufmonitor.service.calleventlogger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.event.IEvent;
import de.janrufmonitor.framework.event.IEventBroker;
import de.janrufmonitor.framework.event.IEventConst;
import de.janrufmonitor.repository.db.AbstractDatabaseHandler;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.AbstractReceiverConfigurableService;
import de.janrufmonitor.util.formatter.Formatter;

public class MySQLCallEventLogger extends AbstractReceiverConfigurableService {
	
	private static final String CFG_LOGDETAILS = "logdetails";

	private class MySqlHandler extends AbstractDatabaseHandler implements ICallEventDatabaseHandler {

		private IRuntime m_runtime;
		private Formatter m_f;

		public MySqlHandler(String driver, String connection, String user, String password, boolean initialize) {
			super(driver, connection, user, password, initialize);
		}
		
		private Formatter getFormatter() {
			if (this.m_f==null) {
				this.m_f = Formatter.getInstance(PIMRuntime.getInstance());
			}
			return this.m_f;
		}
		
		protected void addPreparedStatements() throws SQLException {
			// prepare MySQL statements
			m_preparedStatements.put("INSERT_CALL", m_con.prepareStatement("INSERT INTO calls (uuid, country, areacode, number, msn, faddress, direction) VALUES (?, ?, ?, ?, ?, ?, ?);"));			
			m_preparedStatements.put("INSERT_EVENT", m_con.prepareStatement("INSERT INTO callevents (uuid, event) VALUES (?, ?);"));			
		}

		protected IRuntime getRuntime() {
			if (this.m_runtime==null)
				this.m_runtime = PIMRuntime.getInstance();
			return this.m_runtime;
		}
		
		public void connect() throws SQLException, ClassNotFoundException {
			try {
				super.connect();
			} catch (SQLException e) {
				PropagationFactory.getInstance().fire(
					new Message(Message.ERROR, NAMESPACE, "connecterror", e));
				throw e;
			}
			
		}

		protected void createTables() throws SQLException {
			if (!isConnected()) throw new SQLException ("Database is disconnected.");

			Statement stmt = m_con.createStatement();
			try {
				stmt.execute("DROP TABLE calls;");
				stmt.execute("DROP TABLE callevents;");
			} catch (SQLException e) {
				this.m_logger.warning(e.getMessage());
			} 
			
			stmt.execute("CREATE TABLE IF NOT EXISTS calls (id int(10) NOT NULL AUTO_INCREMENT, uuid VARCHAR(36) COLLATE latin1_general_ci, country VARCHAR(8), areacode VARCHAR(16), number VARCHAR(64), msn VARCHAR(20), calltime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, faddress longtext COLLATE latin1_general_ci, direction int(2), PRIMARY KEY (id)) ENGINE=MyISAM  DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci AUTO_INCREMENT=1;");
			
			stmt.execute("CREATE TABLE IF NOT EXISTS callevents (id int(10) NOT NULL AUTO_INCREMENT, uuid VARCHAR(36) COLLATE latin1_general_ci, event int(4), eventtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY (id),  KEY uuid (uuid)) ENGINE=MyISAM  DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci AUTO_INCREMENT=1 ;");
		}
		
		private void createCall(PreparedStatement ps, String uuid, String country, String areacode, String number, String msn, String faddress, int dir) throws SQLException {
			ps.clearParameters();
			ps.setString(1, uuid);
			ps.setString(2, country);
			ps.setString(3, areacode);
			ps.setString(4, number);
			ps.setString(5, msn);
			ps.setString(6, faddress);
			ps.setInt(7, dir);
			ps.addBatch();
		}
		
		private void createEvent(PreparedStatement ps, String uuid, int event) throws SQLException {
			ps.clearParameters();
			ps.setString(1, uuid);
			ps.setInt(2, event);
			ps.addBatch();
		}
		
		public void insertCall(ICall c) throws SQLException {
			if (!isConnected())
				try {
					this.connect();
				} catch (ClassNotFoundException e) {
					throw new SQLException(e.getMessage());
				}
			
			PreparedStatement ps = this.getStatement("INSERT_CALL");
			ps.clearBatch();
			
			int dir = getDirection(c);
			
			this.createCall(ps, c.getUUID(), c.getCaller().getPhoneNumber().getIntAreaCode(), c.getCaller().getPhoneNumber().getAreaCode(), c.getCaller().getPhoneNumber().getCallNumber(), c.getMSN().getMSN(), getFormatter().parse("%a:fn% %a:ln%%CRLF%%a:str% %a:no%%CRLF%%a:pcode% %a:city%%CRLF%%a:cntry%", (c.getCaller().getAttributes())),dir);

			// execute the rest batch content
			ps.executeBatch();
			
			ps = this.getStatement("INSERT_EVENT");
			ps.clearBatch();
			
			this.createEvent(ps, c.getUUID(), IEventConst.EVENT_TYPE_IDENTIFIED_CALL);

			// execute the rest batch content
			ps.executeBatch();
		}
		
		private int getDirection(ICall c) {
			if (c.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS)!=null && c.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS).getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_OUTGOING))
				return 1;
			return 0;
		}

		public void insertEvent(ICall c, int event) throws SQLException {
			if (!isConnected())
				try {
					this.connect();
				} catch (ClassNotFoundException e) {
					throw new SQLException(e.getMessage());
				}
			
			PreparedStatement ps = this.getStatement("INSERT_EVENT");
			ps.clearBatch();
			
			this.createEvent(ps, c.getUUID(), event);

			// execute the rest batch content
			ps.executeBatch();
		}
		
		protected boolean isInitializing() {
			try {
				if (!isConnected()) return false;
			} catch (SQLException e) {
				return false;
			}

			try {
				Statement stmt = m_con.createStatement();	
				stmt.execute("SELECT uuid FROM calls LIMIT 0,1;");
				// table exists
			} catch (SQLException e) {
				return true;
			}
			
			return false;
		}


	}

	private static String ID = "MySQLCallEventLogger";
	private static String NAMESPACE = "service.MySQLCallEventLogger";
	
	private static String CFG_DB_SERVER = "dbserver";
	private static String CFG_DB_DB = "dbdb";
	private static String CFG_DB_PORT = "dbport";
	private static String CFG_DB_USER = "dbuser";
	private static String CFG_DB_PASSWORD = "dbpassword";
	private static String CFG_KEEP_ALIVE= "keepalive";
	
	private IRuntime m_runtime;
	private ICallEventDatabaseHandler m_dbh;
	
	public MySQLCallEventLogger () {
		super();
		this.getRuntime().getConfigurableNotifier().register(this);	
	}
	
	public String getNamespace() {
		return MySQLCallEventLogger.NAMESPACE;
	}

	public String getID() {
		return MySQLCallEventLogger.ID;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}
	
	public void startup() {
		super.startup();
		
		IEventBroker eventBroker = this.getRuntime().getEventBroker();
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_CALL));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_OUTGOING_CALL));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLACCEPTED));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLCLEARED));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLREJECTED));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_OUTGOING_CALL));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_OUTGOING_CALL_ACCEPTED));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_INCOMING_INFO));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_MANUALCALLACCEPTED));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_INCOMINGCALL));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_OUTGOINGCALL));
		if (this.isLogdetails())
			eventBroker.register(this, eventBroker.createEvent(9999));
		this.m_logger.info("MySQLCallEventLogger is started ...");		
	}

	public void shutdown() {
		super.shutdown();
		IEventBroker eventBroker = this.getRuntime().getEventBroker();
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_CALL));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_OUTGOING_CALL));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLACCEPTED));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLCLEARED));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLREJECTED));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_OUTGOING_CALL));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_OUTGOING_CALL_ACCEPTED));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_INCOMING_INFO));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_MANUALCALLACCEPTED));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_INCOMINGCALL));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_OUTGOINGCALL));
		eventBroker.unregister(this, eventBroker.createEvent(9999));
		this.m_logger.info("MySQLCallEventLogger is shut down ...");
	}
	
	private ICallEventDatabaseHandler getDatabaseHandler() {
		if (this.m_dbh==null) {
			this.m_dbh = new MySqlHandler("com.mysql.jdbc.Driver", "jdbc:mysql://"+this.m_configuration.getProperty(CFG_DB_SERVER, "localhost")+":"+this.m_configuration.getProperty(CFG_DB_PORT, "3306")+"/"+this.m_configuration.getProperty(CFG_DB_DB, "journal"), this.m_configuration.getProperty(CFG_DB_USER), this.m_configuration.getProperty(CFG_DB_PASSWORD), false);
			this.m_dbh.setKeepAlive((m_configuration.getProperty(CFG_KEEP_ALIVE, "false").equalsIgnoreCase("true")? true : false));
		}	
		return this.m_dbh;
	}
	
	public void receivedIdentifiedCall(IEvent event) {
		super.receivedIdentifiedCall(event);
		ICall aCall = (ICall)event.getData();
		if (aCall!=null) {
			if (getRuntime().getRuleEngine().validate(this.getID(), aCall.getMSN(), aCall.getCIP(), aCall.getCaller().getPhoneNumber())) {
				try {
					this.getDatabaseHandler().insertCall(aCall);
				} catch (SQLException e) {
					this.m_logger.log(Level.SEVERE, e.getMessage(), e);
				}
			} else {
				this.m_logger.info("No rule assigned to execute this service for call: "+aCall);
			}
		} 
	}
	
	public void receivedOtherEventCall(IEvent event) {
		super.receivedOtherEventCall(event);
		
		if (event.getType() == IEventConst.EVENT_TYPE_CALLACCEPTED ||
			event.getType() == IEventConst.EVENT_TYPE_CALLREJECTED ||
			event.getType() == IEventConst.EVENT_TYPE_CALLCLEARED) {	
			
			ICall aCall = (ICall)event.getData();
			if (aCall!=null) {
				try {
					this.getDatabaseHandler().insertEvent(aCall, event.getType());
				} catch (SQLException e) {
					this.m_logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
		if (event.getType() == 9999 && this.isLogdetails()) {
			ICall aCall = (ICall)event.getData();
			if (aCall!=null && aCall.getAttribute("tapi.value")!=null) {
				try {
					this.getDatabaseHandler().insertEvent(aCall, Integer.parseInt(aCall.getAttribute("tapi.value").getValue()));
				} catch (SQLException e) {
					this.m_logger.log(Level.SEVERE, e.getMessage(), e);
				} catch (Exception e) {
					this.m_logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
		
	}

	private boolean isLogdetails() {
		return getRuntime().getConfigManagerFactory().getConfigManager().getProperty(NAMESPACE, CFG_LOGDETAILS).equalsIgnoreCase("true");
	}
	
}
