package de.janrufmonitor.ui.jface.wizards.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.janrufmonitor.framework.event.IEventReceiver;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.IService;

public class RuleServicePage extends AbstractPage {

	private String NAMESPACE = "ui.jface.wizards.pages.RuleServicePage";

	private String m_name;
	private IRuntime m_runtime;
	
	public RuleServicePage(String name) {
		super(RuleServicePage.class.getName());
		if (name==null)
			name = "";
		
		this.m_name = name;
		
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}
	
	public String getResult() {
		return this.m_name;
	}

	public void createControl(Composite parent) {
	    Composite nameComposite = new Composite(parent, SWT.NONE);
	    nameComposite.setLayout(new GridLayout(1, false));
	    nameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	    Label ln = new Label(nameComposite, SWT.LEFT);
	    ln.setText(this.m_i18n.getString(this.getNamespace(), "name", "label", this.m_language));

	    final Combo service = new Combo (nameComposite, SWT.READ_ONLY);
	    String[] serviceIDs = getRuntime().getServiceFactory().getAllServiceIDs();
	    
	    // only event receiver services are relevant for rules
	    serviceIDs = this.getEventServices(serviceIDs);
	    
		String[] services = new String[serviceIDs.length];
		int select = 0;
		for (int i=0;i<serviceIDs.length;i++) {
			String servicealias = this.m_i18n.getString(
				"service."+serviceIDs[i],
				"title",
				"label",
				this.m_language
			);
			if (serviceIDs[i].equalsIgnoreCase(this.m_name)) {
				select=i;	
			}
			services[i] = servicealias;
			service.setData(servicealias, serviceIDs[i]);
		}
		service.setItems(services);
		service.select(select);
		this.m_name = service.getItem(service.getSelectionIndex());
		this.m_name = (String) service.getData(m_name);
        
	    // Add the handler to update the name based on input
		service.addModifyListener(new ModifyListener() {
	      public void modifyText(ModifyEvent event) {
	        m_name = service.getItem(service.getSelectionIndex());
	        m_name = (String) service.getData(m_name);
	        setPageComplete(isComplete());
	      }
	    });
	    
	    setPageComplete(isComplete());
	    setControl(nameComposite);
	}

	private String[] getEventServices(String[] serviceIDs) {
		List l = new ArrayList();
		IService s = null;
		for (int i=0;i<serviceIDs.length;i++) {
			s = getRuntime().getServiceFactory().getService(serviceIDs[i]);
			if (s!=null && s instanceof IEventReceiver) {
				l.add(serviceIDs[i]);
			} else {
				this.m_logger.info("Service without EventReceiver: "+serviceIDs[i]);
			}
		}
		
		String[] list = new String[l.size()];
		for (int i=0;i<list.length;i++) {
			list[i] = (String) l.get(i);
		}
		return list;
	}

	public boolean isComplete() {
		if (this.m_name.trim().length()==0) {
			setErrorMessage(this.m_i18n.getString(this.getNamespace(), "nameerror", "label", this.m_language));
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
