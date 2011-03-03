package de.janrufmonitor.ui.jface.wizards.pages;

import de.janrufmonitor.framework.ICip;

public class RuleCipPage extends AbstractCipPage {

	private String NAMESPACE = "ui.jface.wizards.pages.RuleCipPage";
	
	public RuleCipPage(ICip cip) {
		super(cip);
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}
	
}
