package org.libertya.ws.bean.result;

import java.util.ArrayList;
import java.util.HashMap;

public class MultipleRecordsResultBean extends ResultBean {

	/** Registros a retornar */
	protected ArrayList<HashMap<String, String>> records = new ArrayList<HashMap<String, String>>();
	
	/**
	 * Constructor por defecto.  Ver superclase
	 */
	public MultipleRecordsResultBean() {
		super();
	}
	
	/**
	 * Constructor por defecto.  Ver superclase
	 */
	public MultipleRecordsResultBean(boolean error, String errorMsg, HashMap<String, String> map) {
		super(error, errorMsg, map);
	}

	/**
	 * Agrega un nuevo registro a la lista
	 * @param map
	 */
	public void addRecord(HashMap<String, String> map) {
		records.add(map);
	}
	
	/** Basic getter para lista de registros */
	public ArrayList<HashMap<String, String>> getRecords() {
		return records;
	}

	/** Basic setter para lista de registros */
	public void setRecords(
			ArrayList<HashMap<String, String>> records) {
		this.records = records;
	}

	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n - Records: ");
		if (records!=null)
			for (HashMap<String, String> record : records)
				if (record!=null) {
					out.append("\n");
					for (String key : record.keySet())
						out.append(key).append(" = ").
							append(record.get(key)).
							append("; ");
				}
		return out.toString();
	}

}
