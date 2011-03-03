package de.janrufmonitor.logging;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import de.janrufmonitor.framework.IJAMConst;

public class JamFormatter extends Formatter {

	private SimpleDateFormat formatter = 
		new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");

	public String format(LogRecord r) {
		StringBuffer b = new StringBuffer();
		b.append("[ ");
		b.append(r.getLevel().toString());
		b.append(" - ");
		b.append(formatter.format(new Date(r.getMillis())));
		b.append(" - ");
		b.append(Thread.currentThread().getName());
		b.append(" - ");
		b.append(r.getSourceClassName());
		b.append(".");
		if (r.getSourceMethodName().startsWith("<")) {
			b.append(r.getSourceMethodName());
		} else {
			b.append(r.getSourceMethodName()+"()");
		}
		b.append(" - ");
		b.append(r.getMessage());
		b.append(" ]"+IJAMConst.CRLF);
		
		if (r.getLevel().equals(Level.SEVERE) && r.getThrown()!=null) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			PrintStream p = new PrintStream(bout);
			r.getThrown().printStackTrace(p);
			b.append(bout.toString());
			b.append(IJAMConst.CRLF);
		}
		
		return b.toString();
	}
	
}
