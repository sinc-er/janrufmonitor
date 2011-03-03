package de.janrufmonitor.ui.jface.wizards;

import java.util.logging.Level;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IName;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.types.IReadCallerRepository;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.wizards.pages.AbstractPage;
import de.janrufmonitor.ui.jface.wizards.pages.MultiPhoneCallerPage;

public class CallerWizard extends AbstractWizard {

	private String NAMESPACE = "ui.jface.wizards.CallerWizard"; 

	private ICaller m_caller;
	private AbstractPage[] m_pages;
	
	private IRuntime m_runtime;
	
	public CallerWizard(ICaller caller) {
		super();

		if (caller == null) {
			IName name = this.getRuntime().getCallerFactory().createName("", "");
			IPhonenumber pn = this.getRuntime().getCallerFactory().createPhonenumber(false);			
			this.m_caller = this.getRuntime().getCallerFactory().createCaller(name, pn);
		} else {
			this.m_caller = caller;
		}
		
		ICaller clonedCaller = null;
		try {
			clonedCaller = (ICaller) this.m_caller.clone();
		} catch (CloneNotSupportedException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		}
		boolean callerReadonly = this.isReadonly(clonedCaller);
		this.m_pages = new AbstractPage[1];
		this.m_pages[0] = new MultiPhoneCallerPage(clonedCaller, callerReadonly, callerReadonly, false); 
		//this.m_pages[0] = new EasyMultiPhoneCallerPage(clonedCaller, callerReadonly, callerReadonly, false); 

		this.addPage(this.m_pages[0]);
		
		if(callerReadonly)
			setWindowTitle(this.m_i18n.getString(this.getNamespace(), "title2", "label", this.m_language));
		else 
			setWindowTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));


	}

	public String getID() {
		return CallerWizard.class.getName();
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public boolean performFinish() {
		if (this.m_pages[0].isPageComplete()) {
			this.m_caller = ((MultiPhoneCallerPage)this.m_pages[0]).getResult();
			return true;
		}
		return false;
	}

	public boolean performCancel() {
		this.m_caller = null;
		return super.performCancel();
	}
	
	public ICaller getResult() {
		return this.m_caller;
	}

	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	private boolean isReadonly(ICaller c) {
		IAttribute att = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLERMANAGER);
		if (att != null) {
			if (this.isCallerManagerReadOnly(att.getValue())) {
				return true;	
			}
		}
		return false;
	}
	
	private boolean isCallerManagerReadOnly(String man) {
		ICallerManager m = PIMRuntime.getInstance().getCallerManagerFactory().getCallerManager(man);
		if (m!=null) {
			return (m.isSupported(IReadCallerRepository.class) && !m.isSupported(IWriteCallerRepository.class));
		}
		return false;
	}
}
