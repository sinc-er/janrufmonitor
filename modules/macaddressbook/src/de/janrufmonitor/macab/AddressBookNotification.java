package de.janrufmonitor.macab;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddressBookNotification {
	private static final String INSERTED_STRING = "ABInsertedRecords"; //$NON-NLS-1$
	private static final String UPDATED_STRING = "ABUpdatedRecords"; //$NON-NLS-1$
	private static final String DELETED_STRING = "ABDeletedRecords"; //$NON-NLS-1$

	private final Set<String> insertedRecords = new HashSet<String>();
	private final Set<String> updatedRecords = new HashSet<String>();
	private final Set<String> deletedRecords = new HashSet<String>();

	@SuppressWarnings("unchecked")
	AddressBookNotification(Map<String, ?> rawData) {
		Object inserted = rawData.get(INSERTED_STRING);
		if (inserted instanceof List<?>) {
			insertedRecords.addAll((List<String>) inserted);
		}

		Object deleted = rawData.get(DELETED_STRING);
		if (deleted instanceof List<?>) {
			deletedRecords.addAll((List<String>) deleted);
		}

		Object updated = rawData.get(UPDATED_STRING);
		if (updated instanceof List<?>) {
			for (String s : (List<String>) updated) {
				if (!insertedRecords.contains(s) && !deletedRecords.contains(s)) {
					updatedRecords.add(s);
				}
			}
		}
	}

	public AddressBookNotification() {
	}

	public AddressBookNotification(String inserted, String updated,
			String deleted) {
		if (inserted != null) {
			insertedRecords.add(inserted);
		}

		if (updated != null) {
			updatedRecords.add(updated);
		}

		if (deleted != null) {
			deletedRecords.add(deleted);
		}
	}

	/**
	 * Pflegt die Informationen einer neueren Notification ein.
	 * 
	 * @param additionalNotification
	 *            Neue Notification
	 */
	public void addDataFrom(AddressBookNotification additionalNotification) {
		insertedRecords.addAll(additionalNotification.insertedRecords);
		updatedRecords.addAll(additionalNotification.updatedRecords);
		deletedRecords.addAll(additionalNotification.deletedRecords);
		insertedRecords.removeAll(deletedRecords);
		updatedRecords.removeAll(deletedRecords);
		updatedRecords.removeAll(insertedRecords);
	}

	public void clear() {
		insertedRecords.clear();
		updatedRecords.clear();
		deletedRecords.clear();
	}

	public Set<String> getInsertedRecords() {
		return insertedRecords;
	}

	public Set<String> getUpdatedRecords() {
		return updatedRecords;
	}

	public Set<String> getDeletedRecords() {
		return deletedRecords;
	}

	public boolean hasInsertedRecords() {
		return insertedRecords.size() > 0;
	}

	public boolean hasUpdatedRecords() {
		return updatedRecords.size() > 0;
	}

	public boolean hasDeletedRecords() {
		return deletedRecords.size() > 0;
	}

	private boolean hasSomething(Set<String> set, String type) {
		for (String s : set) {
			if (s.endsWith(type)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasChanges() {
		return hasInsertedRecords() || hasUpdatedRecords()
				|| hasDeletedRecords();
	}

	public boolean hasChangedGroups() {
		return hasSomething(insertedRecords, "ABGroup") //$NON-NLS-1$
				|| hasSomething(updatedRecords, "ABGroup") //$NON-NLS-1$
				|| hasSomething(deletedRecords, "ABGroup"); //$NON-NLS-1$
	}

}