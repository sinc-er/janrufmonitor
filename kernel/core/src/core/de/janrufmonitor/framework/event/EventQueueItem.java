package de.janrufmonitor.framework.event;

public class EventQueueItem {

    private IEvent event;
    private IEventReceiver receiver;

    public EventQueueItem(IEvent event, IEventReceiver receiver) {
        this.event = event;
        this.receiver = receiver;
    }

    public IEvent getEvent() {
        return this.event;
    }

    public IEventReceiver getReceiver() {
        return this.receiver;
    }
}
