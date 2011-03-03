package de.janrufmonitor.repository.zip;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.util.io.Stream;

/**
 * This class is an implementation of a ZIP archive, which is used
 * for several storage activities in jAnrufmonitor. It simple creates
 * a new archive or open an existing one by the specified path.
 * 
 *@author     Thilo Brandt
 *@created    2004/09/16
 */
public class ZipArchive {
	
	/**
	 * Version String which is stored as dummy entry in the zip as a signature.
	 */
	public static String VERSION_ENTRY = "~version/"+IJAMConst.VERSION+"/";
	
	private File m_archive;
	private File m_tmpArchive;
	private File m_backupArchive;
	private ZipFile m_zip;
	
	private boolean opened;
	private boolean corrupted;
	private boolean versioned;
	private boolean useLock;
	//private int BUFFER = Short.MAX_VALUE;  //32767
	private Set m_entry_cache;
	
	private Logger m_logger;

	/**
	 * Constructor with a path to a zip archive. If archive does not exist it
	 * will be created.
	 * 
	 * @param archive
	 */
	public ZipArchive(String archive) {
		this(new File(archive), false);
	}
	
	/**
	 * Constructor with a path to a zip archive. If archive does not exist it
	 * will be created.
	 * 
	 * @param archive
	 * @param useLock lock the archive during usage
	 */
	public ZipArchive(String archive, boolean useLock) {
		this(new File(archive), useLock);
	}
	
	/**
	 * Constructor with a File reference to a zip archive. If archive does not exist it
	 * will be created.
	 * 
	 * @param archive
	 * @param useLock lock the archive during usage
	 */
	public ZipArchive(File archive, boolean useLock) {
		m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		this.m_archive = archive;
		this.useLock = useLock;
		this.m_tmpArchive = new File(archive.getAbsolutePath()+".tmp");
		this.m_backupArchive = new File(archive.getAbsolutePath()+"~");
	}

