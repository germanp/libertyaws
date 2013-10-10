package org.libertya.wse.param;

public class Login {

	/** Usuario LY */
	protected String 					userName 	= "";
	/** Contraseña LY */
	protected String 					password 	= "";
	/** Compañía a acceder */
	protected int 						clientID 	= 0;
	/** Organización */
	protected int 						orgID 		= 0;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getClientID() {
		return clientID;
	}
	public void setClientID(int clientID) {
		this.clientID = clientID;
	}
	public int getOrgID() {
		return orgID;
	}
	public void setOrgID(int orgID) {
		this.orgID = orgID;
	}

	
	
}
