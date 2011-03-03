package de.janrufmonitor.ui.jface.wizards.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class MsnPage extends AbstractPage {

	private String NAMESPACE = "ui.jface.wizards.pages.MsnPage";
	
	private IMsn m_msn;
	private IRuntime m_runtime;
	
	public MsnPage(IMsn msn) {
		super(MsnPage.class.getName());
				
		if (msn==null) {
			this.m_msn = PIMRuntime.getInstance().getCallFactory().createMsn("","");
		} else {
			this.m_msn = PIMRuntime.getInstance().getCallFactory().createMsn(msn.getMSN(), msn.getAdditional());
		}
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));
	}
	
	public IMsn getResult() {
		return this.m_msn;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}
	
	public void createControl(Composite parent) {
	    Composite msnComposite = new Composite(parent, SWT.NONE);
	    msnComposite.setLayout(new GridLayout(2, false));
	    msnComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	    Label ln = new Label(msnComposite, SWT.LEFT);
	    ln.setText(this.m_i18n.getString(this.getNamespace(), "msn", "label", this.m_language));
	    final Text msn = new Text(msnComposite, SWT.BORDER);
	    msn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    msn.setText(this.m_msn.getMSN());
	    
	    Label fn = new Label(msnComposite, SWT.LEFT);
	    fn.setText(this.m_i18n.getString(this.getNamespace(), "additional", "label", this.m_language));
	    final Text additional = new Text(msnComposite, SWT.BORDER);
	    additional.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    additional.setText(this.m_msn.getAdditional());
	    
	    // Add the handler to update the first name based on input
	    msn.addModifyListener(new ModifyListener() {
	      public void modifyText(ModifyEvent event) {
	        m_msn.setMSN(msn.getText());
	        setPageComplete(isComplete());
	      }
	    });
	    
	    // Add the handler to update the last name based on input
	    additional.addModifyListener(new ModifyListener() {
	      public void modifyText(ModifyEvent event) {
	        m_msn.setAdditional(additional.getText());
	        setPageComplete(isComplete());
	      }
	    });

	    setPageComplete(isComplete());
	    setControl(msnComposite);
	}
	
	public boolean isComplete() {
		if (this.m_msn.getMSN().trim().length()==0) {
			setErrorMessage(this.m_i18n.getString(this.getNamespace(), "msnerror", "label", this.m_language));
			return false;
		}
		
		if (this.m_msn.getAdditional().trim().length()==0) {
			setErrorMessage(null);
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
