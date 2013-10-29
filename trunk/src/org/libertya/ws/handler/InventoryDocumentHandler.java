package org.libertya.ws.handler;

import java.util.HashMap;

import org.libertya.ws.bean.parameter.DocumentParameterBean;
import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.MInventory;
import org.openXpertya.model.MInventoryLine;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;

public class InventoryDocumentHandler extends GeneralHandler {

	/**
	 * Creación de entrada en Inventario (Recuento de inventario, E/S Simple, etc.)
	 * @param data parametros correspondientes (cabecera y lineas)
	 * @param completeInventory indica si quiere completar el inventario
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	public ResultBean inventoryCreate(DocumentParameterBean data, boolean completeInventory) {
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"completeInventory"}, new Object[]{completeInventory});
			
			/* === Procesar (logica especifica) === */
			// Crear inventory
			MInventory anInventory = new MInventory(getCtx(), 0, getTrxName());

			// Recuperar docType
			int docTypeID = -1;
			try {
				docTypeID = Integer.parseInt(toLowerCaseKeys(data.getMainTable()).get("c_doctype_id"));
			} catch (Exception e) { throw new Exception("C_DocType_ID no especificado"); }
			if (docTypeID <= 0)
				throw new Exception("C_DocType_ID incorrecto");

			// Setear valores y persistir
			setValues(anInventory, data.getMainTable(), true);
			if (!anInventory.save())
				throw new ModelException("Error al persistir inventario:" + CLogger.retrieveErrorAsString());

			// Instanciar y persistir las Lineas de inventario
			for (HashMap<String, String> line : data.getDocumentLines())
			{
				MInventoryLine anInventoryLine = new MInventoryLine(getCtx(), 0, getTrxName());
				anInventoryLine.setM_Inventory_ID(anInventory.getM_Inventory_ID());
				setValues(anInventoryLine, line, true);
				if (!anInventory.save())
					throw new ModelException("Error al persistir linea de inventario:" + CLogger.retrieveErrorAsString());
			}
			// Completar el inventario si corresponde
			if (completeInventory && !DocumentEngine.processAndSave(anInventory, DocAction.ACTION_Complete, false))
				throw new ModelException("Error al completar el inventario:" + Msg.parseTranslation(getCtx(), anInventory.getProcessMsg()));
									
			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("M_Inventory_ID", Integer.toString(anInventory.getM_Inventory_ID()));
			result.put("Inventory_DocumentNo", anInventory.getDocumentNo());
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
	 * Permite completar un inventario indicando su ID
	 * @param data parametros de acceso
	 * @param inventoryID ID del inventario a completar
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	public ResultBean inventoryCompleteByID(ParameterBean data, int inventoryID) {
		return inventoryComplete(data, inventoryID, null, null);
	}

	/**
	 * Permite completar un inventario especificando el mismo mediante una columna y su valor 
	 * @param data parametros de acceso
	 * @param columnName columna a filtrar para recuperar el inventario en cuestión
	 * @param value valor a filtrar para recuperar el inventario en cuestión
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	public ResultBean inventoryCompleteByColumn(ParameterBean data, String columnName, String value) {
		return inventoryComplete(data, -1, columnName, value);
	}

	/**
	 * Completado de inventario 
	 */
	protected ResultBean inventoryComplete(ParameterBean data, int inventoryID, String columnName, String value) {
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"inventoryID", "columnName", "value"}, new Object[]{inventoryID, columnName, value});

			// Recuperar y Completar el inventario
			MInventory anInventory = (MInventory)getPO("M_Inventory", inventoryID, columnName, value, true, false, true, true);
			
			// Si el documento ya está completado retornar error
			if (DocAction.STATUS_Completed.equals(anInventory.getDocStatus()))
				throw new ModelException("Imposible completar el documento dado que el mismo ya se encuentra completado.");
			
			// Completar el documento
			if (!DocumentEngine.processAndSave(anInventory, DocAction.ACTION_Complete, false))
				throw new ModelException("Error al completar el inventario:" + Msg.parseTranslation(getCtx(), anInventory.getProcessMsg()));
			
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
	 * Permite eliminar un inventario indicando su ID
	 * @param data parametros de acceso
	 * @param inventoryID ID del inventario a eliminar
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	public ResultBean inventoryDeleteByID(ParameterBean data, int inventoryID) {
		return inventoryDelete(data, inventoryID, null, null);
				
	}
	
	/**
	 * Permite eliminar un inventario especificando el mismo mediante una columna y su valor
	 * @param data parametros de acceso
	 * @param columnName columna a filtrar para recuperar el inventario en cuestión
	 * @param value valor a filtrar para recuperar el inventario en cuestión
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	public ResultBean inventoryDeleteByColumn(ParameterBean data, String columnName, String value) {
		return inventoryDelete(data, -1, columnName, value);
	}

	/**
	 * Eliminacion de inventario 
	 */
	protected ResultBean inventoryDelete(ParameterBean data, int inventoryID, String columnName, String value) {
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"inventoryID", "columnName", "value"}, new Object[]{inventoryID, columnName, value});
			
			MInventory anInventory = (MInventory)getPO("M_Inventory", inventoryID, columnName, value, true, false, true, true);
			if (!anInventory.delete(false))
				throw new ModelException("Error al intentar eliminar el inventario " + anInventory.getM_Inventory_ID() + ": " + CLogger.retrieveErrorAsString());
			
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
	 * Permite anular un inventario indicando su ID
	 * @param data parametros de acceso
	 * @param inventoryID ID del inventario a anular
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	public ResultBean inventoryVoidByID(ParameterBean data, int inventoryID) {
		return inventoryVoid(data, inventoryID, null, null);
	}

	/**
	 * Permite anular un inventario especificando el mismo mediante una columna y su valor
	 * @param data parametros de acceso
	 * @param columnName columna a filtrar para recuperar el inventario en cuestión
	 * @param value valor a filtrar para recuperar el inventario en cuestión
	 * @return ResultBean con OK, ERROR, etc. 
	 */
	public ResultBean inventoryVoidByColumn(ParameterBean data, String columnName, String value) {
		return inventoryVoid(data, -1, columnName, value);
	}
	
	/**
	 * Anulacion de inventario 
	 */
	protected ResultBean inventoryVoid(ParameterBean data, int inventoryID, String columnName, String value) {
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"inventoryID", "columnName", "value"}, new Object[]{inventoryID, columnName, value});

			// Recuperar y anular el inventario
			MInventory anInventory = (MInventory)getPO("M_Inventory", inventoryID, columnName, value, true, false, true, true);
			if (!DocumentEngine.processAndSave(anInventory, DocAction.ACTION_Void, false))
				throw new ModelException("Error al anular el inventario:" + Msg.parseTranslation(getCtx(), anInventory.getProcessMsg()));
			
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
