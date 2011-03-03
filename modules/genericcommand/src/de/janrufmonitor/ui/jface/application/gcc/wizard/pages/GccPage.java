package de.janrufmonitor.ui.jface.application.gcc.wizard.pages;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.controls.DirectoryFieldEditor;
import de.janrufmonitor.ui.jface.wizards.pages.AbstractPage;
import de.janrufmonitor.util.io.PathResolver;

public class GccPage extends AbstractPage {

	private String NAMESPACE = "ui.jface.application.gcc.wizard.pages.GccPage";
	
	private IRuntime m_runtime;
	
	private DirectoryFieldEditor dfe;
	private Text url;
	private Text name;
	private Button isJournal;
	private Button isEditor;
	private Button isDialog;
	
	public GccPage(String name) {
		super(GccPage.class.getName());
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));
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

	public void createControl(Composite c) {
		Composite co = new Composite(c, SWT.NONE);
		co.setLayout(new GridLayout(2, false));
		co.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    Label l = new Label(co, SWT.LEFT);
	    l.setText(this.m_i18n.getString(this.getNamespace(), "url", "label", this.m_language));

	    url = new Text(co, SWT.BORDER);
	    GridData gd = new GridData();
	    gd.widthHint = 300;
	    url.setLayoutData(gd);
	    url.setText("");
	    url.addModifyListener(new ModifyListener() {
	      public void modifyText(ModifyEvent event) {
	        setPageComplete(isComplete());
	      }
	    });
	    
	    l = new Label(co, SWT.LEFT);
	    l.setText(this.m_i18n.getString(this.getNamespace(), "name", "label", this.m_language));
 
	    name = new Text(co, SWT.BORDER);
	    name.setText("");
	    name.addModifyListener(new ModifyListener() {
	      public void modifyText(ModifyEvent event) {
	        setPageComplete(isComplete());
	      }
	    });
	    
	    isJournal = new Button(co, SWT.CHECK);
	    
	    gd = new GridData();
	    gd.horizontalSpan = 2;
	    isJournal.setLayoutData(gd);
	    isJournal.setText(this.m_i18n.getString(this.getNamespace(), "journal", "label", this.m_language));

	    isEditor = new Button(co, SWT.CHECK);
	    
	    gd = new GridData();
	    gd.horizontalSpan = 2;
	    isEditor.setLayoutData(gd);
	    isEditor.setText(this.m_i18n.getString(this.getNamespace(), "editor", "label", this.m_language));

	    isDialog = new Button(co, SWT.CHECK);
	    
	    gd = new GridData();
	    gd.horizontalSpan = 2;
	    isDialog.setLayoutData(gd);
	    isDialog.setText(this.m_i18n.getString(this.getNamespace(), "dialog", "label", this.m_language));

	    
	    l = new Label(co, SWT.LEFT);
	    l = new Label(co, SWT.LEFT);
	    
	    Composite dco = new Composite(co, SWT.NONE);
		dco.setLayout(new GridLayout(1, false));
		gd = new GridData();
	    gd.horizontalSpan = 2;
		dco.setLayoutData(gd);
	    
	    dfe = new DirectoryFieldEditor(
	    	this.m_i18n.getString(this.getNamespace(), "dname", "label", this.m_language),
	    	this.m_i18n.getString(this.getNamespace(), "dlabel", "label", this.m_language),
	    	this.m_i18n.getString(this.getNamespace(), "dmessage", "label", this.m_language),
			dco
	    );
	    dfe.setStringValue(PathResolver.getInstance().getTempDirectory());

	    this.setPageComplete(isComplete());
	    this.setControl(co);
	}
	
	public Map getResult() {
		Map m = new HashMap();
		m.put("url", url.getText());
		m.put("name", name.getText());
		m.put("journal", Boolean.valueOf(isJournal.getSelection()));
		m.put("editor", Boolean.valueOf(isEditor.getSelection()));
		m.put("dialog", Boolean.valueOf(isDialog.getSelection()));
		m.put("directory", dfe.getStringValue());
		return m;
	}

	protected boolean isComplete() {
		super.isComplete();
		if (url.getText().length()==0) return false;
		if (name.getText().length()==0) return false;
		if (dfe.getStringValue().length()==0) return false;

		return true;
	}
}
