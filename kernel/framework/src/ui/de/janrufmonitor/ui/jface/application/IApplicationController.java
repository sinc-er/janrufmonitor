package de.janrufmonitor.ui.jface.application;

import java.util.Properties;

public interface IApplicationController {
	
	public void setConfiguration(Properties configuration, boolean initialize);
	
	public Object[] getElementArray();
	
	public void generateElementArray(Object[] data);
	
	public void deleteAllElements();
	
	public void deleteElements(Object list);
	
	public void addElements(Object list);
	
	public void updateElement(Object element);
	
	public int countElements();
	
	public void sortElements();
	
	public Object getRepository();
	
}
