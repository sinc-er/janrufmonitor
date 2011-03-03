package de.janrufmonitor.service.client;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;

public class ImageCache {

	private static ImageCache m_instance = null;
	private Logger m_logger;
	
	private Map m_cache;
	    
    private ImageCache() {
        this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
        this.m_cache = new HashMap();
    }
    
    public static synchronized ImageCache getInstance() {
        if (ImageCache.m_instance == null) {
        	ImageCache.m_instance = new ImageCache();
        }
        return ImageCache.m_instance;
    }
    
    public void clear() {
    	if (this.m_cache!=null)
    		this.m_cache.clear();
    	
    	this.m_cache = null;
    	this.m_cache = new HashMap();
    }
    
    public void add(String key, File img) {
    	if (this.m_cache!=null) {
    		this.m_cache.put(key, img);
    		if (this.m_logger.isLoggable(Level.INFO))
    			this.m_logger.info("Adding file to image cache: "+img.getAbsolutePath());
    	}
    }
    
    public boolean contains(String key) {
    	return this.m_cache.containsKey(key);
    }
    
    public File get(String key) {
    	if (this.m_cache.containsKey(key)) {
    		return (File) this.m_cache.get(key);
    	}
    	return null;
    }
    
    public void remove(String key) {
    	if (this.m_cache!=null)
    		this.m_cache.remove(key);
    }
	
}
