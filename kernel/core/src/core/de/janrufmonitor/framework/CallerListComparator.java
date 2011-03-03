package de.janrufmonitor.framework;

import java.util.Comparator;

import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.formatter.Formatter;

public class CallerListComparator implements Comparator {

	public final static int ORDER_CALLERNAME = 2;
	
	public final static int ORDER_CALLERNUMBER = 3;
	
	public final static int ORDER_CITY = 7;
	
	public final static int ORDER_CATEGORY = 8;
	
	private int order = 0;
	
	private boolean direction = false;

	private Formatter m_f;
	
	public CallerListComparator(int order) {
		this.order = order;
	}
	
	public CallerListComparator(int order, boolean direction) {
		this.order = order;
		this.direction = direction;
	}

	public int compare(Object obj, Object obj1) {
        ICaller aCaller = (ICaller) obj;
        ICaller bCaller = (ICaller) obj1;

		int factor = 1;
		
		if (direction)
			factor = -1;
			
	    switch (order) {
        case ORDER_CALLERNAME:
        	IName aName = aCaller.getName();
        	IName bName = bCaller.getName();
        
        	return factor*this.getCompareName(aName).compareTo(this.getCompareName(bName));
            
        case ORDER_CALLERNUMBER:
        	IPhonenumber aNum = aCaller.getPhoneNumber();
        	IPhonenumber bNum = bCaller.getPhoneNumber();
	
            return factor*this.getCompareNumber(aNum).compareTo(this.getCompareNumber(bNum));
  
		case ORDER_CITY:
			String aCity = "";
			String bCity = "";
			IAttribute city = aCaller.getAttribute(IJAMConst.ATTRIBUTE_NAME_CITY);
			if (city!=null)
				aCity = city.getValue();
			
			city = bCaller.getAttribute(IJAMConst.ATTRIBUTE_NAME_CITY);
			if (city!=null)
				bCity = city.getValue();
			
	        return factor*aCity.compareTo(bCity);
	        
		case ORDER_CATEGORY:
			String cat1 = "";
			String cat2 = "";
			
			IAttribute cat = aCaller.getAttribute(IJAMConst.ATTRIBUTE_NAME_CATEGORY);
			if (cat!=null)
				cat1 = cat.getValue();
			
			cat = bCaller.getAttribute(IJAMConst.ATTRIBUTE_NAME_CATEGORY);
			if (cat!=null)
				cat2 = cat.getValue();
			
			return factor*cat1.compareTo(cat2);
    	}

        return 0;
	}
	
    private String getCompareNumber(IPhonenumber pn) {
    	return this.getFormatter().parse(IJAMConst.GLOBAL_VARIABLE_CALLERNUMBER, pn);
    }
	
    private String getCompareName(IName n) {
    	return this.getFormatter().parse(IJAMConst.GLOBAL_VARIABLE_CALLERNAME, n).toLowerCase();
    }
    
	private Formatter getFormatter() {
		if (this.m_f==null) {
			this.m_f = Formatter.getInstance(PIMRuntime.getInstance());
		}
		return this.m_f;
	}

}
