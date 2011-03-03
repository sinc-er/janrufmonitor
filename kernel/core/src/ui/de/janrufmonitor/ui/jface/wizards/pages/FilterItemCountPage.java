package de.janrufmonitor.ui.jface.wizards.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class FilterItemCountPage extends AbstractPage {

	private String NAMESPACE = "ui.jface.wizards.pages.FilterItemCountPage";

	private int m_limit;
	private IRuntime m_runtime;
	
	public FilterItemCountPage(int limit) {
		super(FilterItemCountPage.class.getName());
		this.m_limit = limit;
	}

	public int getResult() {
		return this.m_limit;
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
		String[] cips = new String[] {
			"-1", "5", "25", "100", "150", "250", "500"
		};
		String[] cipList = new String[cips.length];
		int select = 0;
		for (int i=0;i<cips.length;i++) {
			String cipalias = this.m_i18n.getString(
				this.getNamespace(),
				cips[i],
				"label",
				this.m_language
			);
			cipList[i] = cipalias;
			cip.setData(cipalias, cips[i]);
			if (Integer.parseInt(cips[i])==m_limit) {
				select=i;	
			}
		}
		cip.setItems(cipList);		
		cip.select(select);

		String scip = cip.getItem(cip.getSelectionIndex());
		scip = (String) cip.getData(scip);
		
        this.m_limit = 0; 

	    // Add the handler to update the name based on input
		cip.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent event) {
			String scip = cip.getItem(cip.getSelectionIndex());
			scip = (String) cip.getData(scip);
			
	        m_limit = Integer.parseInt(scip);
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

	public String getNamespace() {
		return NAMESPACE;
	}
}
