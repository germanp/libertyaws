package org.libertya.ws.handler;

import java.util.HashMap;

import org.libertya.ws.bean.parameter.DocumentParameterBean;
import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.X_C_Order;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;

public class InOutDocumentHandler extends DocumentHandler {

	/**
	 * Creación de remito de salida 
	 */
	public ResultBean inOutCreateCustomer(DocumentParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut) {
		return inOutCreate(data, true, bPartnerID, bPartnerValue, taxID, completeInOut);
	}

	/**
	 * Creación de remito de entrada 
	 */
	public ResultBean inOutCreateVendor(DocumentParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut) {
		return inOutCreate(data, false, bPartnerID, bPartnerValue, taxID, completeInOut);
	}

	
	/**
	 * Creación de remito
	 * Debe indicarse, además del conjunto de parametros, una de las tres opciones para indicar la entidad comercial
	 * @param data parametros correspondientes
	 * @param isSoTrx si es remito de entrada o de salida
	 * @param bPartnerID identificador de la entidad comercial (o -1 en caso de no indicar)
	 * @param bPartnerValue clave de busqueda de la entidad comercial (o null en caso de no indicar)
	 * @param taxID CUIT de la entidad comercial (o null en caso de no indicar)
	 * @param completeInOut para especificar si se debe completar el remito
	 * @return ResultBean con OK y datos: M_InOut_ID, InOut_DocumentNo creado, etc. o ERROR en caso contrario.
	 */
	protected ResultBean inOutCreate(DocumentParameterBean data, boolean isSoTrx, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"isSoTrx", "bPartnerID", "bPartnerValue", "taxID", "completeInOut"}, new Object[]{isSoTrx, bPartnerID, bPartnerValue, taxID, completeInOut});
			
			/* === Procesar (logica especifica) === */
			// Recuperar BPartner
			MBPartner aBPartner = (MBPartner)getPO("C_BPartner", bPartnerID, "value", bPartnerValue, false, true, true, false);
			if (aBPartner == null || aBPartner.getC_BPartner_ID() == 0) 
				aBPartner = (MBPartner)getPO("C_BPartner", bPartnerID, "taxID", taxID, false, true, true, false);

			// Instanciar y persistir remito
			MInOut anInOut = new MInOut(getCtx(), 0, getTrxName());
			// Recuperar docType
			int docTypeID = -1;
			try {
				docTypeID = Integer.parseInt(toLowerCaseKeys(data.getMainTable()).get("c_doctypetarget_id"));
			} catch (Exception e) { throw new Exception("C_DocTypeTarget_ID no especificado"); }
			if (docTypeID <= 0)
				throw new Exception("C_DocTypeTarget_ID incorrecto");
			// Setear warehouse en el contexto (si viene especificado explicitamente o mediante el warehouse del pedido)
			int warehouseID = -1;
			try {
				// Setear el warehouse a partir del dato recibido en la map
				if (toLowerCaseKeys(data.getMainTable()).get("m_warehouse_id") != null) {
					warehouseID = Integer.parseInt(toLowerCaseKeys(data.getMainTable()).get("m_warehouse_id"));
				}
				// Setear el warehouse a partir del warehouse del pedido 
				if (warehouseID <= 0) {
					int orderID = -1;
					if (toLowerCaseKeys(data.getMainTable()).get("c_order_id") != null) {
						orderID = Integer.parseInt(toLowerCaseKeys(data.getMainTable()).get("c_order_id"));
					} 
					if (orderID > 0) {
						X_C_Order anOrder = new X_C_Order(getCtx(), orderID, getTrxName());
						warehouseID = anOrder.getM_Warehouse_ID();
					}
				}
			} catch (Exception e) { throw new Exception("Error al determinar el M_Warehouse_ID a utilizar en la creacion del remito"); }
			if (warehouseID <= 0)
				throw new Exception("Imposible determinar M_Warehouse_ID.  Indicar uno explicitamente o especificar el pedido asociado al remito");
			Env.setContext(getCtx(), "#M_Warehouse_ID", warehouseID);
			anInOut.setM_Warehouse_ID(warehouseID);
			if (aBPartner != null && aBPartner.getC_BPartner_ID() > 0)
				anInOut.setBPartner(aBPartner);
			anInOut.setIsSOTrx(isSoTrx);
			anInOut.setC_DocType_ID(docTypeID);
			anInOut.setMovementType(isSoTrx ? MInOut.MOVEMENTTYPE_CustomerShipment : MInOut.MOVEMENTTYPE_VendorReceipts);
			setValues(anInOut, data.getMainTable(), true);
			// En caso de ser necesario, copiar los datos de localización en la cabecera
			setBPartnerAddressInDocument(anInOut, bPartnerID);
			if (!anInOut.save())
				throw new ModelException("Error al persistir remito:" + CLogger.retrieveErrorAsString());
			// Instanciar y persistir las Lineas de remito
			for (HashMap<String, String> line : data.getDocumentLines())
			{
				MInOutLine anInOutLine = new MInOutLine(anInOut);
				// Setear el MovementQty a partir del QtyEntered a fin de evitar errores o inconsistencias en validaciones de modelo
				String qtyEntered = toLowerCaseKeys(line).get("qtyentered");
				if (qtyEntered == null || qtyEntered.length()==0)
					throw new ModelException("QtyEntered de la linea de remito no especificado");
				line.put("MovementQty", qtyEntered);
				setValues(anInOutLine, line, true);
				if (!anInOutLine.save())
					throw new ModelException("Error al persistir linea de remito:" + CLogger.retrieveErrorAsString());
			}
			// Completar el remito si corresponde
			if (completeInOut && !DocumentEngine.processAndSave(anInOut, DocAction.ACTION_Complete, false))
				throw new ModelException("Error al completar el remito:" + Msg.parseTranslation(getCtx(), anInOut.getProcessMsg()));
									
			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("M_InOut_ID", Integer.toString(anInOut.getM_InOut_ID()));
			result.put("InOut_DocumentNo", anInOut.getDocumentNo());
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
	 * Creación de un remito a partir de un pedido
	 * @param data información de acceso y de las líneas a remitir (para remisiones parciales).   
	 * 			Si no es indican líneas, se considera remisión completa
	 * @param orderID pedido a tomar como base para la creación del remito
	 * @return ResultBean con OK y datos: M_InOut_ID, InOut_DocumentNo creado, etc. o ERROR en caso contrario.
	 */
	public ResultBean inOutCreateFromOrder(DocumentParameterBean data, int orderID, boolean completeInOut) {
		
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"orderID", "completeInOut"}, new Object[]{orderID, completeInOut});
			
			/* === Procesar (logica especifica) === */
			// Recuperar el pedido
			MOrder anOrder = new MOrder(getCtx(), orderID, getTrxName());
			// Crear el remito a partir del pedido
			MInOut anInOut = new OrderDocumentHandler().createInOutFromOrder(anOrder, completeInOut, data.getDocumentLines());
			
			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("M_InOut_ID", Integer.toString(anInOut.getM_InOut_ID()));
			result.put("InOut_DocumentNo", anInOut.getDocumentNo());
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
	 * Elimina un remito en borrador.  El mismo debe ser indicado por su ID
	 * @param data parametros correspondientes
	 * @param inOutID identificador del remito (M_InOut_ID)
	 * @return ResultBean con OK o ERROR
	 */
	public ResultBean inOutDeleteByID(ParameterBean data, int inOutID) {
		return inOutDelete(data, inOutID, null, null);
	}

	/**
	 * Elimina un remito en borrador.
	 * @param data parametros correspondientes
	 * @param columnName y columnCriteria columna y valor a filtrar para recuperar el remito en cuestión
	 * @return ResultBean con OK o ERROR
	 */
	public ResultBean inOutDeleteByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return inOutDelete(data, -1, null, null);
	}
	
	/**
	 * Elimina un remito en borrador.  El mismo puede ser indicada por su ID, o por un par: Nombre de Columna / Criterio de Columna
	 * 		La segunda manera de recuperar un remito debe devolver solo un registro resultante, o se retornará un error
	 * @param data parametros correspondientes
	 * @param inOutID identificador del remito (M_InOut_ID)
	 * @param columnName y columnCriteria columna y valor a filtrar para recuperar el remito en cuestion
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	protected ResultBean inOutDelete(ParameterBean data, int inOutID, String columnName, String columnCriteria) {
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"inOutID", "columnName", "columnCriteria"}, new Object[]{inOutID, columnName, columnCriteria});
			
			MInOut anInOut = (MInOut)getPO("M_InOut", inOutID, columnName, columnCriteria, true, false, true, true);
			if (!anInOut.delete(false))
				throw new ModelException("Error al intentar eliminar el remito " + anInOut.getM_InOut_ID() + ": " + CLogger.retrieveErrorAsString());
			
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
	 * Completa un remito en borrador.  El mismo debe ser indicado por su ID
	 * @param data parametros correspondientes
	 * @param inOutID identificador del remito (M_InOut_ID)
	 * @return ResultBean con OK o ERROR. 
	 */
	public ResultBean inOutCompleteByID(ParameterBean data, int inOutID) {
		return inOutComplete(data, inOutID, null, null);
	}

	/**
	 * Completa un remito en borrador.  El mismo debe ser indicado por un par: Nombre de Columna / Criterio de Columna
	 * @param data parametros correspondientes
	 * @param columnName y columnCriteria columna y valor a filtrar para recuperar el remito en cuestion
	 * @return ResultBean con OK o ERROR. 
	 */
	public ResultBean inOutCompleteByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return inOutComplete(data, -1, columnName, columnCriteria);
	}

	
	/**
	 * Completa un remito en borrador.  El mismo puede ser indicado por su ID, o por un par: Nombre de Columna / Criterio de Columna
	 * 		La segunda manera de recuperar un remito debe devolver solo un registro resultante, o se retornará un error
	 * @param data parametros correspondientes
	 * @param inOutID identificador de el remito (M_InOut_ID)
	 * @param columnName y columnCriteria columna y valor a filtrar para recuperar el remito en cuestion
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	protected ResultBean inOutComplete(ParameterBean data, int inOutID, String columnName, String columnCriteria) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"inOutID", "columnName", "columnCriteria"}, new Object[]{inOutID, columnName, columnCriteria});

			// Recuperar y Completar el remito
			MInOut anInOut = (MInOut)getPO("M_InOut", inOutID, columnName, columnCriteria, true, false, true, true);
			
			// Si el documento ya está completado retornar error
			if (DocAction.STATUS_Completed.equals(anInOut.getDocStatus()))
				throw new ModelException("Imposible completar el documento dado que el mismo ya se encuentra completado.");
			
			// Completar el documento
			if (!DocumentEngine.processAndSave(anInOut, DocAction.ACTION_Complete, false))
				throw new ModelException("Error al completar el remito:" + Msg.parseTranslation(getCtx(), anInOut.getProcessMsg()));
			
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
	 * Anula un remito en borrador.  El mismo debe ser indicado por su ID
	 * @param data parametros correspondientes
	 * @param inOutID identificador del remito (M_InOut_ID)
	 * @return ResultBean con OK o ERROR 
	 */
	public ResultBean inOutVoidByID(ParameterBean data, int inOutID) {
		return inOutVoid(data, inOutID, null, null);
	}
	
	/**
	 * Anula un remito en borrador.  El mismo debe ser indicado por un par: Nombre de Columna / Criterio de Columna
	 * @param data parametros correspondientes
	 * @param columnName y columnCriteria columna y valor a filtrar para recuperar el remito en cuestion
	 * @return ResultBean con OK o ERROR 
	 */
	public ResultBean inOutVoidByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return inOutComplete(data, -1, columnName, columnCriteria);	
	}

	
	/**
	 * Anula un remito en borrador.  El mismo puede ser indicado por su ID, o por un par: Nombre de Columna / Criterio de Columna
	 * 		La segunda manera de recuperar un remito debe devolver solo un registro resultante, o se retornará un error
	 * @param data parametros correspondientes
	 * @param inOutID identificador del remito (M_InOut_ID)
	 * @param columnName y columnCriteria columna y valor a filtrar para recuperar el remito en cuestion
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	protected ResultBean inOutVoid(ParameterBean data, int inOutID, String columnName, String columnCriteria)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"inOutID", "columnName", "columnCriteria"}, new Object[]{inOutID, columnName, columnCriteria});

			// Recuperar y anular el remito
			MInOut anInOut = (MInOut)getPO("M_InOut", inOutID, columnName, columnCriteria, true, false, true, true);
			if (!DocumentEngine.processAndSave(anInOut, DocAction.ACTION_Void, false))
				throw new ModelException("Error al anular el remito:" + Msg.parseTranslation(getCtx(), anInOut.getProcessMsg()));
			
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
