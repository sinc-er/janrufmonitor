package de.janrufmonitor.ui.jface.wizards.pages;

import de.janrufmonitor.framework.IMsn;

public class FilterMsnPage extends AbstractMsnPage {

	private String NAMESPACE = "ui.jface.wizards.pages.FilterMsnPage";
	
	public FilterMsnPage(IMsn msn) {
		super(msn);
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

}
