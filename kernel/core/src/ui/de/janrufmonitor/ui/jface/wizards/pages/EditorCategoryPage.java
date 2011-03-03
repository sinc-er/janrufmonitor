package de.janrufmonitor.ui.jface.wizards.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class EditorCategoryPage extends AbstractPage {

	private String NAMESPACE = "ui.jface.wizards.pages.EditorCategoryPage";
	private String m_cat;
	private IRuntime m_runtime;
	
	public EditorCategoryPage(String cat) {
		super(EditorCategoryPage.class.getName());
		if (cat==null) cat = "";
		this.m_cat = cat;
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));
	}

	public String getResult() {
		return this.m_cat;
	}
	
	public String getNamespace() {
		return this.NAMESPACE;
	}

	public void createControl(Composite parent) {
	    Composite phoneComposite = new Composite(parent, SWT.NONE);
	    phoneComposite.setLayout(new GridLayout(1, true));
	    phoneComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	    final Label ial = new Label(phoneComposite, SWT.LEFT);
	    ial.setText(this.m_i18n.getString(this.getNamespace(), "category", "label", this.m_language));

	    final Text category = new Text(phoneComposite, SWT.BORDER);
	    category.setText(this.m_cat);
	    category.setTextLimit(128);

	    category.addModifyListener(new ModifyListener() {
	      public void modifyText(ModifyEvent event) {
	      	m_cat = category.getText();
	        setPageComplete(isComplete());
	      }
	    });
	    

	    setPageComplete(isComplete());
	    setControl(phoneComposite);
	}

	public boolean isComplete() {			
		if (this.m_cat==null || this.m_cat.trim().length()==0) {
			return false;
		}

		return super.isComplete();
	}
	
	
	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}
}
