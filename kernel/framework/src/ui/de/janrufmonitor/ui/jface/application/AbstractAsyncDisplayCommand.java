package de.janrufmonitor.ui.jface.application;

import de.janrufmonitor.framework.command.AbstractCommand;
import de.janrufmonitor.ui.swt.DisplayManager;

public abstract class AbstractAsyncDisplayCommand extends AbstractCommand {

	protected boolean isExecuting;
	
	public boolean isExecuting() {
		return this.isExecuting;
	}
	
	public void execute() {
		this.isExecuting = true;
		Thread thread = new Thread () {
			public void run () {
				DisplayManager.getDefaultDisplay().asyncExec(
					new Runnable() {
						public void run() {
							asyncExecute();
						}
					}
				);
			}
		};
		thread.setName(this.getID());
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			this.m_logger.severe(e.getMessage());
		}
		
		this.isExecuting = false;
	}
	
	public abstract void asyncExecute();

}
