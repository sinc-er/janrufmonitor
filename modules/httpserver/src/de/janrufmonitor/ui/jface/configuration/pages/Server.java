package de.janrufmonitor.ui.jface.configuration.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.AbstractTableLabelProvider;
import de.janrufmonitor.ui.jface.configuration.AbstractConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;

import de.janrufmonitor.util.io.PathResolver;

public class Server extends AbstractConfigPage {
	
	private class ServerContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object o) {
			List l = (List)o;
			return l.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {	
		}
		
	}
	
	private class ServerLabelProvider implements ITableLabelProvider {

		private Image img;
		
		private Image getImage() {
			if (img==null) {
				img = new Image(Display.getCurrent(), PathResolver.getInstance(getRuntime()).getImageDirectory() + "clients.gif");
			}
			return img;
		}
		
		public Image getColumnImage(Object arg0, int arg1) {
			return this.getImage();
		}

		public String getColumnText(Object o, int column) {
			String s = (String)o;
			
		    switch (column) {
		    case 0:
		      return s;
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
	
	private class MsnContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object o) {
			List l = (List)o;
			return l.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {	
		}
		
	}
	
	private class MsnLabelProvider extends AbstractTableLabelProvider {
		public String getColumnText(Object o, int column) {
			String msn = (String)o;
			
		    switch (column) {
		    case 0:
		      return msn + " (" +getRuntime().getMsnManager().getMsnLabel(msn)+")";
		    }
			return null;
		}
	}
	
	
    private String NAMESPACE = "ui.jface.configuration.pages.Server";
    private String CONFIG_NAMESPACE_1 = "service.Server";
    private String CONFIG_NAMESPACE_2 = "server.security.SecurityManager";
    
	private IRuntime m_runtime;
	private TableViewer tv;
	private Button active;
	private Button browser;
	private Text port;
	private List dataList;

	public String getParentNodeID() {
		return IConfigPage.SERVICE_NODE;
	}
	
	public String getNodeID() {
		return "Server".toLowerCase();
	}

	public int getNodePosition() {
		return 10;
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
		
		Composite c2 = new Composite(c, SWT.NONE);
		c2.setLayout(new GridLayout(2, false));
		
		new Label(c2, SWT.NONE).setText(
			this.m_i18n.getString(this.getNamespace(), "port", "label", this.m_language)
		);
		
		port = new Text(c2, SWT.BORDER);
		GridData gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.widthHint = 40;
        port.setLayoutData(gd);
        port.setTextLimit(5);
        port.setText(
        	this.getPreferenceStore().getString(this.getConfigNamespace()+SEPARATOR+"port")
        );
		
		Composite c1 = new Composite(c, SWT.NONE);
		c1.setLayout(new GridLayout(3, false));
		
		Composite c11 = new Composite(c1, SWT.NONE);
		c11.setLayout(new GridLayout(1, false));
		
		new Label(c11, SWT.NONE).setText(
				this.m_i18n.getString(this.getNamespace(), "allowed", "label", this.m_language)
			); 
		
		tv = new TableViewer(c11);
		tv.setContentProvider(new ServerContentProvider());
		tv.setLabelProvider(new ServerLabelProvider());
		
		Table t = tv.getTable();
		gd = new GridData();
        gd.horizontalAlignment = GridData.FILL_BOTH;
        gd.grabExcessHorizontalSpace = true;
        gd.widthHint = 150;
        gd.heightHint = 200;
		
		t.setLayoutData(gd);
		TableColumn tc = new TableColumn(t, SWT.LEFT);
		tc.setText(this.m_i18n.getString(this.getNamespace(), "alias", "label", this.m_language));
		
		tv.setInput(this.getDataList());
		
	    for (int i = 0, n = t.getColumnCount(); i < n; i++) {
	      t.getColumn(i).pack();
	    }

	    t.setHeaderVisible(false);
	    t.setLinesVisible(false);
	    
	    Composite c12 = new Composite(c1, SWT.NONE);
		c12.setLayout(new GridLayout(1, false));

		Button add = new Button(c12, SWT.PUSH);
		add.setText(
			this.m_i18n.getString(this.getNamespace(), "add", "label", this.m_language)
		);
		
		Button remove = new Button(c12, SWT.PUSH);
		remove.setText(
			this.m_i18n.getString(this.getNamespace(), "remove", "label", this.m_language)
		);
		
		Composite c13 = new Composite(c1, SWT.NONE);
		c13.setLayout(new GridLayout(1, false));
		
		new Label(c13, SWT.NONE).setText(
			this.m_i18n.getString(this.getNamespace(), "edit", "label", this.m_language)
		); 
		
		final Text ip = new Text(c13, SWT.BORDER);
		gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.widthHint = 150;
        ip.setLayoutData(gd);
        ip.setText("0.0.0.0");
        
        Label l = new Label(c13, SWT.NONE);
        l.setText(
			this.m_i18n.getString(this.getNamespace(), "msnlimit", "label", this.m_language)
		); 
        l.setVisible(getMsns().size()>0);
        
        final CheckboxTableViewer cselect = CheckboxTableViewer.newCheckList(c13, SWT.CHECK | SWT.BORDER);
	    cselect.setLabelProvider(new MsnLabelProvider());
	    cselect.setContentProvider(new MsnContentProvider());
	    cselect.getTable().setVisible(getMsns().size()>0);
	    
	    List selected = this.getMsns();
		cselect.setInput(selected);
		
		this.browser = new Button(c1, SWT.CHECK);
		this.browser.setText(
				this.m_i18n.getString(this.getNamespace(), "browser", "label", this.m_language)
		);
		this.browser.setSelection(
			this.getPreferenceStore().getBoolean(this.CONFIG_NAMESPACE_2+SEPARATOR+"browser")
		);
		
		
		add.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (ip.getText().length()>0 && !dataList.contains(ip.getText())){
						StringBuffer data = new StringBuffer(ip.getText());
						Object[] msns = cselect.getCheckedElements();
						if (msns!=null && msns.length>0) {
							data.append(" - ");
							for (int i=0;i<msns.length;i++){
								data.append(msns[i]);
								if (i<msns.length-1) data.append("/");
							}
						}
						
						dataList.add(data.toString());
						tv.setInput(dataList);
					}
				}
			}
		);
		
		remove.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					StructuredSelection s = (StructuredSelection) tv.getSelection();
					if (!s.isEmpty()) {
						String o = (String)s.getFirstElement();
						cselect.setAllChecked(false);
						if (o.indexOf(" -")==-1) {					
							ip.setText(o);
						}
						else {
							ip.setText(o.substring(0, o.indexOf(" -")));
							String msns = o.substring(o.indexOf(" -")+2, o.length()).trim();
							String[] m = msns.split("/");
							for (int i=0;i<m.length;i++)
								cselect.setChecked(m[i], true);
						}
						dataList.remove(o);
						tv.setInput(dataList);
					}
				}
			}				
		);
        
		return c;
	}

	private List getMsns() {
		String[] msns = getRuntime().getMsnManager().getMsnList();
		List l = new ArrayList();
		for (int i=0;i<msns.length;i++) {
			l.add(msns[i]);
		}
		return l;
	}

	private List getDataList() {
		if (this.dataList==null || this.dataList.size()==0) {
			this.dataList = new ArrayList();
			String allowedList = this.getPreferenceStore().getString(this.CONFIG_NAMESPACE_2 + SEPARATOR + "allowed");
			if (allowedList.trim().length()>0) {
				StringTokenizer st = new StringTokenizer(allowedList, ",");
				while (st.hasMoreTokens()) {
					String ip = st.nextToken().trim();
					if (ip.length()>0) {
						this.dataList.add(ip);
					}
				}
			}
		}
		return this.dataList;
	}
	
	private List getDefaultDataList() {
		if (this.dataList==null || this.dataList.size()==0) {
			this.dataList = new ArrayList();
			String allowedList = this.getPreferenceStore().getDefaultString(this.CONFIG_NAMESPACE_2 + SEPARATOR + "allowed");
			if (allowedList.trim().length()>0) {
				StringTokenizer st = new StringTokenizer(allowedList, ",");
				while (st.hasMoreTokens()) {
					String ip = st.nextToken().trim();
					if (ip.length()>0) {
						this.dataList.add(ip);
					}
				}
			}
		}
		return this.dataList;
	}
	
	public String getConfigNamespace() {
		return this.CONFIG_NAMESPACE_1;
	}
	
	public boolean performOk() {
		this.performApply();
		this.dataList.clear();

		boolean ok = super.performOk();
		
		if (ok && this.getConfigNamespace().length()>0)
			this.getRuntime().getConfigurableNotifier().notifyByNamespace(CONFIG_NAMESPACE_2);
		
		return ok;
	}
	
	protected void performApply() {
		StringBuffer list = new StringBuffer();
		for (int i=0;i<this.dataList.size();i++) {
			String m = (String)this.dataList.get(i);
			list.append(m);
			list.append(",");
		}
		this.getPreferenceStore().setValue(this.CONFIG_NAMESPACE_2 + SEPARATOR + "allowed", list.toString());
		this.getPreferenceStore().setValue(this.getConfigNamespace()+SEPARATOR + "enabled", active.getSelection());
		this.getPreferenceStore().setValue(this.CONFIG_NAMESPACE_2 + SEPARATOR + "browser", browser.getSelection());
		this.getPreferenceStore().setValue(this.getConfigNamespace()+SEPARATOR + "port", port.getText());
	}
	
	protected void performDefaults() {
		super.performDefaults();
		this.dataList.clear();
		active.setSelection(this.getPreferenceStore().getDefaultBoolean(this.getConfigNamespace()+SEPARATOR + "enabled"));
		browser.setSelection(this.getPreferenceStore().getDefaultBoolean(this.CONFIG_NAMESPACE_2 + SEPARATOR + "browser"));
		port.setText(this.getPreferenceStore().getDefaultString(this.getConfigNamespace()+SEPARATOR + "port"));
		
		this.dataList = this.getDefaultDataList();
		tv.setInput(this.dataList);
	}
}
