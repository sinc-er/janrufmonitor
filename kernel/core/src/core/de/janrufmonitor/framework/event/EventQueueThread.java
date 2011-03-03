package de.janrufmonitor.framework.event;

public class EventQueueThread extends Thread {

    public EventQueueThread() {
		this.setName("JAM-EventQueue-Thread-(non-deamon)");
        this.setPriority(NORM_PRIORITY);
        this.setDaemon(false);
        this.start(); 
    }

    public void run() {
    	EventQueueItem eqi = null;
        while (true) {
            eqi = EventQueue.getInstance().dequeue();
            if (eqi != null) {
                new EventSenderThread(eqi.getEvent(), eqi.getReceiver());
            }
        }
    }

}
