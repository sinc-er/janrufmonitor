package de.janrufmonitor.ui.jface.application.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.ui.swt.DisplayManager;

public class ErrorDialog extends org.eclipse.jface.dialogs.MessageDialogWithToggle {

	private String m_url;
	private String m_readMoreLabel;
	
	public ErrorDialog(int level, String dialogTitle, String message, String toggleMessage, boolean toggleState) {
		this(level, dialogTitle, message, toggleMessage, toggleState, null, null);
	}
	
	public ErrorDialog(int level, String dialogTitle, String message, String toggleMessage, boolean toggleState, String url, String readMoreLabel) {
		super(new Shell(DisplayManager.getDefaultDisplay()), dialogTitle, null, message, level, new String[] {"Ok"}, 0, toggleMessage, toggleState);
		setBlockOnOpen(true);
		this.m_readMoreLabel = readMoreLabel;
		this.m_url = url;
	}

	protected Control createDialogArea(Composite parent) {
		Control control = super.createDialogArea(parent);

		if (this.m_url!=null && this.m_readMoreLabel !=null) {
			HyperLink hl = new HyperLink(parent, SWT.LEFT);
			GridData gd = new GridData();
			gd.horizontalSpan = 2;
			hl.setLayoutData(gd);
			hl.setText(m_readMoreLabel);
			hl.addMouseListener( 
				new MouseAdapter() {
					public void mouseDown(MouseEvent e) {
						if (e.button==1) {
							Program.launch(m_url);
						}
					}
				}
			);
			hl.pack();
		}

		return control;
	}
}
