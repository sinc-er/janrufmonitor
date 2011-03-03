package de.janrufmonitor.framework.event;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;

public class EventSenderThread extends Thread {

	private static int counter = 0;
	
    private IEvent event = null;
    private IEventReceiver receiver = null;
    private Logger m_logger;

    public EventSenderThread(IEvent event, IEventReceiver receiver) {
    	this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
    	EventSenderThread.counter++;
    	this.setName("EventThread#"+counter);
        this.event = event;
        this.receiver = receiver;
        this.setPriority(NORM_PRIORITY);
        this.setDaemon(false);
        this.start();
    }

    public void run() {
    	try {
    		if (this.receiver!=null)
    			this.receiver.received(this.event);
    		else
    			m_logger.warning("EventThread not executed, receiver was null. Dropped event: "+this.event);
    	} catch (Exception ex) {
    		this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);
    	}
    	EventSenderThread.counter--;
    }
}
