package de.janrufmonitor.ui.jface.wizards.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class EditorFilterPhonePage extends AbstractPage {

	private String NAMESPACE = "ui.jface.wizards.pages.EditorFilterPhonePage";
	private IPhonenumber m_phone;
	private IRuntime m_runtime;
	
	public EditorFilterPhonePage(IPhonenumber pn) {
		super(EditorFilterPhonePage.class.getName());
		if (pn==null) {
			this.m_phone = PIMRuntime.getInstance().getCallerFactory().createPhonenumber(false);
		} else {
			this.m_phone = PIMRuntime.getInstance().getCallerFactory().createPhonenumber(pn.getIntAreaCode(), pn.getAreaCode(), pn.getCallNumber());
			this.m_phone.setClired(false);
		}
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));
	}

	public IPhonenumber getResult() {
		return this.m_phone;
	}
	
	public String getNamespace() {
		return this.NAMESPACE;
	}

	public void createControl(Composite parent) {
	    Composite phoneComposite = new Composite(parent, SWT.NONE);
	    phoneComposite.setLayout(new GridLayout(3, false));
	    phoneComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	    final Label ial = new Label(phoneComposite, SWT.LEFT);
	    ial.setText(this.m_i18n.getString(this.getNamespace(), "intarea", "label", this.m_language));

	    final Text intAreaCode = new Text(phoneComposite, SWT.BORDER);
	    intAreaCode.setSize(intAreaCode.getSize().x, 50);
	    if (this.m_phone.getIntAreaCode().trim().length()==0) {
	    	this.m_phone.setIntAreaCode(this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_INTAREA));
	    } 
	    intAreaCode.setText(this.m_phone.getIntAreaCode());
	    
	    new Label(phoneComposite, SWT.LEFT);
	    
	    final Label al = new Label(phoneComposite, SWT.LEFT);
	    al.setText(this.m_i18n.getString(this.getNamespace(), "area", "label", this.m_language));

	    final Text areaCode = new Text(phoneComposite, SWT.BORDER);
	    areaCode.setSize(areaCode.getSize().x, 50);
	    areaCode.setText(this.m_phone.getAreaCode());
	    
      
	    intAreaCode.addModifyListener(new ModifyListener() {
	      public void modifyText(ModifyEvent event) {
	      	m_phone.setIntAreaCode(intAreaCode.getText());
	        setPageComplete(isComplete());
	      }
	    });
	    
	    areaCode.addModifyListener(new ModifyListener() {
	      public void modifyText(ModifyEvent event) {
	      	m_phone.setAreaCode(areaCode.getText());
	        setPageComplete(isComplete());
	      }
	    });

	    
	    setPageComplete(isComplete());
	    setControl(phoneComposite);
	}

	public boolean isComplete() {	
		if (!this.isValidIntAreaCode(this.m_phone.getIntAreaCode())) {
			setErrorMessage(this.m_i18n.getString(this.getNamespace(), "intareaerror", "label", this.m_language));
			return false;
		}
		
		if (!this.isValidAreaCode(this.m_phone.getAreaCode())) {
			setErrorMessage(this.m_i18n.getString(this.getNamespace(), "areaerror", "label", this.m_language));
			return false;
		}
		return super.isComplete();
	}
	
	private boolean isValidAreaCode(String areaCode) {
		areaCode = areaCode.trim();
		if (areaCode.startsWith("0")) {
			return false;
		}
		return true;
	}
	
	private boolean isValidIntAreaCode(String intAreaCode) {
		intAreaCode = intAreaCode.trim();
		if (intAreaCode.startsWith("0")) {
			return false;
		}
		return (this.isNumber(intAreaCode) && intAreaCode.length()>0 && intAreaCode.length()<5);
	}

	private boolean isNumber(String text) {
		for (int i=0;i<text.length();i++) {
			char c = text.charAt(i);
			switch (c) {
				case '0': break;
				case '1': break;
				case '2': break;
				case '3': break;
				case '4': break;
				case '5': break;
				case '6': break;
				case '7': break;
				case '8': break;
				case '9': break;
				default: return false;
			}
		}
		return true;
	}
	
	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}
}
