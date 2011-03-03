package de.janrufmonitor.repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.repository.db.ICallDatabaseHandler;
import de.janrufmonitor.repository.db.hsqldb.HsqldbCallDatabaseHandler;
import de.janrufmonitor.repository.imexport.ICallImporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.repository.imexport.ImExportFactory;
import de.janrufmonitor.repository.types.ILocalRepository;
import de.janrufmonitor.repository.zip.ZipArchive;
import de.janrufmonitor.repository.zip.ZipArchiveException;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.io.Stream;
import de.janrufmonitor.util.string.StringUtils;

public class DefaultJournal extends AbstractDatabaseCallManager implements ILocalRepository {

	private class DefaultJournalHandler extends HsqldbCallDatabaseHandler {

		private IRuntime m_runtime;
		
		public DefaultJournalHandler(String driver, String connection, String user, String password, boolean initialize) {
			super(driver, connection, user, password, initialize);
		}

		protected IRuntime getRuntime() {
			if (this.m_runtime==null)
				this.m_runtime = PIMRuntime.getInstance();
			return this.m_runtime;
		}
	}
	
	private class DatExporter {
		
		private ZipArchive m_zip;
		
		public DatExporter(ZipArchive zip) {
			this.m_zip = zip;
		}
		
		public boolean export() throws ZipArchiveException, IOException {
			if (this.m_zip.available() && !this.m_zip.isCorrupted()) {
				List entries = this.m_zip.list();
				if (entries.size()>0) {
					File export = new File(this.m_zip.toFile().getAbsolutePath()+"."+IJAMConst.VERSION+".dat");
					FileWriter exportWriter = new FileWriter(export);
					BufferedWriter bufWriter = new BufferedWriter(exportWriter);
					
					byte[] line = null;
					for (int i=0, n=entries.size();i<n;i++) {
						try {
							line = this.m_zip.getContent((String) entries.get(i));
							if (line != null) {
								bufWriter.write(new String(line));
								bufWriter.newLine();
							}
						} catch (ZipArchiveException e) {
							// ignore
						}
					}
					
					bufWriter.flush();
					bufWriter.close();
					exportWriter.close();
				}
				entries.clear();
				return true;
			}
			return false;
		}
		
		public void importing(ICallList l) {
			File importFile = new File(this.m_zip.toFile().getAbsolutePath()+"."+IJAMConst.VERSION+".dat");
			if (importFile.exists()) {
				IImExporter imp = ImExportFactory.getInstance().getImporter("DatFileCallImporter");
				if (imp!=null && imp instanceof ICallImporter) {
					((ICallImporter)imp).setFilename(importFile.getAbsolutePath());
					ICallList cl = ((ICallImporter)imp).doImport();
					if (cl!=null && cl.size()>0) {
						l.add(cl);
					}
				}
			}
		}
		
		public void rename() throws ZipArchiveException {
			this.m_zip.close();
			this.m_zip.toFile().renameTo(new File(this.m_zip.toFile().getAbsolutePath()+".old"));
		}
	}
	
	private static String ID = "DefaultJournal";
	private static String NAMESPACE = "repository.DefaultJournal";
	
	private static String CFG_DB= "db";
	private static String CFG_COMMIT_COUNT= "commit";
	private static String CFG_KEEP_ALIVE= "keepalive";
	
	private IRuntime m_runtime;
	
	// for compatibility reasons to versions <=4.4
	private static String CFG_OLD_ROOT = "root";
	
	public DefaultJournal() {
		super();
		this.getRuntime().getConfigurableNotifier().register(this);
	}

