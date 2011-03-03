package de.janrufmonitor.ui.jface.wizards.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.janrufmonitor.framework.ICip;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public abstract class AbstractCipPage extends AbstractPage {

	private ICip m_cip;
	private IRuntime m_runtime;
	
	public AbstractCipPage(ICip cip) {
		super(AbstractCipPage.class.getName());
		if (cip==null)
			cip = PIMRuntime.getInstance().getCallFactory().createCip("","");
		
		this.m_cip = cip;
	}

	public ICip getResult() {
		return this.m_cip;
	}

	public void createControl(Composite parent) {
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));

		Composite nameComposite = new Composite(parent, SWT.NONE);
	    nameComposite.setLayout(new GridLayout(1, false));
	    nameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	    Label ln = new Label(nameComposite, SWT.LEFT);
	    ln.setText(this.m_i18n.getString(this.getNamespace(), "name", "label", this.m_language));

	    final Combo cip = new Combo (nameComposite, SWT.READ_ONLY);
		String[] cips = PIMRuntime.getInstance().getCipManager().getCipList();
		String[] cipList = new String[cips.length+1];
		cipList[0] = this.m_i18n.getString(
			this.getNamespace(),
			"*",
			"label",
			this.m_language
		);
		cip.setData(cipList[0], "*");
		int select = 0;
		for (int i=0;i<cips.length;i++) {
			String cipalias = PIMRuntime.getInstance().getCipManager().getCipLabel(cips[i], this.m_language);
			cipList[i+1] = cipalias;
			cip.setData(cipalias, cips[i]);
			if (cips[i].equalsIgnoreCase(this.m_cip.getCIP())) {
				select=i+1;	
			}
		}
		cip.setItems(cipList);		
		cip.select(select);

		String scip = cip.getItem(cip.getSelectionIndex());
		scip = (String) cip.getData(scip);
		
        this.m_cip = PIMRuntime.getInstance().getCipManager().createCip(scip);

	    // Add the handler to update the name based on input
		cip.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent event) {
			String scip = cip.getItem(cip.getSelectionIndex());
			scip = (String) cip.getData(scip);
			
	        m_cip = PIMRuntime.getInstance().getCipManager().createCip(scip);
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
