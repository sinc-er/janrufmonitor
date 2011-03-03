package de.janrufmonitor.ui.jface.wizards.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public abstract class AbstractMsnPage extends AbstractPage {

	private IMsn m_msn;
	private IRuntime m_runtime;
	
	public AbstractMsnPage(IMsn msn) {
		super(AbstractMsnPage.class.getName());
		if (msn==null)
			msn = PIMRuntime.getInstance().getCallFactory().createMsn("","");
		
		this.m_msn = msn;
	}

	public IMsn getResult() {
		return this.m_msn;
	}

	public void createControl(Composite parent) {		
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));

		Composite nameComposite = new Composite(parent, SWT.NONE);
	    nameComposite.setLayout(new GridLayout(1, false));
	    nameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	    Label ln = new Label(nameComposite, SWT.LEFT);
	    ln.setText(this.m_i18n.getString(this.getNamespace(), "name", "label", this.m_language));

		final Combo msn = new Combo (nameComposite, SWT.READ_ONLY);
		
		String[] msns = PIMRuntime.getInstance().getMsnManager().getMsnList();
		String[] msnList = new String[msns.length+1];
		msnList[0] = this.m_i18n.getString(
			this.getNamespace(),
			"*",
			"label",
			this.m_language
		);
		msn.setData(msnList[0], "*");
		int select = 0;
		for (int i=0;i<msns.length;i++) {
			String msnalias = msns[i] + " ("+PIMRuntime.getInstance().getMsnManager().getMsnLabel(msns[i]) +")";
			msnList[i+1] = msnalias;
			msn.setData(msnalias, msns[i]);
			if (msns[i].equalsIgnoreCase(this.m_msn.getMSN())) {
				select=i+1;	
			}
		}
		msn.setItems(msnList);
		msn.select(select);
		
      	String smsn = msn.getItem(msn.getSelectionIndex());
      	smsn = (String) msn.getData(smsn);
      	
        this.m_msn = PIMRuntime.getInstance().getMsnManager().createMsn(smsn);

		
	    // Add the handler to update the name based on input
		msn.addModifyListener(new ModifyListener() {
	      public void modifyText(ModifyEvent event) {
	      	String smsn = msn.getItem(msn.getSelectionIndex());
	      	smsn = (String) msn.getData(smsn);
	      	
	        m_msn = PIMRuntime.getInstance().getMsnManager().createMsn(smsn);
	        setPageComplete(isComplete());
	      }
	    });
	    
	    setPageComplete(isComplete());
	    setControl(nameComposite);
	}
	

	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}
}
