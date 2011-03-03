package de.janrufmonitor.ui.jface.wizards.pages;

import de.janrufmonitor.framework.IPhonenumber;

public class FilterPhonesPage extends AbstractPhonesPage {

	private String NAMESPACE = "ui.jface.wizards.pages.FilterPhonesPage";
	
	public FilterPhonesPage(IPhonenumber[] phones) {
		super(phones);
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

}
