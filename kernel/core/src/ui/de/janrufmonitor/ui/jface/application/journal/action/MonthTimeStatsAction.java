package de.janrufmonitor.ui.jface.application.journal.action;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.ui.jface.application.journal.action.statistic.AbstractStatisticAction;
import de.janrufmonitor.ui.swt.DisplayManager;

public class MonthTimeStatsAction extends AbstractStatisticAction {

	private class StatisticComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			String[] obj0 = (String[]) arg0;
			String[] obj1 = (String[]) arg1;

			int o1 = new Integer(obj0[0]).intValue();
			int o2 = new Integer(obj1[0]).intValue();

			if (o1 < o2)
				return -1;

			if (o1 > o2)
				return 1;

			return obj0[0].compareTo(obj1[0]);
		}

	}

	private static String NAMESPACE = "ui.jface.application.journal.action.MonthTimeStatsAction";

	boolean isCalculating = false;

	private int m_maxcount;

	public MonthTimeStatsAction() {
		super();
	}

	public String getID() {
		return "journal_monthtimestats";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public String[] getColumnTitles() {
		return new String[] {
				getI18nManager().getString(getNamespace(), "monthday", "label",
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
						Date d = null;
						for (int i = 0; i < m_cl.size(); i++) {
							c = m_cl.get(i);
							d = c.getDate();
							Calendar cal = Calendar.getInstance();
							cal.setTime(d);
							int hour = cal.get(Calendar.DAY_OF_MONTH);
								
							int val = new Integer(stat.getProperty(Integer.toString(hour), "0")).intValue();
							val++;
							stat.setProperty(Integer.toString(hour), new Integer(val).toString());
							m_maxcount = Math.max(m_maxcount, val);
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
						
						count = stat.getProperty(key, "");
						items.add(new String[] {
									getI18nManager().getString(getNamespace(), key, "label", getLanguage()), count });
						
					}

					Collections.sort(items, new StatisticComparator());
					
					stat.clear();

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
				Thread.sleep(550);
			} catch (InterruptedException e) {
			}

		return items;
	}

	public int getMaxItemCount() {
		return m_maxcount;
	}
}
