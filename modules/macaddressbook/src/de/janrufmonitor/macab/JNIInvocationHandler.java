package de.janrufmonitor.macab;

import java.util.Map;

class JNIInvocationHandler {
	@SuppressWarnings("unchecked")
	static void addressBookChanged(Object notification) {
		AddressBookNotification noti = null;
		if (notification instanceof Map<?, ?>) {
			noti = new AddressBookNotification((Map<String, ?>) notification);
		}
		MacAddressBookProxy.getInstance().addressBookChanged(noti);
	}

}
