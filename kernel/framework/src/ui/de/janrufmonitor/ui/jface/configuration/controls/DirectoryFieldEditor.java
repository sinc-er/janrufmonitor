package de.janrufmonitor.ui.jface.configuration.controls;

import java.io.File;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DirectoryFieldEditor extends FieldEditor {

	private Text m_textField;
    private boolean isValid;
    private String oldValue;
    private String m_message;
	
	public DirectoryFieldEditor(String name, String labelText, String message, Composite parent){
		super(name, labelText, parent);
		this.setLabelText(labelText);
		this.m_message = message;
	}
	
	protected void adjustForNumColumns(int numCols) {
	}
	
	public void addModifyListener(ModifyListener ml) {
		this.m_textField.addModifyListener(ml);
	}

	protected void doFillIntoGrid(Composite parent, int numCols) {

        GridData gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = numCols - 1;
        gd.widthHint = 200;
               
		Label l = new Label(parent, SWT.LEFT);
		l.setText(this.getLabelText());
		l.pack();
		
		l = new Label(parent, SWT.LEFT);
		l = new Label(parent, SWT.LEFT);
		
		m_textField = new Text(parent, SWT.SINGLE | SWT.BORDER);
		m_textField.setLayoutData(gd);
		
	    Button b = new Button(parent, SWT.PUSH);
		b.setText(JFaceResources.getString("openBrowse"));
		b.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt)
            {
                String newValue = changePressed();
                if(newValue != null)
                    setStringValue(newValue);
            }
        });
		
		b.pack();
	}

	
	protected String changePressed() {
		DirectoryDialog dlg = new DirectoryDialog(this.m_textField.getShell(), SWT.OPEN);
		dlg.setFilterPath(this.m_textField.getText());
		dlg.setMessage(this.m_message);
		String f = dlg.open();
		return f;
	}
	
    public void setStringValue(String value)
    {
        if(m_textField != null)
        {
            if(value == null)
                value = "";
            oldValue = m_textField.getText();
            if(!oldValue.equals(value))
            {
            	m_textField.setText(value);
                valueChanged();
            }
        }
    }
    
    public String getStringValue() {
    	 if(m_textField != null)
         {
             return m_textField.getText();
         }
    	 return "";
    }

	protected void doLoad() {
        if(m_textField != null)
        {
            String value = getPreferenceStore().getString(getPreferenceName());
            File f = new File(value);
            m_textField.setText(f.getAbsolutePath());
            this.oldValue = value;
        }
	}

	protected void doLoadDefault() {
        if(m_textField != null)
        {
            String value = getPreferenceStore().getDefaultString(getPreferenceName());
            File f = new File(value);
            m_textField.setText(f.getAbsolutePath());
        }
        valueChanged();
	}

    protected void doStore() {
        getPreferenceStore().setValue(getPreferenceName(), m_textField.getText());
    }

	public int getNumberOfControls() {
		return 3;
	}
	

    protected void valueChanged()
    {
        setPresentsDefaultValue(false);
        boolean oldState = isValid;
        refreshValidState();
        if(isValid != oldState)
            fireStateChanged("field_editor_is_valid", oldState, isValid);
        String newValue = m_textField.getText();
        if(!newValue.equals(oldValue))
        {
            fireValueChanged("field_editor_value", oldValue, newValue);
            oldValue = newValue;
        }
    }

}
