package de.janrufmonitor.ui.jface.wizards.pages;

import de.janrufmonitor.framework.IPhonenumber;

public class RuleExPhonesPage extends AbstractPhonesPage {

	private String NAMESPACE = "ui.jface.wizards.pages.RuleExPhonesPage";

	public RuleExPhonesPage(IPhonenumber[] phones) {
		super(phones);
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

}
