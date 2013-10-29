package org.libertya.ws.bean.result;

import java.util.ArrayList;
import java.util.HashMap;

import org.libertya.wse.common.ListedMap;

public class CustomServiceResultBean extends ResultBean {

	/** 
	 * Nomina dinámica de resultados.  Similar a la nómina dinámica de argumentos. 
	 */
	public HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
	
	/**
	 * Constructor por defecto.  Ver superclase.
	 */
	public CustomServiceResultBean() {
		super();
	}
	
	/**
	 * Constructor por defecto.  Ver superclase
	 */
	public CustomServiceResultBean(boolean error, String errorMsg, HashMap<String, String> map) {
		super(error, errorMsg, map);
	}

	public HashMap<String, ArrayList<String>> getResult() {
		return result;
	}

	public void setResult(HashMap<String, ArrayList<String>> result) {
		this.result = result;
	}

	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n  Dynamic result: ");
		if (result != null)
			for (String argName : result.keySet()) {
				if (result.get(argName)!=null) {
					out.append("\n ").append(argName).append(" : ");
					for (String value : result.get(argName)) {
						out.append(value).append(" ");
					}
				}
			}
		return out.toString();
	}
	
	public ListedMap[] toListedMap() {
		int i=0;
		ListedMap[] retValue = new ListedMap[result.size()];
		if (result != null) {
			for (String argName : result.keySet()) {
				int j = 0;
				String[] argValues = new String[result.get(argName).size()];
				for (String argValue : result.get(argName)) {
					argValues[j] = argValue;
					j++;
				}
				ListedMap aMap = new ListedMap();
				aMap.setKey(argName);
				aMap.setValues(argValues);
				retValue[i++] = aMap;
			}
		}
		return retValue;
	}
	
	
}
