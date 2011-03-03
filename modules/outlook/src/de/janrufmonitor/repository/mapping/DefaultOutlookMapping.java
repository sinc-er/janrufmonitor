package de.janrufmonitor.repository.mapping;

import java.util.ArrayList;
import java.util.List;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.runtime.PIMRuntime;

public class DefaultOutlookMapping implements IOutlookMapping {

	public List getSupportedNumbers() {
		List l = new ArrayList(20);
		l.add(DefaultOutlookMapping.AssistantTelephoneNumber);
		l.add(DefaultOutlookMapping.Business2TelephoneNumber);
		l.add(DefaultOutlookMapping.BusinessFaxNumber);
		l.add(DefaultOutlookMapping.BusinessTelephoneNumber);
		l.add(DefaultOutlookMapping.CallbackTelephoneNumber);
		l.add(DefaultOutlookMapping.CarTelephoneNumber);
		l.add(DefaultOutlookMapping.CompanyMainTelephoneNumber);
		l.add(DefaultOutlookMapping.Home2TelephoneNumber);
		l.add(DefaultOutlookMapping.HomeFaxNumber);
		l.add(DefaultOutlookMapping.HomeTelephoneNumber);
		l.add(DefaultOutlookMapping.ISDNNumber);
		l.add(DefaultOutlookMapping.MobileTelephoneNumber);
		l.add(DefaultOutlookMapping.OtherFaxNumber);
		l.add(DefaultOutlookMapping.OtherTelephoneNumber);
		l.add(DefaultOutlookMapping.PagerNumber);
		l.add(DefaultOutlookMapping.PrimaryTelephoneNumber);
		l.add(DefaultOutlookMapping.RadioTelephoneNumber);
		l.add(DefaultOutlookMapping.TelexNumber);
		l.add(DefaultOutlookMapping.TTYTDDTelephoneNumber);
		return l;
	}
	
