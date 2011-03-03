package de.janrufmonitor.service.server.http.simple.handler;

import java.io.OutputStream;
import java.lang.reflect.Method;

import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.simple.handler.AbstractHandler;
import de.janrufmonitor.util.string.StringUtils;

public class GetDialExtensions extends AbstractHandler {

	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws HandlerException {
		String calltoclass = null;
		if (PIMRuntime.getInstance().getMonitorListener().getMonitor("FritzBoxMonitor")!=null) {
			calltoclass = "de.janrufmonitor.fritzbox.QuickDialer";
		} else if (PIMRuntime.getInstance().getMonitorListener().getMonitor("XTapiMonitor")!=null) {
			calltoclass = "de.janrufmonitor.xtapi.QuickDialer";
		} else {
			throw new HandlerException("No FritzBoxMonitor or XTapiMonitor available.", 404);
		}
		
		try {
			Class handler = Thread.currentThread().getContextClassLoader().loadClass(calltoclass);
			Object o = handler.newInstance();
			StringBuffer plain = new StringBuffer();

			try {
				Method m = o.getClass().getMethod("getAllExtensions", new Class[] {});
				if (m!=null) {
					Object retcode = m.invoke(o, new Object[] {});
					if (retcode!=null && retcode instanceof String[]) {
						for (int i=0, j=((String[])retcode).length;i<j;i++) {						
							plain.append(StringUtils.replaceString(((String[])retcode)[i], ",", "$ktoken$"));
							plain.append(",");
						}
					}
				}
			} catch (Exception ex) {
			}
			
			resp.setParameter("Content-Length", Long.toString(plain.length()));
			resp.setParameter("Content-Type", "text/plain");
			OutputStream ps = resp.getContentStreamForWrite();
			ps.write(plain.toString().getBytes());
			ps.flush();
			ps.close();
		} catch (HandlerException e) {
			throw e;
		} catch (ClassNotFoundException ex) {
			throw new HandlerException("Class not found: "+calltoclass, 500);
		} catch (InstantiationException e) {
			throw new HandlerException("Cannot instantiate class: "+calltoclass, 500);
		} catch (IllegalAccessException e) {
			throw new HandlerException("Illegal access for class: "+calltoclass, 500);
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
	}

}
