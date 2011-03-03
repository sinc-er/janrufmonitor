package de.janrufmonitor.ui.jface.wizards.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IMultiPhoneCaller;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.formatter.Formatter;

public class CallerCombinePage extends AbstractPage {

	private String NAMESPACE = "ui.jface.wizards.pages.CallerCombinePage";

	private ICallerList m_l;

	private IMultiPhoneCaller m_c;

	private List m_phones;

	private IRuntime m_runtime;

	private Formatter m_f;

	public CallerCombinePage(ICallerList l) {
		super(CallerCombinePage.class.getName());
		if (l == null)
			l = PIMRuntime.getInstance().getCallerFactory().createCallerList();

		this.m_l = l;
		m_phones = new ArrayList();
		IMultiPhoneCaller c = null;
		for (int i = 0; i < l.size(); i++) {
			c = (IMultiPhoneCaller) l.get(i);
			List pns = c.getPhonenumbers();
			Object p = null;
			for (int j=0;j<pns.size();j++) {
				p = pns.get(j);
				if (!m_phones.contains(p))
					m_phones.add(p);
			}
		}
	}

	public IMultiPhoneCaller getResult() {
		return m_c;
	}

	public void createControl(Composite parent) {
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label",
				this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description",
				"label", this.m_language));

		Composite nameComposite = new Composite(parent, SWT.NONE);
		nameComposite.setLayout(new GridLayout(1, false));
		nameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label ln = new Label(nameComposite, SWT.LEFT);
		ln.setText(this.m_i18n.getString(this.getNamespace(), "name", "label",
				this.m_language));

		final Combo callerCombo = new Combo(nameComposite, SWT.READ_ONLY);

		String[] callers = new String[m_l.size()];

		ICaller c = null;
		for (int i = 0; i < callers.length; i++) {
			c = m_l.get(i);
			callers[i] = getFormatter().parse(
					"%a:ln%, %a:fn% (%a:add%)", c);
			callerCombo.setData(callers[i], c);

		}
		callerCombo.setItems(callers);
		callerCombo.select(0);

		String callerString = callerCombo.getItem(callerCombo
				.getSelectionIndex());
		
		this.m_c = ((IMultiPhoneCaller) callerCombo.getData(callerString));
		this.m_c.setPhonenumbers(this.m_phones);

		// Add the handler to update the name based on input
		callerCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				String callerString = callerCombo.getItem(callerCombo
						.getSelectionIndex());
				IMultiPhoneCaller m = (IMultiPhoneCaller) callerCombo
						.getData(callerString);
				try {
					m_c = (IMultiPhoneCaller) m.clone();
					m_c.setPhonenumbers(m_phones);
				} catch (CloneNotSupportedException e) {
					m_logger.log(Level.SEVERE, e.getMessage(), e);
				}
				
				setPageComplete(isComplete());
			}
		});

		setPageComplete(isComplete());
		setControl(nameComposite);
	}

	private Formatter getFormatter() {
		if (this.m_f == null) {
			this.m_f = Formatter.getInstance(PIMRuntime.getInstance());
		}
		return this.m_f;
	}

	protected IRuntime getRuntime() {
		if (this.m_runtime == null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getNamespace() {
		return NAMESPACE;
	}
}
