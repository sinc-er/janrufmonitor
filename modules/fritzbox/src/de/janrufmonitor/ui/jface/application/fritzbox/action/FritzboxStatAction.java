package de.janrufmonitor.ui.jface.application.fritzbox.action;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.action.IAction;
import de.janrufmonitor.ui.jface.application.fritzbox.action.statistic.CbcStatsAction;
import de.janrufmonitor.ui.jface.application.fritzbox.action.statistic.LineStatsAction;
import de.janrufmonitor.ui.jface.application.journal.JournalConfigConst;

public class FritzboxStatAction extends AbstractAction implements JournalConfigConst {

	private static String NAMESPACE = "ui.jface.application.fritzbox.action.FritzboxStatAction";
	
	private IRuntime m_runtime;

	public FritzboxStatAction() {
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
		return "fritzbox_stats";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public IAction[] getSubActions() {
		IAction[] actions = new IAction[3];
		
		actions[0] = new CallStatistic();
		actions[0].setApplication(this.m_app);
		actions[1] = new CbcStatsAction();
		actions[1].setApplication(this.m_app);
		actions[2] = new LineStatsAction();
		actions[2].setApplication(this.m_app);
		return actions;
	}

	public boolean hasSubActions() {
		return isEnabled();
	}
	

}

