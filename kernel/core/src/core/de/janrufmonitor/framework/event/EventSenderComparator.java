package de.janrufmonitor.framework.event;

import java.util.Comparator;

public class EventSenderComparator implements Comparator {

    public EventSenderComparator() { }

    public int compare(Object obj1, Object obj2) {
        IEventSender is1 = (IEventSender) obj1;
        IEventSender is2 = (IEventSender) obj2;

        if (is1.getPriority() < is2.getPriority()) {
            return -1;
        }

        if (is1.getPriority() > is2.getPriority()) {
            return 1;
        }

        if (is1.getPriority() == is2.getPriority()) {
            return 0;
        }

        return 0;
    }

}
