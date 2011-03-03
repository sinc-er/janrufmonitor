package de.janrufmonitor.service.client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.client.request.handler.GetImageHandler;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IRequester;
import de.janrufmonitor.service.commons.http.RequesterFactory;
import de.janrufmonitor.util.io.IImageProvider;
import de.janrufmonitor.util.io.IImageStreamProvider;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.io.Stream;

public class HttpImageProvider implements IImageProvider, IImageStreamProvider {

	private String m_cm = "CallerDirectory";
	private Logger m_logger;	
	
	private Timer m_t;
	private String ID;

	public HttpImageProvider(String id) {
		this();
		this.ID = id;
		this.m_t = new Timer();
		this.m_t.schedule(
			new TimerTask() {
					public void run() {
						ImageCache.getInstance().clear();
					} 
				},
			(60000*60)
		);
	}
	
	public HttpImageProvider() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		this.m_cm = PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty("repository.HttpCallerManager", "remote_repository");
		if (this.m_cm==null || this.m_cm.trim().length()==0) m_cm = "CallerDirectory";
		this.ID = this.m_cm;
	}
	
	public boolean hasImage(ICaller caller) {
		if (caller.getPhoneNumber().isClired()) return false;
		
		if (ImageCache.getInstance().contains(caller.getPhoneNumber().getTelephoneNumber())) return true;

		return (this.getImage(caller)!=null);
	}

	public String getImagePath(ICaller caller) {
		if (caller.getPhoneNumber().isClired()) return "";

		if (!hasImage(caller)) return "";
		
		try {
			IHttpRequest cgh = new GetImageHandler(caller.getPhoneNumber(), this.m_cm);
			IRequester r = this.getRequester(cgh);
			return "http://"+r.getServer()+":"+r.getPort()+cgh.getURI().toString();
		} catch (MalformedURLException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (IOException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (Exception e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		}

		return "";
	}

	public File getImage(ICaller caller) {
		if (caller.getPhoneNumber().isClired()) return null;
		
		File cacheFile = (File) ImageCache.getInstance().get(caller.getPhoneNumber().getTelephoneNumber());
		if (cacheFile!=null){
			this.m_logger.info("Taking image file from cache: "+cacheFile.getName());
			return cacheFile;
		}
		
		try {
			IHttpRequest cgh = new GetImageHandler(caller.getPhoneNumber(), this.m_cm);
			IRequester r = this.getRequester(cgh);
			InputStream in = new BufferedInputStream(r.request().getContentStreamForRead());
			
			File tmpOut = new File(PathResolver.getInstance().getTempDirectory()+"~images"+File.separator+caller.getPhoneNumber().getTelephoneNumber()+this.getExtension("image/jpeg"));
			if (!tmpOut.exists()) {
				tmpOut.getParentFile().mkdirs();
				tmpOut.createNewFile();
			}
			
			FileOutputStream fos = new FileOutputStream(tmpOut);
			Stream.copy(in, fos);
			in.close(); 
			fos.flush();
			fos.close();
			ImageCache.getInstance().add(caller.getPhoneNumber().getTelephoneNumber(), tmpOut);
			return tmpOut;
		} catch (MalformedURLException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (IOException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (NullPointerException e) {
			this.m_logger.log(Level.WARNING, e.getMessage(), e);
		} catch (Exception e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		} 
		return null;
	}
	
	private String getExtension(String mimeType) {
		if (mimeType.toLowerCase().equalsIgnoreCase("image/jpeg")) return ".jpg";
		if (mimeType.toLowerCase().equalsIgnoreCase("image/gif")) return ".gif";
		if (mimeType.toLowerCase().equalsIgnoreCase("image/png")) return ".png";
		return "";
	}
	
	private IRequester getRequester(IHttpRequest request) {
		IRequester r = RequesterFactory.getInstance().getRequester();
		r.setRequest(request);
		return r;
	}

	public String getID() {
		return ID;
	}

	public InputStream getImageInputStream(ICaller caller) {
		File f = this.getImage(caller);
		if (f!=null && f.exists()) {
			try {
				return new FileInputStream(f);
			} catch (FileNotFoundException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return null;
	}
}
