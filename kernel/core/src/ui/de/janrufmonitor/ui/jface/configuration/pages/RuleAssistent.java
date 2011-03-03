package de.janrufmonitor.ui.jface.configuration.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.rules.IRule;
import de.janrufmonitor.framework.rules.IRuleEngine;
import de.janrufmonitor.framework.rules.Rule;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.AbstractTableLabelProvider;
import de.janrufmonitor.ui.jface.configuration.AbstractConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;
import de.janrufmonitor.ui.jface.wizards.RuleWizard;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTImageManager;
import de.janrufmonitor.util.string.StringUtils;

public class RuleAssistent extends AbstractConfigPage {

	private class RuleContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object o) {
			List l = (List)o;
			return l.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {	
		}
		
	}
	
	private class RuleLabelProvider extends AbstractTableLabelProvider {
		public String getColumnText(Object o, int column) {
			IRule rule = (IRule)o;
			
		    switch (column) {
		    case 0:
		      return rule.getName();
		    }
			return null;
		}
	}

	private String NAMESPACE = "ui.jface.configuration.pages.RuleAssistent";
	private String RULE_NAMESPACE = "rules.RuleEngine";
	
	private IRuntime m_runtime;
	private List m_rules;
	
	private CheckboxTableViewer cl;
	
	public String getNamespace() {
		return this.NAMESPACE;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	protected Control createContents(Composite parent) {
		this.setTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));
		this.noDefaultAndApplyButton();
		
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1, false));
		
		Label l = new Label(c, SWT.NONE);
		l.setText(
			this.m_i18n.getString(
				this.NAMESPACE,
				"available",
				"label",
				this.m_language
			)
		);
		
		cl = CheckboxTableViewer.newCheckList(c, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL);
		cl.setLabelProvider(new RuleLabelProvider());
		cl.setContentProvider(new RuleContentProvider());
		List rules = this.getRules();
		cl.setInput(rules);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 100;
	    cl.getTable().setLayoutData(gd);
	    
		IRule rule = null;
		for (int i=0;i<rules.size();i++) {
			rule = (IRule)rules.get(i);
			cl.setChecked(rule, rule.isActive());
		}
		
		cl.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				boolean checked = event.getChecked();
				IRule rule = (IRule) event.getElement();
				rule.setActive(checked);
			}
		});
		
		Composite buttonGroup = new Composite(c, SWT.NONE);
		buttonGroup.setLayout(new GridLayout(4, false));
		
		Button newButton = new Button(buttonGroup, SWT.PUSH);
		newButton.setText(
			this.m_i18n.getString(
				this.NAMESPACE,
				"new",
				"label",
				this.m_language
			)	
		);
		
		newButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					openRuleWizard(null);
					IRule rule = null;
					List rules = getRules();
					for (int i=0;i<rules.size();i++) {
						rule = (IRule)rules.get(i);
						cl.setChecked(rule, rule.isActive());
					}
				}
			}	
		);
		
		final Button changeButton = new Button(buttonGroup, SWT.PUSH);
		changeButton.setText(
			this.m_i18n.getString(
				this.NAMESPACE,
				"change",
				"label",
				this.m_language
			)	
		);
		changeButton.setEnabled(false);
		changeButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					StructuredSelection s = (StructuredSelection) cl.getSelection();
					if (!s.isEmpty()) {
						IRule rule = (IRule) s.getFirstElement();
						openRuleWizard(rule);
						IRule r = null;
						List rules = getRules();
						for (int i=0;i<rules.size();i++) {
							r = (IRule)rules.get(i);
							cl.setChecked(r, r.isActive());
						}						
					}
				}
			}	
		);
		
		final Button copyButton = new Button(buttonGroup, SWT.PUSH);
		copyButton.setText(
			this.m_i18n.getString(
				this.NAMESPACE,
				"copy",
				"label",
				this.m_language
			)	
		);
		copyButton.setEnabled(false);
		copyButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					StructuredSelection s = (StructuredSelection) cl.getSelection();
					if (!s.isEmpty()) {
						if (MessageDialog.openConfirm(
								e.display.getActiveShell(),
								m_i18n.getString(
										NAMESPACE,
										"copytitle",
										"label",
										m_language
									),
								m_i18n.getString(
										NAMESPACE,
										"copyconfirmation",
										"label",
										m_language
									)
								)
							)
						{
							List ruleList = s.toList();
							
							IRule r = null;
							IRule copiedRule = null;
							for (int i=0;i<ruleList.size();i++) {
								r = (IRule) ruleList.get(i);
								copiedRule = getRuntime().getRuleEngine().createRule(r.toString());
								copiedRule.setName(m_i18n.getString(
										NAMESPACE,
										"copyof",
										"label",
										m_language
									) + r.getName());
								m_rules.add(copiedRule);
								cl.add(copiedRule);
								cl.setChecked(copiedRule, copiedRule.isActive());
							}
							cl.refresh();
						}					
					}
				}
			}	
		);
		
		final Button deleteButton = new Button(buttonGroup, SWT.PUSH);
		deleteButton.setText(
			this.m_i18n.getString(
				this.NAMESPACE,
				"delete",
				"label",
				this.m_language
			)	
		);
		deleteButton.setEnabled(false);
		deleteButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					StructuredSelection s = (StructuredSelection) cl.getSelection();
					if (!s.isEmpty()) {
						if (MessageDialog.openConfirm(
								e.display.getActiveShell(),
								m_i18n.getString(
										NAMESPACE,
										"deletetitle",
										"label",
										m_language
									),
								m_i18n.getString(
										NAMESPACE,
										"deleteconfirmation",
										"label",
										m_language
									)
								)
							)
						{
							Object o = s.getFirstElement();
							cl.remove(o);
							m_rules.remove(o);
							cl.refresh();	
						}
					}
				}
			}
		);
		
		l = new Label(c, SWT.NONE);
		l.setText(
			this.m_i18n.getString(
				this.NAMESPACE,
				"ruledesc",
				"label",
				this.m_language
			)
		);
		
		final Text ruleAsText = new Text(c, SWT.BORDER | SWT.WRAP);
		ruleAsText.setLayoutData(new GridData(GridData.FILL_BOTH));
		ruleAsText.setEditable(false);
		
		cl.addSelectionChangedListener(
			new ISelectionChangedListener()	{
				public void selectionChanged(SelectionChangedEvent event) {
					StructuredSelection selected = (StructuredSelection) event.getSelection();
					if (selected.isEmpty()) {
						deleteButton.setEnabled(false);
						changeButton.setEnabled(false);
						copyButton.setEnabled(false);
					} else {
						deleteButton.setEnabled(true);
						changeButton.setEnabled(true);
						copyButton.setEnabled(true);
					}
					IRule rule = (IRule) selected.getFirstElement();
					ruleAsText.setText(createDescription(rule));
				}
			}
		);
		
		return c;
	}

	protected String createDescription(IRule rule){
		if (rule==null) return "";
		
		StringBuffer b = new StringBuffer();
		
		b.append(this.m_i18n.getString(this.NAMESPACE, "rd_1", "label", this.m_language));
		
		String msn = rule.getMsn().getMSN();
		if (msn.equalsIgnoreCase(Rule.GENERIC_SIGN)) {
			b.append(this.m_i18n.getString(this.NAMESPACE, "allmsn", "label", this.m_language));
		} else {
			b.append(this.m_i18n.getString(this.NAMESPACE, "singlemsn", "label", this.m_language) + msn);
		}
		
		b.append(this.m_i18n.getString(this.NAMESPACE, "rd_2", "label", this.m_language));
		
		String cip = this.getRuntime().getCipManager().getCipLabel(rule.getCip(), this.m_language);
		if (cip.equalsIgnoreCase(Rule.GENERIC_SIGN)) {
			b.append(this.m_i18n.getString(this.NAMESPACE, "allcip", "label", this.m_language));
		} else {
			b.append(this.m_i18n.getString(this.NAMESPACE, "singlecip", "label", this.m_language) + cip);
		}
		
		b.append(this.m_i18n.getString(this.NAMESPACE, "rd_3", "label", this.m_language));
		
		b.append(this.m_i18n.getString("service."+rule.getServiceID(), "title", "label", this.m_language));
		
		b.append(this.m_i18n.getString(this.NAMESPACE, "rd_4", "label", this.m_language));
		
		if (rule.getPhonenumbers()==null) {
			b.append(this.m_i18n.getString(this.NAMESPACE, "allcaller", "label", this.m_language));
		} else {
			b.append(this.m_i18n.getString(this.NAMESPACE, "singlecaller", "label", this.m_language) + "["+rule.getPhonenumbers().length+"]");
		}
		
		b.append(this.m_i18n.getString(this.NAMESPACE, "rd_5", "label", this.m_language));
		
		if (rule.getExcludePhonenumbers()==null) {
			b.append(this.m_i18n.getString(this.NAMESPACE, "nocaller", "label", this.m_language));
		} else {
			b.append(this.m_i18n.getString(this.NAMESPACE, "singlecaller", "label", this.m_language) + "["+rule.getExcludePhonenumbers().length+"]");
		}
		
		b.append(this.m_i18n.getString(this.NAMESPACE, "rd_6", "label", this.m_language));

		if (!rule.getTimeslot().equalsIgnoreCase("*")) {
			b.append(this.m_i18n.getString(this.NAMESPACE, "rd_8", "label", this.m_language));
			StringTokenizer st = new StringTokenizer(rule.getTimeslot(), ";");
			if (st.countTokens()==2) {
				StringTokenizer days = new StringTokenizer(st.nextToken(), ",");
				while (days.hasMoreTokens()) {
					b.append(this.m_i18n.getString(this.NAMESPACE, days.nextToken(), "label", this.m_language));
					if (days.hasMoreTokens())
						b.append(", ");
				}
				
				StringTokenizer hours = new StringTokenizer(st.nextToken(), ",");
				if (hours.countTokens()==4) {
					b.append(this.m_i18n.getString(this.NAMESPACE, "rd_9", "label", this.m_language));
					b.append(hours.nextElement()+":"+hours.nextToken());
					b.append(this.m_i18n.getString(this.NAMESPACE, "rd_10", "label", this.m_language));
					b.append(hours.nextElement()+":"+hours.nextToken());
				}
				b.append(this.m_i18n.getString(this.NAMESPACE, "rd_11", "label", this.m_language));
			}
		}
		
		b.append(this.m_i18n.getString(this.NAMESPACE, "rd_7", "label", this.m_language));
		
		b.append((rule.isActive() ? this.m_i18n.getString(this.NAMESPACE, "active", "label", this.m_language): this.m_i18n.getString(this.NAMESPACE, "notactive", "label", this.m_language)));
		
		return b.toString();
	}

	public String getParentNodeID() {
		return IConfigPage.ADVANCED_NODE;
	}

	public String getNodeID() {
		return "RuleAssistent".toLowerCase();
	}

	public int getNodePosition() {
		return 10;
	}
	
	private List getRules() {
		if (this.m_rules==null || this.m_rules.size()==0) {
			this.m_rules = new ArrayList();
			IRuleEngine ruleEditor = this.getRuntime().getRuleEngine();
				
			List rList = ruleEditor.getRules();
			for (int i=0;i<rList.size();i++) {
				IRule rule = (IRule)rList.get(i);
				if (rule!=null) {
					IRule r = ruleEditor.createRule(rule.toString());
					if (r!=null)
						this.m_rules.add(r);
				}
			}
		}
		return this.m_rules;
	}
	
	private String getRuleKey(IRule rule) {
		String key = rule.getName();
		key = StringUtils.replaceString(key, " ", "");
		key = StringUtils.replaceString(key, ":", "");
		key = StringUtils.replaceString(key, "\\", "");
		key = StringUtils.replaceString(key, "#", "");
		return key;
	}
	
	public String getConfigNamespace() {
		return this.RULE_NAMESPACE;
	}
	
	public boolean performOk() {
		PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().removeProperties(RULE_NAMESPACE);
		
		IRule rule = null;
		String key = null;		
		for (int i=0;i<this.m_rules.size();i++) {
			rule = (IRule)this.m_rules.get(i);
			key = this.getRuleKey(rule);
			this.getPreferenceStore().setValue(this.RULE_NAMESPACE+ SEPARATOR + key + "_rule", rule.toString());
		}
		
		return super.performOk();
	}
	
	private void openRuleWizard(IRule rule) {
	    Display display = DisplayManager.getDefaultDisplay();
		Shell shell = new Shell(display);

	    WizardDialog.setDefaultImage(SWTImageManager.getInstance(this.getRuntime()).get(IJAMConst.IMAGE_KEY_PIM_ICON));
	    RuleWizard ruleWiz = new RuleWizard(rule);
	    WizardDialog dlg = new WizardDialog(shell, ruleWiz);
	    dlg.open();
	    if (dlg.getReturnCode() == WizardDialog.OK) {
	    	IRule newRule = ruleWiz.getResult();
	    	if (rule!=null)
	    		this.m_rules.remove(rule);
	    	
	    	if (newRule!=null)
	    		this.m_rules.add(newRule);
	    }
	    cl.setInput(this.m_rules);
	}

}
