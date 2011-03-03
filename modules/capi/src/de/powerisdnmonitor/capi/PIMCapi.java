package de.powerisdnmonitor.capi;

import de.powerisdnmonitor.capi.util.ByteArray;
import de.janrufmonitor.framework.IJAMConst;

import org.capi.capi20.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *  This class implementes the CAPI 2.0 interface
 *
 *@author     Thilo Brandt
 *@created    17. August 2002
 */
public class PIMCapi implements Capi {
	
	private class PIMCapiObserver implements Runnable {

		private int appid = 0;
		private Capi capi = null;
		private List listeners = new ArrayList();

		public PIMCapiObserver(Capi capi, int appid) {
			this.appid = appid;
			this.capi = capi;
		}

		public void run() {
			m_logger.entering(PIMCapiObserver.class.getName(), "run");
			try {
				do {
					capi.waitForSignal(appid);
					synchronized(this.listeners) {
						Iterator els = listeners.iterator();
						while (els.hasNext()) {
							((CapiListener) els.next()).capiEventRaised();
						}
					}
				} while (!listeners.isEmpty());
			} catch (CapiException e) {
				synchronized(this.listeners) {
					Iterator els = listeners.iterator();
					while (els.hasNext()) {
						((CapiListener) els.next()).capiExceptionThrown(e);
					}
				}
			}
			m_logger.exiting(PIMCapiObserver.class.getName(), "run");
		}

		public void addListener(CapiListener listener) {
			m_logger.entering(PIMCapiObserver.class.getName(), "addListener");
			synchronized(this.listeners) {
				listeners.add(listener);
			}
			m_logger.exiting(PIMCapiObserver.class.getName(), "addListener");
		}

		public boolean removeListener(CapiListener listener) {
			m_logger.entering(PIMCapiObserver.class.getName(), "removeListener");
			synchronized(this.listeners) {
				listeners.remove(listener);
			}
			m_logger.info("Removing CapiListener <"+listener.toString()+">");
			m_logger.exiting(PIMCapiObserver.class.getName(), "removeListener");
			return listeners.isEmpty();
		}
		
		public String toString() {
			return "PIMCapiObserver#"+this.appid;
		}
	}
	
	protected Logger m_logger;

    private static Map observers = new HashMap();

    private native static int nInstalled();

    private native static String nGetManufacturer(int contr, int[] p_rc);

    private native static String nGetSerialNumber(int contr, int[] p_rc);

    private native static int nGetVersion(int contr, int[] ver);

    private native static int nRegister(int bufsize, int maxcon, int maxblocks, int maxlen, int[] p_appid);

    private native static int nRelease(int appid);

    private native static int nGetProfile(int contr, byte[] profile);

    private native static int nPutMessage(int appid, byte[] msg);

    private native static byte[] nGetMessage(int appid, int[] p_rc);

    private native static int nGetAddress(byte[] data);

    private native static byte[] nGetData(int address, int size);

    private native static void nReleaseData(byte[] data, int pointer);

    private native static int nWaitForSignal(int appid);

    private native static void init();

    private native static String nGetErrorMessage(int rc);

    private native static String nGetImplementationInfo();

