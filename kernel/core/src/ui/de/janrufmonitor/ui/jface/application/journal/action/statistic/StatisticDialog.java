package de.janrufmonitor.ui.jface.application.journal.action.statistic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTImageManager;
import de.janrufmonitor.util.io.Stream;
import de.janrufmonitor.util.string.StringUtils;

public class StatisticDialog extends TitleAreaDialog {

	private IStatistic m_stat;

	private boolean m_drawStats;
	private II18nManager m_i18n;
	private String m_language;

	public StatisticDialog(Shell shell, IStatistic stat, boolean drawStats) {
		super(shell);
		this.m_stat = stat;
		this.m_drawStats = drawStats;
	}

	public void setStatistic(IStatistic stat) {
		if (stat != null)
			this.m_stat = stat;
	}

	protected Control createContents(Composite parent) {
		Control c = super.createContents(parent);
		setTitle(this.m_stat.getTitle());
		setMessage(this.m_stat.getDescription());
		return c;
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		new Label(composite, SWT.NONE).setText(this.m_stat.getMessage());

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 600;
		gd.heightHint = 400;

		final Table statTable = new Table(composite, SWT.MULTI | SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		statTable.setLinesVisible(false);
		statTable.setHeaderVisible(true);
		statTable.setLayoutData(gd);
		final String[] titles = this.m_stat.getColumnTitles();
		TableColumn column = null;
		for (int i = 0; i < titles.length; i++) {
			column = new TableColumn(statTable, SWT.LEFT);
			column.setText(titles[i]);
		}

		if (this.m_drawStats) {
			// stat-column
			column = new TableColumn(statTable, SWT.LEFT);
			column.setText("");

			statTable.addListener(SWT.PaintItem, new Listener() {
				public void handleEvent(Event event) {
					if (event.index == titles.length) {
						GC gc = event.gc;
						TableItem item = (TableItem) event.item;

						String[] it = ((String[]) item.getData());

						String count = it[titles.length - 1]; // stat.getProperty(item.getText(),
																// "");
						float percent = 100 * (new Float(count).floatValue() / m_stat.getMaxItemCount());
						float p2 = 100 * (new Float(count).floatValue() / m_stat.getMaxListItemCount());

						// int index = statTable.indexOf(item);
						Color foreground = gc.getForeground();
						Color background = gc.getBackground();
						gc.setForeground(statTable.getDisplay().getSystemColor(
								SWT.COLOR_RED));
						gc.setBackground(statTable.getDisplay().getSystemColor(
								SWT.COLOR_YELLOW));
						int width = (statTable.getColumn(titles.length)
								.getWidth() - 1)
								* ((int) percent) / 100;
						gc.fillGradientRectangle(event.x, event.y, width,
								event.height, true);
						Rectangle rect2 = new Rectangle(event.x, event.y,
								width - 1, event.height - 1);
						gc.drawRectangle(rect2);
						gc.setForeground(statTable.getDisplay().getSystemColor(
								SWT.COLOR_LIST_FOREGROUND));

						String text = new Float(p2).toString(); // it[titles.length-1];
						text = text.substring(0, text.indexOf(".") + 2);
						text += " %";

						// item.setText(titles.length-1, "");

						Point size = event.gc.textExtent(text);
						int offset = Math.max(0, (event.height - size.y) / 2);
						gc.drawText(text, event.x + 2, event.y + offset, true);
						gc.setForeground(background);
						gc.setBackground(foreground);
					}
				}
			});
		}

		List items = this.m_stat.getStatisticItems();

		Object oitem = null;
		TableItem item = null;
		for (int i = 0, k = items.size(); i < k; i++) {
			oitem = items.get(i);
			item = new TableItem(statTable, SWT.NULL);
			for (int j = 0; j < titles.length; j++) {
				item.setText(j, ((String[]) oitem)[j]);
			}
			item.setData(oitem);
		}

		for (int i = 0; i < titles.length; i++) {
			statTable.getColumn(i).pack();
		}

		if (this.m_drawStats)
			statTable.getColumn(titles.length).setWidth(80);

		return super.createDialogArea(parent);
	}

	protected void createButtonsForButtonBar(Composite parent) {
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 80;
		gd.horizontalAlignment = SWT.RIGHT;
		
		Button save = this.createButton(parent, SWT.PUSH, getI18nManager().getString("ui.jface.application.journal.action.statistic", "export", "label", getLanguage()), false);
		save.setLayoutData(gd);
		save.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				IStatistic s = m_stat;
				if (s!=null) {
					FileDialog dialog = new FileDialog (new Shell(DisplayManager.getDefaultDisplay()), SWT.SAVE);
					dialog.setText(getI18nManager().getString("ui.jface.application.journal.action.statistic", "title", "label", getLanguage()));

					dialog.setFilterNames(new String[] {getI18nManager().getString("ui.jface.application.journal.action.statistic", "csv", "label", getLanguage())});
					dialog.setFilterExtensions(new String[] {"*.csv"});
					dialog.setFileName(removeInvalidCharacters(s.getTitle())+".csv");
					
					final String filename = dialog.open();
					if (filename==null) return;
					
					File file = new File(filename);
					if (!file.exists()) file.getParentFile().mkdirs();
					
					StringBuffer content = new StringBuffer();
					
					String[] header = s.getColumnTitles();
					for (int i=0;i<header.length;i++) {
						content.append(header[i]); if (i<header.length-1) content.append(";");
					}
					content.append(IJAMConst.CRLF);
					
					List l = s.getStatisticItems();
					String[] line = null;
					for (int i=0,j=l.size();i<j;i++) {
						line = (String[]) l.get(i);
						for (int k=0; k<header.length;k++) {
							content.append(line[k]); if (k<header.length-1) content.append(";");
						}
						content.append(IJAMConst.CRLF);
					}
					try {
						FileOutputStream fos = new FileOutputStream(file);
						ByteArrayInputStream bin = new ByteArrayInputStream(content.toString().getBytes());
						Stream.copy(bin, fos, true);
					} catch (FileNotFoundException ex) {
					} catch (IOException ex) {
					}
					
				}
				s = null;
			}
			
		});
		
		Button close = this.createButton(parent, SWT.PUSH, "Ok", true);
		gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 80;
		gd.horizontalAlignment = SWT.LEFT;
		close.setLayoutData(gd);
		close.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				close();
			}
			
		});

		this.setTitleImage(SWTImageManager
				.getInstance(PIMRuntime.getInstance()).get(
						IJAMConst.IMAGE_KEY_PIM_JPG));
	}
	
	private String removeInvalidCharacters(String s) {
		s = StringUtils.replaceString(s, "?", "");
		s = StringUtils.replaceString(s, "/", "");
		s = StringUtils.replaceString(s, "\\", "");
		s = StringUtils.replaceString(s, "\"", "");
		s = StringUtils.replaceString(s, "*", "");
		s = StringUtils.replaceString(s, ":", "");
		s = StringUtils.replaceString(s, "<", "");
		s = StringUtils.replaceString(s, ">", "");
		s = StringUtils.replaceString(s, "|", "");
		return s;
	}

	protected II18nManager getI18nManager() {
		if (this.m_i18n==null) {
			this.m_i18n = PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager();
		}
		return this.m_i18n;
	}

	protected String getLanguage() {
		if (this.m_language==null) {
			this.m_language = 
				PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(
					IJAMConst.GLOBAL_NAMESPACE,
					IJAMConst.GLOBAL_LANGUAGE
				);
		}
		return this.m_language;
	}
}
