package de.janrufmonitor.repository.mapping;

import java.util.List;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.repository.IMacAddressBookConst;

public interface IMacAddressBookNumberMapping extends IMacAddressBookConst {
	
	final public static String MAPPING_ATTTRIBUTE_ID = "macab-numbertype-"; // outlook-numbertype-<number>=DefaultOutlookNumberMapping.XXXNumber

	public List getSupportedNumbers();
	
	public String mapToJamNumberType(String macabNumberType);
	
	public IAttribute createMacAddressBookNumberTypeAttribute(IPhonenumber pn, String outlookNumberType);
	
	public String[] getPriorityOrder(String jamType);
}
