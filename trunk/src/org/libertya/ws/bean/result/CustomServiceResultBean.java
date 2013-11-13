package org.libertya.ws.bean.result;

import java.util.HashMap;

import org.libertya.wse.common.ListedMap;

public class CustomServiceResultBean extends ResultBean {

	/** 
	 * Nomina dinámica de resultados.  Similar a la nómina dinámica de argumentos. 
	 */
	public ListedMap[] result = new ListedMap[0];
	
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

	public ListedMap[] getResult() {
		return result;
	}

	public void setResult(ListedMap[] result) {
		this.result = result;
	}

	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n  Dynamic result: ");
		if (result != null) {
			for (int i=0; i < result.length; i++) {
				if (result[i]!=null) {
					out.append("\n ").append(result[i].getKey()).append(" : ");
					if (result[i].getValues()!=null) {
						for (int j=0; j<result[i].getValues().length; j++) {
							if (result[i].getValues()[j]!=null)
								out.append(result[i].getValues()[j]).append(" ");
						}
					}
				}
			}
		}
		return out.toString();
	}

	
}
