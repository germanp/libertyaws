package org.libertya.ws.handler;

import java.util.HashMap;

import org.libertya.ws.bean.parameter.OrderParameterBean;
import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.exception.ModelException;
import org.libertya.ws.handler.createFrom.CreateFromInvoice;
import org.libertya.ws.handler.createFrom.CreateFromShipment;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;

public class OrderDocumentHandler extends DocumentHandler {
	
	/**
	 * Creación de pedido de cliente
	 */
	public ResultBean orderCreateCustomer(OrderParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeOrder, boolean createInvoice, boolean completeInvoice, boolean createShipment, boolean completeShipment) {
		return orderCreate(data, true, bPartnerID, bPartnerValue, taxID, completeOrder, createInvoice, completeInvoice, createShipment, completeShipment);	
	}

	/**
	 * Creación de pedido de proveedor 
	 */
	public ResultBean orderCreateVendor(OrderParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeOrder, boolean createInvoice, boolean completeInvoice) {
		return orderCreate(data, false, bPartnerID, bPartnerValue, taxID, completeOrder, createInvoice, completeInvoice, false, false);	
	}

	/**
	 * Creación de pedido
	 * Debe indicarse, además del conjunto de parametros, una de las tres opciones para indicar la entidad comercial
	 * @param data parametros correspondientes
	 * @param isSoTrx pedido de compra o de venta
	 * @param bPartnerID identificador de la entidad comercial (o -1 en caso de no indicar)
	 * @param bPartnerValue clave de busqueda de la entidad comercial (o null en caso de no indicar)
	 * @param taxID CUIT de la entidad comercial (o null en caso de no indicar)
	 * @param completeOrder para especificar si se debe completar el pedido
	 * @param createInvoice para indicar si se debe crear la factura a partir del pedido
	 * @param invoiceDocType_ID en caso de crear factura, el tipo de documento del mismo
	 * @param completeInvoice en caso de crear factura, permite indicar si se debe completar también la factura
	 * @param createInOut para indicar si se debe crear el remito a partir del pedido
	 * @param completeInOut en caso de crear remito, permite indicar si se debe completar también el remito 
	 * @return ResultBean con OK y datos: C_Order_ID, Order_DocumentNo creado, etc. o ERROR en caso contrario.
	 */
	protected ResultBean orderCreate(OrderParameterBean data, boolean isSoTrx, int bPartnerID, String bPartnerValue, String taxID, boolean completeOrder, boolean createInvoice, boolean completeInvoice, boolean createInOut, boolean completeInOut) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"isSoTrx", "bPartnerID", "bPartnerValue", "taxID", "completeOrder", "createInvoice", "completeInvoice", "createInOut", "completeInOut"}, new Object[]{isSoTrx, bPartnerID, bPartnerValue, taxID, completeOrder, createInvoice, completeInvoice, createInOut, completeInOut});
			
			/* === Procesar (logica especifica) === */
			// Recuperar BPartner
			MBPartner aBPartner = (MBPartner)getPO("C_BPartner", bPartnerID, "value", bPartnerValue, false, true, true, false);
			if (aBPartner == null || aBPartner.getC_BPartner_ID() == 0) 
				aBPartner = (MBPartner)getPO("C_BPartner", bPartnerID, "taxID", taxID, false, true, true, false);
			if (aBPartner == null || aBPartner.getC_BPartner_ID() == 0)
				throw new Exception("No se ha podido recuperar una entidad comercial con los criterios especificados");

			// Instanciar y persistir Pedido
			MOrder anOrder = new MOrder(getCtx(), 0, getTrxName());
			
			int docTypeTargetID = -1;
			try {
				docTypeTargetID = Integer.parseInt(toLowerCaseKeys(data.getMainTable()).get("c_doctypetarget_id"));
			} catch (Exception e) { throw new Exception("C_DocTypeTarget_ID no especificado"); }
			if (docTypeTargetID <= 0)
				throw new Exception("C_DocTypeTarget_ID incorrecto");
			anOrder.setBPartner(aBPartner);
			anOrder.setIsSOTrx(isSoTrx);
			anOrder.setC_DocTypeTarget_ID(docTypeTargetID);
			setValues(anOrder, data.getMainTable(), true);
			// En caso de ser necesario, copiar los datos de localización en la cabecera
			setBPartnerAddressInDocument(anOrder, bPartnerID);
			if (!anOrder.save())
				throw new ModelException("Error al persistir pedido:" + CLogger.retrieveErrorAsString());
			// Instanciar y persistir las Lineas de pedido
			for (HashMap<String, String> line : data.getDocumentLines())
			{
				MOrderLine anOrderLine = new MOrderLine(anOrder);
				// Setear el QtyOrdered a partir del QtyEntered a fin de evitar errores o inconsistencias en validaciones de modelo
				String qtyEntered = toLowerCaseKeys(line).get("qtyentered");
				if (qtyEntered == null || qtyEntered.length()==0)
					throw new ModelException("QtyEntered de la linea de pedido no especificado");
				line.put("QtyOrdered", qtyEntered);
				setValues(anOrderLine, line, true);
				// Setear tax por defecto
				anOrderLine.setTax();
				if (!anOrderLine.save())
					throw new ModelException("Error al persistir linea de pedido:" + CLogger.retrieveErrorAsString());
			}
			// Completar el pedido si corresponde
			if (completeOrder && !DocumentEngine.processAndSave(anOrder, DocAction.ACTION_Complete, false))
				throw new ModelException("Error al completar el pedido:" + Msg.parseTranslation(getCtx(), anOrder.getProcessMsg()));

			// Crear factura a partir del pedido (si el pedido está completado)
			MInvoice anInvoice = null;
			if (completeOrder && createInvoice) {
				anInvoice = createInvoiceFromOrder(anOrder, data.getInvoiceDocTypeTargetID(), data.getInvoicePuntoDeVenta(), data.getInvoiceTipoComprobante(), completeInvoice);
			}
			
			// Crear remito a partir del pedido (si el pedido está completado)
			MInOut anInOut = null;
			if (completeOrder && createInOut) {
				anInOut = createInOutFromOrder(anOrder, completeInOut);
			}
			
			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("C_Order_ID", Integer.toString(anOrder.getC_Order_ID()));
			result.put("Order_DocumentNo", anOrder.getDocumentNo());
			if (anInvoice != null) {
				result.put("C_Invoice_ID", Integer.toString(anInvoice.getC_Invoice_ID()));
				result.put("Invoice_DocumentNo", anInvoice.getDocumentNo());
			}
			if (anInOut != null) {
				result.put("M_InOut_ID", Integer.toString(anInOut.getM_InOut_ID()));
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
	 * Elimina un pedido en borrador, el cual es indicado por su ID
	 */
	public ResultBean orderDeleteByID(ParameterBean data, int orderID) {
		return orderDelete(data, orderID, null, null);
	}

	/**
	 * Elimina un pedido en borrador, el cual es indicado por una columna y un criterio
	 */
	public ResultBean orderDeleteByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return orderDelete(data, -1, null, null);
	}
	
	/**
	 * Elimina un pedido en borrador.  El mismo puede ser indicada por su ID, o por un par: Nombre de Columna / Criterio de Columna
	 * 		La segunda manera de recuperar un pedido debe devolver solo un registro resultante, o se retornará un error
	 * @param data parametros correspondientes
	 * @param orderID identificador del pedido (C_Order_ID)
	 * @param columnName y columnCriteria columna y valor a filtrar para recuperar el pedido en cuestion
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	protected ResultBean orderDelete(ParameterBean data, int orderID, String columnName, String columnCriteria) {
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"orderID", "columnName", "columnCriteria"}, new Object[]{orderID, columnName, columnCriteria});
			
			MOrder anOrder = (MOrder)getPO("C_Order", orderID, columnName, columnCriteria, true, false, true, true);
			if (!anOrder.delete(false))
				throw new ModelException("Error al intentar eliminar el pedido " + anOrder.getC_Order_ID() + ": " + CLogger.retrieveErrorAsString());
			
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
	 * Completa un pedido en borrador, el cual es indicado por su ID; con posibilidad de creacion de remito
	 */
	public ResultBean orderCompleteByID(OrderParameterBean data, int orderID, boolean createInvoice, boolean completeInvoice, boolean createInOut, boolean completeInOut) {
		return orderComplete(data, orderID, null, null, createInvoice, completeInvoice, createInOut, completeInOut);
	}

	/**
	 * Completa un pedido en borrador, el cual es indicado por una columna y un criterio de busqueda; con posibilidad de creacion de remito
	 */
	public ResultBean orderCompleteByColumn(OrderParameterBean data, String columnName, String columnCriteria, boolean createInvoice, boolean completeInvoice, boolean createInOut, boolean completeInOut) {
		return orderComplete(data, -1, columnName, columnCriteria, createInvoice, completeInvoice, createInOut, completeInOut);
	}

	/**
	 * Completa un pedido en borrador.  El mismo puede ser indicado por su ID, o por un par: Nombre de Columna / Criterio de Columna
	 * 		La segunda manera de recuperar un pedido debe devolver solo un registro resultante, o se retornará un error
	 * @param data parametros correspondientes
	 * @param orderID identificador de el pedido (C_Order_ID)
	 * @param columnName y columnCriteria columna y valor a filtrar para recuperar el pedido en cuestion
	 * @param createInvoice para indicar si se debe crear la factura a partir del pedido
	 * @param invoiceDocType_ID en caso de crear factura, el tipo de documento del mismo
	 * @param completeInvoice en caso de crear factura, permite indicar si se debe completar también la factura
	 * @param createInOut para indicar si se debe crear el remito a partir del pedido
	 * @param completeInOut en caso de crear remito, permite indicar si se debe completar también el remito 
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	protected ResultBean orderComplete(OrderParameterBean data, int orderID, String columnName, String columnCriteria, boolean createInvoice, boolean completeInvoice, boolean createInOut, boolean completeInOut) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"orderID", "columnName", "columnCriteria", "createInvoice", "completeInvoice", "createInOut", "completeInOut"}, new Object[]{orderID, columnName, columnCriteria, createInvoice, completeInvoice, createInOut, completeInOut});

			// Recuperar y Completar el pedido
			MOrder anOrder = (MOrder)getPO("C_Order", orderID, columnName, columnCriteria, true, false, true, true);
			
			// Si el documento ya está completado retornar error
			if (DocAction.STATUS_Completed.equals(anOrder.getDocStatus()))
				throw new ModelException("Imposible completar el documento dado que el mismo ya se encuentra completado.");
			
			// Completar el documento
			if (!DocumentEngine.processAndSave(anOrder, DocAction.ACTION_Complete, false))
				throw new ModelException("Error al completar el pedido:" + Msg.parseTranslation(getCtx(), anOrder.getProcessMsg()));
			
			
			// Crear factura a partir del pedido (si corresponde)
			MInvoice anInvoice = null;
			if (createInvoice) {
				anInvoice = createInvoiceFromOrder(anOrder, data.getInvoiceDocTypeTargetID(), data.getInvoicePuntoDeVenta(), data.getInvoiceTipoComprobante(), completeInvoice);
			}
			// Crear remito a partir del pedido (si corresponde)			
			MInOut anInOut = null;
			if (createInOut) {
				anInOut = createInOutFromOrder(anOrder, completeInOut);
			}
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			if (anInvoice != null) {
				result.put("C_Invoice_ID", Integer.toString(anInvoice.getC_Invoice_ID()));
				result.put("Invoice_DocumentNo", anInvoice.getDocumentNo());
			}
			if (anInOut != null) {
				result.put("M_InOut_ID", Integer.toString(anInOut.getM_InOut_ID()));
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
	 * Anula un pedido en borrador, el cual es indicado por su ID
	 */
	public ResultBean orderVoidByID(ParameterBean data, int orderID) {
		return orderVoid(data, orderID, null, null);
	}
	
	/**
	 * Anula un pedido en borrador, el cual es indicado por una columna y un criterio de busqueda
	 */
	public ResultBean orderVoidByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return orderVoid(data, -1, columnName, columnCriteria);
	}
	
	/**
	 * Anula un pedido en borrador.  El mismo puede ser indicado por su ID, o por un par: Nombre de Columna / Criterio de Columna
	 * 		La segunda manera de recuperar un pedido debe devolver solo un registro resultante, o se retornará un error
	 * @param data parametros correspondientes
	 * @param orderID identificador del pedido (C_Order_ID)
	 * @param columnName y columnCriteria columna y valor a filtrar para recuperar el pedido en cuestion
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	protected ResultBean orderVoid(ParameterBean data, int orderID, String columnName, String columnCriteria)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"orderID", "columnName", "columnCriteria"}, new Object[]{orderID, columnName, columnCriteria});

			// Recuperar y anular el pedido
			MOrder anOrder = (MOrder)getPO("C_Order", orderID, columnName, columnCriteria, true, false, true, true);
			if (!DocumentEngine.processAndSave(anOrder, DocAction.ACTION_Void, false))
				throw new ModelException("Error al anular el pedido:" + Msg.parseTranslation(getCtx(), anOrder.getProcessMsg()));
			
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
	 * Gestiona la creación de una factura a partir del pedido, apoyandose en la clase CreateFromInvoice.
	 * La creación varia con respecto a la lógica en InvoiceDocumentHandler, y es por ésto que 
	 * fue necesario crear este método. 
	 */
	protected MInvoice createInvoiceFromOrder(MOrder anOrder, int invoiceDocTypeTargetID, int invoicePuntoDeVenta, String invoiceTipoComprobante, boolean completeInvoice) throws ModelException, Exception 
	{
		// Instanciar la nueva factura
		MInvoice anInvoice = new MInvoice(anOrder, invoiceDocTypeTargetID, Env.getDate());
		// Setear los parametros adicionales sobre el tipo de documento a generar
		anInvoice.setC_DocTypeTarget_ID(invoiceDocTypeTargetID);
		anInvoice.setPuntoDeVenta(invoicePuntoDeVenta);
		anInvoice.setTipoComprobante(invoiceTipoComprobante);
		// Copia general de campos de cabecera
		CreateFromInvoice.copyHeaderValuesFromOrder(anInvoice, anOrder, getCtx(), getTrxName());
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
			// Copiar los datos de la linea de pedido en la linea de la factura
			copyPOValues(orderLines[i], anInvoiceLine);
			// Persistir la linea
			if (!anInvoiceLine.save())
				throw new ModelException("Error al persistir linea de factura:" + CLogger.retrieveErrorAsString());
		}
		
		// Completar la factura si corresponde
		if (completeInvoice && !DocumentEngine.processAndSave(anInvoice, DocAction.ACTION_Complete, false))
			throw new ModelException("Error al completar la factura:" + Msg.parseTranslation(getCtx(), anInvoice.getProcessMsg()));

		
		// Retornar la factura generada
		return anInvoice;
	}
	
	/**
	 * Gestiona la creación de un remito a partir del pedido, apoyandose en la clase CreateFromShipment.
	 */
	protected MInOut createInOutFromOrder(MOrder anOrder, boolean completeInOut) throws ModelException, Exception 
	{
		// Instanciar el nuevo remito
		MInOut anInOut = new MInOut(anOrder, 0, Env.getDate());
		// Copia general de campos de cabecera
		CreateFromShipment.copyHeaderValuesFromOrder(anInOut, anOrder, getCtx(), getTrxName());
		// Copiar los datos del pedido en el remito
		copyPOValues(anOrder, anInOut);
		if (!anInOut.save())
			throw new ModelException("Error al persistir Remito:" + CLogger.retrieveErrorAsString());

		// Instanciar y persistir las Lineas de remito a partir de las lineas de pedido
		MOrderLine[] orderLines = anOrder.getLines();
		for (int i=0; i<orderLines.length; i++)
		{
			// Crear nueva linea y setearle los datos originales de la linea de pedido
			MInOutLine anInOutLine = new MInOutLine(anInOut);
			anInOutLine.setOrderLine(orderLines[i], 0, orderLines[i].getQtyOrdered());
			// Copia general de campos de cabecera
			CreateFromShipment.copyLineValuesFromOrderLine(anInOut, anOrder, anInOutLine, orderLines[i], getCtx(), getTrxName());
			// Copiar los datos de la linea de pedido en la linea del remito
			copyPOValues(orderLines[i], anInOutLine);
			// Persistir la linea
			if (!anInOutLine.save())
				throw new ModelException("Error al persistir linea de remito:" + CLogger.retrieveErrorAsString());
		}
		
		// Completar el remito si corresponde
		if (completeInOut && !DocumentEngine.processAndSave(anInOut, DocAction.ACTION_Complete, false))
			throw new ModelException("Error al completar el remito:" + Msg.parseTranslation(getCtx(), anInOut.getProcessMsg()));
		
		// Retornar el remito generado
		return anInOut;		
	}
	
	
	public ResultBean orderUpdateByID(ParameterBean data, int orderID, boolean completeOrder) {
		// TODO: Implementation pending
		return null;
	}
	
	public ResultBean orderUpdateByColumn(ParameterBean data, String columnName, String columnCriteria, boolean completeOrder) {
		// TODO: Implementation pending
		return null;
	}

	
}
