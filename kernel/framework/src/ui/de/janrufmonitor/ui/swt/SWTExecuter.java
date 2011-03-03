package de.janrufmonitor.ui.swt;


public abstract class SWTExecuter {

	private boolean m_sync;
	private String m_name;

	public SWTExecuter(boolean sync, String name) {
		this.m_sync = sync;
		this.m_name = "JAM-"+name +"#"+ Thread.currentThread().getName()+"-Thread-(non-deamon)";
	}
	
	public SWTExecuter(boolean sync) {
		this(sync, null);
	}

	public SWTExecuter(String name) {
		this(false, name);
	}
	
	public SWTExecuter() {
		this(false, null);
	}

	public void start() {
		Thread thread = null;
		if (this.m_sync) {
			thread = new Thread () {
				public void run () {
					DisplayManager.getDefaultDisplay().syncExec(
						new Runnable () {
							public void run () {
								execute();
							}
						}
					);
				}
			};
		} else {
			thread = new Thread () {
				public void run () {
					DisplayManager.getDefaultDisplay().asyncExec(
						new Runnable () {
							public void run () {
								execute();
							}
						}
					);
				}
			};
		}
		if (thread!=null) {
			if (this.m_name!=null) thread.setName(this.m_name);
			thread.start();
		}
	}
	
	protected abstract void execute();

}
