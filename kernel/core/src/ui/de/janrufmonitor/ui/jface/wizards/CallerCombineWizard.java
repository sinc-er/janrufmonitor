package de.janrufmonitor.ui.jface.wizards;

import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IMultiPhoneCaller;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.wizards.pages.AbstractPage;
import de.janrufmonitor.ui.jface.wizards.pages.CallerCombinePage;

public class CallerCombineWizard extends AbstractWizard {

	private String NAMESPACE = "ui.jface.wizards.CallerCombineWizard"; 

	private AbstractPage[] m_pages;
	private IMultiPhoneCaller m_caller;
	private IRuntime m_runtime;
	
	public CallerCombineWizard(ICallerList cl) {
		super();

		this.m_pages = new AbstractPage[1];
		this.m_pages[0] = new CallerCombinePage(cl); 

		this.addPage(this.m_pages[0]);
	}

	public String getID() {
		return CallerCombineWizard.class.getName();
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public boolean performFinish() {
		if (this.m_pages[0].isPageComplete()) {
			this.m_caller = ((CallerCombinePage)this.m_pages[0]).getResult();
			return true;
		}
		return false;
	}

	public boolean performCancel() {
		this.m_caller = null;
		return super.performCancel();
	}
	
	public IMultiPhoneCaller getResult() {
		return this.m_caller;
	}

	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

}
