package de.janrufmonitor.ui.jface.application.journal.action;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.journal.JournalConfigConst;
import de.janrufmonitor.ui.swt.DisplayManager;

public class MsnColorSelectAction extends AbstractAction implements JournalConfigConst {

	private class MsnColorDialog extends TitleAreaDialog {		
		
		private String m_colors;
		private Table msnTable;
			
		public MsnColorDialog(Shell shell) {
			super(shell);
		}
		
		public String getColors() {
			return this.m_colors;
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
			composite.setLayout(new GridLayout(1, false));
			
			new Label(composite, SWT.NONE).setText(
				getI18nManager().getString(
					getNamespace(),
					"msncolor",
					"label",
					getLanguage()
				)		
			);
			
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.heightHint = 80;
			gd.widthHint = 400;
			this.msnTable = new Table(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
			msnTable.setLinesVisible(true);
			msnTable.setHeaderVisible(true);
			msnTable.setLayoutData(gd);
			
			String[] titles = {
				getI18nManager().getString(getNamespace(), "msn", "label", getLanguage())
			};
			for (int i=0; i<titles.length; i++) {
				TableColumn	column = new TableColumn (msnTable, SWT.LEFT);
				column.setText(titles [i]);
			}
		
			String[] msns = getRuntime().getMsnManager().getMsnList();
			for (int i=0;i<msns.length;i++) {
				TableItem item = new TableItem (msnTable, SWT.NULL);
				String msn = msns[i];
				if (msn.length()>0) {
					msn += " (" + getRuntime().getMsnManager().getMsnLabel(msn) + ")";
				}
				item.setText(0, msn);
				item.setData("msn", msns[i]);
				item.setForeground(getMsnFontColor(msns[i]));
			}

			for (int i=0; i<titles.length; i++) {
				msnTable.getColumn(i).pack ();
			}
			
			msnTable.addMouseListener(
				new MouseAdapter() {
					public void mouseDoubleClick(MouseEvent e) {
						if ((e.widget instanceof Table)) {
							if (((Table) e.widget).getSelectionCount()>0) {
								TableItem item = ((Table) e.widget).getSelection()[0];
								ColorDialog dialog = new ColorDialog (item.getDisplay().getActiveShell(), SWT.OPEN);		
								RGB rgb = dialog.open();
						
								if (rgb!=null) {
									item.setForeground(new Color(item.getDisplay(), rgb));
								}
							}
						}
					}
				}
			);

			return super.createDialogArea(parent);
		}
		
		private Color getMsnFontColor(String msn) {
			Map colorMap = new HashMap();
			String colors = m_app.getApplication().getConfiguration().getProperty(CFG_MSNFONTCOLOR, "[]");
			StringTokenizer st = new StringTokenizer(colors, "[");

			while (st.hasMoreTokens()) {
				String singleColor = st.nextToken();
				singleColor = singleColor.substring(0, singleColor.length()-1).trim();
				if (singleColor.length()>0) {
					StringTokenizer s = new StringTokenizer(singleColor, "%");
					while (s.hasMoreTokens()) {
						String key = s.nextToken();
						String color = s.nextToken();
						StringTokenizer cs = new StringTokenizer(color, ",");
						// only add if MSNs is existing
						if (getRuntime().getMsnManager().existMsn(
							getRuntime().getMsnManager().createMsn(key))) {
							
							colorMap.put(key, new Color(
								DisplayManager.getDefaultDisplay(),
								Integer.parseInt(cs.nextToken()),
								Integer.parseInt(cs.nextToken()),
								Integer.parseInt(cs.nextToken())
							));
						}
					}
				}
			}

			if (colorMap.containsKey(msn)) {
				return (Color)colorMap.get(msn);
			}
			return new Color(DisplayManager.getDefaultDisplay(), 0,0,0);		
		}		
		protected void okPressed() {
			TableItem[] items = msnTable.getItems();
			
			String configString = "";
			for (int i=0;i<items.length;i++){
				configString += "[";
				configString += items[i].getData("msn") + "%";
				configString += items[i].getForeground().getRed()+",";
				configString += items[i].getForeground().getGreen()+",";
				configString += items[i].getForeground().getBlue();
				configString += "]";
			}
			this.m_colors = configString;		
			super.okPressed();
		}
		
	}
	
	private static String NAMESPACE = "ui.jface.application.journal.action.MsnColorSelectAction";
	
	private IRuntime m_runtime;

	public MsnColorSelectAction() {
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
		return "journal_msncolorselect";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		MsnColorDialog mcd = new MsnColorDialog(new Shell(DisplayManager.getDefaultDisplay()));
		int ok = mcd.open();
		if (ok==MsnColorDialog.OK) {
			String color = mcd.getColors();
			this.m_app.getApplication().getConfiguration().setProperty(
				CFG_MSNFONTCOLOR, color
			);
			this.m_app.getApplication().storeConfiguration();
			this.m_app.getApplication().updateProviders();
			this.m_app.updateViews(true);
		}
	}
}
