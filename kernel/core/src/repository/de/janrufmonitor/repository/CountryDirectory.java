package de.janrufmonitor.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;

import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.framework.monitor.PhonenumberInfo;
import de.janrufmonitor.repository.db.ICallerDatabaseHandler;
import de.janrufmonitor.repository.db.hsqldb.HsqldbCallerDatabaseHandler;
import de.janrufmonitor.repository.zip.ZipArchive;
import de.janrufmonitor.repository.zip.ZipArchiveException;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.io.Serializer;
import de.janrufmonitor.util.io.SerializerException;
import de.janrufmonitor.util.io.Stream;
import de.janrufmonitor.util.string.StringUtils;
import de.janrufmonitor.util.uuid.UUID;

public class CountryDirectory extends AbstractReadOnlyDatabaseCallerManager {

	private static String ID = "CountryDirectory";

	private String NAMESPACE = "repository.CountryDirectory";

	private static String CFG_DB = "db";

	private static String CFG_ROOT = "root";

	private static String CFG_DATAFILE = "datafile";

	private static String CFG_COMMIT_COUNT = "commit";

	private static String CFG_KEEP_ALIVE = "keepalive";

	private static String CFG_DEFAULT_AREACODE_LENGTH = "areacodelength";

	private IRuntime m_runtime;

	private String m_root;

	private boolean m_isMigrating; 
	
	private class CountryDirectoryHandler extends HsqldbCallerDatabaseHandler {

		private IRuntime m_runtime;

		public CountryDirectoryHandler(String driver, String connection,
				String user, String password, boolean initialize) {
			super(driver, connection, user, password, initialize);
		}

		protected IRuntime getRuntime() {
			if (this.m_runtime == null)
				this.m_runtime = PIMRuntime.getInstance();
			return this.m_runtime;
		}
		
		protected void addPreparedStatements() throws SQLException {
			super.addPreparedStatements();
			
			m_preparedStatements.put("SELECT_CALLER_PHONE2", m_con.prepareStatement("SELECT content FROM callers WHERE country=? AND areacode=? AND number=?;"));
			m_preparedStatements.put("DELETE_ATTRIBUTE_ALL", m_con.prepareStatement("DELETE FROM attributes;"));
		}

		public void deleteCallerList(ICallerList cl) throws SQLException {
			super.deleteCallerList(cl);
			
			PreparedStatement ps = this.getStatement("DELETE_ATTRIBUTE_ALL");
			ps.execute();
		}

		public ICaller getCaller(IPhonenumber pnp) throws SQLException {
			if (!isConnected())
				try {
					this.connect();
				} catch (ClassNotFoundException e) {
					throw new SQLException(e.getMessage());
				}

			try {
				IPhonenumber p = this.normalizePhonenumber(pnp);
	
				// check if caller is in local properties file
				PreparedStatement ps = this.getStatement("SELECT_CALLER_PHONE2");
				ps.clearParameters();
				ps.setString(1, p.getIntAreaCode());
				ps.setString(2, p.getAreaCode());
				ps.setString(3, "area");
				ResultSet rs = ps.executeQuery();
				ICaller c = null;
				while (rs.next()) {
					try {
						c = Serializer.toCaller(rs.getString("content").getBytes(),
								this.getRuntime());
						if (c != null) {
							c.getPhoneNumber().setIntAreaCode(p.getIntAreaCode());
							c.getPhoneNumber().setAreaCode(p.getAreaCode());
							c.getPhoneNumber().setCallNumber(p.getCallNumber());
							c.setUUID(new UUID().toString());
							return c;
						}
					} catch (SerializerException e) {
						this.m_logger.log(Level.SEVERE, e.getMessage(), e);
					}
				}
	
				ps.clearParameters();
				ps.setString(1, p.getIntAreaCode());
				ps.setString(2, "");
				ps.setString(3, "country");
				rs = ps.executeQuery();
				c = null;
				while (rs.next()) {
					try {
						c = Serializer.toCaller(rs.getString("content").getBytes(),
								this.getRuntime());
						if (c != null) {
							c.getPhoneNumber().setIntAreaCode(p.getIntAreaCode());
							c.getPhoneNumber().setAreaCode(p.getAreaCode());
							c.getPhoneNumber().setCallNumber(p.getCallNumber());
							c.setUUID(new UUID().toString());
							return c;
						}
					} catch (SerializerException e) {
						this.m_logger.log(Level.SEVERE, e.getMessage(), e);
					}
				}
				if (p.getIntAreaCode().length()>0 && p.getAreaCode().length()>0 && p.getCallNumber().length()>0) {
					if (this.m_logger.isLoggable(Level.INFO))
						this.m_logger.info("Normalized phone number, but could not detect a country: "+p);
					
					return getRuntime().getCallerFactory().createCaller(getRuntime().getCallerFactory().createName("", "unknown country 00"+p.getIntAreaCode()), p);
				}
			} catch (Exception ex) {
				this.m_logger.log(Level.SEVERE, (pnp!=null ? "Error while analyzing number ["+pnp.getTelephoneNumber() + "]: " : "") +ex.getMessage(), ex);
			}
			return null;
		}

