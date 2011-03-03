package de.janrufmonitor.ui.jface.application.journal.action.statistic;

import java.util.List;

import de.janrufmonitor.framework.ICallList;

public interface IStatistic {

	public String getTitle();
	
	public String getDescription();
	
	public String getMessage();
	
	public String[] getColumnTitles();
	
	public void setCallList(ICallList cl);
	
	public List getStatisticItems(int offset, int count);
	
	public List getStatisticItems();
	
	public int getMaxItemCount();
	
	public int getMaxListItemCount();

}
