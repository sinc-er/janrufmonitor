package de.janrufmonitor.ui.jface.wizards;

import java.util.ArrayList;
import java.util.List;

import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.repository.filter.IFilter;
import de.janrufmonitor.repository.filter.PhonenumberFilter;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.wizards.pages.AbstractPage;
import de.janrufmonitor.ui.jface.wizards.pages.EditorFilterPhonePage;

public class EditorFilterWizard extends AbstractWizard {

	private String NAMESPACE = "ui.jface.wizards.EditorFilterWizard"; 
	
	private IFilter[] m_filters;
	private AbstractPage[] m_pages;
	private IRuntime m_runtime;
	
	public EditorFilterWizard(IFilter[] filters) {
		super();
    	setWindowTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));
		
    	this.m_filters = filters;
		
		this.m_pages = new AbstractPage[2];
		IFilter f = this.getPhoneFilter(filters);
		if (f!=null)
			this.m_pages[0] = new EditorFilterPhonePage(((PhonenumberFilter)f).getPhonenumber());
		else 
			this.m_pages[0] = new EditorFilterPhonePage(null);
		
		this.addPage(this.m_pages[0]);
	}

	public String getID() {
		return EditorFilterWizard.class.getName();
	}
	
	public String getNamespace() {
		return this.NAMESPACE;
	}

	public boolean performFinish() {
		if (this.m_pages[0].isPageComplete()) {
			
			List filterList = new ArrayList();
			
			IPhonenumber phones = ((EditorFilterPhonePage)this.m_pages[0]).getResult();
			if (phones!=null) {
				filterList.add(new PhonenumberFilter(phones));
			}
			
			this.m_filters = new IFilter[filterList.size()];
			for (int i=0;i<filterList.size();i++) {
				this.m_filters[i] = (IFilter)filterList.get(i);
			}
			
			return true;
		}
		this.m_filters = null;
		return false;
	}
	
	public IFilter[] getResult() {
		return this.m_filters;
	}
	
	public boolean performCancel() {
		this.m_filters = null;
		return super.performCancel();
	}
	
	private IFilter getPhoneFilter(IFilter[] f) {
		if (f==null) return null;
		for (int i=0;i<f.length;i++) {
			if (f[i] instanceof PhonenumberFilter) return f[i];
		}
		return null;
	}

	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}
}
