package de.janrufmonitor.macab.listener;

import de.janrufmonitor.macab.AddressBookNotification;

public interface AddressBookChangeListener {
	public void addressBookChanged(AddressBookNotification notification);
}
