package org.libertya.ws.handler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.libertya.ws.bean.parameter.InvoiceParameterBean;
import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.exception.ModelException;
import org.libertya.ws.handler.createFrom.CreateFromInvoice;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MInvoiceTax;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;

public class InvoiceDocumentHandler extends DocumentHandler {

	/**
	 * Creación de factura de cliente
	 * """"""""""""""""""""""""""""""
	 * Debe indicarse, además del conjunto de parametros, una de las tres opciones para indicar la entidad comercial
	 * @param data parametros correspondientes
	 * @param bPartnerID identificador de la entidad comercial (o -1 en caso de no indicar)
	 * @param bPartnerValue clave de busqueda de la entidad comercial (o null en caso de no indicar)
	 * @param taxID CUIT de la entidad comercial (o null en caso de no indicar)
	 * @param completeDocument para especificar si se debe completar la factura o no
	 * @return ResultBean con OK, ERROR, etc.
	 */
	public ResultBean invoiceCreateCustomer(InvoiceParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeDocument)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"bPartnerID", "bPartnerValue", "taxID", "completeDocument"}, new Object[]{bPartnerID, bPartnerValue, taxID, completeDocument});
			
			/* === Procesar (logica especifica) === */	
			// Recuperar BPartner
			MBPartner aBPartner = (MBPartner)getPO("C_BPartner", bPartnerID, "value", bPartnerValue, false, true, true, false);
			if (aBPartner == null || aBPartner.getC_BPartner_ID() == 0) 
				aBPartner = (MBPartner)getPO("C_BPartner", bPartnerID, "taxID", taxID, false, true, true, false);
			if (aBPartner == null || aBPartner.getC_BPartner_ID() == 0)
				throw new Exception("No se ha podido recuperar una entidad comercial con los criterios especificados");
			
			// Instanciar y persistir Invoice
			MInvoice anInvoice = new MInvoice( getCtx(), 0, getTrxName());
			
			int docTypeTargetID = 0;
			try {
				docTypeTargetID = Integer.parseInt(toLowerCaseKeys(data.getMainTable()).get("c_doctypetarget_id"));
			} catch (Exception e) { /* Campo c_doctypetarget_id no especificado. No es un error. El modelo intentará setearlo a partir de otros parámetros. */ }
            if (docTypeTargetID > 1)
                anInvoice.setC_DocTypeTarget_ID(docTypeTargetID);
			anInvoice.setBPartner(aBPartner);
			anInvoice.setIsSOTrx(true);
			setValues(anInvoice, data.getMainTable(), true);
			// En caso de ser necesario, copiar los datos de localización en la cabecera
			setBPartnerAddressInDocument(anInvoice, bPartnerID);
			if (!anInvoice.save())
				throw new ModelException("Error al persistir factura:" + CLogger.retrieveErrorAsString());
			// Instanciar y persistir las Lineas de factura
			for (HashMap<String, String> line : data.getDocumentLines())
			{
				MInvoiceLine anInvoiceLine = new MInvoiceLine(anInvoice);

				// En caso de haber especificado un artículo en la línea de factura, es necesario determinar 
				// el m_warehouse_id dado que este dato es necesario a fin de determinar el impuesto a utilizar
				// (en el caso que c_tax_id no venga configurado como un parámetro más de la línea de factura)}
				// Lógica análoga para c_charge_id (también requiere m_warehouse_id)
				int lineProductID = -1;
				int lineChargeID = -1;
				int lineTaxID = -1;
				try {
					lineProductID = Integer.parseInt(toLowerCaseKeys(line).get("m_product_id"));
					anInvoiceLine.setM_Product_ID(lineProductID);		
				} catch (Exception e) { /* Producto no seteado en la linea */	}
				try {
					lineChargeID = Integer.parseInt(toLowerCaseKeys(line).get("c_charge_id"));
					anInvoiceLine.setC_Charge_ID(lineChargeID);		
				} catch (Exception e) { /* Charge no seteado en la linea */	}
				try {
					lineTaxID = Integer.parseInt(toLowerCaseKeys(line).get("c_tax_id"));
					anInvoiceLine.setC_Tax_ID(lineTaxID);
				} catch (Exception e) { /* Impuesto no seteado en la linea */	}
				
				// Si se especificó el artículo (o cargo), pero no el impuesto, entonces el warehouse se convierte en obligatorio como input para setTax
				if ((lineChargeID > 0 || lineProductID > 0) && lineTaxID == -1 && Env.getContextAsInt(getCtx(), "#M_Warehouse_ID") <= 0)
					throw new ModelException("No se puede determinar el impuesto para la línea de la factura dado que no se pudo recuperar un almacen perteneciente a la organización. " +
											 "Configure esta información o especifique explicitamente el impuesto para la linea de factura. ");
				
				// Setear tax por defecto (luego será redefinido si corresponde)
				anInvoiceLine.setTax();
				
				// Setear el QtyInvoiced a partir del QtyEntered a fin de evitar errores o inconsistencias en validaciones de modelo
				String qtyEntered = toLowerCaseKeys(line).get("qtyentered");
				if (qtyEntered == null || qtyEntered.length()==0)
					throw new ModelException("QtyEntered de la linea de factura no especificado");
				line.put("QtyInvoiced", qtyEntered);

				// Setear precios a partir del PriceEntered a fin de evitar errores o inconsistencias en validaciones de modelo
				// (solo si dicho parametro fue seteado, en caso contrario usara el obtenido a partir del precio del articulo)
				String priceEntered = toLowerCaseKeys(line).get("priceentered");
				if (priceEntered != null && priceEntered.length()>0) {
					HashMap<String, String> forceFieldLine = new HashMap<String, String>();
					forceFieldLine.put("PriceActual", priceEntered);
					forceFieldLine.put("PriceList", priceEntered);
					// Se fuerza el seteo de estas columnas (dado que en metadatos estan indicadas como no actualizables)
					setValues(anInvoiceLine, forceFieldLine, true, true, false);
				}
				
				// Setear demas parametros
				setValues(anInvoiceLine, line, true);
				if (!anInvoiceLine.save())
					throw new ModelException("Error al persistir linea de factura:" + CLogger.retrieveErrorAsString());
			}
			// Instanciar y persistir Otros impuestos
			for (HashMap<String, String> otherTax : data.getOtherTaxes())
			{
				MInvoiceTax aTax = new MInvoiceTax(getCtx(), 0, getTrxName());
				aTax.setC_Invoice_ID(anInvoice.getC_Invoice_ID());
				setValues(aTax, otherTax, true);
				if (!aTax.save())
					throw new ModelException("Error al persistir impuesto:" + CLogger.retrieveErrorAsString());
			}
			// Completar la factura si corresponde
			if (completeDocument && !DocumentEngine.processAndSave(anInvoice, DocAction.ACTION_Complete, false))
				throw new ModelException("Error al completar la factura:" + Msg.parseTranslation(getCtx(), anInvoice.getProcessMsg()));
			
			// Recargar la factura para obtener su grand total
			anInvoice =  new MInvoice( getCtx(), anInvoice.getC_Invoice_ID(), getTrxName());
			
			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("C_Invoice_ID", Integer.toString(anInvoice.getC_Invoice_ID()));
			result.put("Invoice_DocumentNo", anInvoice.getDocumentNo());
			result.put("CAE", anInvoice.getcae());
			result.put("CAE_Vto", anInvoice.getvtocae()!=null?anInvoice.getvtocae().toString():null);
			result.put("GrandTotal", anInvoice.getGrandTotal()!=null?anInvoice.getGrandTotal().toString():null);
			result.put("DueDate", getInvoiceDueDate(anInvoice));
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
	 * Creación de factura a partir de un pedido.  Para determinar el pedido a utilizar se debe especificar su ID
	 */
	public ResultBean invoiceCreateCustomerFromOrderByID(InvoiceParameterBean data, int orderID, boolean completeDocument) {
		return invoiceCreateCustomerFromOrder(data, orderID, null, null, completeDocument);
	}
	
	/**
	 * Creacion de factura a partir de un pedido.  Para determinr el pedido a utilizar, se debe especificar una columna 
	 * 	en searchColumn y un valor asociado en searchCriteria como dato de búsqueda
	 */
	public ResultBean invoiceCreateCustomerFromOrderByColumn(InvoiceParameterBean data, String searchColumn, String searchCriteria, boolean completeDocument) {
		return invoiceCreateCustomerFromOrder(data, -1, searchColumn, searchCriteria, completeDocument);
	}
	
	/**
	 * Creación de factura de cliente a partir de pedido
	 * """""""""""""""""""""""""""""""""""""""""""""""""
	 * @param data parametros correspondientes
	 * @param orderID el ID del pedido a utilizar como base
	 * @param searchColumn y searchCriteria permite buscar un pedido a partir de una columna dada y un valor dado para dicha columna
	 * 			El criterio especificado filtra además por la organización especificada en los parametros data
	 * @param completeDocument para especificar si se debe completar la factura o no
	 * @return ResultBean con OK, ERROR, etc.
	 */
	protected ResultBean invoiceCreateCustomerFromOrder(InvoiceParameterBean data, int orderID, String searchColumn, String searchCriteria, boolean completeDocument)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"orderID", "searchColumn", "searchCriteria", "completeDocument"}, new Object[]{orderID, searchColumn, searchCriteria, completeDocument});
			
			/* === Procesar (logica especifica) === */
			// Pasar a minuscula las keys de las lineas de pedidos en parametros para unificar criterios de busqueda
			// Se hace una sola vez al inicio por un tema de performance (es accedida constantemente)
			ArrayList<HashMap<String, String>> paramOrderLines = new ArrayList<HashMap<String, String>>();
			for (HashMap<String, String> line : data.getDocumentLines())
				paramOrderLines.add(toLowerCaseKeys(line));
			
			// Recuperar el pedido
			MOrder anOrder = (MOrder)getPO("C_Order", orderID, searchColumn, searchCriteria, true, false, true, true);
			// Recuperar dateinvoiced
			Timestamp dateInvoiced = null;
			try {
				dateInvoiced = Timestamp.valueOf(toLowerCaseKeys(data.getMainTable()).get("dateinvoiced"));
			} catch (Exception e) { throw new Exception("dateinvoiced no especificado"); }
			if (dateInvoiced == null)
				throw new Exception("dateinvoiced incorrecto");
			// Recuperar docTypeTargetID			
			int docTypeTargetID = 0;
			try {
				docTypeTargetID = Integer.parseInt(toLowerCaseKeys(data.getMainTable()).get("c_doctypetarget_id"));
			} catch (Exception e) { /* Campo c_doctypetarget_id no especificado. No es un error. El modelo intentará setearlo a partir de otros parámetros. */ } 
			
			// Instanciar y persistir el Invoice basado en la order
			MInvoice anInvoice = new MInvoice(anOrder, docTypeTargetID, dateInvoiced);
			anInvoice.setIsSOTrx(true);
			anInvoice.setDateInvoiced(dateInvoiced);
			anInvoice.setC_DocTypeTarget_ID(docTypeTargetID);
			// Copia general de campos de cabecera
			CreateFromInvoice.copyHeaderValuesFromOrder(anInvoice, anOrder, getCtx(), getTrxName());
			
			// Cargar los datos según lo recibido como parametro
			setValues(anInvoice, data.getMainTable(), true);
			// Copiar los datos del pedido en la factura
			copyPOValues(anOrder, anInvoice);
			if (!anInvoice.save())
				throw new ModelException("Error al persistir Factura:" + CLogger.retrieveErrorAsString());
			
			// Instanciar y persistir las Lineas de factura a partir de las lineas de pedido
			MOrderLine[] orderLines = anOrder.getLines();
			for (int i=0; i<orderLines.length; i++)
			{
				// Crear nueva linea y setearle los datos originales de la linea de pedido
				MInvoiceLine anInvoiceLine = new MInvoiceLine(anInvoice);
				anInvoiceLine.setOrderLine(orderLines[i]);

				// Copia general de campos de cabecera
				CreateFromInvoice.copyLineValuesFromOrderLine(anInvoice, anOrder, anInvoiceLine, orderLines[i], getCtx(), getTrxName());
				
				// Redefinir las columnas que correspondan según la info recibida como parametro
				// (matcheando entre la OrderLine y la paramOrderLine mediante C_OrderLine_ID)
				for (HashMap<String, String> paramOrderLine : paramOrderLines)
				{
					// Si esta línea de pedido (pasada por parametro) no coincide en su c_orderline_id, saltearla y buscar la siguiente
					if (paramOrderLine.get("c_orderline_id") == null ||  orderLines[i].getC_OrderLine_ID() != Integer.parseInt(paramOrderLine.get("c_orderline_id")))
						continue;
					
					// Setear los valores a la linea de factura en funcion de los parametros pasados como parametro 
					setValues(anInvoiceLine, paramOrderLine, true, true, false);
					anInvoiceLine.setPrice(anInvoiceLine.getPriceEntered());
				}
				// Copiar los datos de la linea de pedido en la linea de la factura
				// (Los que todavía no se definieron como parametro para esta línea) 
				copyPOValues(orderLines[i], anInvoiceLine);
				
				// Persistir la linea
				if (!anInvoiceLine.save())
					throw new ModelException("Error al persistir linea de factura:" + CLogger.retrieveErrorAsString());
			}

			// Completar la factura si corresponde
			if (completeDocument && !DocumentEngine.processAndSave(anInvoice, DocAction.ACTION_Complete, false))
				throw new ModelException("Error al completar la factura:" + Msg.parseTranslation(getCtx(), anInvoice.getProcessMsg()));

			// Recargar la factura para obtener su grand total
			anInvoice =  new MInvoice( getCtx(), anInvoice.getC_Invoice_ID(), getTrxName());
			
			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("C_Invoice_ID", Integer.toString(anInvoice.getC_Invoice_ID()));
			result.put("Invoice_DocumentNo", anInvoice.getDocumentNo());
			result.put("CAE", anInvoice.getcae());
			result.put("CAE_Vto", anInvoice.getvtocae()!=null?anInvoice.getvtocae().toString():null);
			result.put("GrandTotal", anInvoice.getGrandTotal()!=null?anInvoice.getGrandTotal().toString():null);
			result.put("DueDate", getInvoiceDueDate(anInvoice));
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
	 * Elimina una factura en borrador, la cual es indicada por su ID
	 */
	public ResultBean invoiceDeleteByID(ParameterBean data, int invoiceID) {
		return invoiceDelete(data, invoiceID, null, null);
	}
	
	/**
	 * Elimina una factura en borrador, la cual es indicada por una columna
	 */
	public ResultBean invoiceDeleteByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return invoiceDelete(data, -1, columnName, columnCriteria);
	}
	
	/**
	 * Elimina una factura en borrador.  La misma puede ser indicada por su ID, o por un par: Nombre de Columna / Criterio de Columna
	 * 		La segunda manera de recuperar una factura debe devolver solo un registro resultante, o se retornará un error
	 * @param data parametros correspondientes
	 * @param invoiceID identificador de la factura (C_Invoice_ID)
	 * @param columnName y columnCriteria columna y valor a filtrar para recuperar la factura en cuestion
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	protected ResultBean invoiceDelete(ParameterBean data, int invoiceID, String columnName, String columnCriteria)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"invoiceID", "columnName", "columnCriteria"}, new Object[]{invoiceID, columnName, columnCriteria});
			
			MInvoice anInvoice = (MInvoice)getPO("C_Invoice", invoiceID, columnName, columnCriteria, true, false, true, true);
			if (!anInvoice.delete(false))
				throw new ModelException("Error al intentar eliminar la factura " + anInvoice.getC_Invoice_ID() + ": " + CLogger.retrieveErrorAsString());
			
			/* === Retornar valor === */
			return new ResultBean(false, null, null);
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
	 * Completa una factura en borrador, la cual es indicada por su ID
	 */
	public ResultBean invoiceCompleteByID(ParameterBean data, int invoiceID) {
		return invoiceComplete(data, invoiceID, null, null);
	}
	
	/**
	 * Completa una factura en borrador, la cual es indicada por una columna
	 */
	public ResultBean invoiceCompleteByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return invoiceComplete(data, -1, columnName, columnCriteria);
	}
	
	
	/**
	 * Completa una factura en borrador.  La misma puede ser indicada por su ID, o por un par: Nombre de Columna / Criterio de Columna
	 * 		La segunda manera de recuperar una factura debe devolver solo un registro resultante, o se retornará un error
	 * @param data parametros correspondientes
	 * @param invoiceID identificador de la factura (C_Invoice_ID)
	 * @param columnName y columnCriteria columna y valor a filtrar para recuperar la factura en cuestion
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	protected ResultBean invoiceComplete(ParameterBean data, int invoiceID, String columnName, String columnCriteria)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"invoiceID", "columnName", "columnCriteria"}, new Object[]{invoiceID, columnName, columnCriteria});

			// Recuperar y Completar la factura si corresponde
			MInvoice anInvoice = (MInvoice)getPO("C_Invoice", invoiceID, columnName, columnCriteria, true, false, true, true);

			// Si el documento ya está completado retornar error
			if (DocAction.STATUS_Completed.equals(anInvoice.getDocStatus()))
				throw new ModelException("Imposible completar el documento dado que el mismo ya se encuentra completado.");
			
			// Completar el documento			
			if (!DocumentEngine.processAndSave(anInvoice, DocAction.ACTION_Complete, false))
				throw new ModelException("Error al completar la factura:" + Msg.parseTranslation(getCtx(), anInvoice.getProcessMsg()));
						
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("C_Invoice_ID", Integer.toString(anInvoice.getC_Invoice_ID()));
			result.put("Invoice_DocumentNo", anInvoice.getDocumentNo());
			if (anInvoice.getcae() != null) {
				result.put("CAE", anInvoice.getcae());
				result.put("CAE_Vto", anInvoice.getvtocae().toString());
			}
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
	 * Anula una factura en borrador, la cual es indicada por su ID
	 */
	public ResultBean invoiceVoidByID(ParameterBean data, int invoiceID) {
		return invoiceVoid(data, invoiceID, null, null);
	}
	
	/**
	 * Anula una factura en borrador, la cual es indicada por una columna
	 */
	public ResultBean invoiceVoidByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return invoiceVoid(data, -1, columnName, columnCriteria);
	}
	
	
	/**
	 * Anula una factura en borrador.  La misma puede ser indicada por su ID, o por un par: Nombre de Columna / Criterio de Columna
	 * 		La segunda manera de recuperar una factura debe devolver solo un registro resultante, o se retornará un error
	 * @param data parametros correspondientes
	 * @param invoiceID identificador de la factura (C_Invoice_ID)
	 * @param columnName y columnCriteria columna y valor a filtrar para recuperar la factura en cuestion
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	protected ResultBean invoiceVoid(ParameterBean data, int invoiceID, String columnName, String columnCriteria)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"invoiceID", "columnName", "columnCriteria"}, new Object[]{invoiceID, columnName, columnCriteria});

			// Recuperar y anular la factura 
			MInvoice anInvoice = (MInvoice)getPO("C_Invoice", invoiceID, columnName, columnCriteria, true, false, true, true);
			if (!DocumentEngine.processAndSave(anInvoice, DocAction.ACTION_Void, false))
				throw new ModelException("Error al anular la factura:" + Msg.parseTranslation(getCtx(), anInvoice.getProcessMsg()));
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			// Buscar el CreditNote_DocumentNo en el anular
			if (anInvoice.getRef_Invoice_ID()>0)
			{	
				MInvoice reversal = new MInvoice(getCtx(), anInvoice.getRef_Invoice_ID(), getTrxName());
				result.put("CreditNote_DocumentNo", reversal.getDocumentNo());
			}
			else
				result.put("CreditNote_DocumentNo", anInvoice.getProcessMsg());
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
	 * Retorna el vencimiento mas proximo de la factura recibida como parametro
	 * @param anInvoice factura a obtener su fecha de vencimiento
	 * @return un string representando la fecha de vencimiento 
	 */
	protected String getInvoiceDueDate(MInvoice anInvoice) {
		StringBuffer query = new StringBuffer();
		query.append(" SELECT duedate ")
			 .append(" FROM c_invoice i ")
		 	 .append(" INNER JOIN c_invoicepayschedule ips ON i.c_invoice_id = ips.c_invoice_id ")
			 .append(" WHERE i.c_invoice_id = ? ")
			 .append(" ORDER BY ips.duedate asc ")
			 .append(" LIMIT 1");
		
		return DB.getSQLValueString(getTrxName(), query.toString(), anInvoice.getC_Invoice_ID());
	}
	
	
	/**
	 * Actualiza campos de la cabecera de la factura únicamente.
	 * @param data el conjunto de datos a actualizar correspondientes a la cabecera de la factura
	 * @param invoiceID el ID de la factura a actualizar 
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	public ResultBean invoiceUpdateByID(ParameterBean data, int invoiceID) {
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"invoiceID"}, new Object[]{invoiceID});
			
			/* === Procesar (logica especifica) === */	
			// Instanciar y persistir Invoice
			MInvoice anInvoice = (MInvoice)getPO("C_Invoice", invoiceID, null, null, false, false, true, true);
			setValues(anInvoice, data.getMainTable(), false);
			if (!anInvoice.save())
				throw new ModelException("Error al actualizar la factura:" + CLogger.retrieveErrorAsString());

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
	
		
}