	public String[] getPriorityOrder(String jamType) {
		if (jamType!=null && jamType.equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_LANDLINE_TYPE)) {
			return new String[] {
					DefaultOutlookMapping.HomeTelephoneNumber,
					DefaultOutlookMapping.Home2TelephoneNumber,
					DefaultOutlookMapping.OtherTelephoneNumber,
					DefaultOutlookMapping.PrimaryTelephoneNumber,
					DefaultOutlookMapping.BusinessTelephoneNumber,					
					DefaultOutlookMapping.Business2TelephoneNumber,					
					DefaultOutlookMapping.ISDNNumber			
				};
		}
		if (jamType!=null && jamType.equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_MOBILE_TYPE)) {
			return new String[] {
					DefaultOutlookMapping.MobileTelephoneNumber,
					DefaultOutlookMapping.CarTelephoneNumber,
					DefaultOutlookMapping.RadioTelephoneNumber
				};
		}	
		if (jamType!=null && jamType.equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_FAX_TYPE)) {
			return new String[] {
					DefaultOutlookMapping.HomeFaxNumber,
					DefaultOutlookMapping.OtherFaxNumber,
					DefaultOutlookMapping.BusinessFaxNumber,
					DefaultOutlookMapping.TelexNumber
				};
		}				
		return new String[] {
				DefaultOutlookMapping.HomeTelephoneNumber,
				DefaultOutlookMapping.Home2TelephoneNumber,
				DefaultOutlookMapping.OtherTelephoneNumber,
				DefaultOutlookMapping.PrimaryTelephoneNumber,
				DefaultOutlookMapping.BusinessTelephoneNumber,					
				DefaultOutlookMapping.Business2TelephoneNumber,					
				DefaultOutlookMapping.ISDNNumber			
			};
	}

	public String mapToJamNumberType(String outlookNumberType) {
		if (outlookNumberType.equalsIgnoreCase(DefaultOutlookMapping.AssistantTelephoneNumber)) return IJAMConst.ATTRIBUTE_VALUE_LANDLINE_TYPE;
		if (outlookNumberType.equalsIgnoreCase(DefaultOutlookMapping.Business2TelephoneNumber)) return IJAMConst.ATTRIBUTE_VALUE_LANDLINE_TYPE;
		if (outlookNumberType.equalsIgnoreCase(DefaultOutlookMapping.BusinessTelephoneNumber)) return IJAMConst.ATTRIBUTE_VALUE_LANDLINE_TYPE;
		if (outlookNumberType.equalsIgnoreCase(DefaultOutlookMapping.CompanyMainTelephoneNumber)) return IJAMConst.ATTRIBUTE_VALUE_LANDLINE_TYPE;
		if (outlookNumberType.equalsIgnoreCase(DefaultOutlookMapping.MobileTelephoneNumber)) return IJAMConst.ATTRIBUTE_VALUE_MOBILE_TYPE;
		if (outlookNumberType.equalsIgnoreCase(DefaultOutlookMapping.CarTelephoneNumber)) return IJAMConst.ATTRIBUTE_VALUE_MOBILE_TYPE;
		if (outlookNumberType.equalsIgnoreCase(DefaultOutlookMapping.RadioTelephoneNumber)) return IJAMConst.ATTRIBUTE_VALUE_MOBILE_TYPE;
		if (outlookNumberType.equalsIgnoreCase(DefaultOutlookMapping.BusinessFaxNumber)) return IJAMConst.ATTRIBUTE_VALUE_FAX_TYPE;
		if (outlookNumberType.equalsIgnoreCase(DefaultOutlookMapping.Home2TelephoneNumber)) return IJAMConst.ATTRIBUTE_VALUE_LANDLINE_TYPE;
		if (outlookNumberType.equalsIgnoreCase(DefaultOutlookMapping.HomeTelephoneNumber)) return IJAMConst.ATTRIBUTE_VALUE_LANDLINE_TYPE;
		if (outlookNumberType.equalsIgnoreCase(DefaultOutlookMapping.HomeFaxNumber)) return IJAMConst.ATTRIBUTE_VALUE_FAX_TYPE;
		if (outlookNumberType.equalsIgnoreCase(DefaultOutlookMapping.OtherFaxNumber)) return IJAMConst.ATTRIBUTE_VALUE_FAX_TYPE;
		if (outlookNumberType.equalsIgnoreCase(DefaultOutlookMapping.TelexNumber)) return IJAMConst.ATTRIBUTE_VALUE_FAX_TYPE;
		if (outlookNumberType.equalsIgnoreCase(DefaultOutlookMapping.OtherTelephoneNumber)) return IJAMConst.ATTRIBUTE_VALUE_LANDLINE_TYPE;
		if (outlookNumberType.equalsIgnoreCase(DefaultOutlookMapping.PrimaryTelephoneNumber)) return IJAMConst.ATTRIBUTE_VALUE_LANDLINE_TYPE;
		if (outlookNumberType.equalsIgnoreCase(DefaultOutlookMapping.ISDNNumber)) return IJAMConst.ATTRIBUTE_VALUE_LANDLINE_TYPE;
		return getDefaultJamNumberType();
	}

	public String mapToOutlookNumberType(IPhonenumber pn, IAttributeMap attributes) {
		if (pn==null || attributes==null || attributes.size()==0) return getDefaultOutlookNumberType();
		IAttribute a = attributes.get(DefaultOutlookMapping.MAPPING_ATTTRIBUTE_ID+pn.getTelephoneNumber());
		if (a!=null) {
			return a.getValue();
		}
		return getDefaultOutlookNumberType();
	}

	public String getDefaultJamNumberType() {
		return "";
	}

	public String getDefaultOutlookNumberType() {
		return DefaultOutlookMapping.PrimaryTelephoneNumber;
	}

	public IAttribute createOutlookNumberTypeAttribute(IPhonenumber pn, String outlookNumberType) {
		return PIMRuntime.getInstance().getCallerFactory().createAttribute(DefaultOutlookMapping.MAPPING_ATTTRIBUTE_ID+pn.getTelephoneNumber(), outlookNumberType);
	}

	public List getSupportedContactFields() {
		List l = new ArrayList(12);
		l.add(DefaultOutlookMapping.BusinessAddressCity);
		l.add(DefaultOutlookMapping.BusinessAddressCountry);
		l.add(DefaultOutlookMapping.BusinessAddressPostalCode);
		l.add(DefaultOutlookMapping.BusinessAddressStreet);
		l.add(DefaultOutlookMapping.CompanyName);
		l.add(DefaultOutlookMapping.FirstName);
		l.add(DefaultOutlookMapping.LastName);
		l.add(DefaultOutlookMapping.HomeAddressCity);
		l.add(DefaultOutlookMapping.HomeAddressCountry);
		l.add(DefaultOutlookMapping.HomeAddressPostalCode);
		l.add(DefaultOutlookMapping.HomeAddressStreet);
		return l;
	}

	public String mapToJamField(String outlookContactField) {
		if (outlookContactField.equalsIgnoreCase(DefaultOutlookMapping.FirstName)) return IJAMConst.ATTRIBUTE_NAME_FIRSTNAME;
		if (outlookContactField.equalsIgnoreCase(DefaultOutlookMapping.LastName)) return IJAMConst.ATTRIBUTE_NAME_LASTNAME;
		if (outlookContactField.equalsIgnoreCase(DefaultOutlookMapping.CompanyName)) return IJAMConst.ATTRIBUTE_NAME_ADDITIONAL;
		if (outlookContactField.equalsIgnoreCase(DefaultOutlookMapping.HomeAddressCity)) return IJAMConst.ATTRIBUTE_NAME_CITY;
		if (outlookContactField.equalsIgnoreCase(DefaultOutlookMapping.HomeAddressStreet)) return IJAMConst.ATTRIBUTE_NAME_STREET;
		if (outlookContactField.equalsIgnoreCase(DefaultOutlookMapping.HomeAddressPostalCode)) return IJAMConst.ATTRIBUTE_NAME_POSTAL_CODE;
		if (outlookContactField.equalsIgnoreCase(DefaultOutlookMapping.HomeAddressCountry)) return IJAMConst.ATTRIBUTE_NAME_COUNTRY;
		if (outlookContactField.equalsIgnoreCase(DefaultOutlookMapping.BusinessAddressCity)) return IJAMConst.ATTRIBUTE_NAME_CITY;
		if (outlookContactField.equalsIgnoreCase(DefaultOutlookMapping.BusinessAddressStreet)) return IJAMConst.ATTRIBUTE_NAME_STREET;
		if (outlookContactField.equalsIgnoreCase(DefaultOutlookMapping.BusinessAddressPostalCode)) return IJAMConst.ATTRIBUTE_NAME_POSTAL_CODE;
		if (outlookContactField.equalsIgnoreCase(DefaultOutlookMapping.BusinessAddressCountry)) return IJAMConst.ATTRIBUTE_NAME_COUNTRY;
		if (outlookContactField.equalsIgnoreCase(DefaultOutlookMapping.User3)) return IJAMConst.ATTRIBUTE_NAME_GEO_ACC;
		if (outlookContactField.equalsIgnoreCase(DefaultOutlookMapping.User4)) return IJAMConst.ATTRIBUTE_NAME_GEO_ACC;
		return null;
	}

	public String mapToOutlookContactField(String jamField) {
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_FIRSTNAME)) return DefaultOutlookMapping.FirstName;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_LASTNAME)) return DefaultOutlookMapping.LastName;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_ADDITIONAL)) return DefaultOutlookMapping.CompanyName;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_STREET)) return DefaultOutlookMapping.HomeAddressStreet;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_POSTAL_CODE)) return DefaultOutlookMapping.HomeAddressPostalCode;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_CITY)) return DefaultOutlookMapping.HomeAddressCity;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_COUNTRY)) return DefaultOutlookMapping.HomeAddressCountry;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_GEO_ACC)) return DefaultOutlookMapping.User3;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_GEO_LAT)) return DefaultOutlookMapping.User3;
		if (jamField.equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_GEO_LNG)) return DefaultOutlookMapping.User3;
		return null;
	}

	public IAttribute createOutlookContactFieldAttribute(String outlookContactField) {
		String jamMapping = mapToJamField(outlookContactField);
		if (jamMapping!=null) {
			return PIMRuntime.getInstance().getCallerFactory().createAttribute(DefaultOutlookMapping.MAPPING_ATTTRIBUTE_FIELD_ID + jamMapping, outlookContactField);			
		}
		return null;
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("DefaultOutlookMapping:");
		s.append(", ");
		List l = getSupportedNumbers();
		s.append("NumberMapping (<Outlook>=<JAM>):");s.append(", ");
		String item = null;
		for (int i=0,j=l.size();i<j;i++) {
			item = (String) l.get(i);
			s.append(item);
			s.append("=");
			s.append(mapToJamNumberType(item));
			s.append(", ");
		}	
		l = getSupportedContactFields();
		s.append("ContactFieldMapping (<Outlook>=<JAM>):");s.append(", ");
		 item = null;
		for (int i=0,j=l.size();i<j;i++) {
			item = (String) l.get(i);
			s.append(item);
			s.append("=");
			s.append(mapToJamField(item));
			s.append(", ");
		}
		return s.toString();
	}

	public IAttributeMap getSpecialAttributes() {
		return null;
	}

}
