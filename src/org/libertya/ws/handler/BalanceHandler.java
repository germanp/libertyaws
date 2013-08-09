package org.libertya.ws.handler;

import org.openXpertya.util.Env;

public class BalanceHandler extends GeneralHandler {

	
	/**
	 * Adiciona el query SQL para el Currency Convert, basándose en la moneda de la compañía
	 * @param columnName nombre de la columna que contiene el valor a convertir
	 * @param tableAlias prefijo a utilizar como nombre de tabla 
	 * @return parte del SQL relacionado con el currency convert
	 */
	protected String getCurrencyConvertSQL(String columnName, String tableAlias) {
		StringBuffer out = new StringBuffer();
		out.append(" currencyconvert( ")
			.append(tableAlias).append(".").append(columnName).append(", ")
			.append(tableAlias).append(".c_currency_id, ")
			.append(Env.getContext(getCtx(), "$C_Currency_ID")).append(", ")
			.append(" ('now'::text)::timestamp(6) with time zone, COALESCE(").append(tableAlias).append(".c_conversiontype_id,0), ").append(tableAlias).append(".ad_client_id, ").append(tableAlias).append(".ad_org_id) ");
		return out.toString();
	}
	
}
