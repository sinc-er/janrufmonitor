package de.janrufmonitor.ui.jface.wizards.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class InitWelcomePage extends AbstractPage {

	private String NAMESPACE = "ui.jface.wizards.pages.InitWelcomePage";

	private IRuntime m_runtime;
	
	public InitWelcomePage(String f) {
		super(InitWelcomePage.class.getName());
	}

	public boolean performFinish() {
		return true;
	}
	
	
	public void createControl(Composite parent) {
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));

		Composite c = new Composite(parent, SWT.NONE);
	    c.setLayout(new GridLayout(1, true));
	    c.setLayoutData(new GridData(GridData.FILL_BOTH));

	    Text l = new Text(c, SWT.LEFT | SWT.WRAP);
	    l.setText(this.m_i18n.getString(getNamespace(), "welcome", "label", this.m_language));
	    l.setEditable(false);	
	    l.setBackground(parent.getBackground());
	    
	    setPageComplete(isComplete());
	    setControl(c);
	}
	
	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getNamespace() {
		return NAMESPACE;
	}
}
