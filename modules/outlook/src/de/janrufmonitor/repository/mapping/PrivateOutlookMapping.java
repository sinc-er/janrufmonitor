package de.janrufmonitor.repository.mapping;

import java.util.ArrayList;
import java.util.List;

public class PrivateOutlookMapping extends DefaultOutlookMapping {

	public List getSupportedNumbers() {
		List l = new ArrayList(8);
		l.add(DefaultOutlookMapping.Home2TelephoneNumber);
		l.add(DefaultOutlookMapping.HomeFaxNumber);
		l.add(DefaultOutlookMapping.HomeTelephoneNumber);
		l.add(DefaultOutlookMapping.ISDNNumber);
		l.add(DefaultOutlookMapping.MobileTelephoneNumber);
		l.add(DefaultOutlookMapping.OtherFaxNumber);
		l.add(DefaultOutlookMapping.OtherTelephoneNumber);
		l.add(DefaultOutlookMapping.PrimaryTelephoneNumber);
		return l;
	}
	
	public List getSupportedContactFields() {
		List l = new ArrayList(6);
		l.add(DefaultOutlookMapping.FirstName);
		l.add(DefaultOutlookMapping.LastName);
		l.add(DefaultOutlookMapping.HomeAddressCity);
		l.add(DefaultOutlookMapping.HomeAddressCountry);
		l.add(DefaultOutlookMapping.HomeAddressPostalCode);
		l.add(DefaultOutlookMapping.HomeAddressStreet);
		l.add(DefaultOutlookMapping.User3);
		return l;
	}
}
