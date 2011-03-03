package de.janrufmonitor.framework.event;

import java.util.Comparator;

public class EventReceiverComparator implements Comparator {

    public EventReceiverComparator() { }
 
    public int compare(Object obj1, Object obj2) {
        IEventReceiver is1 = ((EventMapper) obj1).getReceiver();
        IEventReceiver is2 = ((EventMapper) obj2).getReceiver();

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
