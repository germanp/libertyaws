package org.libertya.ws.bean.parameter;

import java.util.HashMap;

import org.libertya.wse.common.SimpleMap;
import org.libertya.wse.utils.MapTranslator;

public class ParameterBean {
	
	/** Usuario LY */
	protected String 					userName 	= "";
	/** Contraseña LY */
	protected String 					password 	= "";
	/** Compañía a acceder */
	protected int 						clientID 	= 0;
	/** Organización */
	protected int 						orgID 		= 0;
	/** Coleccion para la tabla principal */
	protected HashMap<String, String>	mainTable	= new HashMap<String, String>();


	/**
	 * Constructor base
	 */
	public ParameterBean() {
		// Implementado solo para java2wsdl
	}
	
	/**
	 * Constructor por defecto.  
	 * @param userName usuario LY
	 * @param password contraseña LY
	 * @param clientID compañía a acceder
	 * @param orgID organización a acceder
	 */
	public ParameterBean(String userName, String password, int clientID, int orgID)
	{
		this.userName = userName;
		this.password = password;
		this.clientID = clientID;
		this.orgID = orgID;
	}
	
	/**
	 * Constructor para wrapper
	 */
	public ParameterBean(String userName, String password, int clientID, int orgID, SimpleMap[] data)
	{
		this(userName, password, clientID, orgID);
		load(data);
	}
	
	/**
	 * Incorpora una nueva columna a la tabla principal. Por ejemplo, para
	 * la subclae BPartnerParameterBean, la tabla principal será C_BParnter;
	 * y para la subclase OrderParameterBean, dicha tabla será C_Order.
	 * @param columnName nombre de la columna
	 * @param columnValue valor de la columna
	 */
	public void addColumnToMainTable(String columnName, String columnValue) {
		addColumnOnTable(mainTable, columnName, columnValue);
	}
	
	/**
	 * Setea un valor en el mapa de datos correspondiente
	 * @param table map clave/valor 
	 * @param columnName nombre de la columna 
	 * @param columnValue valor de la columna
	 */
	protected void addColumnOnTable(HashMap<String, String> table, String columnName, String columnValue) {
		table.put(columnName, columnValue);
	}

	
	/** Basic getter para el username */
	public String getUserName() {
		return userName;
	}

	/** Basic setter para el username */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/** Basic getter para el password */
	public String getPassword() {
		return password;
	}

	/** Basic setter para el password */
	public void setPassword(String password) {
		this.password = password;
	}

	/** Basic getter para el ID de compañía */
	public int getClientID() {
		return clientID;
	}

	/** Basic setter para el ID de compañía */
	public void setClientID(int clientID) {
		this.clientID = clientID;
	}

	/** Basic getter para el ID de organización */
	public int getOrgID() {
		return orgID;
	}

	/** Basic setter para el ID de organización */
	public void setOrgID(int orgID) {
		this.orgID = orgID;
	}

	/** Basic getter para los datos de la tabla principal */
	public HashMap<String, String> getMainTable() {
		return mainTable;
	}

	/** Basic setter para los datos de la tabla principal */
	public void setMainTable(HashMap<String, String> mainTable) {
		this.mainTable = mainTable;
	}
		

	@Override
	public String toString() {
		StringBuffer out = new StringBuffer();
		out.append(this.getClass().getName()).append(" - ");
		out.append("UserName = ").append(userName).append("; ");
		out.append("ClientID = ").append(clientID).append("; ");
		out.append("OrgID = ").append(orgID).append("\n");
		if (mainTable!=null) {
			out.append("  mainTable: ");
			for (String key : mainTable.keySet())
				out.append(key).append(" = ").
					append(mainTable.get(key)).
					append("; ");
		}
		return out.toString();
	}
	
	/**
	 * Carga desde el wrapper
	 */
	public void load(SimpleMap[] data) {
		mainTable = MapTranslator.simpleMap2HashMap(data);
	}
}
