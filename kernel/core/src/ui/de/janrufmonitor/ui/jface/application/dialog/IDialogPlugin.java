package de.janrufmonitor.ui.jface.application.dialog;

public interface IDialogPlugin {

	public String getLabel();
	
	public void setDialog(IDialog d);
	
	public void run();
	
	public boolean isEnabled();
	
	public void setID(String id);
	
}
