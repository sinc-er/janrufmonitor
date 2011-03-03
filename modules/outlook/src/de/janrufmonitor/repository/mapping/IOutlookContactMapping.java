package de.janrufmonitor.repository.mapping;

import java.util.List;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.repository.OutlookContactConst;

public interface IOutlookContactMapping  extends OutlookContactConst {

	final public static String MAPPING_ATTTRIBUTE_FIELD_ID = "outlook-contactfield-"; // outlook-contactfield-<IJAMConst.FIRSTNAME>=IOutlookContactMapping.FirstName

	public List getSupportedContactFields();
	
	public String mapToJamField(String outlookContactField);
	
	public String mapToOutlookContactField(String jamField);
	
	public IAttribute createOutlookContactFieldAttribute(String outlookContactField);
	
	public IAttributeMap getSpecialAttributes();
	
}
