package de.janrufmonitor.framework.installer;

import de.janrufmonitor.repository.zip.ZipArchiveException;

public class JamArchiveException extends ZipArchiveException {
	
	private static final long serialVersionUID = 1L;

	public JamArchiveException() {
		super();
	}
	
	public JamArchiveException(String t) {
		super(t);
	}
	
	public JamArchiveException(ZipArchiveException ze) {
		super(ze.getMessage());
	}
}