    static {
    	try {
			System.loadLibrary("pimcapi");
			init();
    	} catch (UnsatisfiedLinkError ex) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, ex.getMessage(), ex);
    	}
    }
    
    public PIMCapi() {
		this.m_logger = LogManager.getLogManager().getLogger(
				IJAMConst.DEFAULT_LOGGER
		);
    }

    public String getImplementationInfo() {
        return "PIMCapi version 1.0, " + nGetImplementationInfo();
    }

    public void addListener(int appID, CapiListener listener) {
		this.m_logger.entering(PIMCapi.class.getName(), "addListener");
        PIMCapiObserver cn = null;
        Integer id = new Integer(appID);
        
        // observer does not exists, must be created
        // and started
        if (!observers.containsKey(id)) {
            cn = new PIMCapiObserver(this, appID);
			observers.put(id, cn);
			this.m_logger.info("Creating and starting new PIMCapiObserver: "+cn.toString());
			Thread t = new Thread(cn);
			t.setName(cn.toString());
			t.setDaemon(true);
			t.start();
        } else {
            cn = (PIMCapiObserver) observers.get(id);
			this.m_logger.info("Taking existing PIMCapiObserver: "+cn.toString());
        }
        cn.addListener(listener);
		this.m_logger.info("Adding CapiListener <"+listener.toString()+"> to PIMCapiObserver <"+cn.toString()+">");
		this.m_logger.exiting(PIMCapi.class.getName(), "addListener");
    }

    public void removeListener(int appID, CapiListener listener) {
		this.m_logger.entering(PIMCapi.class.getName(), "removeListener");
        Integer id = new Integer(appID);
        if (observers.containsKey(id)) {
			PIMCapiObserver cn = (PIMCapiObserver) observers.get(id);
			if (cn != null) {
				if (cn.removeListener(listener)) {
					// remove empty observer object
					observers.remove(id);
					// make object eligable for garbage collection
					cn = null;
					// release CAPI for this listener
					try {
						this.release(appID);
					} catch (CapiException e) {
						this.m_logger.log(Level.SEVERE, e.getMessage(), e);
					}
				}
			}
        }
		this.m_logger.exiting(PIMCapi.class.getName(), "removeListener");
    }

    public CapiMessage createMessage(int appID, int type, int number) throws CapiException {
        return new PIMCapiMessage(appID, type, number);
    }

    public CapiMessage createMessage(byte[] msg) throws CapiException {
        return new PIMCapiMessage(msg);
    }

    public int register() throws CapiException {
        return register(3072, 2, 2, 256);
    }

    public int register(int maxcon, int maxblocks, int maxlen) throws CapiException {
        return register(1024 + 1024 * maxcon, maxcon, maxblocks, maxlen);
    }

    public int register(int bufsize, int maxcon, int maxblocks, int maxlen) throws CapiException {
        int[] p_appid = new int[1];
        checkError(nRegister(bufsize, maxcon, maxblocks, maxlen, p_appid));
        return p_appid[0];
    }

    public void release(int appID) throws CapiException {
        checkError(nRelease(appID));
    }

    public void putMessage(CapiMessage msg) throws CapiException {
		this.m_logger.entering(PIMCapi.class.getName(), "putMessage");
        PIMCapiMessage message = (PIMCapiMessage) msg;
        int appID = message.getAppID();
        if (message.getType() == CapiMessage.DATA_B3_REQ) {
            byte[] data = message.getB3Data();
            int pointer = nGetAddress(data);
            message.setValue("Data length", new Short((short) data.length));
            message.setValue("Data", new Integer(pointer));
            checkError(nPutMessage(appID, msg.getBytes()));
            nReleaseData(data, pointer);
        } else {
            checkError(nPutMessage(appID, message.getBytes()));
        }
		this.m_logger.exiting(PIMCapi.class.getName(), "putMessage");
    }

    public CapiMessage getMessage(int appID) throws CapiException {
		this.m_logger.entering(PIMCapi.class.getName(), "getMessage");
        int[] p_rc = new int[1];
        byte[] v = nGetMessage(appID, p_rc);
        checkError(p_rc[0]);
        PIMCapiMessage msg = new PIMCapiMessage(v);
        if (msg.getType() == CapiMessage.DATA_B3_IND) {
            int pointer = ((Integer) msg.getValue("Data")).intValue();
            int size = ((Short) msg.getValue("Data length")).shortValue();
            msg.setB3Data(nGetData(pointer, size));
        }
		this.m_logger.exiting(PIMCapi.class.getName(), "getMessage");
        return msg;
    }

    public void waitForSignal(int appID) throws CapiException {
        checkError(nWaitForSignal(appID));
    }

    public boolean installed() throws CapiException {
        checkError(nInstalled());
        return true;
    }

    public String getManufacturer() throws CapiException {
        return getManufacturer(0);
    }

    public String getManufacturer(int controller) throws CapiException {
        int[] rc = new int[1];
        String m = nGetManufacturer(controller, rc);
        checkError(rc[0]);
        return m;
    }

    public int[] getVersion() throws CapiException {
        return getVersion(0);
    }

    public int[] getVersion(int controller) throws CapiException {
        int[] v = new int[4];
        checkError(nGetVersion(controller, v));
        return v;
    }

    public String getSerialNumber() throws CapiException {
        return getSerialNumber(0);
    }

    public String getSerialNumber(int controller) throws CapiException {
        int[] rc = new int[1];
        String sn = nGetSerialNumber(controller, rc);
        checkError(rc[0]);
        return sn;
    }

    public int getNumberOfControllers() throws CapiException {
        byte[] buf = getProfile(0);
        return ByteArray.getLowOrderInt(buf, 0, 2);
    }

    public byte[] getProfile(int controller) throws CapiException {
        byte[] v = new byte[64];
        checkError(nGetProfile(controller, v));
        return v;
    }

    private static void checkError(int rc) throws CapiException {
        if (rc < 0) {
            // internal
            throw new CapiException(nGetErrorMessage(rc));
        }
        if (rc != 0) {
            throw new CapiException(rc);
        }
    }
}
