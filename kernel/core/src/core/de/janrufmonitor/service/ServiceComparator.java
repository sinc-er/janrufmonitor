package de.janrufmonitor.service;

import java.util.Comparator;

public class ServiceComparator implements Comparator {

    public ServiceComparator() { }

    public int compare(Object obj1, Object obj2) {
        IService is1 = (IService) obj1;
        IService is2 = (IService) obj2;

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
