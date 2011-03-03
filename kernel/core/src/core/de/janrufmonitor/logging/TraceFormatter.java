package de.janrufmonitor.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import de.janrufmonitor.framework.IJAMConst;

public class TraceFormatter extends Formatter {

	private SimpleDateFormat formatter = 
		new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");

	public String format(LogRecord r) {
		StringBuffer b = new StringBuffer();
		b.append("[ ");
		b.append(formatter.format(new Date(r.getMillis())));
		b.append(", ");
		b.append(r.getMessage());
		b.append(" ]"+IJAMConst.CRLF);
		return b.toString();
	}
}