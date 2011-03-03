package de.janrufmonitor.framework.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;

public class CallerCacheManager {

	private static CallerCacheManager m_instance = null;
	private Logger m_logger;
	
	private List m_cache;
	
	private CallerCacheManager() {
		this.m_logger = LogManager.getLogManager().getLogger(
				IJAMConst.DEFAULT_LOGGER);
		this.m_cache = new ArrayList();
	}

	public static synchronized CallerCacheManager getInstance() {
		if (CallerCacheManager.m_instance == null) {
			CallerCacheManager.m_instance = new CallerCacheManager();
			Logger l = LogManager.getLogManager().getLogger(
					IJAMConst.DEFAULT_LOGGER);
			if (l != null)
				l.info("Created new CallerCacheManager instance.");
			
		}
		return CallerCacheManager.m_instance;
	}

	public synchronized void addToCache(List l) {
		if (CallerCacheManager.m_instance.m_cache!=null) {
			CallerCacheManager.m_instance.m_cache.addAll(l);
		} else {
			m_logger.log(Level.SEVERE, "CallerCacheManager not initialized.");
		}
	}
	
	public synchronized List getCache() {
		if (CallerCacheManager.m_instance.m_cache!=null) {
			return CallerCacheManager.m_instance.m_cache;
		}
		return new ArrayList();
	}
	
	public void invalidate() {
		if (CallerCacheManager.m_instance.m_cache!=null) {
			CallerCacheManager.m_instance.m_cache.clear();
		}
		CallerCacheManager.m_instance = null;
	}
	
	public boolean hasEntries() {
		if (CallerCacheManager.m_instance.m_cache!=null) {
			return CallerCacheManager.m_instance.m_cache.size()>0;
		}
		return false;
	}	
}
