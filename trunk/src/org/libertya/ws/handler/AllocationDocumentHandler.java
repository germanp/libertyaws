package org.libertya.ws.handler;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Vector;

import org.libertya.ws.bean.parameter.AllocationParameterBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.AllocationGeneratorException;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBankAccount;
import org.openXpertya.model.MCash;
import org.openXpertya.model.MCashBook;
import org.openXpertya.model.MCashLine;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MEntidadFinanciera;
import org.openXpertya.model.MEntidadFinancieraPlan;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.PO;
import org.openXpertya.model.POCRGenerator;
import org.openXpertya.model.POCRGenerator.POCRType;
import org.openXpertya.model.RetencionProcessor;
import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.process.GeneratorRetenciones;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;

public class AllocationDocumentHandler extends GeneralHandler {

	/**
	 * Registra un recibo de cliente que contiene múltiples medios de cobro imputados a una o mas facturas 
	 * @param data parametros correspondientes a las facturas y pagos a cancelar
	 * @param bPartnerID identificador de la entidad comercial (o -1 en caso de no indicar)
	 * @param bPartnerValue clave de busqueda de la entidad comercial (o null en caso de no indicar)
	 * @param taxID CUIT de la entidad comercial (o null en caso de no indicar)
	 * @return ResultBean con OK y datos: C_AllocationHdr_ID, AllocationHdr_DocumentNo creado, etc. o ERROR en caso contrario.
	 */
	public ResultBean allocationCreateReceipt(AllocationParameterBean data, int bPartnerID, String bPartnerValue, String taxID) {
		return allocationCreateReceipt(data, bPartnerID, bPartnerValue, taxID, false);
	}
	
	/**
	 * Registra un recibo de cliente que contiene múltiples medios de cobro imputados que se tomarán como anticipos, 
	 * es decir que no se imputan a ninguna factura y quedan como crédito en la cuenta corriente de la Entidad Comercial
	 * @param data parametros correspondientes a las facturas y pagos a cancelar
	 * @param bPartnerID identificador de la entidad comercial (o -1 en caso de no indicar)
	 * @param bPartnerValue clave de busqueda de la entidad comercial (o null en caso de no indicar)
	 * @param taxID CUIT de la entidad comercial (o null en caso de no indicar)
	 * @return ResultBean con OK y datos: C_AllocationHdr_ID, AllocationHdr_DocumentNo creado, etc. o ERROR en caso contrario.
	 */
	public ResultBean allocationCreateEarlyReceipt(AllocationParameterBean data, int bPartnerID, String bPartnerValue, String taxID) {
		return allocationCreateReceipt(data, bPartnerID, bPartnerValue, taxID, true);
	}
	
