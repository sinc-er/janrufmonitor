package de.janrufmonitor.framework.event;

import java.util.LinkedList;

public class EventQueue {

    private static EventQueue instance = null;
    private LinkedList queue = null;

    private EventQueue() {
        this.queue = new LinkedList();
    }

    public static synchronized EventQueue getInstance() {
        if (EventQueue.instance == null) {
            EventQueue.instance = new EventQueue();
        }
        return EventQueue.instance;
    }

    public synchronized EventQueueItem dequeue() {
        try {
            if (this.queue.size() == 0) {
                wait();
            }
        } catch (InterruptedException ex) {
            return null;
        }
        return (EventQueueItem) this.queue.removeFirst();
    }

    public synchronized void enqueue(EventQueueItem queueItem) {
        this.queue.addLast(queueItem);
        notify();
    }

    public synchronized void flush() {
        this.queue.clear();
    }

    public synchronized boolean isEmpty() {
        if (this.queue.size() == 0) {
            return true;
        }
        return false;
    }

}
