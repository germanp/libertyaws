package org.libertya.ws.bean.result;

import java.util.ArrayList;
import java.util.HashMap;

public class StorageResultBean extends ResultBean {

	/** Listado de existencias  */
	protected ArrayList<HashMap<String, String>> stockList = new ArrayList<HashMap<String, String>>();

	/**
	 * Constructor por defecto.  Ver superclase.
	 */
	public StorageResultBean() {
		super();
	}
	
	/**
	 * Constructor por defecto.  Ver superclase
	 */
	public StorageResultBean(boolean error, String errorMsg, HashMap<String, String> map) {
		super(error, errorMsg, map);
	}
	
	
	/** Standard getter */
	public ArrayList<HashMap<String, String>> getStockList() {
		return stockList;
	}

	/** Standard setter */
	public void setStockList(ArrayList<HashMap<String, String>> stockList) {
		this.stockList = stockList;
	}
	
	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n - Stock List: ");
		if (stockList!=null)
			for (HashMap<String, String> stockItem : stockList)
				if (stockItem!=null) {
					out.append("\n");
					for (String key : stockItem.keySet())
						out.append(key).append(" = ").
							append(stockItem.get(key)).
							append("; ");
				}
		return out.toString();
	}
	
}
