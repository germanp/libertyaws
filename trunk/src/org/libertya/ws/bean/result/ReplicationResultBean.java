package org.libertya.ws.bean.result;

import java.util.HashMap;
import java.util.Vector;

public class ReplicationResultBean extends ResultBean {

	/** Impuestos de la factura */
	protected Vector<String[]> eventLog = new Vector<String[]>();
	
	/**
	 * Constructor por defecto.  Ver superclase.
	 */
	public ReplicationResultBean() {
		super();
	}
	
	/**
	 * Constructor por defecto.  Ver superclase
	 */
	public ReplicationResultBean(boolean error, String errorMsg, HashMap<String, String> map) {
		super(error, errorMsg, map);
	}

	/** Basic getter para el log de eventos */
	public Vector<String[]> getEventLog() {
		return eventLog;
	}

	/** Basic setter para el log de eventos */
	public void setEventLog(Vector<String[]> eventLog) {
		this.eventLog = eventLog;
	}
	
	
}
