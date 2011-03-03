package de.janrufmonitor.repository.zip;

import java.io.File;

/**
 * This class is an implementation of a ZIP archive
 * exception which should be thrown on locking problems
 * with the ZIP implementation.
 * 
 *@author     Thilo Brandt
 *@created    2005/05/25
 */
public class ZipArchiveLockedException extends ZipArchiveException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor taking the ZIP file which causes the problem.
	 * @param zipArchive
	 */
	public ZipArchiveLockedException(File zipArchive) {
		super("Could not open archive "+zipArchive.getName()+". File is already locked by an application. Make sure that no .lck file exists.");
	}

}
