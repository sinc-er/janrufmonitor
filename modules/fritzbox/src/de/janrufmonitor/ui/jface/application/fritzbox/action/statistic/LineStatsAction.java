package de.janrufmonitor.ui.jface.application.fritzbox.action.statistic;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.ui.jface.application.journal.action.statistic.AbstractStatisticAction;
import de.janrufmonitor.ui.swt.DisplayManager;

public class LineStatsAction extends AbstractStatisticAction {

	private class StatisticComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			String[] obj0 = (String[]) arg0;
			String[] obj1 = (String[]) arg1;
			int o1 = new Integer(obj0[1]).intValue();
			int o2 = new Integer(obj1[1]).intValue();

			if (o1 < o2)
				return 1;

			if (o1 > o2)
				return -1;
			return obj0[0].compareTo(obj1[0]);
		}

	}

	private static String NAMESPACE = "ui.jface.application.fritzbox.action.statistic.LineStatsAction";

	boolean isCalculating = false;
	int m_maxcount, m_maxlistcount;

	public LineStatsAction() {
		super();
	}

	public String getID() {
		return "fritzbox_linestat";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public String[] getColumnTitles() {
		return new String[] {
				getI18nManager().getString(getNamespace(), "line", "label",
						getLanguage()),
				getI18nManager().getString(getNamespace(), "calls", "label",
						getLanguage()) };
	}

	public List getStatisticItems() {
		final List items = new ArrayList();

		ProgressMonitorDialog pmd = new ProgressMonitorDialog(DisplayManager
				.getDefaultDisplay().getActiveShell());
		try {
			IRunnableWithProgress r = new IRunnableWithProgress() {
				public void run(IProgressMonitor progressMonitor) {
					isCalculating = true;

					progressMonitor.beginTask(getI18nManager().getString(
							getNamespace(), "calculating", "label",
							getLanguage()), IProgressMonitor.UNKNOWN);

					progressMonitor.worked(1);

					final Properties stat = new Properties();

					if (m_cl != null) {
						ICall c = null;
						for (int i = 0; i < m_cl.size(); i++) {
							c = m_cl.get(i);
							if (isLineCall(c)) {
								
								String key = getLineCall(c);

								String value = stat.getProperty(key, "");
								if (value.length() == 0) {
									stat.setProperty(key.trim(), "1");
									stat.setProperty(key + ".line", key);
								} else {
									int val = new Integer(value).intValue();
									val++;
									stat.setProperty(key.trim(), new Integer(val)
											.toString());
									
									m_maxcount = Math.max(m_maxcount, val);
								}
								m_maxlistcount++;
							}
						}
					}

					progressMonitor.worked(1);
					progressMonitor.setTaskName(getI18nManager().getString(
							getNamespace(), "createstatistic", "label",
							getLanguage()));
					
					Iterator iter = stat.keySet().iterator();

					String key = null;
					String count = null;
					while (iter.hasNext()) {
						key = (String) iter.next();
						if (!(key.endsWith(".line"))) {
							count = stat.getProperty(key, "");
							items.add(new String[] {
									stat.getProperty(key + ".line"), count });
						}
					}

					stat.clear();

					Collections.sort(items, new StatisticComparator());

					progressMonitor.done();
					isCalculating = false;
				}
			};
			pmd.setBlockOnOpen(false);
			pmd.run(true, false, r);

			// ModalContext.run(r, true, pmd.getProgressMonitor(),
			// DisplayManager.getDefaultDisplay());
		} catch (InterruptedException e) {
			m_logger.log(Level.SEVERE, e.getMessage(), e);
			isCalculating = false;
		} catch (InvocationTargetException e) {
			m_logger.log(Level.SEVERE, e.getMessage(), e);
			isCalculating = false;
		}

		while (isCalculating)
			try {
				Thread.sleep(1250);
			} catch (InterruptedException e) {
			}

		return items;
	}

	private boolean isLineCall(ICall c) {
		IAttribute a = c.getAttribute("fritzbox.line");
		return (a != null && a.getValue().length()>0);
	}
	
	private String getLineCall(ICall c) {
		IAttribute a = c.getAttribute("fritzbox.line");
		return a.getValue();
	}

	public int getMaxItemCount() {
		return m_maxcount;
	}
	
	public int getMaxListItemCount() {
		return m_maxlistcount;
	}
	
}
