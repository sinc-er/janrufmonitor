package de.janrufmonitor.ui.jface.wizards.pages;

import de.janrufmonitor.framework.IMsn;

public class RuleMsnPage extends AbstractMsnPage {

	private String NAMESPACE = "ui.jface.wizards.pages.RuleMsnPage";
	
	public RuleMsnPage(IMsn msn) {
		super(msn);
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

}