	/**
	 * Registra un recibo de cliente que contiene múltiples medios de cobro imputados a una o mas facturas 
	 * @param data parametros correspondientes a las facturas y pagos a cancelar
	 * @param bPartnerID identificador de la entidad comercial (o -1 en caso de no indicar)
	 * @param bPartnerValue clave de busqueda de la entidad comercial (o null en caso de no indicar)
	 * @param taxID CUIT de la entidad comercial (o null en caso de no indicar)
	 * @param isEarlyPayment verificacion para recibos de cliente adelantados, no deberían recibirse facturas a cancelar
	 * @return ResultBean con OK y datos: C_AllocationHdr_ID, AllocationHdr_DocumentNo creado, etc. o ERROR en caso contrario.
	 */
	protected ResultBean allocationCreateReceipt(AllocationParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean isEarlyPayment) {
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"bPartnerID", "bPartnerValue", "taxID", "isEarlyPayment"}, new Object[]{bPartnerID, bPartnerValue, taxID, isEarlyPayment});
			
			/* === Procesar (logica especifica) === */
			// Recuperar BPartner
			MBPartner aBPartner = (MBPartner)getPO("C_BPartner", bPartnerID, "value", bPartnerValue, false, true, true, false);
			if (aBPartner == null || aBPartner.getC_BPartner_ID() == 0) 
				aBPartner = (MBPartner)getPO("C_BPartner", bPartnerID, "taxID", taxID, false, true, true, false);
			if (aBPartner == null || aBPartner.getC_BPartner_ID() == 0)
				throw new Exception("No se ha podido recuperar una entidad comercial con los criterios especificados");

			// Si es un RC de tipo adelantado, no deberían recibirse facturas a cancelar
			if (isEarlyPayment && data.getInvoices()!=null && data.getInvoices().size() > 0) {
				throw new Exception("En los recibos de cliente adelantados no pueden recibirse facturas a cancelar");
			}
			
			// Si es un RC de tipo normal, debería recibirse al menos una factura a pagar
			if (!isEarlyPayment && (data.getInvoices()==null || data.getInvoices().size() == 0)) {
				throw new Exception("No se han indicado facturas a cancelar para este recibo de cliente");
			}
			
			// Instanciar nuevo allocation (tipo recibo)
			POCRGenerator rcGenerator = new POCRGenerator(getCtx(), POCRType.CUSTOMER_RECEIPT, getTrxName());
			MAllocationHdr allocationHdr = rcGenerator.createAllocationHdr();
			allocationHdr.setC_BPartner_ID(aBPartner.getC_BPartner_ID());
			setValues(allocationHdr, data.getMainTable(), true);
			allocationHdr.setIsManual(false);

			// Guardar encabezado
			if (!allocationHdr.save())
				throw new Exception("Error al guardar encabezado de recibo: " + CLogger.retrieveErrorAsString());
			
			// Agregar las facturas a cancelar
			for (HashMap<String, String> invoiceMap : data.getInvoices()) {
				invoiceMap = toLowerCaseKeys(invoiceMap);
				int invoiceID = -1;
				BigDecimal amount = new BigDecimal(-1);
				try {
					invoiceID = Integer.parseInt(invoiceMap.get("c_invoice_id"));
					amount = new BigDecimal(invoiceMap.get("amount")); 
				}
				catch (Exception e) {
					throw new ModelException(" Error al recuperar datos de la factura a cancelar:" + e.getMessage());
				}
				if (invoiceID<=0 || amount.compareTo(BigDecimal.ZERO)<=0)
					throw new ModelException(" Valores indicados para la factura a cancelar son inválidos" );
				rcGenerator.addInvoice(invoiceID, amount);
			}
			
			// Agregar los medios de pago para cancelar las facturas
			for (HashMap<String, String> paymentMap : data.getPayments()) {
				// Adicionar el pago a la nomina
				addPaymentToAllocation(rcGenerator, paymentMap, aBPartner, allocationHdr, isEarlyPayment);
			}
			
			// Generar las lineas del allocation en funcion de los pagos y facturas cargados en rcGenerator
			rcGenerator.generateLines();

			// Completar el allocation tal como se realiza en VOrdenPago
			try {
				rcGenerator.completeAllocation();
			} catch (AllocationGeneratorException e) {
				throw new ModelException("Error al completar el recibo: " + e.getMessage());
			}
			
			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("C_AllocationHdr_ID", Integer.toString(allocationHdr.getC_AllocationHdr_ID()));
			result.put("AllocationHdr_DocumentNo", allocationHdr.getDocumentNo());
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
	 * Anula recibos, indicado por columna y criterio de busqueda 
	 */
	public ResultBean allocationVoidByColumn(AllocationParameterBean data, String columnName, String columnCriteria, String allocationAction) {
		return allocationVoid(data, -1, columnName, columnCriteria, allocationAction);
	}

	/**
	 * Anula un recibo, indicado por su ID
	 */
	public ResultBean allocationVoidByID(AllocationParameterBean data, int allocationID, String allocationAction) {
		return allocationVoid(data, allocationID, null, null, allocationAction);
	}
	
	/**
	 * Realiza la anulación de un Recibo emitido previamente
	 * @param data información basica de acceso
	 * @param allocationID recibo a anular
	 * 		Revertir unicamente <code>AllocationParameterBean.ALLOCATIONACTION_RevertAllocation</code>, o bien 
	 *		Revertir y Anular Cobros <code>ALLOCATIONACTION_VoidPayments</code>, o bien
	 *		Revertir y Anular Cobros y Retenciones <code>ALLOCATIONACTION_VoidPaymentsRetentions</code>. 
	 * @return ResultBean con OK o ERROR. 
	 */
	protected ResultBean allocationVoid(AllocationParameterBean data, int allocationID, String columnName, String columnCriteria, String allocationAction) {
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"allocationID", "columnName", "columnCriteria", "allocationAction"}, new Object[]{allocationID, columnName, columnCriteria, allocationAction});
			
			/* === Procesar (logica especifica) === */
			PO[] pos = getPOs("C_AllocationHdr", allocationID, columnName, columnCriteria, false, false, false, true);
			for (PO po : pos) {
				// Setear el tipo de anulacion
				((X_C_AllocationHdr)po).setAllocationAction(allocationAction);
				// Revertir y efectuar acciones adicionales según el tipo de anulación
				if (!DocumentEngine.processAndSave((DocAction)po, DocAction.ACTION_Void, true)) {
					throw new ModelException("Error al revertir el recibo: " + Msg.parseTranslation(getCtx(), ((DocAction)po).getProcessMsg()));
				}
			}
			
			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
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
	 * Incorpora un nuevo pago al generador 
	 * @param rcGenerator generador de pagos
	 * @param paymentMap datos del pago
	 * @param ppm tipo de pago 
	 */
	protected void addPaymentToAllocation(POCRGenerator rcGenerator, HashMap<String, String> paymentMap, MBPartner aBPartner, MAllocationHdr allocationHdr, boolean isEarlyPayment) throws ModelException, Exception {

		// Determinar el monto y tipo de pago a instanciar 
		paymentMap = toLowerCaseKeys(paymentMap);
		BigDecimal amount = new BigDecimal(paymentMap.get("amount"));
		int posPaymentMediumID = Integer.parseInt(paymentMap.get("c_pospaymentmedium_id"));
		MPOSPaymentMedium ppm = new MPOSPaymentMedium(getCtx(), posPaymentMediumID, getTrxName());

		/* A Credito no es un medio de cobro valido en RC */
		if (MPOSPaymentMedium.TENDERTYPE_Credit.equals(ppm.getTenderType()))
			throw new ModelException("El tipo de medio de pago Credito no es valido para recibos de cliente");
		
		/* Retención (invoice) */
		else if (MPOSPaymentMedium.TENDERTYPE_Retencion.equals(ppm.getTenderType())) {
			// Incorporar la retencion (en Recibos de Cliente, la misma NO es calculada)
			GeneratorRetenciones genRet = new GeneratorRetenciones(aBPartner.getC_BPartner_ID(), new Vector<Integer>(), new Vector<BigDecimal>(), amount, true);
			genRet.setTrxName(getTrxName());
			RetencionProcessor rp = genRet.addRetencion(Integer.parseInt(paymentMap.get("c_retencionschema_id")));
			rp.setAmount(amount);
			rp.setRetencionNumber(paymentMap.get("retenc_documentno"));		
			rp.setDateTrx(Timestamp.valueOf(paymentMap.get("retenc_date")));
			rp.setCurrency(new MCurrency(getCtx(), ppm.getC_Currency_ID(), getTrxName()));
			// Para retenciones no se setean demas campos mediante setValues()
			genRet.save(allocationHdr);
			// Recuperar el ID de la factura de retención (credito) recien generada 
			int invoiceID = DB.getSQLValue(getTrxName(), " SELECT C_Invoice_ID FROM M_Retencion_Invoice WHERE C_AllocationHdr_ID = ? ORDER BY M_Retencion_Invoice_ID DESC", allocationHdr.getC_AllocationHdr_ID());
			if (invoiceID>0)
				rcGenerator.addInvoicePaymentMedium(invoiceID, amount);
			else
				throw new ModelException(" Error al generar la retencion como medio de pago ");
		}
		
		/* Nota de crédito (invoice) */
		else if (MPOSPaymentMedium.TENDERTYPE_CreditNote.equals(ppm.getTenderType())) {
			
			// Recibos de cliente adelantado no pueden generarse con notas de credito
			if (isEarlyPayment)
				throw new ModelException(" Una nota de crédito no puede ser parte de un recibo de cliente anticipado ");

			int invoiceID = -1;
			try {
				invoiceID = Integer.parseInt(paymentMap.get("c_invoice_id"));
			}
			catch (Exception e) {
				throw new ModelException(" Error al recuperar el C_Invoice_ID de la nota de credito:" + e.getMessage());
			}
			if (invoiceID<=0)
				throw new ModelException(" C_Invoice_ID de la nota de credito invalido (debe ser mayor que cero)");
			MInvoice anInvoice = (MInvoice)getPO("C_Invoice", invoiceID, null, null, false, false, true, true);
			// Verificar monedas
			validateInvoiceCurrency(anInvoice, ppm);
			// Para NC no se setean demas campos mediante serValues()
			rcGenerator.addInvoicePaymentMedium(invoiceID, amount);
		}
		
		/* Cheque (payment) */
		else if (MPOSPaymentMedium.TENDERTYPE_Check.equals(ppm.getTenderType())) {
			MPayment check = new MPayment(getCtx(), 0, getTrxName());
			// Setear valores
			setValues(check, paymentMap, true);
			check.setIsReceipt(true);
			check.setC_DocType_ID(true);
			check.setC_BPartner_ID(aBPartner.getC_BPartner_ID());
			check.setAmount(ppm.getC_Currency_ID(), amount);
			// Antes de persistir, verificar monedas entre la cuenta bancaria y el tipo de medio de pago
			validatePaymentCurrency(check, ppm);
			// Setear en el pago el currency del tipo de medio de pago			
			check.setC_Currency_ID(ppm.getC_Currency_ID());
			if (!DocumentEngine.processAndSave(check, DocAction.ACTION_Complete, true))
				throw new ModelException("Error al crear el pago con cheque: " + Msg.parseTranslation(getCtx(), check.getProcessMsg()));
			rcGenerator.addPaymentPaymentMedium(check.getC_Payment_ID(), amount);
		}
		
		/* Tarjeta de credito (payment) */
		else if (MPOSPaymentMedium.TENDERTYPE_CreditCard.equals(ppm.getTenderType())) {
			MPayment creditCard = new MPayment(getCtx(), 0, getTrxName());
			// Setear valores
			setValues(creditCard, paymentMap, true);
			creditCard.setIsReceipt(true);
			creditCard.setC_DocType_ID(true);
			creditCard.setC_BPartner_ID(aBPartner.getC_BPartner_ID());
			creditCard.setAmount(ppm.getC_Currency_ID(), amount);
			creditCard.setC_BankAccount_ID(getBankAccountIDFromEntidadFinancieraPlan(creditCard));
			// Antes de persistir, verificar monedas entre la cuenta bancaria y el tipo de medio de pago
			validatePaymentCurrency(creditCard, ppm);
			// Setear en el pago el currency del tipo de medio de pago
			creditCard.setC_Currency_ID(ppm.getC_Currency_ID());
			if (!DocumentEngine.processAndSave(creditCard, DocAction.ACTION_Complete, true))
				throw new ModelException("Error al crear el pago con tarjeta de credito: " + Msg.parseTranslation(getCtx(), creditCard.getProcessMsg()));
			rcGenerator.addPaymentPaymentMedium(creditCard.getC_Payment_ID(), amount);
		}
		
		/* Transferencia bancaria (payment) */
		else if (MPOSPaymentMedium.TENDERTYPE_DirectDeposit.equals(ppm.getTenderType())) {
			MPayment deposit = new MPayment(getCtx(), 0, getTrxName());
			// Setear valores
			setValues(deposit, paymentMap, true);
			deposit.setIsReceipt(true);
			deposit.setC_DocType_ID(true);
			deposit.setC_BPartner_ID(aBPartner.getC_BPartner_ID());
			deposit.setAmount(ppm.getC_Currency_ID(), amount);
			// Antes de persistir, verificar monedas entre la cuenta bancaria y el tipo de medio de pago
			validatePaymentCurrency(deposit, ppm);
			// Setear en el pago el currency del tipo de medio de pago
			deposit.setC_Currency_ID(ppm.getC_Currency_ID());
			if (!DocumentEngine.processAndSave(deposit, DocAction.ACTION_Complete, true))
				throw new ModelException("Error al generar pago mediante transferencia bancaria o deposito: " + Msg.parseTranslation(getCtx(), deposit.getProcessMsg()));
			
			rcGenerator.addPaymentPaymentMedium(deposit.getC_Payment_ID(), amount);
		}
		
		/* Pago en efectivo (cashLine) */
		else if (MPOSPaymentMedium.TENDERTYPE_Cash.equals(ppm.getTenderType())) {
			// Recuperar la caja
			int cashID = -1;
			try {
				cashID = Integer.parseInt(paymentMap.get("c_cash_id"));
			}
			catch (Exception e) {
				throw new ModelException(" Error al recuperar el C_Cash_ID correspondiente a la caja: " + e.getMessage());
			}
			if (cashID<=0)
				throw new ModelException(" C_Cash_ID invalido (debe ser mayor que cero)");
			MCash cash = new MCash(getCtx(), cashID, getTrxName());
			// Nueva línea de caja
			MCashLine cashLine = new MCashLine(cash);
			// Setear valores
			setValues(cashLine, paymentMap, true);
			cashLine.setAmount(amount);
			cashLine.setCashType(MCashLine.CASHTYPE_Invoice);
			cashLine.setC_BPartner_ID(aBPartner.getC_BPartner_ID());
			// Anters de persistir, verificar monedas
			validateCashLineCurrency(cashLine, ppm);
			// Setear en el pago el currency del tipo de medio de pago
			cashLine.setC_Currency_ID(ppm.getC_Currency_ID());
			// Persistir la nueva línea de caja (guardar, procesar y volver a guardar)
			if (!DocumentEngine.processAndSave(cashLine, DocAction.ACTION_Complete, true))
				throw new ModelException("Error al generar el pago en efectivo: " + Msg.parseTranslation(getCtx(), cashLine.getProcessMsg()));

			// Incorporar el nuevo pago en efectivo
			rcGenerator.addCashLinePaymentMedium(cashLine.getC_CashLine_ID(), new BigDecimal(paymentMap.get("amount")));
		}
		
		/* Cobro adelantado (puede ser efectivo o payment) */
		else if (MPOSPaymentMedium.TENDERTYPE_AdvanceReceipt.equals(ppm.getTenderType())) {
			
			// Recibos de cliente adelantado no pueden generarse con pagos adelantados
			if (isEarlyPayment)
				throw new ModelException(" Un cobro adelantado no puede ser parte de un recibo de cliente anticipado ");
			
			// Si no se especifico ningun medio, error
			if (paymentMap.get("c_payment_id")==null && paymentMap.get("c_cashline_id")==null)
				throw new ModelException("Cobro adelantado requiere id del pago o linea de caja a utilizar (ID debe ser no nulo)");
			// Determinar que se especifico
			int paymentID = -1;
			int cashLineID = -1;
			try {
				paymentID = Integer.parseInt(paymentMap.get("c_payment_id"));
			}
			catch (Exception e) {}
			try {
				cashLineID = Integer.parseInt(paymentMap.get("c_cashline_id"));
			}
			catch (Exception e) {}
			if (paymentID <= 0 && cashLineID <= 0)
				throw new ModelException("Cobro adelantado requiere id del pago o linea de caja a utilizar (ID debe ser mayor a cero)");
			// Especificar ambos tampoco es correcto
			if (paymentID > 0 && cashLineID > 0)
				throw new ModelException("Se debe especificar SOLO el id del pago o la linea de caja a utilizar, pero no ambas");
			// Para pago anticipado no se setean demas campos mediante setValues()
			// Si es un pago...
			if (paymentID > 0) {
				// Verificar monedas
				MPayment payment = (MPayment)getPO("C_Payment", paymentID, null, null, false, false, true, true); 
				validatePaymentCurrency(payment, ppm);
				rcGenerator.addPaymentPaymentMedium(paymentID, amount);
			}
			// Si es una linea de caja...
			else if (cashLineID > 0) {
				MCashLine cashLine = (MCashLine)getPO("C_CashLine", cashLineID, null, null, false, false, true, true);
				validateCashLineCurrency(cashLine, ppm);
				rcGenerator.addCashLinePaymentMedium(cashLineID, amount);
			}
		}
		
	}
	
	/**
	 * Valida que las monedas del medio de pago seleccionado y de la nota de credito coincidan
	 * @param invoice nota de credito a usar como medio de pago  
	 * @param ppm tipo de medio de pago
	 * @throws Exception en caso de no concordar las monedas
	 */
	protected void validateInvoiceCurrency(MInvoice invoice, MPOSPaymentMedium ppm) throws ModelException {
		if (ppm.getC_Currency_ID() != invoice.getC_Currency_ID())
			throw new ModelException (" La moneda del tipo de medio de pago (POSPaymentMedium) difiere con respecto a la moneda de la nota de credito ");
	}

	/**
	 * Valida que las monedas del medio de pago seleccionado y el payment indicado coincidan
	 * @param payment el pago (transferencia, cheque, tarjeta de credito) a usar como medio de pago
	 * @param ppm tipo de medio de pago
	 * @throws Exception en caso de no concordar las monedas o de no poder determinar la moneda de la cuenta
	 */
	protected void validatePaymentCurrency(MPayment payment, MPOSPaymentMedium ppm) throws ModelException {
		// Para tarjeta de credito la cuenta se determina indirectamente a traves de la entidad financiera
		// En los otros casos (cheques, transferencias bancarias o pago anticipado, la cuenta se obtiene de manera directa)
		// De todas maneras esta lógica queda fuera de este método, siendo el método que invoca el encargado de setear
		// correctamente el c_bankaccount_id en cada caso
		MBankAccount ba = new MBankAccount(getCtx(), payment.getC_BankAccount_ID(), getTrxName()); 
		if (ppm.getC_Currency_ID() != ba.getC_Currency_ID())
			throw new ModelException (" La moneda del tipo de medio de pago (POSPaymentMedium) difiere con respecto a la moneda de la cuenta del pago ");
	}

	/**
	 * Para pagos en tarjeta se obtiene la cuenta bancaria a partir del plan de entidad financiera
	 * @param payment pago con tarjeta
	 * @return el ID de la cuenta bancaria asociada al pago con tarjeta
	 * @throws ModelException en caso de no poder determinar la cuenta bancaria
	 */
	protected int getBankAccountIDFromEntidadFinancieraPlan(MPayment payment) throws ModelException {
		MEntidadFinancieraPlan efp = null;
		MEntidadFinanciera ef = null;
		int bankAccountID = -1;
		if (payment.getM_EntidadFinancieraPlan_ID() > 0)
			efp = new MEntidadFinancieraPlan(getCtx(), payment.getM_EntidadFinancieraPlan_ID(), getTrxName());
		else
			throw new ModelException( " Imposible determinar la cuenta bancaria relacionada con el pago con tarjeta de credito (pago sin plan de entidad financiera) ");
		if (efp.getM_EntidadFinanciera_ID() > 0)
			ef = new MEntidadFinanciera(getCtx(), efp.getM_EntidadFinanciera_ID(), getTrxName());
		else
			throw new ModelException( " Imposible determinar la cuenta bancaria relacionada con el pago con tarjeta de credito (plan de entidad financiera sin entidad financiera) ");
		if (ef.getC_BankAccount_ID() > 0)
			bankAccountID = ef.getC_BankAccount_ID();	
		else
			throw new ModelException( " Imposible determinar la cuenta bancaria relacionada con el pago con tarjeta de credito (entidad financiera sin cuenta bancaria) ");
		return bankAccountID;
	}
	
	/**
	 * Valida que las monedas del medio de pago seleccionado y el libro de caja coincidan
	 * @param cashLine linea de caja a usar como medio de pago  
	 * @param ppm tipo de medio de pago
	 * @throws Exception en caso de no concordar las monedas
	 */
	protected void validateCashLineCurrency(MCashLine cashLine, MPOSPaymentMedium ppm) throws ModelException {
		MCash cash = new MCash(getCtx(), cashLine.getC_Cash_ID(), getTrxName());
		MCashBook cashBook = new MCashBook(getCtx(), cash.getC_CashBook_ID(), getTrxName());
		if (ppm.getC_Currency_ID() != cashBook.getC_Currency_ID())
			throw new ModelException (" La moneda del tipo de medio de pago (POSPaymentMedium) difiere con respecto a la moneda del libro de caja ");
	}
	
	
}