	public String getID() {
		return DefaultJournal.ID;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	public String getNamespace() {
		return DefaultJournal.NAMESPACE;
	}
	
	private void migrate(ICallList list, String path_zip) {
		this.m_logger.info("Checking migration for "+path_zip+"...");
		File zip = new File(path_zip);
		if (zip.exists()) {
			ZipArchive za = new ZipArchive(zip, false);
			try {
				za.open();
				if (za.available() && !za.isCorrupted() && za.size()>3) {
					if (!za.isCreatedByCurrentVersion()){
						// Start DAT export ...
						DatExporter de = new DatExporter(za);
						if (de.export()) {
							de.rename();
							this.m_logger.info("Exporting old entries successfully finished.");
							de.importing(list);
							this.m_logger.info("Importing old entries successfully finished.");
						}
					} 
				}
				if (za.available())
					za.close();
			} catch (ZipArchiveException e) {
				this.m_logger.info("Database is not an old zip archive.");
			} catch (IOException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	public void startup() {
		// check for old ZipArchive <=4.4 in configuration
		// only works, if migartion from 4.4 was done !!
		ICallList migrationList = this.getRuntime().getCallFactory().createCallList();
		String root = this.m_configuration.getProperty(CFG_OLD_ROOT);
		if (root != null) {
			root = PathResolver.getInstance(this.getRuntime()).resolve(root);
			this.m_logger.info("Found old DB root path: "+root);
			this.migrate(migrationList, root);
		}
		
		root = PathResolver.getInstance(this.getRuntime()).resolve(this.m_configuration.getProperty(CFG_DB, PathResolver.getInstance(this.getRuntime()).getDataDirectory()+"/journal.db"));
		
		// check if found ZipArchive is in old format
		this.migrate(migrationList, root);
		
		File props = new File(root + ".properties");
		if (!props.exists())  {
			props.getParentFile().mkdirs();
			try {
				File db_raw = new File(root);
				if (db_raw.exists()) {
					// exctract old data
					ZipArchive z = new ZipArchive(root);
					z.open();
					if (z.isCreatedByCurrentVersion()) {
						InputStream in = z.get(db_raw.getName()+".properties");
						if (in!=null) {
							FileOutputStream out = new FileOutputStream(db_raw.getAbsolutePath()+".properties");
							Stream.copy(in, out, true);
						}
						in = z.get(db_raw.getName()+".script");
						if (in!=null) {
							FileOutputStream out = new FileOutputStream(db_raw.getAbsolutePath()+".script");
							Stream.copy(in, out, true);
						}						
					} else {
						// check if an old db file was selected
						if (z.size()==3) {
							this.m_logger.info("Found 4.5 compatible database");
							InputStream in = z.get(db_raw.getName()+".properties");
							if (in!=null) {
								FileOutputStream out = new FileOutputStream(db_raw.getAbsolutePath()+".properties");
								Stream.copy(in, out, true);
							}
							in = z.get(db_raw.getName()+".script");
							if (in!=null) {
								FileOutputStream out = new FileOutputStream(db_raw.getAbsolutePath()+".script");
								Stream.copy(in, out, true);
							}						
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
				String[] entries = new String[] { db_raw.getName()+".properties", db_raw.getName()+".script" };
				InputStream[] ins = new InputStream[] { new FileInputStream(db_raw.getAbsolutePath()+".properties"),new FileInputStream(db_raw.getAbsolutePath()+".script") };
				z.add(entries, ins);
				z.close();
			} catch (ZipArchiveException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			} catch (FileNotFoundException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}

		super.startup();
		
		if (migrationList.size()>0) {
			PropagationFactory.getInstance().fire(
				new Message(
					Message.INFO,
					this.getNamespace(),
					"migrate",
					new Exception("Found old journal archive file. Data must be migrated.")
				)
			);
			this.setCalls(migrationList);
			// delete old config entry
			this.getRuntime().getConfigManagerFactory().getConfigManager().removeProperty(getNamespace(), CFG_OLD_ROOT);
			this.m_configuration.remove(CFG_OLD_ROOT);
			this.getRuntime().getConfigManagerFactory().getConfigManager().saveConfiguration();			
		}
	}

	public void shutdown() {
		try {
			if (this.getDatabaseHandler().isConnected())
				this.getDatabaseHandler().disconnect();
			
			String root = PathResolver.getInstance(this.getRuntime()).resolve(this.m_configuration.getProperty(CFG_DB, PathResolver.getInstance(this.getRuntime()).getDataDirectory()+"/journal.db"));
			File db_raw = new File(root);
			ZipArchive z = new ZipArchive(root);
			z.open();
			String[] entries = new String[2];
			InputStream[] ins = new InputStream[2];
			if (new File(db_raw.getAbsolutePath()+".properties").exists()) {
				entries[0] = db_raw.getName()+".properties";
				ins[0] = new FileInputStream(db_raw.getAbsolutePath()+".properties");
			}
			
			if (new File(db_raw.getAbsolutePath()+".script").exists()) {
				entries[1] = db_raw.getName()+".script";
				ins[1] = new FileInputStream(db_raw.getAbsolutePath()+".script");
			}
			z.add(entries, ins);
			z.close();
		} catch (ZipArchiveException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (FileNotFoundException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (SQLException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		super.shutdown();
	}

	protected ICallDatabaseHandler getDatabaseHandler() {
		if (this.m_dbh==null) {
			String db_path = PathResolver.getInstance(this.getRuntime()).resolve(this.m_configuration.getProperty(CFG_DB, PathResolver.getInstance(this.getRuntime()).getDataDirectory()+"journal.db"));
			db_path = StringUtils.replaceString(db_path, "\\", "/");
			File db = new File(db_path + ".properties");
			boolean initialize = false;
			if (!db.exists())  {
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
			this.m_dbh = new DefaultJournalHandler("org.hsqldb.jdbcDriver", "jdbc:hsqldb:file:"+db_path, "sa", "", initialize);
			this.m_dbh.setCommitCount(Integer.parseInt(m_configuration.getProperty(CFG_COMMIT_COUNT, "50")));
			this.m_dbh.setKeepAlive((m_configuration.getProperty(CFG_KEEP_ALIVE, "true").equalsIgnoreCase("true")? true : false));
		}	
		return this.m_dbh;
	}

	public String getFile() {
		return PathResolver.getInstance(this.getRuntime()).resolve(this.m_configuration.getProperty(CFG_DB, PathResolver.getInstance(this.getRuntime()).getDataDirectory()+"/journal.db"));
	}
	
	public String getFileType() {
		return "*.db";
	}

	public void setFile(String filename) {
		this.getRuntime().getConfigManagerFactory().getConfigManager().setProperty(getNamespace(), CFG_DB, filename);
		this.getRuntime().getConfigManagerFactory().getConfigManager().saveConfiguration();		
	}

}
