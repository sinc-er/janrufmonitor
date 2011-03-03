package de.janrufmonitor.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.janrufmonitor.repository.db.AbstractDatabaseHandler;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class GoogleContactsProxyDatabaseHandler extends AbstractDatabaseHandler {

	private IRuntime m_runtime;
	
	public GoogleContactsProxyDatabaseHandler(String driver, String connection, String user, String password, boolean initialize) {
		super(driver, connection, user, password, initialize);
	}

	protected void addPreparedStatements() throws SQLException {
		if (!isConnected()) throw new SQLException ("Database is disconnected.");

		// prepare statements
		m_preparedStatements.put("INSERT", m_con.prepareStatement("INSERT INTO mapping (uuid, country, areacode, number) VALUES (?,?,?,?);"));

		m_preparedStatements.put("DELETE", m_con.prepareStatement("DELETE FROM mapping WHERE uuid=?;"));
		
		m_preparedStatements.put("DELETE_ALL", m_con.prepareStatement("DELETE FROM mapping;"));

		m_preparedStatements.put("SELECT", m_con.prepareStatement("SELECT uuid FROM mapping WHERE country=? AND areacode=? AND number=?;"));
	}

	protected void createTables() throws SQLException {
		if (!isConnected()) throw new SQLException ("Database is disconnected.");

		Statement stmt = m_con.createStatement();
		stmt.execute("CREATE TABLE mapping (uuid VARCHAR(36), country VARCHAR(8), areacode VARCHAR(16), number VARCHAR(64));");
	}

	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}
	
	public void insert(String uuid, String country, String areacode, String number) throws SQLException {
		if (!isConnected()) throw new SQLException ("Database is disconnected.");
		
		PreparedStatement ps = (PreparedStatement) this.m_preparedStatements.get("INSERT");
		ps.setString(1, uuid);
		ps.setString(2, country);
		ps.setString(3, areacode);
		ps.setString(4, number);
		ps.execute();
		commit();
	}
	
	
	public void deleteAll() throws SQLException {
		if (!isConnected()) throw new SQLException ("Database is disconnected.");
		
		PreparedStatement ps = (PreparedStatement) this.m_preparedStatements.get("DELETE_ALL");
		ps.execute();
		commit();
	}
	
	
	public void delete(String uuid) throws SQLException {
		if (!isConnected()) throw new SQLException ("Database is disconnected.");
		
		PreparedStatement ps = (PreparedStatement) this.m_preparedStatements.get("DELETE");
		ps.setString(1, uuid);
		ps.execute();
		commit();
	}
	
	public List select(String country, String areacode, String number) throws SQLException {
		if (!isConnected()) throw new SQLException ("Database is disconnected.");
		List uuids = new ArrayList();
		PreparedStatement ps = (PreparedStatement) this.m_preparedStatements.get("SELECT");
		ps.setString(1, country);
		ps.setString(2, areacode);
		ps.setString(3, number);
		ResultSet r = ps.executeQuery();
		while (r.next()) {
			uuids.add(r.getString("uuid"));
		}
	
		return uuids;
	}
	
	public void disconnect() throws SQLException {
		if (isConnected()) {		
			super.setInitializing(false);
			Statement st = m_con.createStatement();
			st.execute("SHUTDOWN");
		}
		super.disconnect();
	}

	public void commit() throws SQLException {
		if (isConnected()) {			
			Statement st = m_con.createStatement();
			st.execute("COMMIT");
		}
		super.commit();
	}

	public void setInitializing(boolean init) {
		super.setInitializing(init);
	}

}
