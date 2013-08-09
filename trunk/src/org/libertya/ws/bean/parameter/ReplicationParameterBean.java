package org.libertya.ws.bean.parameter;

public class ReplicationParameterBean extends ParameterBean {

	/** Acciones a efectuar */
	protected byte[] actionsXML;
	/** Host que origina la invocación */ 
	protected int sourceHostPos;
	/** Host destinatario */ 
	protected int targetHostPos;

	
	/**
	 * Constructor por defecto.  Ver superclase.
	 */
	public ReplicationParameterBean() {
		super();
	}

	
	/**
	 * Constructor por defecto.  Ver superclase.
	 */
	public ReplicationParameterBean(String userName, String password, int clientID,	int orgID) {
		super(userName, password, clientID, orgID);
	}

	/** Basic getter para las acciones */
	public byte[] getActionsXML() {
		return actionsXML;
	}

	/** Basic setter para las acciones */
	public void setActionsXML(byte[] actionsXML) {
		this.actionsXML = actionsXML;
	}

	/** Basic getter para host origen */
	public int getSourceHostPos() {
		return sourceHostPos;
	}

	/** Basic setter para host origen */
	public void setSourceHostPos(int sourceHostPos) {
		this.sourceHostPos = sourceHostPos;
	}
	
	/** Basic getter para host destino */
	public int getTargetHostPos() {
		return targetHostPos;
	}

	/** Basic setter para host destino */
	public void setTargetHostPos(int targetHostPos) {
		this.targetHostPos = targetHostPos;
	}
	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n  ");
		out.append("sourceHostPos = ").append(sourceHostPos).append("; ");
		out.append("targetHostPos = ").append(targetHostPos).append("; ");
		return out.toString();
	}
	
}
