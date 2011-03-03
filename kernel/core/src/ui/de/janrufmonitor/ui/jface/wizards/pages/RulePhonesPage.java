package de.janrufmonitor.ui.jface.wizards.pages;

import de.janrufmonitor.framework.IPhonenumber;

public class RulePhonesPage extends AbstractPhonesPage {

	private String NAMESPACE = "ui.jface.wizards.pages.RulePhonesPage";
	
	public RulePhonesPage(IPhonenumber[] phones) {
		super(phones);
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

}
