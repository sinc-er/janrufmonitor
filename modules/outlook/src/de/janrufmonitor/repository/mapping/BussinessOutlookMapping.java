package de.janrufmonitor.repository.mapping;

import java.util.ArrayList;
import java.util.List;

import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.PIMRuntime;

public class BussinessOutlookMapping extends DefaultOutlookMapping {

	public List getSupportedNumbers() {
		List l = new ArrayList(13);
		l.add(DefaultOutlookMapping.AssistantTelephoneNumber);
		l.add(DefaultOutlookMapping.Business2TelephoneNumber);
		l.add(DefaultOutlookMapping.BusinessFaxNumber);
		l.add(DefaultOutlookMapping.BusinessTelephoneNumber);
		l.add(DefaultOutlookMapping.CallbackTelephoneNumber);
		l.add(DefaultOutlookMapping.CarTelephoneNumber);
		l.add(DefaultOutlookMapping.CompanyMainTelephoneNumber);
		l.add(DefaultOutlookMapping.MobileTelephoneNumber);
		l.add(DefaultOutlookMapping.PagerNumber);
//		l.add(DefaultOutlookMapping.PrimaryTelephoneNumber);
		l.add(DefaultOutlookMapping.RadioTelephoneNumber);
		l.add(DefaultOutlookMapping.TelexNumber);
		l.add(DefaultOutlookMapping.TTYTDDTelephoneNumber);
		return l;
	}
	

	public List getSupportedContactFields() {
		List l = new ArrayList(7);
		l.add(DefaultOutlookMapping.BusinessAddressCity);
		l.add(DefaultOutlookMapping.BusinessAddressCountry);
		l.add(DefaultOutlookMapping.BusinessAddressPostalCode);
		l.add(DefaultOutlookMapping.BusinessAddressStreet);
		l.add(DefaultOutlookMapping.CompanyName);
		l.add(DefaultOutlookMapping.FirstName);
		l.add(DefaultOutlookMapping.LastName);
		l.add(DefaultOutlookMapping.User4);
		return l;
	}

	public String mapToOutlookContactField(String jamField) {
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_FIRSTNAME)) return DefaultOutlookMapping.FirstName;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_LASTNAME)) return DefaultOutlookMapping.LastName;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_ADDITIONAL)) return DefaultOutlookMapping.CompanyName;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_STREET)) return DefaultOutlookMapping.BusinessAddressStreet;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_POSTAL_CODE)) return DefaultOutlookMapping.BusinessAddressPostalCode;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_CITY)) return DefaultOutlookMapping.BusinessAddressCity;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_COUNTRY)) return DefaultOutlookMapping.BusinessAddressCountry;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_GEO_ACC)) return DefaultOutlookMapping.User4;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_GEO_LAT)) return DefaultOutlookMapping.User4;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_GEO_LNG)) return DefaultOutlookMapping.User4;
		return null;
	}
	
	public IAttributeMap getSpecialAttributes() {
		IAttributeMap m = PIMRuntime.getInstance().getCallerFactory().createAttributeMap();
		m.add(PIMRuntime.getInstance().getCallerFactory().createAttribute("outlook.business", "true"));
		return m;
	}
	
}
