package de.janrufmonitor.ui.jface.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import de.janrufmonitor.framework.configuration.IConfigManager;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.io.PathResolver;

public class PreferenceConfigManagerStore implements IPreferenceStore, IPersistentPreferenceStore {

	private static String NAMESPACE_SEPARATOR = ":";
    private String DEFAULT_VALUE_IDENTIFIER = "value";
    private String DEFAULT_DEFAULT_IDENTIFIER = "default";
    
	private IConfigManager m_cm;
	private List m_pcl;
	private IRuntime m_runtime;
	
	public PreferenceConfigManagerStore() {
		super();
		this.m_cm = this.getRuntime().getConfigManagerFactory().getConfigManager();
		this.m_pcl = new ArrayList();
	}

	public void addPropertyChangeListener(IPropertyChangeListener propertyChangeListener) {
		if (!this.m_pcl.contains(propertyChangeListener)) {
			this.m_pcl.add(propertyChangeListener);
		}
	}

	public boolean contains(String property) {
		return true;
	}

	public void firePropertyChangeEvent(String property, Object oldValue, Object newValue) {
		IPropertyChangeListener pcl = null;
		for (int i=0;i<this.m_pcl.size();i++) {
			pcl = (IPropertyChangeListener)this.m_pcl.get(i);
			pcl.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
		}
		
		// 2004/12/28: removed due to performance reasons
		//String[] p = this.tokenizeProperty(property);
		//this.getRuntime().getConfigurableNotifier().notifyByNamespace(p[0]);
	}

	public boolean getBoolean(String property) {
		String[] propertyData = this.tokenizeProperty(property);
		return Boolean.valueOf(this.m_cm.getProperty(propertyData[0], propertyData[1], DEFAULT_VALUE_IDENTIFIER)).booleanValue();
	}

	public boolean getDefaultBoolean(String property) {
		String[] propertyData = this.tokenizeProperty(property);
		return Boolean.valueOf(this.m_cm.getProperty(propertyData[0], propertyData[1], DEFAULT_DEFAULT_IDENTIFIER)).booleanValue();
	}

	public double getDefaultDouble(String property) {
		String[] propertyData = this.tokenizeProperty(property);
		return Double.parseDouble(this.m_cm.getProperty(propertyData[0], propertyData[1], DEFAULT_DEFAULT_IDENTIFIER));
	}

	public float getDefaultFloat(String property) {
		String[] propertyData = this.tokenizeProperty(property);
		return Float.parseFloat(this.m_cm.getProperty(propertyData[0], propertyData[1], DEFAULT_DEFAULT_IDENTIFIER));
	}

	public int getDefaultInt(String property) {
		String[] propertyData = this.tokenizeProperty(property);
		return Integer.parseInt(this.m_cm.getProperty(propertyData[0], propertyData[1], DEFAULT_DEFAULT_IDENTIFIER));
	}

	public long getDefaultLong(String property) {
		String[] propertyData = this.tokenizeProperty(property);
		return Long.parseLong(this.m_cm.getProperty(propertyData[0], propertyData[1], DEFAULT_DEFAULT_IDENTIFIER));
	}

	public String getDefaultString(String property) {
		String[] propertyData = this.tokenizeProperty(property);
		return this.decode(this.m_cm.getProperty(propertyData[0], propertyData[1], DEFAULT_DEFAULT_IDENTIFIER));
	}

	public double getDouble(String property) {
		String[] propertyData = this.tokenizeProperty(property);
		return Double.parseDouble(this.m_cm.getProperty(propertyData[0], propertyData[1], DEFAULT_VALUE_IDENTIFIER));
	}

	public float getFloat(String property) {
		String[] propertyData = this.tokenizeProperty(property);
		return Float.parseFloat(this.m_cm.getProperty(propertyData[0], propertyData[1], DEFAULT_VALUE_IDENTIFIER));
	}

	public int getInt(String property) {
		String[] propertyData = this.tokenizeProperty(property);
		return Integer.parseInt(this.m_cm.getProperty(propertyData[0], propertyData[1], DEFAULT_VALUE_IDENTIFIER));
	}

	public long getLong(String property) {
		String[] propertyData = this.tokenizeProperty(property);
		return Long.parseLong(this.m_cm.getProperty(propertyData[0], propertyData[1], DEFAULT_VALUE_IDENTIFIER));
	}

	public String getString(String property) {
		String[] propertyData = this.tokenizeProperty(property);
		return this.decode(this.m_cm.getProperty(propertyData[0], propertyData[1], DEFAULT_VALUE_IDENTIFIER));
	}

	public boolean isDefault(String property) {
		return this.getDefaultString(property).equalsIgnoreCase(this.getString(property));
	}

	public boolean needsSaving() {
		return true;
	}

	public void putValue(String property, String s) {
		this.setValue(property, s);
	}

	public void removePropertyChangeListener(IPropertyChangeListener propertyChangeListener) {
		if (this.m_pcl.contains(propertyChangeListener)) {
			this.m_pcl.remove(propertyChangeListener);
		}
	}

	public void setDefault(String property, double d) {
		this.setDefault(property, Double.toString(d));
	}

	public void setDefault(String property, float f) {
		this.setDefault(property, Float.toString(f));
	}

	public void setDefault(String property, int i) {
		this.setDefault(property, Integer.toString(i));
	}

	public void setDefault(String property, long l) {
		this.setDefault(property, Long.toString(l));
	}

	public void setDefault(String property, String s) {
		String[] propertyData = this.tokenizeProperty(property);
		this.m_cm.setProperty(propertyData[0], propertyData[1], this.DEFAULT_DEFAULT_IDENTIFIER);
	}

	public void setDefault(String property, boolean b) {
		this.setDefault(property, Boolean.toString(b));
	}

	public void setToDefault(String property) {
		this.setValue(property, this.getDefaultString(property));
	}

	public void setValue(String property, double d) {
		this.setValue(property, Double.toString(d));
	}

	public void setValue(String property, float f) {
		this.setValue(property, Float.toString(f));
	}

	public void setValue(String property, int i) {
		this.setValue(property, Integer.toString(i));
	}

	public void setValue(String property, long l) {
		this.setValue(property, Long.toString(l));
	}

	public void setValue(String property, String s) {
		String oldValue = this.getString(property);
		String[] propertyData = this.tokenizeProperty(property);
		this.m_cm.setProperty(propertyData[0], propertyData[1], this.encode(s));
		this.firePropertyChangeEvent(property, oldValue, this.encode(s));
	}

	public void setValue(String property, boolean b) {
		this.setValue(property, Boolean.toString(b));
	}
	
	private String[] tokenizeProperty(String property) {
		StringTokenizer st = new StringTokenizer(property, NAMESPACE_SEPARATOR);
		String[] tokens = new String[2];
		if (st.countTokens()>=2) {
			tokens[0] = st.nextToken();
			tokens[1] = st.nextToken();
			return tokens;
		} 
		return new String[] {"-", "-"};
	}

	public void save() throws IOException {
		this.m_cm.saveConfiguration();
	}
	
	private String encode(String s) {
		return PathResolver.getInstance(getRuntime()).encode(s);
	}
	
	private String decode(String s){
		return PathResolver.getInstance(getRuntime()).resolve(s);
	}
	
	private IRuntime getRuntime() {
		if (this.m_runtime==null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

}
