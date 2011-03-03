package de.janrufmonitor.ui.jface.application.journal.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.monitor.IMonitor;
import de.janrufmonitor.ui.jface.application.journal.JournalConfigConst;
import de.janrufmonitor.ui.jface.application.journal.JournalController;
import de.janrufmonitor.ui.jface.application.journal.action.statistic.AbstractStatisticAction;
import de.janrufmonitor.ui.jface.application.journal.action.statistic.StatisticDialog;
import de.janrufmonitor.ui.swt.DisplayManager;

public class DurationStatsAction extends AbstractStatisticAction implements
		JournalConfigConst {

	private class StatisticComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			String[] obj0 = (String[]) arg0;
			String[] obj1 = (String[]) arg1;

			int o1 = new Integer(obj0[2]).intValue();
			int o2 = new Integer(obj1[2]).intValue();

			if (o1 < o2)
				return 1;

			if (o1 > o2)
				return -1;

			return obj0[0].compareTo(obj1[0]);
		}

	}

	private static String NAMESPACE = "ui.jface.application.journal.action.DurationStatsAction";
	private int m_maxcount;

	public DurationStatsAction() {
		super();
	}

	public String getID() {
		return "journal_msnstats";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public String[] getColumnTitles() {
		return new String[] {
				getI18nManager().getString(getNamespace(), "msn", "label",
						getLanguage()),
				getI18nManager().getString(getNamespace(), "duration", "label",
						getLanguage()) };
	}

	public List getStatisticItems() {
		Properties stat = new Properties();

		if (this.m_cl != null) {
			String msn = null;
			ICall c = null;
			for (int i = 0; i < this.m_cl.size(); i++) {
				c = this.m_cl.get(i);
				msn = c.getMSN().getMSN();
				if (this.m_cl.get(i).getMSN().getAdditional().length() > 0) {
					msn += " (" + this.m_cl.get(i).getMSN().getAdditional()
							+ ")";
				}
				String value = stat.getProperty(msn, "");
				if (value.length() == 0) {
					stat.setProperty(msn, Integer.toString(getDurationTime(c)));
					stat.setProperty(msn+".string", getDuration(getDurationTime(c)));
				} else {
					int val = new Integer(value).intValue();
					val+=getDurationTime(c);
					stat.setProperty(msn, new Integer(val).toString());
					stat.setProperty(msn+".string", getDuration(val));
					m_maxcount = Math.max(m_maxcount, val);
				}
			}
		}

		Iterator iter = stat.keySet().iterator();
		List items = new ArrayList();
		String msn = null;
		while (iter.hasNext()) {
			msn = (String) iter.next();
			if (!msn.endsWith(".string")) {
				String count = stat.getProperty(msn, "");
				items.add(new String[] { msn, stat.getProperty(msn+".string"), count});
			}

		}

		Collections.sort(items, new StatisticComparator());

		return items;
	}

	private int getDurationTime(ICall c) {
		IAttribute ring = c.getAttribute("fritzbox.duration");
		if (ring != null)
			return Integer.parseInt(ring.getValue());

		return 0;
	}
	
	public void run() {
		this.setCallList(((JournalController)this.m_app.getController()).getCallList());
		
		StatisticDialog mcd = new StatisticDialog(new Shell(DisplayManager.getDefaultDisplay()), this, false);
		mcd.open();
	}

	private String getDuration(int duration) {
		try {
			duration = duration / 60;
			StringBuffer sb = new StringBuffer(64);
			if ((duration / (60*24)) > 0) {
				sb.append((duration / (60*24)));
				sb.append(" d ");
			}
			if ((duration / 60) > 0) {
				sb.append(((duration / 60)%24));
				sb.append(" h ");
			}
			sb.append((duration % 60));
			sb.append(" min ");
			return sb.toString();
		} catch (Exception e) {
		}
		return "";
	}

	public int getMaxItemCount() {
		return m_maxcount;
	}
	
	public boolean isEnabled() {
		IMonitor m = getRuntime().getMonitorListener().getMonitor("FritzBoxMonitor");
		return (m!=null);
	}

	
}
