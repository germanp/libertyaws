package org.libertya.ws.handler;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;

import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.ResultBean;
import org.openXpertya.apps.ProcessParameter;
import org.openXpertya.model.FiscalDocumentPrint;
import org.openXpertya.model.MProcess;
import org.openXpertya.print.fiscal.action.FiscalCloseAction;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.ProcessInfoUtil;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Trx;

public class ProcessExecuteHandler extends GeneralHandler {


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
											data.getMainTable());
			
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
	 * Ejecuta un AD_Process.
	 * @param title titulo del proceso
	 * @param processID id del proceso
	 * @param arguments datos que requiere el proceso.  <br> 
	 *  - Los tipos DEBEN ser los correctos! Esto es responsabilidad de quien invoque a este método. <br>  
	 *  - NO CONTEMPLA argumentos con rangos! <br>
	 * @param ctx contexto
	 * @param trxName transaccion.
	 * @return el ProcessInfoResultante
	 */
	protected ProcessInfo executeProcess(String title, int processID, HashMap<String, String> arguments) throws Exception {
		
		// Nuevo ProcessInfo según el processID indicado
		ProcessInfo pi = new ProcessInfo(title, processID);
		
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
