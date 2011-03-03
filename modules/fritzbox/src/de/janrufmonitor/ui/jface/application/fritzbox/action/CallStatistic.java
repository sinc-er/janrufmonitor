package de.janrufmonitor.ui.jface.application.fritzbox.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.ApplicationImageDescriptor;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTImageManager;
import de.janrufmonitor.util.formatter.Formatter;
import de.janrufmonitor.util.string.StringUtils;

public class CallStatistic extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.fritzbox.action.CallStatistic";

	private IRuntime m_runtime;

	public CallStatistic() {
		super();
		this.setText(
			this.getI18nManager().getString(
				this.getNamespace(),
				"title",
				"label",
				this.getLanguage()
			)
		);
		this.setImageDescriptor(new ApplicationImageDescriptor(
			SWTImageManager.getInstance(this.getRuntime()).getImagePath("callstatistic.gif")
		));			
	}
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return "fritzbox_callstat";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		Object [] o = this.m_app.getController().getElementArray();
		if (o!=null && o.length>0 && o[0] instanceof ICall) {
			Map msnStats = new HashMap();
			int count = o.length;
			int accumated = 0;
			ICall c = null;
			IAttribute att = null;
			
			for (int i=0;i<o.length;i++) {
				c = (ICall) o[i];
				att = c.getAttribute("fritzbox.duration");
				if (att!=null) {
					int call_dur = 0;
					try {
						call_dur = Integer.parseInt(att.getValue());
					} catch (Exception e){
						this.m_logger.log(Level.SEVERE, e.toString(), e);
					}
					
					accumated += call_dur;
					if (msnStats.containsKey(c.getMSN().getMSN())) {
						int d = ((Integer)msnStats.get(c.getMSN().getMSN())).intValue();
						d += call_dur;
						msnStats.put(c.getMSN().getMSN(), new Integer(d));
					} else {
						msnStats.put(c.getMSN().getMSN(), new Integer(call_dur));
					}
				}
			}
			
			accumated = accumated / 60;
			
			int h = accumated / 60;
			int min = accumated % 60;
			
			Iterator iter = msnStats.keySet().iterator();
			StringBuffer sb = new StringBuffer();
			String msn = null;
			Formatter f = Formatter.getInstance(getRuntime());
			String CRLF = "\n";
			while (iter.hasNext()) {
				msn = (String) iter.next();
				int value = ((Integer)msnStats.get(msn)).intValue() / 60;
				sb.append("\t");
				sb.append(f.parse(IJAMConst.GLOBAL_VARIABLE_MSNFORMAT, getRuntime().getMsnManager().createMsn(msn)));
				sb.append(": ");
				sb.append((value / 60));
				sb.append("h ");
				sb.append((value % 60));
				sb.append("min ");
				if (iter.hasNext())
					sb.append(CRLF);
			}
								
			String text = this.getI18nManager().getString(this.getNamespace(), "stat", "description", this.getLanguage());
			text = StringUtils.replaceString(text, "{%1}", Integer.toString(count));
			text = StringUtils.replaceString(text, "{%2}", Integer.toString(h)+"h "+Integer.toString(min)+"min");
			text = StringUtils.replaceString(text, "{%3}", sb.toString());
			
			MessageDialog.openInformation(
					new Shell(DisplayManager.getDefaultDisplay()),
					this.getI18nManager().getString(this.getNamespace(), "stat", "label", this.getLanguage()),
					text);

			
			this.m_app.updateViews(false);
			return;
		}
	}

}
