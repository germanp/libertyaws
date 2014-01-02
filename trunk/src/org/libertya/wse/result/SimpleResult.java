package org.libertya.wse.result;

import org.libertya.ws.bean.result.ResultBean;
import org.libertya.wse.common.SimpleMap;
import org.libertya.wse.utils.MapTranslator;

public class SimpleResult {

	/** El resultado fue un error */
	protected boolean error = false;
	/** Mensaje de error */
	protected String errorMsg = "";
	/** Valores resultantes */
	protected SimpleMap[] resultValues;
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
	public SimpleMap[] getResultValues() {
		return resultValues;
	}
	public void setResultValues(SimpleMap[] resultValues) {
		this.resultValues = resultValues;
	}
	
	public SimpleResult () { }
	
	public SimpleResult (ResultBean data) {
		resultValues = MapTranslator.hashMap2SimpleMap(data.getMainResult());
		error = data.isError();
		errorMsg = data.getErrorMsg();
	}
	
	
}
