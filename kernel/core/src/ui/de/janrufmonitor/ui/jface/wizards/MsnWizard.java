package de.janrufmonitor.ui.jface.wizards;

import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.wizards.pages.AbstractPage;
import de.janrufmonitor.ui.jface.wizards.pages.MsnPage;

public class MsnWizard extends AbstractWizard {

	private String NAMESPACE = "ui.jface.wizards.MsnWizard"; 
	
	private IMsn m_msn;
	private AbstractPage[] m_pages;
	private IRuntime m_runtime;
	
	public MsnWizard(IMsn msn) {
		super();
    	setWindowTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));

		this.m_msn = msn;
		
		this.m_pages = new AbstractPage[1];
		this.m_pages[0] = new MsnPage(this.m_msn);
		
		this.addPage(this.m_pages[0]);
	}

	public String getID() {
		return MsnWizard.class.getName();
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public boolean performFinish() {
		if (this.m_pages[0].isPageComplete()) {
			this.m_msn = ((MsnPage)this.m_pages[0]).getResult();
			return true;
		}
		return false;
	}

	public IMsn getResult() {
		return this.m_msn;
	}
	
	public boolean performCancel() {
		this.m_msn = null;
		return super.performCancel();
	}
	
	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}
}
