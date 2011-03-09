package de.janrufmonitor.repository;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import de.janrufmonitor.framework.CallListComparator;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.framework.event.IEvent;
import de.janrufmonitor.framework.event.IEventBroker;
import de.janrufmonitor.framework.event.IEventConst;
import de.janrufmonitor.framework.event.IEventReceiver;
import de.janrufmonitor.repository.xml.XMLSerializer;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.io.Stream;

public class HtmlCallManager extends AbstractPersistentCallManager implements
		IConfigurable, IEventReceiver {

	private String ID = "HtmlCallManager";
	private String NAMESPACE = "repository.HtmlCallManager";

	private String CONFIG_KEY = "database";

	private IRuntime m_runtime;

	public HtmlCallManager() {
		super();
		this.getRuntime().getConfigurableNotifier().register(this);
	}

	public void loadDatabase() {
		File xml = this.getXmlFile();
		if (!xml.exists()) {
			if (!xml.getParentFile().exists()) xml.getParentFile().mkdirs();

			try {
				FileOutputStream fos = new FileOutputStream(xml);
				ByteArrayInputStream in = new ByteArrayInputStream(XMLSerializer.toXML(this.m_callList, false).getBytes());
				Stream.copy(in, fos, true);
			} catch (FileNotFoundException e) {
				this.m_logger.severe("File not found: " + xml.getAbsolutePath());
			} catch (IOException e) {
				this.m_logger.severe("Can't create new file " + xml.getAbsolutePath()
						+ ": " + e.getMessage());
			}
		}
		try {
			FileInputStream fin = new FileInputStream(xml);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Stream.copy(fin, out, true);
			this.m_callList = XMLSerializer.toCallList(out.toString());
		} catch (FileNotFoundException e) {
			this.m_logger.severe("File not found: " + xml.getAbsolutePath());
		} catch (IOException e) {
			this.m_logger.severe("IO Error on file " + xml.getAbsolutePath()
					+ ": " + e.getMessage());
		}
	}

	public void saveDatabase() {
		File xml = this.getXmlFile();

		if (!xml.getParentFile().exists()) xml.getParentFile().mkdirs();

		try {
			FileOutputStream fos = new FileOutputStream(xml);
			ByteArrayInputStream in = new ByteArrayInputStream(XMLSerializer.toXML(this.m_callList, false).getBytes());
			Stream.copy(in, fos, true);
		} catch (FileNotFoundException e) {
			this.m_logger.severe("File not found: " + xml.getAbsolutePath());
		} catch (IOException e) {
			this.m_logger.severe("Can't create new file " + xml.getAbsolutePath()
					+ ": " + e.getMessage());

		}
		this.generateHtml(this.getHtmlFile());
		
		// check for file size
		if (xml.length()>300000L) {
			// split file
			SimpleDateFormat formatter
			 = new SimpleDateFormat("yyyyMMdd");
			String ts = formatter.format(new Date(System.currentTimeMillis()));
			xml.renameTo(new File(xml.getAbsolutePath() + "."+ts));
			this.getHtmlFile().renameTo(new File(this.getHtmlFile().getAbsolutePath() + "."+ts));
			
			this.m_callList.clear();
		}
		
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public void setConfiguration(Properties configuration) {
		super.setConfiguration(configuration);
		this.m_callList = this.getRuntime().getCallFactory().createCallList(0);
		this.loadDatabase();
	}

	public IRuntime getRuntime() {
		if (this.m_runtime == null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	public void received(IEvent event) {
		if (event.getType() == IEventConst.EVENT_TYPE_CALLACCEPTED
				|| event.getType() == IEventConst.EVENT_TYPE_CALLCLEARED
				|| event.getType() == IEventConst.EVENT_TYPE_CALLREJECTED)

		this.saveDatabase();
	}

	public String getReceiverID() {
		return this.getID();
	}

	public void shutdown() {
		super.shutdown();
		IEventBroker b = this.getRuntime().getEventBroker();
		b.unregister(this, b.createEvent(IEventConst.EVENT_TYPE_CALLACCEPTED));
		b.unregister(this, b.createEvent(IEventConst.EVENT_TYPE_CALLCLEARED));
		b.unregister(this, b.createEvent(IEventConst.EVENT_TYPE_CALLREJECTED));
		//this.saveDatabase();
	}

	public void startup() {
		super.startup();
		IEventBroker b = this.getRuntime().getEventBroker();
		b.register(this, b.createEvent(IEventConst.EVENT_TYPE_CALLACCEPTED));
		b.register(this, b.createEvent(IEventConst.EVENT_TYPE_CALLCLEARED));
		b.register(this, b.createEvent(IEventConst.EVENT_TYPE_CALLREJECTED));
		if (this.m_callList==null)
			this.m_callList = getRuntime().getCallFactory().createCallList();
	}

	public String getID() {
		return this.ID;
	}

	private File getHtmlFile() {
		return new File(PathResolver.getInstance(this.getRuntime()).resolve(this.m_configuration.getProperty(CONFIG_KEY, IJAMConst.PATHKEY_DATAPATH+"journal.html")));
	}
	
	private File getXmlFile() {
		return new File(getHtmlFile().getParentFile(), "~" + getHtmlFile().getName() + ".xml");
	}


	private void generateHtml(File htmlFile) {
		this.m_logger.info("Writing " + this.m_callList.size()
				+ " calls to html format");

		if (!htmlFile.exists()) {
			try {
				if (!htmlFile.getParentFile().exists())
					htmlFile.getParentFile().mkdirs();

				FileOutputStream fos = new FileOutputStream(htmlFile);
				fos.write("".getBytes());
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				this.m_logger.severe("File not found: " + htmlFile.getAbsolutePath());
			} catch (IOException e) {
				this.m_logger.severe("Can't create new file " + htmlFile.getAbsolutePath()
						+ ": " + e.getMessage());
			}
		}

		try {
			FileWriter dbWriter = new FileWriter(htmlFile);
			BufferedWriter bufWriter = new BufferedWriter(dbWriter);

			// added 2005/10/03: sorting by date
			this.m_callList.sort(CallListComparator.ORDER_DATE, false);
			StringBuffer sb = JournalBuilder.parseFromTemplate(this.m_callList, this.m_configuration);
			if (sb!=null)
				bufWriter.write(sb.toString());

			bufWriter.flush();
			bufWriter.close();
			dbWriter.close();
		} catch (FileNotFoundException ex) {
			this.m_logger.severe("File not found: " + htmlFile.getAbsolutePath());
		} catch (IOException ex) {
			this.m_logger.severe("IO Error on file " + htmlFile.getAbsolutePath());
		}
	}

}