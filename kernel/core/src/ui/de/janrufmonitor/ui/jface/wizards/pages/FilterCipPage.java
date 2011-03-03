package de.janrufmonitor.ui.jface.wizards.pages;

import de.janrufmonitor.framework.ICip;

public class FilterCipPage extends AbstractCipPage {

	private String NAMESPACE = "ui.jface.wizards.pages.FilterCipPage";
	
	public FilterCipPage(ICip cip) {
		super(cip);
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

}
