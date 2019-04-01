package org.libertya.ws.handler;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.ResultBean;
import org.openXpertya.apps.ProcessParameter;
import org.openXpertya.model.FiscalDocumentPrint;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Order;
import org.openXpertya.model.X_C_Promotion;
import org.openXpertya.model.X_C_Promotion_Code;
import org.openXpertya.model.X_C_Promotion_Code_Batch;
import org.openXpertya.model.X_M_InOut;
import org.openXpertya.print.fiscal.action.FiscalCloseAction;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.ProcessInfoUtil;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;

public class ProcessExecuteHandler extends GeneralHandler {
	
	
	/** UID de la pestaña de facturas de cliente */
	public static final String INVOICE_TAB_UID 	=  "CORE-AD_Tab-263";
	/** UID de la pestaña de pedidos de cliente */
	public static final String ORDER_TAB_UID 	=  "CORE-AD_Tab-186";
	/** UID de la pestaña de remitos de salida */
	public static final String INOUT_TAB_UID 	=  "CORE-AD_Tab-257";
	/** UID de la pestaña de recibos de cliente */
	public static final String RECEIPT_TAB_UID 	=  "CORE-AD_Tab-1000188";
	
	/** Mapeo Tabla -> ID de proceso de impresion */
	protected static HashMap<String, Integer> tablesAndPrintProcesses = null;
	
