package de.janrufmonitor.ui.jface.application.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.AbstractTableLabelProvider;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.IConfigConst;
import de.janrufmonitor.ui.jface.application.RendererRegistry;
import de.janrufmonitor.ui.jface.application.rendering.ITableCellRenderer;
import de.janrufmonitor.ui.swt.DisplayManager;

public class ColumnSelectAction extends AbstractAction implements IConfigConst {

	private class ColumnContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object o) {
			List l = (List)o;
			return l.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {	
		}
		
	}
	
	private class ColumnLabelProvider extends AbstractTableLabelProvider {

		public String getColumnText(Object o, int column) {
			String col = (String)o;
			
		    switch (column) {
		    case 0: {
		    	ITableCellRenderer tr = RendererRegistry.getInstance().getRenderer(col);
		    	if (tr!=null) return tr.getLabel();
		    	return "";
		    	}
		    }
			return null;
		}
		
	}
	
	private class ColumnSelectDialog extends TitleAreaDialog {		
		
		private String m_rm;
				
		public ColumnSelectDialog(Shell shell) {
			super(shell);
		}
		
		public String getColumns() {
			return this.m_rm;
		}
		
		public void setColumns(String cols) {
			this.m_rm = cols;
		}
		
		protected Control createContents(Composite parent) {
			Control c = super.createContents(parent);
			setTitle(
				getI18nManager().getString(
					getNamespace(),
					"dialogtitle",
					"label",
					getLanguage()
				)
			);
			setMessage(getI18nManager().getString(
					getNamespace(),
					"dialogtitle",
					"description",
					getLanguage()
				));
			return c;
		}

		protected Control createDialogArea(Composite parent) {
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout(3, false));
			
			new Label(composite, SWT.NONE).setText(
				getI18nManager().getString(
					getNamespace(),
					"current",
					"label",
					getLanguage()
				)		
			);
			
			new Label(composite, SWT.NONE);
			
			new Label(composite, SWT.NONE).setText(
				getI18nManager().getString(
					getNamespace(),
					"available",
					"label",
					getLanguage()
				)		
			);
			
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.widthHint = 210;
			gd.heightHint = 350;
			
		    final TableViewer current = new TableViewer(composite, SWT.BORDER | SWT.V_SCROLL);
		    current.setLabelProvider(new ColumnLabelProvider());
		    current.setContentProvider(new ColumnContentProvider());
			current.getTable().setLayoutData(gd);
		    List columns = this.getCurrentColumns();
			current.setInput(columns);
			
			Composite buttonsArea = new Composite(composite, SWT.NONE);
			buttonsArea.setLayout(new GridLayout(1, false));

			gd = new GridData(GridData.FILL_BOTH);
		//	gd.widthHint = 80;
			Button add = new Button(buttonsArea, SWT.PUSH);
			add.setLayoutData(gd);
			add.setText(
				getI18nManager().getString(
					getNamespace(),
					"add",
					"label",
					getLanguage()
				)				
			);
			
			Button remove = new Button(buttonsArea, SWT.PUSH);
			remove.setLayoutData(gd);
			remove.setText(
				getI18nManager().getString(
					getNamespace(),
					"remove",
					"label",
					getLanguage()
				)						
			);
			
			gd = new GridData(GridData.FILL_BOTH);
			gd.widthHint = 210;
			gd.heightHint = 350;
			final TableViewer available = new TableViewer(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
			available.setLabelProvider(new ColumnLabelProvider());
			available.setContentProvider(new ColumnContentProvider());
			available.getTable().setLayoutData(gd);
			columns = this.getAllColumns();
			available.setInput(columns);
			Composite buttonsArea2 = new Composite(composite, SWT.NONE);
			buttonsArea2.setLayout(new GridLayout(2, false));

			Button up = new Button(buttonsArea2, SWT.PUSH);
			up.setText(
				getI18nManager().getString(
					getNamespace(),
					"up",
					"label",
					getLanguage()
				)						
			);
			
			up.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {						
						IStructuredSelection s = (IStructuredSelection) current.getSelection();
						if (!s.isEmpty()) {
							List currentCols = getCurrentColumns();
							String move_col = (String) s.getFirstElement();
							
							int pos = -1;
							for (int i=0;i<currentCols.size();i++) {
								if (move_col.equalsIgnoreCase((String) currentCols.get(i))) pos = i-1;
							}
							currentCols.remove(move_col);
							currentCols.add(Math.max(0, pos), move_col);
							
							toCurrentColumns(currentCols);
							current.setInput(currentCols);							
						}
					}
				}	
			);
		
			Button down = new Button(buttonsArea2, SWT.PUSH);
			down.setText(
				getI18nManager().getString(
					getNamespace(),
					"down",
					"label",
					getLanguage()
				)						
			);
			down.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {						
						IStructuredSelection s = (IStructuredSelection) current.getSelection();
						if (!s.isEmpty()) {
							List currentCols = getCurrentColumns();
							String move_col = (String) s.getFirstElement();
							
							int pos = -1;
							for (int i=0;i<currentCols.size();i++) {
								if (move_col.equalsIgnoreCase((String) currentCols.get(i))) pos = i+1;
							}
							currentCols.remove(move_col);
							currentCols.add(Math.min(currentCols.size(), pos), move_col);
							
							toCurrentColumns(currentCols);
							current.setInput(currentCols);							
						}
					}
				}	
			);
			
			remove.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {		
						IStructuredSelection s = (IStructuredSelection) current.getSelection();
						if (!s.isEmpty()) {
							List currentCols = getCurrentColumns();
							String remove_col = (String) s.getFirstElement();
							if (currentCols.contains(remove_col)) {
								currentCols.remove(remove_col);
								toCurrentColumns(currentCols);
								current.setInput(currentCols);
							}
						}						
					}
				}	
			);
			
			add.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						IStructuredSelection s = (IStructuredSelection) available.getSelection();
						if (!s.isEmpty()) {
							List currentCols = getCurrentColumns();
							String add_col = (String) s.getFirstElement();
							if (!currentCols.contains(add_col)) {
								currentCols.add(add_col);
								toCurrentColumns(currentCols);
								current.setInput(currentCols);
							}
						}		
					}
				}	
			);
			
			return super.createDialogArea(parent);
		}

		private List getAllColumns() {
			List rlist = RendererRegistry.getInstance().getAllRendererIDsForApplication(m_app.getApplication());
			Collections.sort(rlist, new Comparator() {

				public int compare(Object r1, Object r2) {
					ITableCellRenderer tr1 = RendererRegistry.getInstance().getRenderer((String) r1);
					ITableCellRenderer tr2 = RendererRegistry.getInstance().getRenderer((String) r2);
					if (tr1!=null && tr2!=null) {
						return tr1.getLabel().toLowerCase().compareTo(tr2.getLabel().toLowerCase());
					}					
					return 0;
				}});
			return rlist;
		}

		private List getCurrentColumns() {
			List l = new ArrayList();
			StringTokenizer st = new StringTokenizer(this.m_rm, ",");
			while (st.hasMoreTokens()) {
				l.add(st.nextToken());
			}
			return l;
		}
		
		private void toCurrentColumns(List l) {
			this.m_rm = "";
			if (l==null || l.size()==0)  {
				return;
			}
			for (int i=0;i<l.size();i++) {
				this.m_rm += l.get(i);
				if (i+1<l.size()) this.m_rm += ",";
			}
		}
		
	}
	
	private static String NAMESPACE = "ui.jface.application.action.ColumnSelectAction";
	
	private IRuntime m_runtime;

	public ColumnSelectAction() {
		super();
		this.setText(
			this.getI18nManager().getString(
				this.getNamespace(),
				"title",
				"label",
				this.getLanguage()
			)
		);
	}
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return "columnselect";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		ColumnSelectDialog mcd = new ColumnSelectDialog(new Shell(DisplayManager.getDefaultDisplay()));
		mcd.setColumns(
			this.m_app.getApplication().getConfiguration().getProperty(
				CFG_RENDERER_LIST, ""
			)		
		);
		int ok = mcd.open();
		if (ok==ColumnSelectDialog.OK) {
			String rep = mcd.getColumns();
			this.m_app.getApplication().getConfiguration().setProperty(
				CFG_RENDERER_LIST, rep
			);
			this.m_app.getApplication().storeConfiguration();
			this.m_app.getApplication().updateProviders();
			this.m_app.updateViews(false);
		}
	}
}