		private IPhonenumber normalizePhonenumber(IPhonenumber pn) {
			IPhonenumber nomalizedPhonenumber = this.getRuntime()
					.getCallerFactory().createPhonenumber("");

			// check if pn is still normalized
			if (pn.getIntAreaCode().length() > 0
					&& pn.getAreaCode().length() > 0
					&& pn.getCallNumber().length() > 0) {
				return pn;
			}

			String intAreaCode = this.detectIntAreaCode(pn);
			String areaCode = this.detectAreaCode(pn, intAreaCode);
			String callNumber = this
					.detectCallnumber(pn, intAreaCode, areaCode);

			nomalizedPhonenumber.setIntAreaCode(intAreaCode);
			nomalizedPhonenumber.setAreaCode(areaCode);
			nomalizedPhonenumber.setCallNumber(callNumber);
			return nomalizedPhonenumber;
		}

		private String getPrefix() {
			String prefix = this.getRuntime().getConfigManagerFactory()
					.getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE,
							IJAMConst.GLOBAL_INTAREA_PREFIX);
			return (prefix == null ? "0" : prefix);
		}

		private String getLocalAreaCode() {
			String ac = this.getRuntime().getConfigManagerFactory()
					.getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE,
							IJAMConst.GLOBAL_INTAREA);
			return (ac == null ? "" : ac);
		}

		private String detectIntAreaCode(IPhonenumber pn) {

			// first hierarchy level is set
			if (pn.getIntAreaCode().length() > 0) {
				return pn.getIntAreaCode();
			}
			
			// added 2010/11/02: special check for local intarea code 39 (Italy)
			if (pn.getIntAreaCode().length() == 0 && this.isSpecialLocalIntAreaCode())
				return this.getLocalAreaCode();

			// first level is not set
			if (pn.getIntAreaCode().length() == 0
					&& pn.getTelephoneNumber().startsWith(this.getPrefix())) {
				String intAreaCode = pn.getTelephoneNumber();
				// remove trailing prefix
				intAreaCode = intAreaCode.substring(intAreaCode.indexOf(this
						.getPrefix())
						+ this.getPrefix().length(), intAreaCode.length());
				for (int i = 1; i < intAreaCode.length() - 1; i++) {
					String check = intAreaCode.substring(0, i);
					if (this.isIntAreaCodeExisting(check)) {
						return check;
					}
				}
				this.m_logger.warning("number contains invalid intareacode: "+pn.getTelephoneNumber());
				if (pn.getTelephoneNumber().length()>3)
					return pn.getTelephoneNumber().substring(1,3);
			}
			return this.getLocalAreaCode();
		}

		private String detectAreaCode(IPhonenumber pn, String intAreaCode) {

			// second hierarchy level is set
			if (pn.getAreaCode().length() > 0) {
				return pn.getAreaCode();
			}

			String areaCode = pn.getTelephoneNumber();
			int from = 0;
			int to = this.getDefaultAreaCodeLenth();

			// no prefix match
			if (!pn.getTelephoneNumber().startsWith(this.getPrefix()) || this.isSpecialLocalIntAreaCode()) {
				for (int i = areaCode.length() - 1; i > 1; i--) {
					String check = areaCode.substring(0, i);
					if (this.isAreaCodeExisting(intAreaCode, check)) {
						return check;
					}
				}
				return areaCode.substring(Math.max(0, from), Math.min(to,
						areaCode.length()));
			}

			// prefix is set
			areaCode = areaCode.substring(this.getPrefix().length()
					+ intAreaCode.length(), areaCode.length());

			for (int i = areaCode.length() - 1; i > 1; i--) {
				String check = areaCode.substring(0, i);
				if (this.isAreaCodeExisting(intAreaCode, check)) {
					return check;
				}
			}
			return areaCode.substring(Math.max(0, from), Math.min(to, areaCode
					.length()));
		}

		private int getDefaultAreaCodeLenth() {
			String value = m_configuration.getProperty(
					CFG_DEFAULT_AREACODE_LENGTH, "3");
			try {
				return Integer.parseInt(value);
			} catch (Exception ex) {
				this.m_logger.warning(ex.getMessage());
			}
			return 3;
		}

		private String detectCallnumber(IPhonenumber pn, String intAreaCode,
				String areaCode) {

			// second hierarchy level is set
			if (pn.getCallNumber().length() > 0) {
				return pn.getCallNumber();
			}

			String callNumber = pn.getTelephoneNumber();

			// remove asterix
			if (callNumber.indexOf("*") > -1) {
				if (callNumber.indexOf("*")==0) {
					callNumber = StringUtils.replaceString(callNumber, "*", "");	
				} else 
					callNumber = callNumber.substring(0, callNumber.indexOf("*") - 1);
			}

			// no prefix match
			if (!pn.getTelephoneNumber().startsWith(this.getPrefix()) || this.isSpecialLocalIntAreaCode()) {
				return callNumber.substring(callNumber.indexOf(areaCode)
						+ areaCode.length(), callNumber.length());
			}

			return callNumber.substring(this.getPrefix().length()
					+ intAreaCode.length() + areaCode.length(), callNumber
					.length());
		}

		private boolean isAreaCodeExisting(String intAreaCode, String p) {
			PreparedStatement ps = this
					.getStatement("SELECT_CALLER_PHONE_COUNT");

			try {
				ps.setString(1, intAreaCode);
				ps.setString(2, p);
				ps.setString(3, "area");

				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					if (rs.getInt(1) > 0)
						return true;
				}
			} catch (SQLException e) {
				this.m_logger.warning(e.getMessage());
			}

			return false;
		}

		private boolean isIntAreaCodeExisting(String p) {
			PreparedStatement ps = this
					.getStatement("SELECT_CALLER_PHONE_COUNT");

			try {
				ps.setString(1, p);
				ps.setString(2, "");
				ps.setString(3, "country");

				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					if (rs.getInt(1) > 0)
						return true;
				}
			} catch (SQLException e) {
				this.m_logger.warning(e.getMessage());
			}

			return false;
		}
		
		/**
		 * Checks if a special local intarea code is set, e.g. 39 for Italy
		 * 
		 * @return
		 */
		private boolean isSpecialLocalIntAreaCode() {
			return this.getLocalAreaCode().equalsIgnoreCase("39");
		}

	}

	public CountryDirectory() {
		super();
		this.getRuntime().getConfigurableNotifier().register(this);
	}

	public void shutdown() {
		int retry = 0;
		while (m_isMigrating && retry < 10) {
			m_logger.info("repository is shutdown, but still migrating. Retrycount: "+retry);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				m_logger.log(Level.SEVERE, e.getMessage(), e);
			}
			retry++;
		}
		if (retry==10)
			m_logger.warning("Aborting migration of areacode files. The database may be corrupted.");
		
		super.shutdown();
	}

	public void startup() {
		String root = PathResolver.getInstance(this.getRuntime()).resolve(
				this.m_configuration.getProperty(CFG_DB, PathResolver
						.getInstance(this.getRuntime()).getDataDirectory()
						+ "/countrycodes.db"));

		File props = new File(root + ".properties");
		if (!props.exists()) {
			props.getParentFile().mkdirs();
			try {
				File db_raw = new File(root);
				if (db_raw.exists()) {
					// exctract old data
					ZipArchive z = new ZipArchive(root);
					z.open();
					if (z.isCreatedByCurrentVersion()) {
						InputStream in = z
								.get(db_raw.getName() + ".properties");
						if (in != null) {
							FileOutputStream out = new FileOutputStream(db_raw
									.getAbsolutePath()
									+ ".properties");
							Stream.copy(in, out, true);
						}
						in = z.get(db_raw.getName() + ".script");
						if (in != null) {
							FileOutputStream out = new FileOutputStream(db_raw
									.getAbsolutePath()
									+ ".script");
							Stream.copy(in, out, true);
						}
					}
					z.close();
				}
			} catch (ZipArchiveException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			} catch (FileNotFoundException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			} catch (IOException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			}
		} else {
			try {
				File db_raw = new File(root);
				ZipArchive z = new ZipArchive(root);
				z.open();
				String[] entries = new String[] {
						db_raw.getName() + ".properties",
						db_raw.getName() + ".script" };
				InputStream[] ins = new InputStream[] {
						new FileInputStream(db_raw.getAbsolutePath()
								+ ".properties"),
						new FileInputStream(db_raw.getAbsolutePath()
								+ ".script") };
				z.add(entries, ins);
				z.close();
			} catch (ZipArchiveException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			} catch (FileNotFoundException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}

		super.startup();

		this.insertPropertiesFiles();
	}

	private void insertPropertiesFiles() {
		String restart = System.getProperty("jam.installer.restart");
		if (restart==null || restart.equalsIgnoreCase("true")) {
			this.m_logger.info("Detected jam.installer.restart flag as: "+System.getProperty("jam.installer.restart"));
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			
			restart = System.getProperty("jam.installer.restart");
			if (restart !=null && restart.equalsIgnoreCase("true")) {
				this.m_logger.info("Areacode migration is not started, due to installation of new modules.");
				return;
			}
		}
		
		this.m_isMigrating = true;
		ICallerList l = getRuntime().getCallerFactory().createCallerList();
		// Read country file
		File countryFile = new File(this.getHierarchyRoot() + File.separator
				+ this.getDatafile());
		if (countryFile.exists() && countryFile.isFile()) {
			try {
				Properties areaCodes = new Properties();
				FileInputStream istream = new FileInputStream(countryFile);
				areaCodes.load(istream);
				this.m_logger
						.info("Loaded international area code properties file.");
				istream.close();

				ICaller c = null;
				IPhonenumber pn = null;
				IAttributeMap m = null;
				String key = null;
				String value = null;
				Enumeration i = areaCodes.keys();
				while (i.hasMoreElements()) {
					key = (String) i.nextElement();
					value = areaCodes.getProperty(key);
					pn = getRuntime().getCallerFactory().createPhonenumber(key,
							"", "country");
					m = getRuntime().getCallerFactory().createAttributeMap();
					m.add(getRuntime().getCallerFactory().createAttribute(
							IJAMConst.ATTRIBUTE_NAME_COUNTRY, value));
					c = getRuntime().getCallerFactory().createCaller(
							getRuntime().getCallerFactory().createName("", ""),
							pn, m);
					l.add(c);
				}
				this.storeCountryAreacodes(l);
				l.clear();
			} catch (IOException ex) {
				this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);

			}
		}

		// Read areacodes file
		File countryDir = new File(this.getHierarchyRoot());
		if (countryDir.exists() && countryDir.isDirectory()) {
			File[] areacodedirs = countryDir.listFiles();
			for (int i = 0; i < areacodedirs.length; i++) {
				if (areacodedirs[i].isDirectory()) {
					File areacodefile = new File(areacodedirs[i], this
							.getDatafile());
					if (areacodefile.exists() && areacodefile.isFile()) {
						try {
							Properties areaCodes = new Properties();
							FileInputStream istream = new FileInputStream(
									areacodefile);
							areaCodes.load(istream);
							this.m_logger
									.info("Loaded area code properties file "
											+ areacodefile.getAbsolutePath());
							istream.close();

							ICaller c = null;
							IPhonenumber pn = null;
							IAttributeMap m = null;
							String key = null;
							String value = null;
							Enumeration e = areaCodes.keys();
							while (e.hasMoreElements()) {
								key = (String) e.nextElement();
								value = areaCodes.getProperty(key);
								pn = getRuntime().getCallerFactory()
										.createPhonenumber(
												areacodedirs[i].getName(), key,
												"area");
								m = getRuntime().getCallerFactory()
										.createAttributeMap();
								m.add(getRuntime().getCallerFactory()
										.createAttribute(
												IJAMConst.ATTRIBUTE_NAME_CITY,
												value));
								c = getRuntime().getCallerFactory()
										.createCaller(
												getRuntime().getCallerFactory()
														.createName("", ""),
												pn, m);
								l.add(c);
							}
							
							this.storeCountryAreacodes(l);
							l.clear();
							
							if (!areacodefile.delete()) areacodefile.deleteOnExit();
							if (!areacodedirs[i].delete()) areacodedirs[i].deleteOnExit();
						} catch (IOException ex) {
							this.m_logger
									.log(Level.SEVERE, ex.getMessage(), ex);

						}
					}
				}
			}
		}

		if (countryFile!=null && countryFile.exists() && !countryFile.delete()) countryFile.deleteOnExit();
		
		if (countryFile!=null && countryDir.exists() && countryDir.isDirectory()) {
			File[] areacodedirs = countryDir.listFiles();
			for (int i = 0; i < areacodedirs.length; i++) {
				if (areacodedirs[i].isDirectory()) {
					File areacodefile = new File(areacodedirs[i], this
							.getDatafile());
					if (areacodefile.exists() && areacodefile.isFile()) {
						if (!areacodefile.delete()) areacodefile.deleteOnExit();
						if (!areacodedirs[i].delete()) areacodedirs[i].deleteOnExit();
					}
				}
			}
		}
		this.m_isMigrating = false;
	}
	
	private void storeCountryAreacodes(ICallerList l) {
		try {
			if (l.size() > 0)
				getDatabaseHandler().insertOrUpdateCallerList(l);
				getDatabaseHandler().deleteCallerList(getRuntime().getCallerFactory().createCallerList());
				getDatabaseHandler().commit();
				if (m_logger.isLoggable(Level.INFO))
					m_logger.info("Committed database entries.");
				
				if (!getDatabaseHandler().isKeepAlive())
					getDatabaseHandler().disconnect();
				
		} catch (SQLException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			this.m_isMigrating = false;
			return;
		}
	}

	public String getID() {
		return CountryDirectory.ID;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime == null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	protected ICallerDatabaseHandler getDatabaseHandler() {
		if (this.m_dbh == null) {
			String db_path = PathResolver.getInstance(this.getRuntime())
					.resolve(
							this.m_configuration.getProperty(CFG_DB,
									PathResolver.getInstance(this.getRuntime())
											.getDataDirectory()
											+ "/countrycodes.db"));
			db_path = StringUtils.replaceString(db_path, "\\", "/");
			File db = new File(db_path + ".properties");
			boolean initialize = false;
			if (!db.exists()) {
				initialize = true;
				db.getParentFile().mkdirs();
				try {
					File db_raw = new File(db_path);
					if (!db_raw.exists()) {
						ZipArchive z = new ZipArchive(db_path);
						z.open();
						z.close();
					}
				} catch (ZipArchiveException e) {
					this.m_logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
			this.m_dbh = new CountryDirectoryHandler("org.hsqldb.jdbcDriver",
					"jdbc:hsqldb:file:" + db_path, "sa", "", initialize);
			this.m_dbh.setCommitCount(Integer.parseInt(m_configuration
					.getProperty(CFG_COMMIT_COUNT, "50")));
			this.m_dbh.setKeepAlive((m_configuration.getProperty(
					CFG_KEEP_ALIVE, "true").equalsIgnoreCase("true") ? true
					: false));
		}
		return this.m_dbh;
	}

	public ICaller getCaller(IPhonenumber number)
			throws CallerNotFoundException {
		if (number == null)
			throw new CallerNotFoundException(
					"Phone number is not set (null). No caller found.");

		if (number.isClired())
			throw new CallerNotFoundException(
					"Phone number is CLIR. Identification impossible.");

		if (PhonenumberInfo.isInternalNumber(number))
			throw new CallerNotFoundException(
					"Phone number is internal number.");

		ICaller c = null;
		try {
			c = getDatabaseHandler().getCaller(number);
			
			if (!getDatabaseHandler().isKeepAlive()){
				getDatabaseHandler().disconnect();
			}
				
			
			if (c != null)
				return c;
		} catch (SQLException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		}
		throw new CallerNotFoundException(
				"No caller entry found for phonenumber : "
						+ number.getTelephoneNumber());
	}

	private String getHierarchyRoot() {
		if (this.m_root != null) {
			return this.m_root;
		}

		String root = this.m_configuration.getProperty(CFG_ROOT);
		if (root == null) {
			root = PathResolver.getInstance(this.getRuntime())
					.getDataDirectory()
					+ File.separator + "areacodes";
		}
		root = PathResolver.getInstance(this.getRuntime()).resolve(root);

		File r = new File(root);

		if (!r.exists() && !r.mkdir())
			r.mkdirs();

		this.m_root = r.getAbsolutePath() + File.separator;
		this.m_logger.info("Set country manager root directory to: " + root);

		return this.m_root;
	}

	private String getDatafile() {
		String df = this.m_configuration.getProperty(CFG_DATAFILE);
		return (df == null ? "cdata" : df);
	}


}
