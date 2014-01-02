package org.libertya.ws.bean.result;

import java.util.HashMap;

import org.libertya.wse.common.SimpleMap;
import org.libertya.wse.utils.MapTranslator;

public class ResultBean {

	/** El resultado fue un error */
	protected boolean error = false;
	/** Mensaje de error */
	protected String errorMsg = "";
	/** Valores de retorno principales o de tabla principal */
	protected HashMap<String, String> mainResult = new HashMap<String, String>();
	
	/**
	 * Constructor base
	 */
	public ResultBean() {
		// Implementado solo para java2wsdl
	}
	
	/**
	 * Constructor
	 * @param error true si estamos indicando un error o false en caso contrario
	 * @param errorMsg mensaje de error en caso de que error sea true
	 * @param map pares clave/valor de los datos resultantes
	 */
	public ResultBean(boolean error, String errorMsg, HashMap<String, String> map)
	{
		this();
		this.error = error;
		this.errorMsg = errorMsg;
		this.mainResult = map;
	}
	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer();
		if (error)
			out.append("ERROR. ").
				append(errorMsg!=null?errorMsg:"").append(". ");
		else
		{
			out.append("OK. ");
			if (mainResult != null) {
				out.append("\n");
				for (String key : mainResult.keySet())
					out.append(key).append(" = ").
						append(mainResult.get(key)).
						append("; ");
			}
		}
		return out.toString();
	}
	
	/** Basic getter para marca de error */
	public boolean isError() {
		return error;
	}
	
	/** Basic setter para marca de error */
	public void setError(boolean error) {
		this.error = error;
	}
	
	/** Basic getter para mensaje de error */
	public String getErrorMsg() {
		return errorMsg;
	}
	
	/** Basic setter para marca de error */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	/** Basic getter para datos de resultado */
	public HashMap<String, String> getMainResult() {
		return mainResult;
	}
	
	/** Basic setter para datos de resultado */
	public void setMainResult(HashMap<String, String> mainResult) {
		this.mainResult = mainResult;
	}

	/**
	 * Setea un valor en el mapa de datos correspondiente
	 * @param table map clave/valor 
	 * @param columnName nombre de la columna 
	 * @param columnValue valor de la columna
	 */
	protected void addColumnOnTable(HashMap<String, String> table, String columnName, String columnValue) {
		table.put(columnName, columnValue);
	}
	
	/**
	 * Volcado al wrapper
	 */
	public SimpleMap[] toSimpleMap() {
		return MapTranslator.hashMap2SimpleMap(mainResult);
	}
}