	/**
	 * Cierre de lote de tarjeta de crédito
	 */
	public ResultBean processCreditCardBatchClose(ParameterBean data) {
		try
		{
			// AD_ComponentObjectUID del proceso de cierre de tarjeta de credito
			final String BATCH_CLOSING_CREDIT_CARD_PROCESS_COMPONENTUID =  "CORE-AD_Process-1010404";
			
			/* === Configuracion inicial === */
			init(data, new String[]{}, new Object[]{});	
			
			// Invocar la ejecución del proceso
			ProcessInfo pi = executeProcess("Cierre de lote de tarjeta de credito", 
											getProcessIDFromComponentObjectUID(BATCH_CLOSING_CREDIT_CARD_PROCESS_COMPONENTUID), 
											data.getMainTable(),
											null,
											null);
			
			// En caso de error disparar una excepcion
			if (pi.isError())
				throw new Exception("Error en ejecución: " + pi.getSummary());

			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
			/* === Retornar valor === */
			return new ResultBean(false, null, new HashMap<String, String>());
		}
		catch (Exception e) {
			return processException(e, wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}
	

	
	/**
	 * Cierre de impresora fiscal
	 */
	public ResultBean processFiscalPrinterClose(ParameterBean data) {
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{}, new Object[]{});	
					
			// Recuperar parámetros
			if (toLowerCaseKeys(data.getMainTable()).get("fiscalclosetype")==null)
				throw new Exception ("Argumento FiscalCloseType obligatorio");
			if (toLowerCaseKeys(data.getMainTable()).get("c_controlador_fiscal_id")==null)
				throw new Exception ("Argumento C_Controlador_Fiscal_ID obligatorio");
			String fiscalCloseType = toLowerCaseKeys(data.getMainTable()).get("fiscalclosetype");
			int C_Controlador_Fiscal_ID = Integer.parseInt(toLowerCaseKeys(data.getMainTable()).get("c_controlador_fiscal_id"));
			
			// Invocar a la acción de cierre (basado en logica de FiscalPrinterControlPanel.getBtnFiscalClose())
			FiscalCloseAction fca = new FiscalCloseAction(new FiscalDocumentPrint(), getTrxName(), fiscalCloseType, C_Controlador_Fiscal_ID);
			if (!fca.execute())
				throw new Exception ("Error en ejecución: " + fca.getErrorMsg() + ". " + fca.getErrorDesc());

			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
			/* === Retornar valor === */
			return new ResultBean(false, null, new HashMap<String, String>());
		}
		catch (Exception e) {
			return processException(e, wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}

	/**
	 * Recuperacion de un documento de impresion en formato PDF
	 */
	public ResultBean processRetrievePdfFromDocument(ParameterBean data) {
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{}, new Object[]{});	
			
			// Parseo argumento tableName
			String tableName = null;
			tableName = toLowerCaseKeys(data.getMainTable()).get("tablename");
			if (Util.isEmpty(tableName))
				throw new Exception("Argumento tableName no especificado");

			// Parseo argumento recordID
			int recordID = -1;
			try {
				recordID = Integer.parseInt(toLowerCaseKeys(data.getMainTable()).get("recordid"));				
			} catch (Exception e) {
				throw new Exception ("Argumento recordID no especificado o invalido");
			}
			
			// Invocar la ejecución del proceso
			ProcessInfo pi = executeProcess("Impresion de Documento", 
											getPrintProcessID(tableName, recordID), 
											data.getMainTable(),
											recordID,
											true);
			
			// En caso de error disparar una excepcion
			if (pi.isError())
				throw new Exception("Error en ejecución: " + pi.getSummary());
			
			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			String encodedBase64 = Base64.encodeBase64String(pi.getReportResultStream().toByteArray());
			result.put("PDF", encodedBase64);
			return new ResultBean(false, null, result);
		}
		catch (Exception e) {
			return processException(e, wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}
	
	
	/**
	 * Operación de Generacion de Cupones de Descuentos
	 * Esta operacion genera unicamente UN cupon de descuento
	 */
	public ResultBean processGeneratePromotionCode(ParameterBean data) {
		try
		{
			// AD_ComponentObjectUID del proceso de Generacion de Cupones de Descuentos
			final String GENERATE_PROMOTION_CODES_PROCESS_COMPONENTUID =  "CORE-AD_Process-1010614";
			
			// Argumento dentro del ParameterBean: numero de documento de la factura original
			final String ARG_INVOICE_DOC = "invoiceDoc";
			// Argumento dentro del ParameterBeann: nro de documento del cliente
			final String ARG_CUSTOMER_DNI = "customerDNI";
			
			/* === Configuracion inicial === */
			init(data, new String[]{}, new Object[]{});	
			
			// Numero de documento de la factura original con la que se generara el cupon promocional
			int invoiceID = -1;
			// Numero de documento del cliente especificado en la factura original
			String customerDNI = "";
			
			// Validar que la factura original exista 
			try {
				// Se cargo el parametro?
				if (toLowerCaseKeys(data.getMainTable()).get(ARG_INVOICE_DOC.toLowerCase()) == null) 
					throw new Exception("Debe especificar el numero de documento de la factura original con el cual generar el cupon promocional, bajo la clave " + ARG_INVOICE_DOC);
				// Recuperar la factura, si es que existe
				String invDocNo = toLowerCaseKeys(data.getMainTable()).get("invoicedoc");
				invoiceID = DB.getSQLValue(getTrxName(), 	" SELECT i.C_Invoice_ID " +
															" FROM C_Invoice i " +
															" INNER JOIN C_DocType dt ON i.C_DocTypeTarget_ID = dt.C_DocType_ID " +
															" WHERE dt.docTypeKey LIKE 'CI%'" +
															" AND i.documentNo = '" + invDocNo + "'" +
															" AND i.isSoTrx = 'Y'" +
															" AND i.docStatus IN ('CO', 'CL') " +
															" AND i.AD_Client_ID = " + data.getClientID());
				if (invoiceID <= 0)
					throw new Exception("No se ha encontrado una factura con el numero de documento ingresado");
			} catch (Exception e) {
				throw new Exception("Error al recuperar la factura: " + e.getMessage());
			}
						
			// Validar que la factura original pertenezca a quien se indica que corresponde
			try {
				// Se cargo el parametro?
				if (toLowerCaseKeys(data.getMainTable()).get(ARG_CUSTOMER_DNI.toLowerCase()) == null)
					throw new Exception("Debe especificar el numero de documento (DNI) del cliente registrado en la factura con la que se esta generando el cupon promocional, bajo la clave " + ARG_CUSTOMER_DNI);
				customerDNI = toLowerCaseKeys(data.getMainTable()).get(ARG_CUSTOMER_DNI.toLowerCase());
				int cant = DB.getSQLValue(getTrxName(), " SELECT count(1) " +
														" FROM C_Invoice " +
														" WHERE C_Invoice_ID = " + invoiceID +
														" AND (cuit = '" + customerDNI + "' OR nroidentificcliente = '" + customerDNI + "')");
				if (cant<=0) 
					throw new Exception("El DNI especificado no corresponde al cliente registrado en la factura indicada");
			} catch (Exception e) {
				throw new Exception("Error al recuperar el cliente: " + e.getMessage());
			}

			// Cargar en la map de argumentos el argument C_Invoice_Orig_ID, el cual es requerido en la clase
			data.getMainTable().put("C_Invoice_Orig_ID", ""+invoiceID);
			
			// Invocar la ejecución del proceso
			ProcessInfo pi = executeProcess("Generacion de Cupones de Descuentos", 
											getProcessIDFromComponentObjectUID(GENERATE_PROMOTION_CODES_PROCESS_COMPONENTUID), 
											data.getMainTable(),
											null,
											true);
			
			// En caso de error disparar una excepcion
			if (pi.isError())
				throw new Exception("Error en ejecución: " + pi.getSummary());

			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();

			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			
			// Basado en la informacion que retorna el proceso, cargar la información resultante
			// Recuperar el lote desde el InfoLog
			int promBatchID = pi.getLogs()[0].getP_ID();
			if (promBatchID<=0)
				throw new Exception("Error al recuperar el lote de promocion");
			X_C_Promotion_Code_Batch aPromBatch = new X_C_Promotion_Code_Batch(getCtx(), promBatchID, null);
			// Recuperar EL cupon y guardarlo en la map de resultados (por definicion, solo se genera un solo cupon)
			int promCodeID = DB.getSQLValue(null, "SELECT C_Promotion_Code_ID FROM C_Promotion_Code WHERE C_Promotion_Code_Batch_ID = " + promBatchID);
			if (promCodeID<=0)
				throw new Exception("Error al recuperar el cupon de la promocion");
			X_C_Promotion_Code aPromCode = new X_C_Promotion_Code(getCtx(), promCodeID, null);
			// Codigo de cupon
			result.put("Code", aPromCode.getCode());
			// Promocion (texto de la promo)
			X_C_Promotion aProm = new X_C_Promotion(getCtx(), aPromCode.getC_Promotion_ID(), null);
			if (aProm!=null)
				result.put("Promotion", aProm.getName());
			// Valido desde
			if (aPromCode.getValidFrom()!=null)
				result.put("ValidFrom", aPromCode.getValidFrom().toString());
			// Valido hasta
			if (aPromCode.getValidTo()!=null)
				result.put("ValidTo", aPromCode.getValidTo().toString());
			// Nro Lote
			result.put("Batch", aPromBatch.getDocumentNo());
			
			return new ResultBean(false, null, result);
		}
		catch (Exception e) {
			return processException(e, wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}

	/**
	 * Determina el ID del proceso a invocar en funcion de la tabla utilizada (similara a la actividad de APanel
	 */
	protected int getPrintProcessID(String tableName, int recordID) throws Exception {
		
		// Inicializar la map
		if (tablesAndPrintProcesses==null) { 
			tablesAndPrintProcesses = new HashMap<String, Integer>();
			tablesAndPrintProcesses.put(X_C_Invoice.Table_Name.toLowerCase(), 			DB.getSQLValue(null, "SELECT AD_Process_ID FROM AD_Tab WHERE AD_ComponentObjectUID = '" + INVOICE_TAB_UID + "'") );
			tablesAndPrintProcesses.put(X_C_Order.Table_Name.toLowerCase(), 			DB.getSQLValue(null, "SELECT AD_Process_ID FROM AD_Tab WHERE AD_ComponentObjectUID = '" + ORDER_TAB_UID + "'") );
			tablesAndPrintProcesses.put(X_M_InOut.Table_Name.toLowerCase(), 			DB.getSQLValue(null, "SELECT AD_Process_ID FROM AD_Tab WHERE AD_ComponentObjectUID = '" + INOUT_TAB_UID + "'") );
			tablesAndPrintProcesses.put(X_C_AllocationHdr.Table_Name.toLowerCase(), 	DB.getSQLValue(null, "SELECT AD_Process_ID FROM AD_Tab WHERE AD_ComponentObjectUID = '" + RECEIPT_TAB_UID + "'") );
		}
		
		// Es una tabla valida?
		if (!tablesAndPrintProcesses.containsKey(tableName.toLowerCase()))
			throw new Exception("No es posible recuperar el proceso de informe para la tabla " + tableName);
		
		// Existe el registro?
		if (0 >= DB.getSQLValue(getTrxName(), "SELECT count(1) " + getFromWhereClause(tableName, recordID)) )
			throw new Exception("No es posible recuperar el registro " + recordID + " de la tabla " + tableName);

		// Valor a retornar
		int processID = -1;
		
		// Determinar el doctype del documento a fin de recuperar - si es que lo tiene - el proceso de impresion a utilizar
		int docTypeID = DB.getSQLValue(null, "SELECT C_DocTypeTarget_ID " + getFromWhereClause(tableName, recordID));
		if (docTypeID <= 0) 
			docTypeID = DB.getSQLValue(null, "SELECT C_DocType_ID " + getFromWhereClause(tableName, recordID));
		if (docTypeID > 0)
			processID = DB.getSQLValue(null, "SELECT AD_Process_ID FROM C_DocType WHERE C_DocType_ID = " + docTypeID);
		
		// Si el doctype no tiene un proceso de impresion especificado, recuperar el configurado en la pestaña asociada a la tabla
		if (processID <= 0)
			processID = tablesAndPrintProcesses.get(tableName.toLowerCase());
		
		// Si no se encuentra un proceso, elevar la excepcion
		if (processID <= 0)
			throw new Exception("No existe una configuracion de informe Jasper asociado al documento en metadatos, ni para el tipo de documento del documento, ni para la pestaña asociada a la tabla");
		
		return processID;
	}
	
	/** Clausula FROM/WHERE a utilizar en todos los casos */
	protected String getFromWhereClause(String tableName, int recordID) {
		return " FROM " + tableName + " WHERE " + tableName + "_ID = " + recordID;
	}
	
	/**
	 * Ejecuta un AD_Process.
	 * @param title titulo del proceso
	 * @param processID id del proceso
	 * @param arguments datos que requiere el proceso.  <br> 
	 *  - Los tipos DEBEN ser los correctos! Esto es responsabilidad de quien invoque a este método. <br>  
	 *  - NO CONTEMPLA argumentos con rangos! <br>
	 * @param ctx contexto
	 * @param trxName transaccion.
	 * @param recordID si se desea ejecutar el proceso para un registro en particular
	 * @param streamOnly si es un informe, requerir la generacion de la impresion y guardarla en el ProcessInfo, pero no imprimirla
	 * @return el ProcessInfoResultante
	 */
	protected ProcessInfo executeProcess(String title, int processID, HashMap<String, String> arguments, Integer recordID, Boolean streamOnly) throws Exception {
		
		// Nuevo ProcessInfo según el processID indicado
		ProcessInfo pi = new ProcessInfo(title, processID);
		if (recordID != null)
			pi.setRecord_ID(recordID);
		if (streamOnly != null)
			pi.setToStreamOnly(streamOnly);		
		
		// Iterar por los parametros y cargarlos
    	PreparedStatement pstmt = ProcessParameter.GetProcessParameters(processID);
    	ResultSet rs = pstmt.executeQuery();
    	while (rs.next()) {
    		String paramName = rs.getString("ColumnName");
    		Object paramValue = createParamValue(arguments.get(paramName), rs.getInt("AD_Reference_ID"));
            if (paramValue == null)
            	continue;
            // TODO: parameter_To, info_To? Ver ProcessParameter.saveParameters como referencia.
    		ProcessInfoParameter aParam = new ProcessInfoParameter(paramName, paramValue, null, null, null);
    		pi.setParameter(ProcessInfoUtil.addToArray(pi.getParameter(), aParam));
    	}
    	
		// Ejecutar el proceso
		MProcess process = new MProcess(getCtx(), processID, getTrxName());
    	MProcess.execute(getCtx(), process, pi, getTrxName());
		return pi;
	}

	
	/**
	 * Recupera un processID a partir de un componentObjectUID
	 */
	protected int getProcessIDFromComponentObjectUID(String componentObjectUID) throws Exception {
		int processID = DB.getSQLValue(null, " SELECT AD_PROCESS_ID FROM AD_PROCESS WHERE AD_ComponentObjectUID = '" + componentObjectUID + "' ");
		if (processID <= 0)
			throw new Exception("Imposible recuperar AD_Process_ID a partir de componentObjectUID: " + componentObjectUID);
		return processID;
	
	}
	
	/**
	 * Retorna el valor del parametro creado segun el tipo de dato (displayType)
	 */
	protected static Object createParamValue(String value, int displayType) {
		Object retValue = null;
		// Imposible hacer mucho mas si el value es null
		if (value == null)
			return null;
		// Instanciar segun tipo
        if  (String.class == DisplayType.getClass(displayType, false))
        	retValue = value;
        else if (Integer.class == DisplayType.getClass(displayType, false))
        	retValue = Integer.valueOf(value);
        else if (BigDecimal.class == DisplayType.getClass(displayType, false))
        	retValue = new BigDecimal(value);
        else if (Timestamp.class == DisplayType.getClass(displayType, false)) 
        	retValue = Timestamp.valueOf(value);
        else if (byte[].class == DisplayType.getClass(displayType, false))
        	retValue = value.getBytes(); 
        // Retornar valor
        return retValue;
	}
	

	
}
