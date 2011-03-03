package de.janrufmonitor.ui.jface.application.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.filter.IFilter;
import de.janrufmonitor.repository.types.IReadCallerRepository;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.IApplicationController;

public class EditorController implements IApplicationController,
		EditorConfigConst {

	protected Logger m_logger;

	protected Properties m_configuration;

	private IRuntime m_runtime;

	private ICallerList m_data;

	public EditorController() {
		this.m_logger = LogManager.getLogManager().getLogger(
				IJAMConst.DEFAULT_LOGGER);
	}

	public void setConfiguration(Properties configuration, boolean initialize) {
		if (configuration != null)
			this.m_configuration = configuration;
		else {
			this.m_logger
					.severe("Configuration data in controller is invalid.");
		}
		if (initialize)
			this.m_data = null;
	}

	public synchronized Object[] getElementArray() {
		if (this.m_data == null)
			this.buildControllerData();

		return this.m_data.toArray();
	}

	public synchronized void deleteAllElements() {
		if (this.m_data != null) {
			this.deleteElements(this.m_data);
		}
	}

	public synchronized void deleteElements(Object list) {
		if (list != null && list instanceof ICallerList) {
			Map cms = new HashMap();

			ICallerList tmplist = null;
			ICaller c = null;
			for (int i = 0; i < ((ICallerList) list).size(); i++) {
				c = ((ICallerList) list).get(i);
				IAttribute att = c
						.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLERMANAGER);

				String cname = (att == null ? "all" : att.getValue());
				if (cms.containsKey(cname)) {
					tmplist = (ICallerList) cms.get(cname);
					tmplist.add(c);
				} else {
					tmplist = this.getRuntime().getCallerFactory()
							.createCallerList(1);
					tmplist.add(c);
					cms.put(cname, tmplist);
				}
			}

			List managers = this.getActiveCallerManagers();

			ICallerManager mgr = null;
			for (int i = 0; i < managers.size(); i++) {
				mgr = this.getRuntime().getCallerManagerFactory()
						.getCallerManager((String) managers.get(i));
				if (mgr != null) {
					if (mgr.isSupported(IWriteCallerRepository.class)) {
						tmplist = (ICallerList) cms.get("all");
						if (tmplist != null) {
							this.m_logger.info("removing " + tmplist.size()
									+ " callers to manager: "
									+ mgr.getManagerID());
							((IWriteCallerRepository) mgr)
									.removeCaller(tmplist);
						}

						tmplist = (ICallerList) cms.get(mgr.getManagerID());
						if (tmplist != null) {
							this.m_logger.info("removing " + tmplist.size()
									+ " callers to manager: "
									+ mgr.getManagerID());
							((IWriteCallerRepository) mgr)
									.removeCaller(tmplist);
						}
					}
				}
			}
		}
	}

	public synchronized void addElements(Object list) {
		if (list != null && list instanceof ICallerList) {
			ICallerManager mgr = this._getRepository();
			if (mgr != null) {
				if (mgr.isSupported(IWriteCallerRepository.class)) {
					((IWriteCallerRepository) mgr)
							.setCaller((ICallerList) list);
				}
			}
		}
	}

	public synchronized void updateElement(Object element) {
		if (element != null && element instanceof ICallerList) {
			ICallerManager mgr = this._getRepository();
			if (mgr != null) {
				if (mgr.isSupported(IWriteCallerRepository.class)) {
					for (int i=0,j=((ICallerList)element).size();i<j;i++) {
						((IWriteCallerRepository) mgr)
						.updateCaller(((ICallerList)element).get(i));
					}
				}
			}			
		}
		if (element != null && element instanceof ICaller) {
			List managers = this.getActiveCallerManagers();

			ICallerManager mgr = null;
			IAttribute att = ((ICaller) element)
					.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLERMANAGER);

			for (int i = 0; i < managers.size(); i++) {
				mgr = this.getRuntime().getCallerManagerFactory()
						.getCallerManager((String) managers.get(i));
				if (mgr != null) {
					if (mgr.isSupported(IWriteCallerRepository.class)) {
						if (att == null
								|| att.getValue().equalsIgnoreCase(
										mgr.getManagerID()))
							((IWriteCallerRepository) mgr)
									.updateCaller((ICaller) element);
					}
				}
			}
		}
	}

	public synchronized int countElements() {
		if (this.m_data == null)
			this.buildControllerData();

		return this.m_data.size();
	}

	public synchronized void sortElements() {
		if (this.m_data == null)
			this.buildControllerData();
		doSorting();
	}

	public ICallerList getCallerList() {
		return this.m_data;
	}

	private void doSorting() {
		if (this.m_data != null && this.m_data.size() > 1) {
			this.m_data.sort(this.getSortOrder(), this.getSortDirection());
		}
	}

	private void buildControllerData() {
		ICallerManager cm = this._getRepository();
		if (cm != null && cm.isActive()
				&& cm.isSupported(IReadCallerRepository.class)) {
			this.m_data = ((IReadCallerRepository) cm).getCallers(this
					.getFilter());
			this.doSorting();
		}
		if (this.m_data == null)
			this.m_data = this.getRuntime().getCallerFactory()
					.createCallerList();
	}

	private ICallerManager _getRepository() {
		String managerID = this.m_configuration.getProperty(CFG_REPOSITORY, "");
		if (managerID.length() > 0) {
			ICallerManager cm = this.getRuntime().getCallerManagerFactory()
					.getCallerManager(managerID);
			if (cm != null)
				return cm;
		}
		this.m_logger.severe("CallerManager with ID " + managerID
				+ " does not exist.");
		return null;
	}

	protected IRuntime getRuntime() {
		if (this.m_runtime == null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	protected int getSortOrder() {
		return Integer.parseInt(this.m_configuration
				.getProperty(CFG_ORDER, "2"));
	}

	protected boolean getSortDirection() {
		return (this.m_configuration.getProperty(CFG_DIRECTION, "false"))
				.equalsIgnoreCase("true");
	}

	protected IFilter getFilter() {
		String fstring = this.m_configuration.getProperty(CFG_FILTER, "");
		IFilter[] f = new EditorFilterManager().getFiltersFromString(fstring);
		return (f != null && f.length > 0 ? f[0] : null);
	}

	protected List getActiveCallerManagers() {
		List l = new ArrayList();
		l.add(this._getRepository().getManagerID());
		return l;
	}

	// protected List getActiveCallerManagers() {
	// String[] managers =
	// this.getRuntime().getCallerManagerFactory().getAllCallerManagerIDs();
	//		
	// List l = new ArrayList();
	//		
	// for (int i=0;i<managers.length;i++) {
	// ICallerManager man =
	// this.getRuntime().getCallerManagerFactory().getCallerManager(managers[i]);
	// if (man !=null && man.isActive()){
	// l.add(managers[i]);
	// }
	// }
	// return l;
	// }

	public void generateElementArray(Object[] data) {
		if (data != null) {
			this.m_data = this.getRuntime().getCallerFactory()
					.createCallerList();
			for (int i = 0; i < data.length; i++) {
				if (data[i] instanceof ICaller)
					this.m_data.add((ICaller) data[i]);
			}
		}
	}

	public Object getRepository() {
		return this._getRepository();
	}

}
