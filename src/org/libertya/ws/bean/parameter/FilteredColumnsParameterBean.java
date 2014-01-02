package org.libertya.ws.bean.parameter;

import java.util.ArrayList;

public class FilteredColumnsParameterBean extends ParameterBean {

	
	/** Columnas a devolver, o todas si la colección se encuentra sin entradas */
	protected ArrayList<String> filterColumns = new ArrayList<String>();
	
	/**
	 * Constructor por defecto.  Ver superclase.
	 */
	public FilteredColumnsParameterBean() {
		super();
	}

	/**
	 * Constructor por defecto.  Ver superclase.
	 */
	public FilteredColumnsParameterBean(String userName, String password, int clientID,	int orgID) {
		super(userName, password, clientID, orgID);
	}

	/**
	 * Constructor para wrapper
	 */
	public FilteredColumnsParameterBean(String userName, String password, int clientID,	int orgID, String[] filterColumns) {
		super(userName, password, clientID, orgID);
		load(filterColumns);
	}
	
	
	/**
	 * Incorpora una nueva columna a la nómina de columnas a incluir
	 * @param columnName nombre de la columna
	 */
	public void addColumnToFilter(String columnName) {
		filterColumns.add(columnName);
	}
	
	/** Default filterColumns getter */
	public ArrayList<String> getFilterColumns() {
		return filterColumns;
	}

	/** Default filterColumns setter */
	public void setFilterColumns(ArrayList<String> filterColumns) {
		this.filterColumns = filterColumns;
	}


	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n  Filtered Columns: ");
		if (filterColumns!=null)
			for (String column : filterColumns)
				if (column!=null)
					out.append("\n    " + column);
		return out.toString();
	}

	public void load(String[] columns) {
		if (columns != null) {
			for (String aColumn : columns) 
				filterColumns.add(aColumn);
		}
	}
}
