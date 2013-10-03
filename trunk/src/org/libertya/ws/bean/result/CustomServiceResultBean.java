package org.libertya.ws.bean.result;

import java.util.HashMap;

import org.openXpertya.plugin.common.DynamicResult;

public class CustomServiceResultBean extends ResultBean {

	/** Nomina dinámica de argumentos. */
	public DynamicResult result = new DynamicResult();
	
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

	public DynamicResult getResult() {
		return result;
	}

	public void setResult(DynamicResult result) {
		this.result = result;
	}

	
	
}
