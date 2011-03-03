package de.janrufmonitor.ui.jface.configuration.controls;

import org.eclipse.swt.widgets.Composite;


public class BooleanFieldEditor extends org.eclipse.jface.preference.BooleanFieldEditor {

	public BooleanFieldEditor(String arg0, String arg1, int arg2, Composite arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public BooleanFieldEditor(String arg0, String arg1, Composite arg2) {
		super(arg0, arg1, arg2);
	}
	
	public void doLoad() {
		super.doLoad();
	}
	
	public void doLoadDefault() {
		super.doLoadDefault();
	}
}
