package de.janrufmonitor.repository.mapping;

import java.util.ArrayList;
import java.util.List;

public class PrivateMacAddressBookMapping extends DefaultMacAddressBookMapping {
	
	public List getSupportedNumbers() {
		List<String> l = new ArrayList <String>(3);
		l.add(DefaultMacAddressBookMapping.HOME);
		l.add(DefaultMacAddressBookMapping.HOME_FAX);
		l.add(DefaultMacAddressBookMapping.MOBILE);
		return l;
	}

	public List getSupportedContactFields() {
		List<String> l = new ArrayList <String>(7);
		l.add(DefaultMacAddressBookMapping.STREET);
		l.add(DefaultMacAddressBookMapping.ZIP);
		l.add(DefaultMacAddressBookMapping.CITY);
		l.add(DefaultMacAddressBookMapping.COUNTRY);
		l.add(DefaultMacAddressBookMapping.FIRSTNAME);
		l.add(DefaultMacAddressBookMapping.LASTNAME);
		return l;
	}
}
