package de.janrufmonitor.ui.jface.wizards.pages;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.controls.DatePicker;

public class FilterDatePage extends AbstractPage {

	private String NAMESPACE = "ui.jface.wizards.pages.FilterDatePage";

	private Date[] m_dates;
	
	private Button active;
	private DatePicker dateFrom;
	private DatePicker dateTo;
	private IRuntime m_runtime;
	
	public FilterDatePage(Date[] dates) {
		super(FilterDatePage.class.getName());
		
		if (this.isValidDates(dates))
			this.setDates(dates);
		else 
			this.setDates(new Date[3]);
		
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}
	
	public Date[] getResult() {
		if (this.isNoTimeLimited())
			this.m_dates = null;
		
		return this.m_dates;
	}

	public void createControl(Composite parent) {
	    Composite c = new Composite(parent, SWT.NONE);
	    c.setLayout(new GridLayout(1, false));
	    c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    active = new Button(c, SWT.CHECK);
	    active.setText(
	    	this.m_i18n.getString(this.getNamespace(), "*", "label", this.m_language)	
	    );
	    active.setSelection(this.isNoTimeLimited());    
	    
	    final Group group = new Group(c, SWT.SHADOW_NONE);
	    group.setLayout(new GridLayout(1, false));
	    group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    group.setVisible(!this.isNoTimeLimited());
	    
	    new Label(group, SWT.LEFT).setText(this.m_i18n.getString(this.getNamespace(), "times", "label", this.m_language));
	    
		final Combo dateList = new Combo (group, SWT.READ_ONLY);
		
		String[] dateTechList = new String[] {
			"0",
			"-100",
			"-101",
			"-107",
			"-108",
			"-130",
			"-131",
			"1",
			"7",
			"14",
			"30",
			"90",
			"365"
		};
				
		String[] dateLabelList = new String[] {
				m_i18n.getString(NAMESPACE, "0", "label", m_language),
				m_i18n.getString(NAMESPACE, "-100", "label", m_language),
				m_i18n.getString(NAMESPACE, "-101", "label", m_language),
				m_i18n.getString(NAMESPACE, "-107", "label", m_language),
				m_i18n.getString(NAMESPACE, "-108", "label", m_language),
				m_i18n.getString(NAMESPACE, "-130", "label", m_language),
				m_i18n.getString(NAMESPACE, "-131", "label", m_language),
			m_i18n.getString(NAMESPACE, "1", "label", m_language),
			m_i18n.getString(NAMESPACE, "7", "label", m_language),
			m_i18n.getString(NAMESPACE, "14", "label", m_language),
			m_i18n.getString(NAMESPACE, "30", "label", m_language),
			m_i18n.getString(NAMESPACE, "90", "label", m_language),
			m_i18n.getString(NAMESPACE, "365", "label", m_language)
		};

		int select = 0;
		for (int i=0;i<dateTechList.length;i++) {
			dateList.setData(dateLabelList[i], dateTechList[i]);
			if (dateTechList[i].equalsIgnoreCase(this.getSelectedDate())) {
				select=i;	
			}
		}
		dateList.setItems(dateLabelList);
		dateList.select(select);
		dateList.setEnabled(!this.isIndividualTime());
		
		new Label(group, SWT.LEFT);
		
	    final Button personal = new Button(group, SWT.CHECK);
	    personal.setText(this.m_i18n.getString(this.getNamespace(), "name", "label", this.m_language));
	    personal.setSelection(this.isIndividualTime());
	    
	    final Group group2 = new Group(group, SWT.SHADOW_ETCHED_IN);
	    group2.setLayout(new GridLayout(1, false));
	    group2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    group2.setVisible(this.isIndividualTime());
	    
	    Composite dates = new Composite(group2, SWT.NONE);
	    dates.setLayout(new GridLayout(2, false));
	    
	    new Label(dates, SWT.LEFT).setText(this.m_i18n.getString(this.getNamespace(), "from", "label", this.m_language));
	    
	    new Label(dates, SWT.LEFT).setText(this.m_i18n.getString(this.getNamespace(), "to", "label", this.m_language));
	  
	    new Label(dates, SWT.LEFT);
	    
	    final Button current = new Button(dates, SWT.CHECK);
	    current.setText(
	    	this.m_i18n.getString(this.getNamespace(), "current", "label", this.m_language)	
	    );
	    current.setSelection(this.m_dates[0]==null ? true : false);
	    
	    GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 105;
	
	    dateFrom = new DatePicker(dates, SWT.NONE, false);
	    dateFrom.setLayoutData(gd);
	    dateFrom.setDate(this.m_dates[1] != null ? this.m_dates[1] : new Date());
	    dateFrom.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {				
					setDates(
						new Date[] {
							m_dates[0],
							dateFrom.getDate(),
							null
						}
					);					
					active.setSelection(false);
				}
			}	
	    );
	    
	    dateTo = new DatePicker(dates, SWT.NONE, true);
	    dateTo.setLayoutData(gd);
	    dateTo.setDate(this.m_dates[0] != null ? this.m_dates[0] : new Date());
	    dateTo.setVisible(this.m_dates[0]==null ? false : true);
	    dateTo.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setDates(
						new Date[] {
							dateTo.getDate(),
							m_dates[1],
							null
						}
					);
				}
			}	
	    );
	    
	    current.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {				
					if (current.getSelection()) {
						dateTo.setVisible(false);
						dateTo.setDate(new Date());
						setDates(
							new Date[] {
								null,
								m_dates[1],
								null
							}
						);						
					} else {
						dateTo.setVisible(true);
						dateTo.setDate(new Date());
					}
				}
			}
	    );
	    
	    active.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {				
					if (active.getSelection()) {
						setDates(
							new Date[] {
								null,
								null,
								null
							}
						);
						group.setVisible(false);
					} else {
						group.setVisible(true);
					}
				}
			}	
	    );
	    
	    personal.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {				
					if (personal.getSelection()) {
						setDates(
							new Date[] {
								null,
								null,
								null
							}
						);
						dateList.select(0);
						dateList.setEnabled(false);
						group2.setVisible(true);
					} else {
						setDates(
							new Date[] {
								null,
								null,
								null
							}
						);
						dateList.select(0);						
						dateList.setEnabled(true);
						group2.setVisible(false);
					}
				}
			}	
	    );
	    
		dateList.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				String smsn = dateList.getItem(dateList.getSelectionIndex());
				smsn = (String) dateList.getData(smsn);
	      	
				setDates(new Date[] {
					null,
					null,
					new Date(Long.parseLong(smsn))
				});
	
				setPageComplete(isComplete());
			}
	    });

	    setPageComplete(isComplete());
	    setControl(c);
	}

	private String getSelectedDate() {
		if (this.m_dates==null || this.m_dates.length!=3 || this.m_dates[2]==null) return "0";
		return Long.toString(this.m_dates[2].getTime());
	}
	
	private boolean isNoTimeLimited() {
		return ((this.m_dates.length==3 && this.m_dates[0]==null && this.m_dates[1]==null && this.m_dates[2]==null) ||
				(this.m_dates.length==2 && this.m_dates[0]==null && this.m_dates[1]==null)
			   );
	}
	
	private boolean isIndividualTime() {
		return (!isNoTimeLimited() && (this.m_dates[0]!=null || this.m_dates[1]!=null));
	}
	
	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}
	
	private void setDates(Date[] dates) {
		if (this.isValidDates(dates)) {
			this.m_dates = new Date[3];
			this.m_dates[0] = dates[0];
			this.m_dates[1] = dates[1];
			if (dates.length==3) {
				this.m_dates[2] = dates[2];
			}
		} else {
			this.m_logger.warning("Cound not set date objects: "+dates);
		}
	}
	
	private boolean isValidDates(Date[] dates) {
		if (dates!=null) {
			if (dates.length>1 && dates.length<=3) {
				return true;
			}
		}
		return false;
	}
}
