package org.libertya.ws.bean.parameter;

import java.util.HashMap;

public class BPartnerParameterBean extends ParameterBean {

	/** Coleccion para las subtablas C_BPartnerLocation y C_Location */
	protected HashMap<String, String>	location	= new HashMap<String, String>();

	/**
	 * Constructor por defecto. Ver superclase.
	 */
	public BPartnerParameterBean() {
		super();
	}
	
	/**
	 * Constructor por defecto.  Ver superclase.
	 */
	public BPartnerParameterBean(String userName, String password, int clientID, int orgID) {
		super(userName, password, clientID, orgID);
	}

	/**
	 * Incorpora una nueva columna a los datos de parámetro la E.C. 
	 * @param columnName nombre de la columna
	 * @param columnValue valor de la columna
	 */
	public void addColumnToBPartner(String columnName, String columnValue) {
		addColumnToMainTable(columnName, columnValue);
	}
	
	/**
	 * Incorpora una nueva columna a los datos de parámetro de dirección de la E.C. 
	 * @param columnName nombre de la columna
	 * @param columnValue valor de la columna
	 */
	public void addColumnToLocation(String columnName, String columnValue) {
		addColumnOnTable(location, columnName, columnValue);
	}

	/**
	 * @return las columnas de datos de parámetro la E.C.
	 */
	public HashMap<String, String> getBPartnerColumns() {
		return mainTable;
	}
	
	/**
	 * @return las columnas de datos de parámetro de direccion la E.C.
	 */
	public HashMap<String, String> getLocationColumns() {
		return location;
	}
	
	/** Basic getter para la direccion */
	public HashMap<String, String> getLocation() {
		return location;
	}
	
	/** Basic setter para la direccion */
	public void setLocation(HashMap<String, String> location) {
		this.location = location;
	}

	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n  location: ");
		for (String key : location.keySet())
			out.append(key).append(" = ").
				append(location.get(key)).
				append("; ");
		return out.toString();

		
	}
}
