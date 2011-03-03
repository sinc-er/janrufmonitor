package de.janrufmonitor.ui.jface.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.ui.jface.application.action.IAction;

public abstract class AbstractMenuBuilder {

	private static String SEPARATOR_SIGN = "-";
	
	protected IApplication m_app;
	protected Logger m_logger;
	protected List m_addActions;
	protected List m_addPopupMenuActions;
	
	private II18nManager m_i18n;
	private String m_language;
	
	public AbstractMenuBuilder(IApplication app, List additionalActions, List additionalPopupActions) {
		this.m_app = app;
		this.m_addActions = additionalActions;
		this.m_addPopupMenuActions = additionalPopupActions;
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		this.checkAddActions();
	}
	
	protected void addAction(MenuManager m, String id) {
		if (id==null) return;
		
		if (id.startsWith(AbstractMenuBuilder.SEPARATOR_SIGN)) {
			this.addSeparator(m);
			return;
		}
		
		IAction a = ActionRegistry.getInstance().getAction(id, this.m_app);
		if (a!=null) {
			if (a.hasSubActions() && a instanceof AbstractAction) {
				IAction[] subActions = a.getSubActions();
				
				if (subActions!=null && subActions.length>0) {
					MenuManager subActionsMenu = new MenuManager(
						((AbstractAction)a).getText()
					);
					m.add(subActionsMenu);
					for (int i=0;i<subActions.length;i++) {
						subActionsMenu.add((org.eclipse.jface.action.IAction)subActions[i]);
					}
				}
			} else {
				m.add((org.eclipse.jface.action.IAction)a);
				this.m_logger.info("Added menu entry <"+id+"> to "+this.m_app.getID());

			}
		}
	}
	
	protected void addAction(List m, String id) {
		if (id.equalsIgnoreCase(AbstractMenuBuilder.SEPARATOR_SIGN)) {
			return;
		}
		
		IAction a = ActionRegistry.getInstance().getAction(id, this.m_app);
		if (a!=null) {
			m.add((org.eclipse.jface.action.IAction)a);
			this.m_logger.info("Added menu entry <"+id+"> to "+this.m_app.getID());
		}
	}	
	
	protected void addSeparator(MenuManager m) {
		m.add(new Separator());
	}
	
	protected II18nManager getI18nManager() {
		if (this.m_i18n==null) {
			this.m_i18n = this.getRuntime().getI18nManagerFactory().getI18nManager();
		}
		return this.m_i18n;
	}
	
	protected String getLanguage() {
		if (this.m_language==null) {
			this.m_language = 
				this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(
					IJAMConst.GLOBAL_NAMESPACE,
					IJAMConst.GLOBAL_LANGUAGE
				);
		}
		return this.m_language;
	}
	
	private void checkAddActions() {
		if (this.m_addActions==null)
			this.m_addActions = new ArrayList();
		if (this.m_addPopupMenuActions==null)
			this.m_addPopupMenuActions = new ArrayList();		
	}

	public abstract IRuntime getRuntime();
	
	public abstract MenuManager createMenu();
	
	public abstract Menu createPopupMenu(Control c);

	public List getToolbarActions() {
		return Collections.EMPTY_LIST;
	}
	
	public List getStartupActions() {
		return Collections.EMPTY_LIST;
	}
	
}
