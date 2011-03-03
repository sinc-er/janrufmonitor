package de.janrufmonitor.ui.jface.wizards;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.wizards.pages.AbstractPage;
import de.janrufmonitor.ui.jface.wizards.pages.EditorCategoryPage;

public class EditorCategoryWizard extends AbstractWizard {

	private String NAMESPACE = "ui.jface.wizards.EditorCategoryWizard"; 
	
	private String m_filters;
	private AbstractPage[] m_pages;
	private IRuntime m_runtime;
	
	public EditorCategoryWizard(String cat) {
		super();
    	setWindowTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));
		
    	this.m_filters = cat;
		
		this.m_pages = new AbstractPage[1];
		this.m_pages[0] = new EditorCategoryPage(cat);
		
		this.addPage(this.m_pages[0]);
	}

	public String getID() {
		return EditorCategoryWizard.class.getName();
	}
	
	public String getNamespace() {
		return this.NAMESPACE;
	}

	public boolean performFinish() {
		if (this.m_pages[0].isPageComplete()) {
			this.m_filters = ((EditorCategoryPage)this.m_pages[0]).getResult();
			if (this.m_filters!=null)
				return true;
		}
		this.m_filters = null;
		return false;
	}
	
	public String getResult() {
		return this.m_filters;
	}
	
	public boolean performCancel() {
		this.m_filters = null;
		return super.performCancel();
	}
	

	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}
}
