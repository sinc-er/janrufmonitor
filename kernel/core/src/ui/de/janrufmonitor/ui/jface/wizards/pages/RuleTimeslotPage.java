package de.janrufmonitor.ui.jface.wizards.pages;

import java.util.Calendar;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class RuleTimeslotPage extends AbstractPage {

	private String NAMESPACE = "ui.jface.wizards.pages.RuleTimeslotPage";

	private String m_ts;
	private IRuntime m_runtime;
	private Text[] times; 
	
	public RuleTimeslotPage(String ts) {
		super(RuleTimeslotPage.class.getName());
		if (ts==null)
			ts = "*";
		
		this.m_ts = ts;
		
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}
	
	public String getResult() {
		return this.m_ts;
	}

	public void createControl(Composite parent) {
	    Composite nameComposite = new Composite(parent, SWT.NONE);
	    nameComposite.setLayout(new GridLayout(1, false));
	    nameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	    final Button c = new Button(nameComposite, SWT.CHECK);
	    c.setText(this.m_i18n.getString(this.getNamespace(), "timeslotactive", "label", this.m_language));
	    c.setSelection(this.m_ts.equalsIgnoreCase("*"));

	    final Composite slot = new Composite(nameComposite, SWT.NONE);
	    slot.setLayout(new GridLayout(2, false));
	    slot.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    slot.setVisible(!this.m_ts.equalsIgnoreCase("*"));

	    Label ln = new Label(slot, SWT.LEFT);
	    ln.setText(this.m_i18n.getString(this.getNamespace(), "name", "label", this.m_language));
	    new Label(slot, SWT.LEFT);
	    
	    final Button[] days = new Button[7];
	    days[0] = new Button(slot, SWT.CHECK);
	    days[0].setText(this.m_i18n.getString(this.getNamespace(), "sunday", "label", this.m_language));
	    days[0].setData(new Integer(Calendar.SUNDAY));
	    
	    days[1] = new Button(slot, SWT.CHECK);
	    days[1].setText(this.m_i18n.getString(this.getNamespace(), "monday", "label", this.m_language));
	    days[1].setData(new Integer(Calendar.MONDAY));
	    
	    days[2] = new Button(slot, SWT.CHECK);
	    days[2].setText(this.m_i18n.getString(this.getNamespace(), "tuesday", "label", this.m_language));
	    days[2].setData(new Integer(Calendar.TUESDAY));

	    days[3] = new Button(slot, SWT.CHECK);
	    days[3].setText(this.m_i18n.getString(this.getNamespace(), "wednesday", "label", this.m_language));
	    days[3].setData(new Integer(Calendar.WEDNESDAY));

	    days[4] = new Button(slot, SWT.CHECK);
	    days[4].setText(this.m_i18n.getString(this.getNamespace(), "thursday", "label", this.m_language));
	    days[4].setData(new Integer(Calendar.THURSDAY));
	   
	    days[5] = new Button(slot, SWT.CHECK);
	    days[5].setText(this.m_i18n.getString(this.getNamespace(), "friday", "label", this.m_language));
	    days[5].setData(new Integer(Calendar.FRIDAY));
		   
	    days[6] = new Button(slot, SWT.CHECK);
	    days[6].setText(this.m_i18n.getString(this.getNamespace(), "saturday", "label", this.m_language));
	    days[6].setData(new Integer(Calendar.SATURDAY));
	    
	    new Label(slot, SWT.LEFT);
	    new Label(slot, SWT.LEFT);
	    new Label(slot, SWT.LEFT);
	    
	    ln = new Label(slot, SWT.LEFT);
	    ln.setText(this.m_i18n.getString(this.getNamespace(), "from", "label", this.m_language));
	    ln = new Label(slot, SWT.LEFT);
	    ln.setText(this.m_i18n.getString(this.getNamespace(), "to", "label", this.m_language));

	    times = new Text[2];
	    
	    times[0] = new Text(slot, SWT.BORDER);
	    times[0].setTextLimit(5);
	    times[0].setText("00:00");
	    
	    times[1] = new Text(slot, SWT.BORDER);
	    times[1].setTextLimit(5);
	    times[1].setText("23:59");
	    	    
	    SelectionAdapter sa = new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		m_ts = "";
	    		StringBuffer s = new StringBuffer();
	    		for (int i=0;i<days.length;i++) {
	    			if (days[i].getSelection()) {
	    				s.append(((Integer)days[i].getData()).intValue());
	    				s.append(",");
	    			}
	    		}
	    		setPageComplete(isComplete());
	    		s.append(";");
	    		if (times[0].getText().length()==5 && times[1].getText().length()==5) {
		    		s.append(times[0].getText().substring(0,2));
		    		s.append(",");
		    		s.append(times[0].getText().substring(3,5));
		    		s.append(",");
		    		s.append(times[1].getText().substring(0,2));
		    		s.append(",");
		    		s.append(times[1].getText().substring(3,5));
		    		m_ts = s.toString();
	    		}

	    		setPageComplete(isComplete());
			}
	    };
	    
	    KeyAdapter kl = new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				m_ts = "";
	    		StringBuffer s = new StringBuffer();
	    		for (int i=0;i<days.length;i++) {
	    			if (days[i].getSelection()) {
	    				s.append(((Integer)days[i].getData()).intValue());
	    				s.append(",");
	    			}
	    		}
	    		s.append(";");
	    		if (times[0].getText().length()==5 && times[1].getText().length()==5) {
		    		s.append(times[0].getText().substring(0,2));
		    		s.append(",");
		    		s.append(times[0].getText().substring(3,5));
		    		s.append(",");
		    		s.append(times[1].getText().substring(0,2));
		    		s.append(",");
		    		s.append(times[1].getText().substring(3,5));
		    		m_ts = s.toString();
	    		}

	    		setPageComplete(isComplete());
			}
	    };
	    
	    for (int i=0;i<days.length;i++) {
	    	days[i].addSelectionListener(sa);
	    }
	    
	    for (int i=0;i<times.length;i++) {
	    	times[i].addKeyListener(kl);
	    }
	    
	    c.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (c.getSelection()) {
					m_ts = "*";
					slot.setVisible(false);
				    for (int i=0;i<days.length;i++) {
				    	days[i].setSelection(false);
				    }
				    times[0].setText("00:00");
				    times[1].setText("23:59");
				} else {
					m_ts = "";
					slot.setVisible(true);
				}
				setPageComplete(isComplete());
			}
	    });
	    
	    this.setTimeslotData(days, times);
	    
	    setPageComplete(isComplete());
	    setControl(nameComposite);
	}

	public boolean isComplete() {
		if(times!=null && times.length==2) {
			if (times[0]!=null && times[0].getText().length()<5) {
				setErrorMessage(this.m_i18n.getString(this.getNamespace(), "fromerror", "label", this.m_language));
				return false;
			}
			if (times[1]!=null && times[1].getText().length()<5) {
				setErrorMessage(this.m_i18n.getString(this.getNamespace(), "toerror", "label", this.m_language));
				return false;
			}
			if (!this.validTime(times[0].getText())){
				setErrorMessage(this.m_i18n.getString(this.getNamespace(), "fromerror", "label", this.m_language));
				return false;
			}
			if (!this.validTime(times[1].getText())){
				setErrorMessage(this.m_i18n.getString(this.getNamespace(), "toerror", "label", this.m_language));
				return false;
			}
			if (!this.validStartEndTime(times[0].getText(), times[1].getText())){
				setErrorMessage(this.m_i18n.getString(this.getNamespace(), "startenderror", "label", this.m_language));
				return false;
			}
		}
		
		if (this.m_ts.trim().length()==0 || this.m_ts.trim().startsWith(";")) {
			setErrorMessage(this.m_i18n.getString(this.getNamespace(), "tserror", "label", this.m_language));
			return false;
		}
				
		return super.isComplete();
	}

	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}	
	
	private boolean validStartEndTime(String start, String end) {
		int s = Integer.parseInt(start.substring(0,2) + start.substring(3,5));
		int e = Integer.parseInt(end.substring(0,2) + end.substring(3,5));
		return s<e;
	}
	
	private boolean validTime(String t) {
		return t.indexOf(":")==2;
	}
	
	private void setTimeslotData(Button[] adays, Text[] atimes) {
		// Format: Day1,Day2,Day3;hh,mm,hh,mm
		StringTokenizer st = new StringTokenizer(m_ts, ";");
		if (st.countTokens()==1) {
			this.m_logger.info("Timeslot string: "+m_ts);
		} else if (st.countTokens()==2) {
			StringTokenizer days = new StringTokenizer(st.nextToken(), ",");
			String day = null;
			for (int i=0;i<adays.length;i++) {
				if (adays[i]!=null) {
					adays[i].setSelection(false);
				}
			}
			
			while (days.hasMoreTokens()) {
				// check if day matches
				day = days.nextToken();
				for (int i=0;i<adays.length;i++) {
					if (adays[i]!=null) {
						if (((Integer)adays[i].getData()).intValue() == Integer.parseInt(day)) {
							adays[i].setSelection(true);
						}
					}
				}
			}   
			
			// check if time matches
			StringTokenizer hours = new StringTokenizer(st.nextToken(), ",");
			if (hours.countTokens()==4) {
				atimes[0].setText(hours.nextToken()+":"+hours.nextToken());
				atimes[1].setText(hours.nextToken()+":"+hours.nextToken());
			}
			
		} else {
			this.m_logger.warning("Invalid timeslot string: "+m_ts);
		}
	}
}
