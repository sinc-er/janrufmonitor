package de.janrufmonitor.ui.jface.application.history;

import de.janrufmonitor.ui.jface.application.dialog.AbstractDialogPlugin;

public class HistoryDialogPlugin extends AbstractDialogPlugin {

	private History h = null;
	
	public String getLabel() {
		return this.getI18nManager().getString(History.NAMESPACE, "label", "label", this.getLanguage());
	}

	public boolean isEnabled() {
		return !this.m_dialog.getCall().getCaller().getPhoneNumber().isClired();
	}

	public void run() {
		if (this.h==null) {
			// check if blocking or not
			//boolean isBlocking = (getRuntime().getConfigManagerFactory().getConfigManager().getProperty(Journal.NAMESPACE, "blocking").equalsIgnoreCase("true"));
			this.h = new History(this.m_dialog.getCall().getCaller());
			this.h.open();
			this.h=null;
		} else {
			this.h.focus();
		}
	}

}
