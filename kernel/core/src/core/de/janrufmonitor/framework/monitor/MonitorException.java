package de.janrufmonitor.framework.monitor;

public class MonitorException extends Exception {

	private static final long serialVersionUID = 1L;


	/**
     *  Constructor for the MonitorException object
     */
    public MonitorException() {
        super();
    }


    /**
     *  Constructor for the MonitorException object
     *
     *@param  msg  Description of the Parameter
     */
    public MonitorException(String msg) {
        super(msg);
    }

}
