package org.libertya.wse.result;

import org.libertya.wse.common.ListedMap;

public class Result {

	/** El resultado fue un error */
	protected boolean error = false;
	/** Mensaje de error */
	protected String errorMsg = "";
	/** Valores resultantes */
	protected ListedMap[] resultValues;
	
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public ListedMap[] getResultValues() {
		return resultValues;
	}
	public void setResultValues(ListedMap[] resultValues) {
		this.resultValues = resultValues;
	}
	
	
}
