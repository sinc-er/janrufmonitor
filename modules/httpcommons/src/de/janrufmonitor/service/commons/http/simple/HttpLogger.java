package de.janrufmonitor.service.commons.http.simple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.io.PathResolver;

import simple.http.Request;
import simple.http.Response;

public class HttpLogger {

    private static HttpLogger instance = null;
    private final static String PROPERTIES_FILE = "http_log.properties"; 
    
	private final static String PARAMETER_LOGFILE = "logfile";
	private final static String PARAMETER_DUMPFILE = "dumpfile";
	private final static String PARAMETER_ISDUMP = "enabledump";
	private final static String PARAMETER_MAXSIZE = "maxsize";
    
	private final static String DEFAULT_LOGFILE = "access.log";
	private final static String DEFAULT_DUMPFILE = "dump_http.log";
    private final static int DEFAULT_MAX_SIZE = 500000;
	private final static String DEFAULT_ISDUMP = "false";
	
	private Properties m_configuration;

    private HttpLogger() {
		this.loadPropertiesFile();
    }

    public static synchronized HttpLogger getInstance() {
        if (HttpLogger.instance == null) {
            HttpLogger.instance = new HttpLogger();
        }
        return HttpLogger.instance;
    }
    
    public void dump(Response response) {
		try {
			File dump = new File(PathResolver.getInstance(PIMRuntime.getInstance()).getLogDirectory()+this.getDumpfile());
			FileOutputStream os = new FileOutputStream(dump, true);
			os.write(("--->\r\n"+response.toString()+"\r\n<---\r\n").getBytes());			
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
    }
    
	public void dump(Request request) {
		try {
			File dump = new File(PathResolver.getInstance(PIMRuntime.getInstance()).getLogDirectory()+this.getDumpfile());
			FileOutputStream os = new FileOutputStream(dump, true);
			os.write(("--->\r\n"+request.toString()+"\r\n<---\r\n").getBytes());			
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
    
    /**
     * Writes a log entry:
     * client-ip - - [11/Jan/2004:06:44:59 +0100] "GET / HTTP/1.0" 200 11190 "Referer" "Agent"
     */
    public void write(Request request, Response response) {
		StringBuffer b = new StringBuffer();
		b.append(request.getInetAddress().getHostName());
		b.append(" - - [");
		
		SimpleDateFormat date = new SimpleDateFormat(
			"dd/MMM/yyyy:HH:mm:ss Z",
			Locale.US
		);
		
		String p = date.format(new Date());
		b.append(p);
		b.append("] \"");
		b.append(request.getMethod());
		b.append(" ");
		b.append(request.getURI());
		b.append(" ");
		b.append("HTTP/1.1");
		b.append("\" ");
		b.append(response.getCode());
		b.append(" ");
		p = response.getValue("Content-Length");
		b.append((p==null || p.length()==0 ? "0" : p));
		b.append(" \"");
		p = response.getValue("Referer");
		b.append((p==null || p.length()==0 ? "-" : p));
		b.append("\" \"");
		p = request.getValue("User-Agent");
		b.append((p==null || p.length()==0 ? "-" : p));
		b.append("\"\r\n");
		
    	try {
    		File log = new File(PathResolver.getInstance(PIMRuntime.getInstance()).getLogDirectory()+this.getLogfile());
    		log.getParentFile().mkdirs();
			FileOutputStream os = new FileOutputStream(log, true);
			os.write(b.toString().getBytes());			
			os.close();
			
			if (log.length()>this.getMaxsize()) {
				log.renameTo(new File(log.getAbsolutePath() + "." + Long.toString(new Date().getTime())));
				log.delete();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
    }

	private void loadPropertiesFile() {
		this.m_configuration = new Properties();
		try {
			InputStream in = new FileInputStream(PathResolver.getInstance(PIMRuntime.getInstance()).getConfigDirectory()+HttpLogger.PROPERTIES_FILE);
			this.m_configuration.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			this.m_configuration.setProperty(HttpLogger.PARAMETER_LOGFILE, HttpLogger.DEFAULT_LOGFILE);
			this.m_configuration.setProperty(HttpLogger.PARAMETER_MAXSIZE, Integer.toString(HttpLogger.DEFAULT_MAX_SIZE));
			this.m_configuration.setProperty(HttpLogger.PARAMETER_DUMPFILE, HttpLogger.DEFAULT_DUMPFILE);
			this.m_configuration.setProperty(HttpLogger.PARAMETER_ISDUMP, HttpLogger.DEFAULT_ISDUMP);
			
			try {
				OutputStream os = new FileOutputStream(PathResolver.getInstance(PIMRuntime.getInstance()).getConfigDirectory()+HttpLogger.PROPERTIES_FILE);
				this.m_configuration.store(os, "");
				os.flush();
				os.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public boolean isDumpEnabled() {
    	return (this.m_configuration.getProperty(PARAMETER_ISDUMP, DEFAULT_ISDUMP).equalsIgnoreCase("true") ? true : false);
    }
    
	private String getDumpfile() {
		return this.m_configuration.getProperty(PARAMETER_DUMPFILE, DEFAULT_DUMPFILE);
	}
    
    private String getLogfile() {
    	return this.m_configuration.getProperty(PARAMETER_LOGFILE, DEFAULT_LOGFILE);
    }
    
    private int getMaxsize() {
    	String value = this.m_configuration.getProperty(PARAMETER_MAXSIZE);
    	if (value==null)
    		return DEFAULT_MAX_SIZE;
    	return Integer.parseInt(value);
    }
}
