package de.janrufmonitor.application.console.command;

import de.janrufmonitor.framework.CallListComparator;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.repository.ICallManager;
import de.janrufmonitor.repository.filter.IFilter;
import de.janrufmonitor.repository.types.IReadCallRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.formatter.Formatter;
import de.janrufmonitor.util.string.StringUtils;
import de.janrufmonitor.framework.command.AbstractConsoleCommand;


public class ConsoleJournal extends AbstractConsoleCommand {

	private String ID = "journal";
	private String NAMESPACE = "application.console.command.ConsoleJournal";
	
	private IRuntime m_runtime;
	private boolean isExecuting; 

	public IRuntime getRuntime() {
		if (m_runtime==null)
			m_runtime = PIMRuntime.getInstance();
		return m_runtime;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public void execute() {
		isExecuting = true;
		System.out.println("Retrieving journal ...");
		System.out.println("");
		
		ICallManager m = PIMRuntime.getInstance().getCallManagerFactory().getDefaultCallManager();
		ICallList l = PIMRuntime.getInstance().getCallFactory().createCallList();
		
		if (m!=null && m.isActive() && m.isSupported(IReadCallRepository.class)) {
			l = ((IReadCallRepository)m).getCalls((IFilter)null);
		}
		
		System.out.println("Sorting journal for date ...");
		System.out.println("");
		l.sort(CallListComparator.ORDER_DATE, false);
		System.out.println("Date                 | Caller                    | Number               | MSN                  | Service (CIP)");
		System.out.println("--------------------------------------------------------------------------------------------------------------------");
		for (int i=0;i<l.size();i++) {
			Formatter f = Formatter.getInstance(PIMRuntime.getInstance());
			StringBuffer callLine = new StringBuffer();
			callLine.append(
				trim(
					f.parse(
						IJAMConst.GLOBAL_VARIABLE_CALLTIME,
						l.get(i).getDate()
					), 
					20
				)
			);
			callLine.append(" | ");
			callLine.append(
				trim(
					StringUtils.replaceString(f.parse(
						IJAMConst.GLOBAL_VARIABLE_CALLERNAME,
						l.get(i).getCaller()), IJAMConst.CRLF, " "
					),
					25
				)
			);
			callLine.append(" | ");
			callLine.append(
				trim(
				f.parse(
					IJAMConst.GLOBAL_VARIABLE_CALLERNUMBER,
					l.get(i).getCaller()
				), 20)
			);
			callLine.append(" | ");
			callLine.append(
				trim(
				l.get(i).getMSN().getMSN() + (l.get(i).getMSN().getAdditional().equalsIgnoreCase("")? "" : " ("+l.get(i).getMSN().getAdditional()+")")
				,20)
			);
			callLine.append(" | ");
			callLine.append(
				trim(
				l.get(i).getCIP().getAdditional()
				,18)
			);			
			System.out.println(callLine.toString());
		}
		System.out.println("");
		isExecuting = false;
	}

	public boolean isExecutable() {
		return true;
	}

	public boolean isExecuting() {
		return this.isExecuting;
	}

	public String getID() {
		return this.ID;
	}
	
	private String trim(String s, int length) {
		if (s.length()>length) {
			return s.substring(0, length-3) + "...";
		} else {
			int fill = length - s.length();
			for (int i=0;i<fill;i++)
				s += " ";
		}
		return s;
	}

	public String getLabel() {
		return "Journal             - JOURNAL + <ENTER>";
	}

}
