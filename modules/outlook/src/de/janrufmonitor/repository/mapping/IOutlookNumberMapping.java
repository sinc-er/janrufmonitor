package de.janrufmonitor.repository.mapping;

import java.util.List;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.repository.OutlookContactConst;

public interface IOutlookNumberMapping extends OutlookContactConst {
	
	final public static String MAPPING_ATTTRIBUTE_ID = "outlook-numbertype-"; // outlook-numbertype-<number>=DefaultOutlookNumberMapping.XXXNumber
	
	public List getSupportedNumbers();
	
	public String mapToJamNumberType(String outlookNumberType);
	
	public String mapToOutlookNumberType(IPhonenumber pn, IAttributeMap attributes);
	
	public String getDefaultOutlookNumberType();
	
	public String getDefaultJamNumberType();
	
	public IAttribute createOutlookNumberTypeAttribute(IPhonenumber pn, String outlookNumberType);
	
	public String[] getPriorityOrder(String jamType);

}
