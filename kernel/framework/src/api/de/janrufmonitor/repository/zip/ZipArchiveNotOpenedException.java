package de.janrufmonitor.repository.zip;

import java.io.File;

/**
 * This class is an implementation of a ZIP archive
 * exception which should be thrown on opening/closing problems
 * with the ZIP implementation.
 * 
 *@author     Thilo Brandt
 *@created    2005/05/25
 */
public class ZipArchiveNotOpenedException extends ZipArchiveException {

	private static final long serialVersionUID = 1L;

	public ZipArchiveNotOpenedException(File zipArchive) {
		super("Could not open archive "+zipArchive.getName()+". Make sure that the file has the correct access permissions and is not used by other applications.");
	}

}
