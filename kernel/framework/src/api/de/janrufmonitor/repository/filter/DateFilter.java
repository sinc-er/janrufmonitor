package de.janrufmonitor.repository.filter;

import java.util.Date;

/**
 * This class is an date filter which can be used as 
 * a "from-to date filter" or a "to date filter".<br><br>
 * <code>IFilter df = new DateFilter(null, new Date(12334567));</code> - 
 * filter is used as a "to date filter"<br>
 * <code>IFilter df = new DateFilter(new Date(), new Date(12334567));</code> -
 * filter is used as a "from-to date filter".
 * 
 *@author     Thilo Brandt
 *@created    2004/07/17
 */
public final class DateFilter extends AbstractFilter {

	private Date m_DateFrom;
	private Date m_DateTo;
	private long m_timeframe = -1;

	/**
	 * Creates a new date filter with the specified dates.
	 * 
	 * @param dateFrom beginning date
	 * @param dateTo end date
	 */
	public DateFilter(Date dateFrom, Date dateTo) {
		super();
		this.m_DateFrom = dateFrom;
		this.m_DateTo = dateTo;
		this.m_filter = dateTo;
		this.m_type = FilterType.DATE;
	}
	
	/**
	 * Creates a new date filter with the specified dates.
	 * 
	 * @param dateFrom beginning date
	 * @param dateTo end date
	 */
	public DateFilter(Date dateFrom, Date dateTo, long timeframe) {
		super();
		this.m_DateFrom = dateFrom;
		this.m_DateTo = dateTo;
		this.m_filter = dateTo;
		this.m_timeframe = timeframe;
		this.m_type = FilterType.DATE;
	}
	
	/**
	 * Creates a new date filter with the specified timeframe.
	 * 
	 * @param timeframe timeframe in days
	 */
	public DateFilter(long timeframe) {
		super();
		this.m_DateFrom = null;
		this.m_DateTo = null;
		this.m_filter = null;
		this.m_timeframe = timeframe;
		this.m_type = FilterType.DATE;
	}
	
	/**
	 * Gets the from date argument.
	 * 
	 * @return a valid date object or null.
	 */
	public Date getDateFrom() {
		if (this.m_timeframe>-1) {
			return new Date(System.currentTimeMillis());
		}
		return this.m_DateFrom;
	}
	
	/**
	 * Gets the to date argument.
	 * 
	 * @return a valid date object.
	 */
	public Date getDateTo() {
		if (this.m_timeframe>-1) {
			long delta = this.m_timeframe * 86400000L;
			return new Date(System.currentTimeMillis()-delta);
		}		
		return this.m_DateTo;
	}
	
	/**
	 * Gets the timeframe argument
	 * 
	 * @return a valid timeframe. -1 is returned if no timeframe is set.
	 */
	public long getTimeframe(){
		return this.m_timeframe;
	}

	public String toString() {
		return DateFilter.class.getName()+"#"+this.m_DateFrom+"#"+this.m_DateTo;
	}
}
