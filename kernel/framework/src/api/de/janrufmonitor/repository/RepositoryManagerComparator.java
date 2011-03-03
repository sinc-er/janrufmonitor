package de.janrufmonitor.repository;

import de.janrufmonitor.framework.manager.IRepositoryManager;

import java.util.Comparator;

public class RepositoryManagerComparator implements Comparator {

	private boolean invers;

    public RepositoryManagerComparator() { 
    	this(false);
    }
    	
	public RepositoryManagerComparator(boolean invers) { 
		this.invers = invers;
	}

    public int compare(Object obj1, Object obj2) {
        IRepositoryManager ip1 = (IRepositoryManager) obj1;
        IRepositoryManager ip2 = (IRepositoryManager) obj2;

        if (ip1.getPriority() < ip2.getPriority()) {
            return (this.invers ? 1: -1);
        }

        if (ip1.getPriority() > ip2.getPriority()) {
			return (this.invers ? -1: 1);
        }

        if (ip1.getPriority() == ip2.getPriority()) {
            return 0;
        }

        return 0;
    }

}
