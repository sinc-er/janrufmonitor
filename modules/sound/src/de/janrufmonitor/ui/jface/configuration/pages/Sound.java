package de.janrufmonitor.ui.jface.configuration.pages;

import java.io.File;
import java.util.*;
import java.util.List;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;
import de.janrufmonitor.ui.jface.wizards.AbstractWizard;
import de.janrufmonitor.ui.jface.wizards.pages.AbstractPage;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTImageManager;

public class Sound extends AbstractConfigPage {
	
	private class SoundDataWizard extends AbstractWizard {

		private String NAMESPACE = "ui.jface.wizards.SoundDataWizard"; 
		
		private SoundDataObject m_sdo;
		private AbstractPage[] m_pages;
		private IRuntime m_runtime;

		public SoundDataWizard(SoundDataObject sdo) {
			super();
	    	setWindowTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));

			this.m_sdo = sdo;
			
			this.m_pages = new AbstractPage[2];
			this.m_pages[0] = new SelectMsnPage(this.m_sdo.getMSN());
			this.m_pages[1] = new SelectFilePage(this.m_sdo.getFilename());
			
			this.addPage(this.m_pages[0]);
			this.addPage(this.m_pages[1]);
		}

		public String getID() {
			return SoundDataWizard.class.getName();
		}

		public String getNamespace() {
			return this.NAMESPACE;
		}

		public boolean performFinish() {
			if (this.m_pages[0].isPageComplete() && 
				this.m_pages[1].isPageComplete()) {
				IMsn m = ((SelectMsnPage)this.m_pages[0]).getResult();
				String file = ((SelectFilePage)this.m_pages[1]).getResult();
				this.m_sdo = new SoundDataObject(m, file);
				return true;
			}
			return false;
		}

		public SoundDataObject getResult() {
			return this.m_sdo;
		}
		
		public boolean performCancel() {
			this.m_sdo = null;
			return super.performCancel();
		}

		protected IRuntime getRuntime() {
			if (this.m_runtime==null) {
				this.m_runtime = PIMRuntime.getInstance();
			}
			return this.m_runtime;
		}
	}

	public class SelectMsnPage extends AbstractPage {

		private String NAMESPACE = "ui.jface.wizards.pages.sound.SelectMsnPage";

		private IMsn m_msn;
		private IRuntime m_runtime;
		
		public SelectMsnPage(IMsn msn) {
			super(SelectMsnPage.class.getName());
			if (msn==null)
				msn = PIMRuntime.getInstance().getCallFactory().createMsn("","");
			
			this.m_msn = msn;
			
			setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
			setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));
		}

		public String getNamespace() {
			return this.NAMESPACE;
		}
		
		public IMsn getResult() {
			return this.m_msn;
		}

		public void createControl(Composite parent) {
		    Composite nameComposite = new Composite(parent, SWT.NONE);
		    nameComposite.setLayout(new GridLayout(1, false));
		    nameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		    Label ln = new Label(nameComposite, SWT.LEFT);
		    ln.setText(this.m_i18n.getString(this.getNamespace(), "name", "label", this.m_language));

			final Combo msn = new Combo (nameComposite, SWT.READ_ONLY);
			
			String[] msns = PIMRuntime.getInstance().getMsnManager().getMsnList();
			String[] msnList = new String[msns.length];

			int select = 0;
			for (int i=0;i<msns.length;i++) {
				String msnalias = msns[i] + " ("+PIMRuntime.getInstance().getMsnManager().getMsnLabel(msns[i]) +")";
				msnList[i] = msnalias;
				msn.setData(msnalias, msns[i]);
				if (msns[i].equalsIgnoreCase(this.m_msn.getMSN())) {
					select=i;	
				}
			}
			msn.setItems(msnList);
			msn.select(select);
			
			if (msn.getSelectionIndex()>=0) {
				String smsn = msn.getItem(msn.getSelectionIndex());
		      	smsn = (String) msn.getData(smsn);
		      	
		        this.m_msn = PIMRuntime.getInstance().getMsnManager().createMsn(smsn);
			}
			
		    // Add the handler to update the name based on input
			msn.addModifyListener(new ModifyListener() {
		      public void modifyText(ModifyEvent event) {
		      	if (msn.getSelectionIndex()>=0) {
			      	String smsn = msn.getItem(msn.getSelectionIndex());
			      	smsn = (String) msn.getData(smsn);
			      	
			        m_msn = PIMRuntime.getInstance().getMsnManager().createMsn(smsn);
		      	}
		        setPageComplete(isComplete());
		      }
		    });
		    
		    setPageComplete(isComplete());
		    setControl(nameComposite);
		}
		
		protected boolean isComplete() {
			return (this.m_msn.getMSN().length()>0);
		}
		
		protected IRuntime getRuntime() {
			if (this.m_runtime==null) {
				this.m_runtime = PIMRuntime.getInstance();
			}
			return this.m_runtime;
		}
	}

	public class SelectFilePage extends AbstractPage {

		private String NAMESPACE = "ui.jface.wizards.pages.sound.SelectFilePage";

		private String file;
		private IRuntime m_runtime;
		
		public SelectFilePage(String file) {
			super(SelectMsnPage.class.getName());
			if (file==null)
				file = "";
			
			this.file = file;
			
			setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
			setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));
		}

		public String getNamespace() {
			return this.NAMESPACE;
		}
		
		public String getResult() {
			return this.file;
		}

		public void createControl(Composite parent) {
		    Composite nameComposite = new Composite(parent, SWT.NONE);
		    nameComposite.setLayout(new GridLayout(2, false));
		    nameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		    GridData gd = new GridData();
	        gd.horizontalAlignment = GridData.FILL;
	        gd.grabExcessHorizontalSpace = true;
	        gd.horizontalSpan = 1;
	        gd.widthHint = 200;
	               
			Label l = new Label(nameComposite, SWT.LEFT);
			l.setText(
					this.m_i18n.getString(getNamespace(), "sound", "label", this.m_language)	
			);
			l.pack();
			
			l = new Label(nameComposite, SWT.LEFT);
				
			final Text m_textField = new Text(nameComposite, SWT.SINGLE | SWT.BORDER);
			m_textField.setLayoutData(gd);
			m_textField.setText(file);
			
		    Button b = new Button(nameComposite, SWT.PUSH);
			b.setText(JFaceResources.getString("openBrowse"));
			b.addSelectionListener(new SelectionAdapter() {
	            public void widgetSelected(SelectionEvent evt) {
	                String newValue = changePressed(m_textField);
	                if(newValue != null) {
	                	m_textField.setText(newValue);
	                	file = newValue;
	                }
	                setPageComplete(isComplete());
	            }
	        });
			
			b.pack();
		    
		    setPageComplete(isComplete());
		    setControl(nameComposite);
		}
		
		protected boolean isComplete() {
			return (file.length()>0);
		}
		
		protected String changePressed(Text t) {
			FileDialog dlg = new FileDialog(t.getShell(), SWT.OPEN);
			dlg.setFilterExtensions(new String[] {"*.wav", "*.mp3"});
			dlg.setFileName(t.getText());
			String f = dlg.open();
			return f;
		}
		
		protected IRuntime getRuntime() {
			if (this.m_runtime==null) {
				this.m_runtime = PIMRuntime.getInstance();
			}
			return this.m_runtime;
		}
	}
	
	private class FileDialogFieldEditor extends FieldEditor {

		private Text m_textField;
	    private boolean isValid;
	    private String oldValue;
	    private String[] m_ext;
	    private int m_style;
		
		public FileDialogFieldEditor(String name, String labelText, Composite parent){
			this(name, labelText, parent, SWT.SAVE);
		}
		
		public FileDialogFieldEditor(String name, String labelText, Composite parent, int dialogStyle){
			super(name, labelText, parent);
			this.setLabelText(labelText);
			this.m_style = dialogStyle;
		}
		
		public String getValue() {
			return this.m_textField.getText();
		}
		
		protected void adjustForNumColumns(int numCols) {
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
		
		public void setFileExtensions(String[] exts) {
			this.m_ext = exts;
		}
		
		protected String changePressed() {
			FileDialog dlg = new FileDialog(this.m_textField.getShell(), this.m_style);
			dlg.setFilterExtensions(this.m_ext);
			dlg.setFileName(this.m_textField.getText());
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

		public void doLoad() {
	        if(m_textField != null)
	        {
	            String value = getPreferenceStore().getString(getPreferenceName());
	            File f = new File(value);
	            m_textField.setText(f.getAbsolutePath());
	            this.oldValue = value;
	        }
		}

		public void doLoadDefault() {
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

	private class SoundDataObject  {
		
		private IMsn m;
		private String f;		
		
		public SoundDataObject(IMsn msn, String filename) {
			this.m = msn;
			this.f = filename;
		}
		
		public String getFilename(){
			return this.f;
		}
		
		public IMsn getMSN(){
			return this.m;
		}
		
		public boolean equals(Object o) {
			if (o !=null && o instanceof SoundDataObject) {
				return (((SoundDataObject)o).m.equals(m) && ((SoundDataObject)o).f.equals(f)); 
			}
			return false;
		}
		public int hashCode() {
			return m.hashCode() + f.hashCode();
		}
	}
	
	private class SoundContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object o) {
			List l = (List)o;
			return l.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {	
		}
		
	}
	
	private class SoundLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object arg0, int arg1) {
			return null;
		}

		public String getColumnText(Object o, int column) {
			SoundDataObject s = (SoundDataObject)o;
			
		    switch (column) {
		    case 0:
		      return s.getMSN().getMSN();
		    case 1:
		      return s.getFilename();
		    }
			return null;
		}

		public void addListener(ILabelProviderListener arg0) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object arg0, String arg1) {
			return false;
		}

		public void removeListener(ILabelProviderListener arg0) {
		}
		
	}
	
	private class SoundViewerSorter extends ViewerSorter {
		private static final int ASCENDING = 0;
		private static final int DESCENDING = 1;
	
		private int column;
		private int direction;
	
		public void doSort(int column) {
			if (column == this.column) {
				direction = 1 - direction;
			} else {
				this.column = column;
				direction = ASCENDING;
			}
		}
		
		public int compare(Viewer viewer, Object o1, Object o2) {
			int rc = 0;
			IMsn msn1 = ((SoundDataObject)o1).getMSN();
			IMsn msn2 = ((SoundDataObject)o2).getMSN();
			
		    switch (column) {
		    case 0:
		      rc = getComparator().compare(msn1.getMSN(), msn2.getMSN());
		      break;
		    case 1:
		      rc = getComparator().compare(msn1.getAdditional(), msn2.getAdditional());
		      break;
		    }
		    
		    if (direction == DESCENDING) rc = -rc;

		    return rc;
		}
	}
	
    private String NAMESPACE = "ui.jface.configuration.pages.Sound";
    private String CONFIG_NAMESPACE = "service.Sound";
    
	private IRuntime m_runtime;
	private TableViewer tv;
	private Button active;
	private FileDialogFieldEditor fd;
	private List dataList;
	private List removableData;

	public String getParentNodeID() {
		return IConfigPage.SERVICE_NODE;
	}
	
	public String getNodeID() {
		return "Sound".toLowerCase();
	}

	public int getNodePosition() {
		return 25;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null) 
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	protected Control createContents(Composite parent) {
		this.setTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));
		//this.noDefaultAndApplyButton();
		
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1, false));
	
		String label = this.m_i18n.getString(this.getNamespace(), "enabled", "label", this.m_language);
		if (label.length()<150)
			for (int i=150;i>label.length();i--){
				label += " ";
			}
		
		this.active = new Button(c, SWT.CHECK);
		this.active.setText(
			label
		);
		this.active.setSelection(
			this.getPreferenceStore().getBoolean(this.getConfigNamespace()+SEPARATOR+"enabled")
		);
		
		Composite c1 = new Composite(c, SWT.NONE);
		c1.setLayout(new GridLayout(2, false));
		
	    fd = new FileDialogFieldEditor(
				this.getConfigNamespace()+SEPARATOR+"default_soundfile",
				this.m_i18n.getString(this.getNamespace(), "default_soundfile", "label", this.m_language),
				c1,
				SWT.OPEN
		);
		fd.setFileExtensions(new String[] {"*.wav", "*.mp3"});
		fd.setPreferenceStore(this.getPreferenceStore());
		fd.doLoad();
		
		
		tv = new TableViewer(c);
		tv.setSorter(new SoundViewerSorter());
		tv.setContentProvider(new SoundContentProvider());
		tv.setLabelProvider(new SoundLabelProvider());
		
		Table t = tv.getTable();
		t.setLayoutData(new GridData(GridData.FILL_BOTH));
		TableColumn tc = new TableColumn(t, SWT.LEFT);
		tc.setText(this.m_i18n.getString(this.getNamespace(), "msn", "label", this.m_language));
		tc.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        ((SoundViewerSorter) tv.getSorter())
	            .doSort(0);
	        tv.refresh();
	      }
	    });
		
		tc = new TableColumn(t, SWT.LEFT);
		tc.setText(this.m_i18n.getString(this.getNamespace(), "alias", "label", this.m_language));
		tc.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        ((SoundViewerSorter) tv.getSorter())
	            .doSort(1);
	        tv.refresh();
	      }
	    });
		
		tv.setInput(this.getDataList());
		
	    for (int i = 0, n = t.getColumnCount(); i < n; i++) {
	      t.getColumn(i).pack();
	    }

	    t.setHeaderVisible(true);
	    t.setLinesVisible(true);	
	    this.createPopupMenu();
	    
		return c;
	}
	
	private void createPopupMenu() {
		final Table t = tv.getTable();
		
		Menu popUpMenu = new Menu (t.getShell(), SWT.POP_UP);
		
		MenuItem item = new MenuItem (popUpMenu, SWT.PUSH);
		item.setText (this.m_i18n.getString(this.getNamespace(), "add", "label", this.m_language));
		item.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					openDataWizard("", "");
				}
			}
		);		
		item = new MenuItem (popUpMenu, SWT.PUSH);
		item.setText (this.m_i18n.getString(this.getNamespace(), "remove", "label", this.m_language));
		item.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (t.getSelectionCount()>0) {
						TableItem i = t.getSelection()[0];
						removeSoundDataObject(i.getText(0), i.getText(1));
					}
				}
			}
		);		
		item = new MenuItem (popUpMenu, SWT.PUSH);
		item.setText (this.m_i18n.getString(this.getNamespace(), "edit", "label", this.m_language));
		item.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (t.getSelectionCount()>0) {
						TableItem i = t.getSelection()[0];
						openDataWizard(i.getText(0), i.getText(1));
					}
				}
			}
		);		
		t.setMenu(popUpMenu);
		
		t.addMouseListener(
			new MouseAdapter() {
				public void mouseDoubleClick(MouseEvent e) {
					if ((e.widget instanceof Table)) {
						if (t.getSelectionCount()>0) {
							if (t.getSelectionCount()>0) {
								TableItem i = ((Table) e.widget).getSelection()[0];
								openDataWizard(i.getText(0), i.getText(1));
							}
						}
						if (t.getSelectionCount()==0) {
							openDataWizard("", "");
						}						
					}
				}
			}
		);
		
		t.addKeyListener(
			new KeyAdapter() {
				public void keyPressed(KeyEvent e){
					if (e.character == SWT.DEL) {
						if (t.getSelectionCount()>0) {
							TableItem i = t.getSelection()[0];
							removeSoundDataObject(i.getText(0), i.getText(1));
						}
					}
				}	
			}		
		);

	}
	
	private void removeSoundDataObject(String msn, String file) {
		IMsn oldMsn = this.getRuntime().getMsnManager().createMsn(msn);
		
		SoundDataObject sdo = new SoundDataObject(oldMsn, file);
		
		this.dataList.remove(sdo);
		this.removableData.add(sdo);
		tv.setInput(this.dataList);
	}
	
	private void openDataWizard(String msn, String file) {
	    Display display = DisplayManager.getDefaultDisplay();
		Shell shell = new Shell(display);

	    // Create the dialog
		IMsn oldMsn = this.getRuntime().getMsnManager().createMsn(msn);
		SoundDataObject oldSdo = new  SoundDataObject(oldMsn, file);
	    WizardDialog.setDefaultImage(SWTImageManager.getInstance(this.getRuntime()).get(IJAMConst.IMAGE_KEY_PIM_ICON));
	    SoundDataWizard sdoW = new SoundDataWizard(oldSdo);
	    WizardDialog dlg = new WizardDialog(shell, sdoW);
	    dlg.open();
	    if (dlg.getReturnCode() == WizardDialog.OK) {
	    	SoundDataObject result = sdoW.getResult();
	    	this.dataList.remove(oldSdo);
	    	this.dataList.add(result);
	    }
	    tv.setInput(this.dataList);
	}
	
	private List getDataList() {
		if (this.dataList==null || this.dataList.size()==0) {
			this.dataList = new ArrayList();
			this.removableData = new ArrayList();
			String msnList = this.getPreferenceStore().getString(this.CONFIG_NAMESPACE + SEPARATOR + "list");
			if (msnList.trim().length()>0) {
				StringTokenizer st = new StringTokenizer(msnList, ",");
				while (st.hasMoreTokens()) {
					String number = st.nextToken().trim();
					if (number.length()>0) {
						IMsn msn = this.getRuntime().getMsnManager().createMsn(number);
						String soundfile = this.getPreferenceStore().getString(this.CONFIG_NAMESPACE + SEPARATOR + number+ "_soundfile");
						if (soundfile!=null && soundfile.length()>0) {
							SoundDataObject sdo = new SoundDataObject(msn, soundfile);
							this.dataList.add(sdo);
						}
					}
				}
			}
		}
		return this.dataList;
	}
	
	public String getConfigNamespace() {
		return this.CONFIG_NAMESPACE;
	}
	
	public boolean performOk() {
		this.performApply();
		
		this.dataList.clear();
		this.removableData.clear();
		
		return super.performOk();
	}
	
	protected void performApply() {
		for (int i=0;i<this.removableData.size();i++) {
			SoundDataObject m = (SoundDataObject)this.removableData.get(i);
			this.getRuntime().getConfigManagerFactory().getConfigManager().removeProperty(this.CONFIG_NAMESPACE, m.getMSN().getMSN()+"_soundfile");
		}
		
		StringBuffer list = new StringBuffer();
		for (int i=0;i<this.dataList.size();i++) {
			SoundDataObject m = (SoundDataObject)this.dataList.get(i);
			this.getPreferenceStore().setValue(this.CONFIG_NAMESPACE+SEPARATOR+m.getMSN().getMSN()+"_soundfile", m.getFilename());
			list.append(m.getMSN().getMSN());
			list.append(",");
		}
		this.getPreferenceStore().setValue(this.CONFIG_NAMESPACE + SEPARATOR + "list", list.toString());
		fd.doStore();
		this.getPreferenceStore().setValue(this.getConfigNamespace()+SEPARATOR+"enabled", active.getSelection());
	}
}
