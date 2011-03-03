package de.janrufmonitor.ui.jface.application.dialer;

import java.util.Properties;

import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.IService;
import de.janrufmonitor.service.client.Client;
import de.janrufmonitor.service.client.request.handler.GetDialExtensions;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IHttpResponse;
import de.janrufmonitor.service.commons.http.IRequester;
import de.janrufmonitor.service.commons.http.RequesterFactory;
import de.janrufmonitor.ui.jface.application.AbstractAsyncDisplayCommand;
import de.janrufmonitor.ui.swt.DisplayManager;

public class ClientDialerCommand extends AbstractAsyncDisplayCommand implements IConfigurable {

	private static String NAMESPACE = "ui.jface.application.dialer.ClientDialerCommand";
	
	private IRuntime m_runtime;
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getNamespace() {
		return ClientDialerCommand.NAMESPACE;
	}

	public boolean isExecutable() {
		if (isConnected()) {
			IRequester r = this.getRequester(new GetDialExtensions());
			IHttpResponse resp = r.request();
			return (resp.getCode()!=404 && resp.getCode()!=500);
		}
		return false;
	}

	public String getID() {
		return "ClientDialerCommand";
	}

	public String getConfigurableID() {
		return this.getID();
	}

	public void setConfiguration(Properties configuration) {
	}

	public void asyncExecute() {
		ClientDialerDialog id = new ClientDialerDialog(new Shell(DisplayManager.getDefaultDisplay()));
		id.open();
	}

	private boolean isConnected() {
		IService client = PIMRuntime.getInstance().getServiceFactory().getService("Client");
		if (client!=null && client instanceof Client) {
			return ((Client)client).isConnected();
		}
		return false;
	}
	
	private IRequester getRequester(IHttpRequest request) {
		return RequesterFactory.getInstance().
			createRequester(request);
	}
}
