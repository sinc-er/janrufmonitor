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

public class WeekTimeStatsAction extends AbstractStatisticAction {

	private class StatisticComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			String[] obj0 = (String[]) arg0;
			String[] obj1 = (String[]) arg1;
			
			int o1 = new Integer(obj0[2]).intValue();
			int o2 = new Integer(obj1[2]).intValue();
			
			return o1-o2;
		}

	}

	private static String NAMESPACE = "ui.jface.application.journal.action.WeekTimeStatsAction";

	boolean isCalculating = false;

	private int m_maxcount;

	public WeekTimeStatsAction() {
		super();
	}

	public String getID() {
		return "journal_weektimestats";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public String[] getColumnTitles() {
		return new String[] {
				getI18nManager().getString(getNamespace(), "weekday", "label",
						getLanguage()),
				getI18nManager().getString(getNamespace(), "calls", "label",
						getLanguage()) };
	}
	
	private String getDayFormat(int i) {
		return getI18nManager().getString(getNamespace(), Integer.toString(i), "label", getLanguage());
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
							int hour = cal.get(Calendar.DAY_OF_WEEK);
								
							int val = new Integer(stat.getProperty(getDayFormat(hour), "0")).intValue();
							val++;
							stat.setProperty(getDayFormat(hour), new Integer(val).toString());
							stat.setProperty(getDayFormat(hour)+".sort", new Integer(hour).toString());
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
						
						if(!key.endsWith(".sort")) {
							count = stat.getProperty(key, "");
							items.add(new String[] {
										getI18nManager().getString(getNamespace(), key, "label", getLanguage()), count, stat.getProperty(key+".sort") });

						}
						
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
