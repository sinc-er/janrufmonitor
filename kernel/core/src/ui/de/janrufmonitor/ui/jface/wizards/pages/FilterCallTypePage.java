package de.janrufmonitor.ui.jface.wizards.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.swt.SWTImageManager;

public class FilterCallTypePage extends AbstractPage {

	private String NAMESPACE = "ui.jface.wizards.pages.FilterCallTypePage";

	private IAttributeMap m_map;
	private IRuntime m_runtime;
	
	public FilterCallTypePage(IAttributeMap map) {
		super(FilterCallTypePage.class.getName());
		this.m_map = map;
	}

	public IAttributeMap getResult() {
		return this.m_map;
	}

	public void createControl(Composite parent) {
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));

		Composite c = new Composite(parent, SWT.NONE);
	    c.setLayout(new GridLayout(1, false));
	    c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	    final Button active = new Button(c, SWT.CHECK);
	    active.setText(
	    	this.m_i18n.getString(this.getNamespace(), "*", "label", this.m_language)	
	    );
	    active.setSelection(isSet("*"));    
	    
	    final Button rej = new Button(c, SWT.RADIO);
	    rej.setText(
	    	this.m_i18n.getString(this.getNamespace(), IJAMConst.ATTRIBUTE_VALUE_REJECTED, "label", this.m_language)	
	    );
	    rej.setSelection(isSet(IJAMConst.ATTRIBUTE_VALUE_REJECTED));
	    rej.setImage(SWTImageManager.getInstance(PIMRuntime.getInstance()).get(IJAMConst.IMAGE_KEY_REJECTED_GIF));
	    
	    final Button out = new Button(c, SWT.RADIO);
	    out.setText(
	    	this.m_i18n.getString(this.getNamespace(), IJAMConst.ATTRIBUTE_VALUE_OUTGOING, "label", this.m_language)	
	    );
	    out.setSelection(isSet(IJAMConst.ATTRIBUTE_VALUE_OUTGOING)); 
	    out.setImage(SWTImageManager.getInstance(PIMRuntime.getInstance()).get(IJAMConst.IMAGE_KEY_OUTGOING_GIF));
	    
	    final Button acc = new Button(c, SWT.RADIO);
	    acc.setText(
	    	this.m_i18n.getString(this.getNamespace(), IJAMConst.ATTRIBUTE_VALUE_ACCEPTED, "label", this.m_language)	
	    );
	    acc.setSelection(isSet(IJAMConst.ATTRIBUTE_VALUE_ACCEPTED)); 
	    acc.setImage(SWTImageManager.getInstance(PIMRuntime.getInstance()).get(IJAMConst.IMAGE_KEY_ACCEPTED_GIF));
		
	    final Button miss = new Button(c, SWT.RADIO);
	    miss.setText(
	    	this.m_i18n.getString(this.getNamespace(), IJAMConst.ATTRIBUTE_VALUE_MISSED, "label", this.m_language)	
	    );
	    miss.setSelection(isSet(IJAMConst.ATTRIBUTE_VALUE_MISSED)); 
	    miss.setImage(SWTImageManager.getInstance(PIMRuntime.getInstance()).get(IJAMConst.IMAGE_KEY_AWAY_GIF));

	    
	    if (active.getSelection()) {
	    	rej.setVisible(false);
			out.setVisible(false);
			acc.setVisible(false);
			miss.setVisible(false);
	    }
	    
	    active.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {				
						if (active.getSelection()) {
							m_map = getRuntime().getCallFactory().createAttributeMap();
							rej.setVisible(false);
							out.setVisible(false);
							acc.setVisible(false);
							miss.setVisible(false);
						} else {
							rej.setVisible(true);
							out.setVisible(true);
							acc.setVisible(true);
							miss.setVisible(true);
						}
						setPageComplete(isComplete());
					}
				}	
		    );
	    
	    rej.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {				
						if (rej.getSelection()) {
							m_map = getRuntime().getCallFactory().createAttributeMap();
							m_map.add(getRuntime().getCallFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS, IJAMConst.ATTRIBUTE_VALUE_REJECTED));
							active.setSelection(false);
						} 
						setPageComplete(isComplete());
					}
				}	
		    );	 
	    
	    out.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {				
						if (out.getSelection()) {
							m_map = getRuntime().getCallFactory().createAttributeMap();
							m_map.add(getRuntime().getCallFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS, IJAMConst.ATTRIBUTE_VALUE_OUTGOING));
							active.setSelection(false);
						}
						setPageComplete(isComplete());
					}
				}	
		    );	  
	    
	    acc.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {				
						if (acc.getSelection()) {
							m_map = getRuntime().getCallFactory().createAttributeMap();
							m_map.add(getRuntime().getCallFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS, IJAMConst.ATTRIBUTE_VALUE_ACCEPTED));
							active.setSelection(false);
						} 
						setPageComplete(isComplete());
					}
				}	
		    );	  	 	    
        
	    miss.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {				
						if (miss.getSelection()) {
							m_map = getRuntime().getCallFactory().createAttributeMap();
							m_map.add(getRuntime().getCallFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS, IJAMConst.ATTRIBUTE_VALUE_MISSED));
							active.setSelection(false);
						} 
						setPageComplete(isComplete());
					}
				}	
		    );	
	    setPageComplete(isComplete());
	    setControl(c);
	}
	
	private boolean isSet(String name) {		
		if (name.equalsIgnoreCase("*")) return (this.m_map==null || this.m_map.size()==0);
		if (this.m_map==null || this.m_map.size()==0) return false;
		IAttribute a = this.m_map.get(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS);
		if (a==null || a.getValue()==null) return false;
		return a.getValue().equalsIgnoreCase(name);
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
