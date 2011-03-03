package de.janrufmonitor.repository;

public interface IMacAddressBookConst {

	// contact fields
	final public static String FIRSTNAME = "First", 
	MIDDLENAME = "Middle", 
	LASTNAME = "Last", 
	TITLE = "Title", 
	ORGANIZATION = "Organization", 
	EMAIL = "Email", 
	PHONE = "Phone", 
	ADDRESS = "Address", 
	CHAT = "ICQInstant", 
	UID = "UID", 
	BIRTHDAY = "Birthday", 
	FULL_NAME = "@FullName@",
	FULL_NAME_LAST_NAME_FIRST = "@FullNameLN@",
	PERSON_FLAG = "ABPersonFlags", 
	CREATION = "Creation",
	MODIFICATION = "Modification", 
	PARENT_GROUPS = "parentGroups", 
	HAS_PICTURE = "hasPicture"; 
	
	// address fields
	final public static String STREET = "Street", 
	ZIP = "ZIP", 
	COUNTRY_CODE = "CountryCode", 
	COUNTRY = "Country", 
	CITY = "City";
	
	// phone fields
	final public static String  HOME = "_$!<Home>!$_",
	WORK = "_$!<Work>!$_",
	MOBILE = "_$!<Mobile>!$_",
	HOME_FAX = "_$!<HomeFAX>!$_",
	WORK_FAX = "_$!<WorkFAX>!$_",
	MAIN = "_$!<Main>!$_",
	PAGER = "_$!<Pager>!$_",
	OTHER = "_$!<Other>!$_";
}
