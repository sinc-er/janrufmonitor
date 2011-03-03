package de.janrufmonitor.ui.jface.application.journal.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import de.janrufmonitor.ui.jface.application.journal.JournalConfigConst;
import de.janrufmonitor.ui.jface.application.journal.action.statistic.AbstractStatisticAction;

public class MsnStatsAction extends AbstractStatisticAction implements
		JournalConfigConst {

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

	private static String NAMESPACE = "ui.jface.application.journal.action.MsnStatsAction";

	public MsnStatsAction() {
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
				getI18nManager().getString(getNamespace(), "calls", "label",
						getLanguage()) };
	}

	public List getStatisticItems() {
		Properties stat = new Properties();

		if (this.m_cl != null) {
			for (int i = 0; i < this.m_cl.size(); i++) {
				String msn = this.m_cl.get(i).getMSN().getMSN();
				if (this.m_cl.get(i).getMSN().getAdditional().length() > 0) {
					msn += " (" + this.m_cl.get(i).getMSN().getAdditional()
							+ ")";
				}
				String value = stat.getProperty(msn, "");
				if (value.length() == 0) {
					stat.setProperty(msn, "1");
				} else {
					int val = new Integer(value).intValue();
					val++;
					stat.setProperty(msn, new Integer(val).toString());
				}
			}
		}

		Iterator iter = stat.keySet().iterator();
		List items = new ArrayList();
		while (iter.hasNext()) {
			String msn = (String) iter.next();
			String count = stat.getProperty(msn, "");
			items.add(new String[] { msn, count });
		}

		Collections.sort(items, new StatisticComparator());

		return items;
	}

}
