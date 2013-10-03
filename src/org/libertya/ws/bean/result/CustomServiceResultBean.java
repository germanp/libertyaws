package org.libertya.ws.bean.result;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomServiceResultBean extends ResultBean {

	/** 
	 * Nomina dinámica de resultados.  Similar a la nómina dinámica de argumentos. 
	 */
	public ArrayList<HashMap<String, ArrayList<String>>> result = new ArrayList<HashMap<String, ArrayList<String>>>();
	
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

	public ArrayList<HashMap<String, ArrayList<String>>> getResult() {
		return result;
	}

	public void setResult(ArrayList<HashMap<String, ArrayList<String>>> result) {
		this.result = result;
	}


	
	
}
