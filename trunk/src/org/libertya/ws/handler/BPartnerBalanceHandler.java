package org.libertya.ws.handler;

import java.math.BigDecimal;
import java.util.HashMap;

import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.X_C_CashLine;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Payment;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class BPartnerBalanceHandler extends BalanceHandler {

	/**
	 * Sumatoria de pedidos no facturados
	 */
	public ResultBean bPartnerBalanceSumOrdersNotInvoiced(ParameterBean data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"bPartnerID", "bPartnerList", "cuit", "clientID", "orgID"}, new Object[]{bPartnerID, bPartnerList, cuit, clientID, orgID});
			
			/* === Procesar (logica especifica) === */			
			BigDecimal amount = null;
			
			StringBuffer sql = new StringBuffer();
			
			sql.append("  SELECT sum(coalesce(orders.pendingToInvoiceAmt, 0)) as amount ")
				.append(" FROM ")
				.append(" ( ")
				.append(" 	SELECT o.C_Order_ID, o.DocumentNo, o.DateAcct, o.C_DocType_ID, ") 
			 	.append(" 	coalesce(currencyconvert(o.grandtotal - sum(matches.totalamtinvoiced), o.c_currency_id, 118, ('now'::text)::timestamp(6) with time zone, COALESCE(c_conversiontype_id,0), o.ad_client_id, o.ad_org_id),0) as pendingToInvoiceAmt ") 
			 	.append(" 	FROM ")
			 	.append(" 	( ")
				.append(" 		SELECT ol.c_orderline_id, ol.linetotalamt, coalesce(sum(il.linetotalamt),0) as totalamtinvoiced ") 
				.append(" 		FROM C_OrderLine ol ")
				.append(" 		JOIN C_Order o ON ol.C_Order_ID = o.C_Order_ID ") 
				.append(" 		JOIN C_BPartner bp ON o.c_bpartner_id = bp.c_bpartner_id ")
				.append(" 		LEFT JOIN C_InvoiceLine il ON ol.C_OrderLine_ID = il.C_OrderLine_ID ") 
				.append(" 		LEFT JOIN C_Invoice i ON il.C_Invoice_ID = i.C_Invoice_ID ")
				.append(" 		WHERE o.DocStatus IN ('CO','CL', 'RE', 'VO') ")
				.append(" 		AND (i.C_Invoice_ID IS NULL OR i.DocStatus IN ('CO','CL', 'RE', 'VO')) ")
				.append("		AND o.issotrx = 'Y' ");
			addSQLFilters(sql, "o", bPartnerID, bPartnerList, cuit, clientID, orgID);
			sql.append(" 		GROUP BY ol.c_orderline_id, ol.linetotalamt, ol.qtyordered ") 
				.append(" 		HAVING sum(il.qtyinvoiced) IS NULL OR ol.qtyordered - sum(il.qtyinvoiced) > 0 ") 
				.append(" 	) AS matches ")
				.append(" 	JOIN C_OrderLine ol ON ol.C_OrderLine_ID = matches.C_OrderLine_ID ")
				.append(" 	JOIN C_Order o ON o.C_Order_ID = ol.C_Order_ID ")
				.append(" 	GROUP BY o.C_Order_ID, o.DocumentNo, o.C_DocType_ID, o.DateAcct, o.C_Currency_ID, o.C_ConversionType_ID, o.AD_Client_ID, o.AD_Org_ID, o.GrandTotal ") 
				.append(" ) as orders ")
				.append(" WHERE 1 = ? ");
			
			// Obtener resultado
			amount = DB.getSQLValueBD(getTrxName(), sql.toString(), 1);
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("Amount", amount!=null?amount.toString():null);
			return new ResultBean(false, null, result);
		}
		catch (ModelException me) {
			return processException(me, wsInvocationArguments(data));
		}
		catch (Exception e) {
			return processException(e, wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}

	/**
	 * Sumatoria de Facturas
	 */
	public ResultBean bPartnerBalanceSumInvoices(ParameterBean data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"bPartnerID", "bPartnerList", "cuit", "clientID", "orgID"}, new Object[]{bPartnerID, bPartnerList, cuit, clientID, orgID});
			
			/* === Procesar (logica especifica) === */			
			BigDecimal amount = null;
			
			StringBuffer sql = new StringBuffer();
			// Consulta principal
			addSQLMain(sql);
			// Filtros SQL
			addSQLFilters(sql, "v", bPartnerID, bPartnerList, cuit, clientID, orgID);
			// Consulta especifica para este servicio
			sql.append(" AND v.documenttable = '").append(X_C_Invoice.Table_Name).append("'");
			sql.append(" AND 1 = ? ");
			
			// Obtener resultado
			amount = DB.getSQLValueBD(getTrxName(), sql.toString(), 1);
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("Amount", amount!=null?amount.toString():null);
			return new ResultBean(false, null, result);
		}
		catch (ModelException me) {
			return processException(me, wsInvocationArguments(data));
		}
		catch (Exception e) {
			return processException(e, wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}		
	}

	/**
	 * Sumatoria de cobros y pagos (Banco y Efectivo)
	 */
	public ResultBean bPartnerBalanceSumPayments(ParameterBean data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"bPartnerID", "bPartnerList", "cuit", "clientID", "orgID"}, new Object[]{bPartnerID, bPartnerList, cuit, clientID, orgID});
			
			/* === Procesar (logica especifica) === */			
			BigDecimal amount = null;
			
			StringBuffer sql = new StringBuffer();
			// Consulta principal
			addSQLMain(sql, " LEFT JOIN c_payment p ON v.document_id = p.c_payment_id " +
							" LEFT JOIN c_bankaccount ba ON p.c_bankaccount_id = ba.c_bankaccount_id ");
			// Filtros SQL
			addSQLFilters(sql, "v", bPartnerID, bPartnerList, cuit, clientID, orgID);
			// Consulta especifica para este servicio.  Lineas de caja o payments que no sean en cartera
			sql.append(" AND (v.documenttable = '").append(X_C_CashLine.Table_Name).append("'")
				.append(" OR (v.documenttable = '").append(X_C_Payment.Table_Name).append("' AND ba.ischequesencartera = 'N') ) ");
			sql.append(" AND 1 = ? ");
			
			// Obtener resultado
			amount = DB.getSQLValueBD(getTrxName(), sql.toString(), 1);
			
			// Se invierte el monto para facilitar la lectura del resultado, quedando en positivo el
			// balance final en el caso que el monto de cobros supere el de pagos hacia la EC. consultada
			if (amount!=null)
				amount = amount.multiply(new BigDecimal(-1));			
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("Amount", amount!=null?amount.toString():null);
			return new ResultBean(false, null, result);
		}
		catch (ModelException me) {
			return processException(me, wsInvocationArguments(data));
		}
		catch (Exception e) {
			return processException(e, wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}

	/**
	 * Sumatoria de Cheques en cartera
	 */
	public ResultBean bPartnerBalanceSumChecks(ParameterBean data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"bPartnerID", "bPartnerList", "cuit", "clientID", "orgID"}, new Object[]{bPartnerID, bPartnerList, cuit, clientID, orgID});
			
			/* === Procesar (logica especifica) === */			
			BigDecimal amount = null;
			
			StringBuffer sql = new StringBuffer();
			// Consulta principal
			addSQLMain(sql, " INNER JOIN c_payment p ON v.document_id = p.c_payment_id INNER JOIN c_bankaccount ba ON p.c_bankaccount_id = ba.c_bankaccount_id ");
			// Filtros SQL
			addSQLFilters(sql, "v", bPartnerID, bPartnerList, cuit, clientID, orgID);
			// Consulta especifica para este servicio
			sql.append(" AND v.documenttable = '").append(X_C_Payment.Table_Name).append("' ");
			sql.append(" AND v.issotrx = 'Y' ");
			sql.append(" AND ba.ischequesencartera = 'Y' ");
			sql.append(" AND p.docstatus = 'CO' ");		// Si el cheque NO está en completado, NO esta en la cartera
			sql.append(" AND 1 = ? ");
			
			// Obtener resultado
			amount = DB.getSQLValueBD(getTrxName(), sql.toString(), 1);
			
			// Se invierte el monto para facilitar la lectura del resultado.  Siempre se intepreta como positivo,
			// más allá de que tradicionalmente los cobros (cheques en este caso) implican una "deuda" para con el cliente
			if (amount!=null)
				amount = amount.multiply(new BigDecimal(-1));
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("Amount", amount!=null?amount.toString():null);
			return new ResultBean(false, null, result);
		}
		catch (ModelException me) {
			return processException(me, wsInvocationArguments(data));
		}
		catch (Exception e) {
			return processException(e, wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}
	
	
	/**
	 * Sobrecarga de método
	 */
	protected void addSQLMain(StringBuffer sql) {
		addSQLMain(sql, "");
	}
	
	
	/**
	 * Parte inicial del query de consulta de saldos
	 * @param sql el query que se esta generando
	 */
	protected void addSQLMain(StringBuffer sql, String additionalJoins) {
		sql.append("  SELECT sum(v.signo_issotrx * ").append(getCurrencyConvertSQL("amount", "v")).append(" ) ")
			.append(" FROM v_documents v ")
			.append(" INNER JOIN c_bpartner bp ON v.c_bpartner_id = bp.c_bpartner_id")
			.append(  additionalJoins  )
			.append(" WHERE v.DocStatus IN ('CO', 'CL', 'RE', 'VO') ");
	}
	
	/**
	 * Adiciona al query de consulta, el SQL correspondiente en función de los parámetros recibidos
	 * @param sql el query que se está generando
	 * @param bPartnerID filtrado por bPartnerID de la EC
	 * @param bPartnerList filtrado por lista de bPartnerIDs de la EC
	 * @param cuit filtrado por taxID de la EC
	 * @param clientID filtrado por compañía especificada
	 * @param orgID por organización especificada
	 * @param tableAlias prefijo a utilizar como nombre de tabla
	 * @throws ModelException en caso de no haber especificado un criterio de busqueda
	 * @throws Exception en caso de error
	 */
	protected void addSQLFilters(StringBuffer sql, String tableAlias, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID) throws ModelException, Exception {
		// Filtrar por compañía
		if (clientID > 0)
			sql.append(" AND ").append(tableAlias).append(".AD_Client_ID = ").append(Env.getAD_Client_ID(getCtx()));
		// Filtrar por organizacion			
		if (orgID > 0)
			sql.append(" AND ").append(tableAlias).append(".AD_Org_ID = ").append(Env.getAD_Org_ID(getCtx()));
		// Filtros por EC: bPartnerID
		if (bPartnerID > 0)
			sql.append(" AND ").append(tableAlias).append(".C_BPartner_ID = ").append(bPartnerID);
		// Filtros por EC: lista de bPartnerIDs			
		else if (bPartnerList != null && bPartnerList.length > 0) {
			sql.append(" AND ").append(tableAlias).append(".C_BPartner_ID IN ( ");
			for (int i=0; i<bPartnerList.length; i++)
				sql.append(bPartnerList[i]).append((i+1==bPartnerList.length?"":","));
			sql.append(" ) ");
		}
		// Filtros por EC: cuit			
		else if (cuit!=null && cuit.length() > 0)
			sql.append(" AND bp.taxID = '").append(cuit).append("' ");
		else
			throw new ModelException("Debe especificar una o mas entidades comerciales");

	}
}