	/**
	 * Opens the existing archive or creates a new onw if archive 
	 * does not exist.
	 * 
	 * @throws ZipArchiveException
	 */
	public void open() throws ZipArchiveException {
		if (this.useLock) {
			try {
				this.createLockFile();
			} catch (ZipArchiveException e) {
				this.m_logger.severe("ZipException occured: "+e.getMessage());
				PropagationFactory.getInstance().fire(new Message(Message.ERROR, Message.DEFAULT_NAMESPACE, "lockedarchive", new String[] {this.m_archive.getName()}, e));
				throw e;
			}
		}

		try {
			if (!this.m_archive.exists()){
				this.m_logger.info("Zip archive does not exist. A new archive is created.");

				// create non-existing archives
				ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(this.m_archive)); 	
				zipOut.setMethod(ZipOutputStream.DEFLATED);
				zipOut.setLevel(Deflater.BEST_SPEED);
				ZipEntry dummy = new ZipEntry(VERSION_ENTRY);
				zipOut.putNextEntry(dummy);
				zipOut.closeEntry();
				zipOut.close();
			}
			this.m_zip = new ZipFile(m_archive);
			this.m_entry_cache = new HashSet(this.m_zip.size());
			this.prefillEntryCache();
			this.opened = true;
			if (this.existEntry(VERSION_ENTRY)) 
				versioned = true;
		} catch (ZipException e) {
			this.m_logger.severe("ZipException occured: file "+this.m_archive.getName()+" is corrupted: "+e.getMessage());
			PropagationFactory.getInstance().fire(new Message(Message.ERROR, Message.DEFAULT_NAMESPACE, "invalidarchive", new String[] {this.m_archive.getName()}, e));
		} catch (IOException e) {
			throw new ZipArchiveException(e.getMessage());
		}
		this.m_logger.info("Zip archive successfully opened.");
	}
	
	/**
	 * Checks the consistency of the ZipArchive and returns true if the
	 * archive is not consistent.
	 * 
	 * @return true wether the ZipArchive is courrupted, otherwise false.
	 */
	public boolean isCorrupted() {
		return this.corrupted;
	}
	
	/**
	 * Closes the archive.
	 * 
	 * @throws ZipArchiveException
	 */
	public void close() throws ZipArchiveException {
		try {
			if (this.m_zip!=null) {
				this.m_zip.close();
			}
		} catch (IOException e) {
			this.opened = false;
			throw new ZipArchiveException(e.getMessage());
		}
		this.m_zip = null;
		if (this.useLock)
			this.removeLockFile();
		
		this.opened = false;
		this.m_entry_cache.clear();
		this.m_entry_cache = null;
		this.m_logger.info("Zip archive successfully closed.");
	}
	
	/**
	 * Checks if the archive is already opened.
	 * @return
	 */
	public boolean available() {
		return this.opened;
	}
	
	/** 
	 * Returns the number of entries stored in the archive.
	 * 
	 * @return number of entries
	 * @throws ZipArchiveException
	 */
	public int size() throws ZipArchiveException {
		if (!this.available())
			throw new ZipArchiveNotOpenedException(this.m_archive);

		return this.m_zip.size();
	}
	
	/**
	 * Adds a single new entry. 
	 * 
	 * @param entry specifies the path in the Zip
	 * @param content content object as InputStream
	 * @throws ZipArchiveException
	 */
	public void add(String entry, InputStream content) throws ZipArchiveException {
		this.add(new String[] {entry}, new InputStream[] {content});
	}
	
	/**
	 * Adds multiple entries to the zip archive.
	 * 
	 * @param entries array of entry strings
	 * @param contents array of InputStreams with content
	 * @throws ZipArchiveException
	 */
	public synchronized void add(String[] entries, InputStream[] contents) throws ZipArchiveException {
		if (!this.available())
			throw new ZipArchiveNotOpenedException(this.m_archive);
		
		this.m_logger.info("Adding "+entries.length+" entries to zip archive "+this.m_archive.getName());
		
		// generate of existing entries
		Map m = new HashMap();
		for (int i=0;i<entries.length;i++) {
			if (entries[i]!=null && !m.containsKey(entries[i])) {
				m.put(entries[i], contents[i]);				
			}
		}
		this.add(m);
	}
	
	/**
	 * Adds multiple entries to the zip archive. The key of the map entry must be a String which is used as zip entry name.
	 * The value of the map element must be a java.io.InputStream, which should be buffered.
	 * 
	 * @param m Map of contents. Key of map must be a String and is used as zip entry name 
	 * @throws ZipArchiveException
	 */
	public synchronized void add(Map m) throws ZipArchiveException {
		if (!this.available())
			throw new ZipArchiveNotOpenedException(this.m_archive);
		
		this.m_logger.info("Adding "+m.size()+" entries to zip archive "+this.m_archive.getName());
		
		// read all resting items from list to tmp stream
		ZipOutputStream zipOut = null;
		BufferedOutputStream bof = null;
		try {
			ZipInputStream zin = new ZipInputStream(new FileInputStream(this.m_archive));
			BufferedInputStream bin = new BufferedInputStream(zin);
			zipOut = new ZipOutputStream(new FileOutputStream(this.m_tmpArchive));
			zipOut.setLevel(Deflater.BEST_SPEED);
			zipOut.setMethod(ZipOutputStream.DEFLATED);
			bof = new BufferedOutputStream(zipOut);
			ZipEntry ze = null;
			while ((ze = this.getNextEntry(zin))!=null) {
				if (!m.containsKey(ze.getName())) {				
					zipOut.putNextEntry(ze);
					if (!ze.isDirectory())
						Stream.copy(bin, bof);
				}
			}
			zin.close();
		} catch (FileNotFoundException e) {
			this.m_logger.severe(e.toString()+" : "+e.getMessage());
		} catch (IOException e) {
			this.m_logger.severe(e.toString()+" : "+e.getMessage());
		}

		// add new entries now
		BufferedInputStream bufferedIn = null;
		Iterator i = m.keySet().iterator();
		InputStream tmpStream = null;
		String key = null;
		while (i.hasNext()) {
			try {
				key = (String) i.next();
				tmpStream = (InputStream) m.get(key);
				if (key!=null && tmpStream!=null) {
					if (key.length()>0) {
						ZipEntry e = new ZipEntry(key);
						zipOut.putNextEntry(e);
						this.m_entry_cache.add(key);
						if (!e.isDirectory()) {
							bufferedIn = new BufferedInputStream(tmpStream);
							Stream.copy(bufferedIn, bof);
							zipOut.closeEntry();
							tmpStream.close();
							bufferedIn.close();
						} else {
							zipOut.closeEntry();
						}
					}
				}
			} catch (IOException e) {
				this.m_logger.warning(e.toString());
			}
		}
		
		// set versioned flag if missing
		if (!this.versioned) {
			ZipEntry dummy = new ZipEntry(VERSION_ENTRY);
			try {
				zipOut.putNextEntry(dummy);
				zipOut.closeEntry();
				this.versioned = true;
			} catch (IOException e) {
				this.m_logger.warning(e.toString()+" : "+e.getMessage());
			}	
		}
		
		try {
			zipOut.close();
			bof.close();
		} catch (IOException e) {
			throw new ZipArchiveException(e.getMessage());
		}
		this.m_logger.info("Closing tmp zip archive.");
		this.rename();
		this.m_logger.info("Zip archive successfully renamed.");
	}
	
	
	private ZipEntry getNextEntry(ZipInputStream z) throws IOException {
		try {
			return z.getNextEntry();
		} catch (Exception e) {
			this.m_logger.severe("Found corrupted zip entry.");
			z.closeEntry();
			this.corrupted = true;
			return this.getNextEntry(z);
		}
	}
	
	/**
	 * Checks wether an extry exists or not
	 * 
	 * @param entry entry to check
	 * @return true id entry is existing, false if not.
	 * @throws ZipArchiveException
	 */
	public boolean existEntry(String entry) throws ZipArchiveException {
		if (!this.available())
			throw new ZipArchiveNotOpenedException(this.m_archive);
	
		ZipEntry ze = this.m_zip.getEntry(entry);
		return ze!=null;
	}
	
	/**
	 * Gets a single entry's InputStream by its entryname.  
	 * @param entry
	 * @return the entry's content or null if entry does not exist.
	 * @throws ZipArchiveException
	 */
	public InputStream get(String entry) throws ZipArchiveException {
		if (!this.available())
			throw new ZipArchiveNotOpenedException(this.m_archive);

		ZipEntry ze = this.m_zip.getEntry(entry);
		if (ze==null) return null;
		
		if (ze.isDirectory())
			return null; 
		
		try {
			return this.m_zip.getInputStream(ze);
		} catch (IOException e) {
			throw new ZipArchiveException(e.getMessage());
		}
	}
	
	/**
	 * Gets the content as byte array for a single entryname
	 * 
	 * @param entry
	 * @return the content of the entry as byte array
	 * @throws ZipArchiveException
	 */
	public byte[] getContent(String entry) throws ZipArchiveException {
		if (!this.available())
			throw new ZipArchiveNotOpenedException(this.m_archive);
		
		InputStream rawin = this.get(entry);
		if (rawin!=null) {
			BufferedInputStream in = new BufferedInputStream(rawin);
			try {
				return this.toByteArray(in);
			} catch (IOException e) {
				throw new ZipArchiveException(e.getMessage());
			}
		}
		return null;
	}
	
	private byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		Stream.copy(in, bout);
		in.close();
		
		return bout.toByteArray();
	}
	
	/**
	 * Returns the ZipArchive as a java.io.File 
	 * representation.
	 * 
	 * @return a valid File object
	 */
	public File toFile() {
		return this.m_archive;
	}
	
	/**
	 * Return wether this archive was created by the current program version.
	 * 
	 * @return true if the current version has created this file, otherwise false.
	 * @throws ZipArchiveException
	 */
	public boolean isCreatedByCurrentVersion() throws ZipArchiveException  {
		return this.existEntry(ZipArchive.VERSION_ENTRY);
	}
	
	private void prefillEntryCache() {
		ZipEntry z = null;
		for (Enumeration e = this.m_zip.entries();e.hasMoreElements();) {
			try {
				z = (ZipEntry)e.nextElement();
				this.m_entry_cache.add(z.getName());
			} catch (Throwable ex) {
				this.m_logger.severe("Invalid ZIP entry found. Entry was dropped: "+ex.getMessage());
				this.corrupted = true;
			}
		}
	}
	
	/**
	 * Returns a list of all entrynames in this archive.
	 * 
	 * @return
	 * @throws ZipArchiveException
	 */
	public List list() throws ZipArchiveException {
		if (!this.available())
			throw new ZipArchiveNotOpenedException(this.m_archive);
			
		List entries = new ArrayList(this.m_entry_cache.size());

		for (Iterator i = this.m_entry_cache.iterator();i.hasNext();){
			entries.add(i.next());
		}

		return entries;
	}
	
	/**
	 * Returns a list of all entrynames in this archive, filtered by a FilenameFilter object.
	 * 
	 * @param filter
	 * @return
	 * @throws ZipArchiveException
	 */
	public List list(FilenameFilter filter) throws ZipArchiveException {
		if (!this.available())
			throw new ZipArchiveNotOpenedException(this.m_archive);

		if (filter == null)
			throw new ZipArchiveException("Invalid filter object: null");

		List entries = new ArrayList();
		String key = null;
		for (Iterator i = this.m_entry_cache.iterator();i.hasNext();){
			key = (String)i.next();
			if (filter.accept(null, key))
				entries.add(key);
		}
	
		return entries;
	}
	
	/**
	 * Removes a single entry.
	 * 
	 * @param entry
	 * @throws ZipArchiveException
	 */
	public void remove(String entry) throws ZipArchiveException {
		this.remove(new String[] {entry});
	}
	
	/**
	 * Removes multiple entries.
	 * 
	 * @param entries
	 * @throws ZipArchiveException
	 */
	public synchronized void remove(String[] entries) throws ZipArchiveException {
		if (!this.available())
			throw new ZipArchiveNotOpenedException(this.m_archive);

		// remove deletable items from list
		List l = new ArrayList(entries.length);
		for (int i=0;i<entries.length;i++) {
			l.add(entries[i]);
		}
		this.remove(l);
	}
	
	/**
	 * Removes multiple entries as a List with Strings.
	 * 
	 * @param entries
	 * @throws ZipArchiveException
	 */
	public synchronized void remove(List entries) throws ZipArchiveException {
		
		this.m_entry_cache.removeAll(entries);
		
		// read all resting items from list to tmp stream
		ZipOutputStream zipOut = null;
		BufferedOutputStream bof = null;
		try {
			ZipInputStream zin = new ZipInputStream(new FileInputStream(this.m_archive));
			BufferedInputStream bin = new BufferedInputStream(zin);
			zipOut = new ZipOutputStream(new FileOutputStream(this.m_tmpArchive));
			zipOut.setLevel(Deflater.BEST_SPEED);
			zipOut.setMethod(ZipOutputStream.DEFLATED);
			bof = new BufferedOutputStream(zipOut);
			ZipEntry ze = null;
			while ((ze = this.getNextEntry(zin))!=null) {
				if (!entries.contains(ze.getName())) {
					zipOut.putNextEntry(ze);
					if (!ze.isDirectory())
						Stream.copy(bin, bof);
				}
			}
			zin.close();
		} catch (FileNotFoundException e) {
			this.m_logger.severe(e.toString()+" : "+e.getMessage());
		} catch (IOException e) {
			this.m_logger.severe(e.toString()+" : "+e.getMessage());
		}
		
		// set versioned flag if missing
		if (!this.versioned) {
			ZipEntry dummy = new ZipEntry(VERSION_ENTRY);
			try {
				zipOut.putNextEntry(dummy);
				zipOut.closeEntry();
				this.versioned = true;
			} catch (IOException e) {
				this.m_logger.warning(e.toString()+" : "+e.getMessage());
			}	
		}
		
		try {
			zipOut.close();
			bof.close();
		} catch (FileNotFoundException e) {
			if (zipOut!=null) try {
				zipOut.close();
			} catch (IOException e1) {
				throw new ZipArchiveException(e.getMessage()+" : "+e1.getMessage());
			}
			throw new ZipArchiveException(e.getMessage());
		} catch (IOException e) {
			throw new ZipArchiveException(e.getMessage());
		}
		this.m_logger.info("Closing tmp zip archive.");
		this.rename();
		this.m_logger.info("Zip archive successfully renamed.");
	}
	
	private void rename() throws ZipArchiveException {
		this.close();
			
		if (!this.m_archive.delete()) {
			this.m_logger.severe("Cannot replace old archive with new one.");
			if (this.m_backupArchive.exists() && !this.m_backupArchive.delete())
				this.m_backupArchive.deleteOnExit();
			
			this.m_archive.renameTo(this.m_backupArchive);
			//this.m_archive.deleteOnExit();
		}
				
		this.m_tmpArchive.renameTo(this.m_archive);
			
		this.open();
	}
	

	
	/**
	 * Restores a backed up file with ~ extension.
	 * 
	 * @throws ZipArchiveException
	 */
	public void restore() throws ZipArchiveException {
		if (this.m_backupArchive==null || !this.m_backupArchive.exists())
			return;
			
		try {
			FileInputStream in = new FileInputStream(this.m_backupArchive);
			FileOutputStream out = new FileOutputStream(this.m_archive);
			Stream.copy(in,out);
			out.close();
			in.close();
			out = null;
			in = null;
		} catch (FileNotFoundException e) {
			throw new ZipArchiveException(e.getMessage());
		} catch (IOException e) {
			throw new ZipArchiveException(e.toString()+ ": "+e.getMessage());
		}
	}
	
	/**
	 * Backs up the content of the archive to a ~ file.
	 * 
	 * @throws ZipArchiveException
	 */
	public synchronized void backup() throws ZipArchiveException {
		if (this.m_backupArchive==null || !this.m_backupArchive.exists())
			return;
			
		try {
			FileInputStream in = new FileInputStream(this.m_archive);
			FileOutputStream out = new FileOutputStream(this.m_backupArchive);
			Stream.copy(in,out, true);
			out = null;
			in = null;
		} catch (FileNotFoundException e) {
			throw new ZipArchiveException(e.getMessage());
		} catch (IOException e) {
			throw new ZipArchiveException(e.toString()+ ": "+e.getMessage());
		}
	}
	
	private void createLockFile() throws ZipArchiveException {
		File lockFile = new File(this.m_archive.getAbsolutePath()+".lck");
		if (lockFile.exists()) throw new ZipArchiveLockedException(this.m_archive);
		
		try {
			FileOutputStream out = new FileOutputStream(lockFile);
			InputStream in = new ByteArrayInputStream("".getBytes());
			Stream.copy(in, out, true);
			out=null;
			in=null;
			this.m_logger.info("Created lock file "+lockFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			throw new ZipArchiveException(e.getMessage());
		} catch (IOException e) {
			throw new ZipArchiveException(e.getMessage());
		}
	}
	
	private void removeLockFile() throws ZipArchiveException {
		File lockFile = new File(this.m_archive.getAbsolutePath()+".lck");
		if (lockFile.exists()) {
			if (!lockFile.delete()) lockFile.deleteOnExit();
			this.m_logger.info("Unlocked archive "+this.m_archive.getAbsolutePath());
		} else {
			this.m_logger.warning("Archive "+this.m_archive.getAbsolutePath()+" was not locked.");
		}
	}

}

