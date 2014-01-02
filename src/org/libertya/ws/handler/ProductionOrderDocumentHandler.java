package org.libertya.ws.handler;

import java.util.HashMap;

import org.libertya.ws.bean.parameter.DocumentParameterBean;
import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.MProductionOrder;
import org.openXpertya.model.MProductionOrderline;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;

public class ProductionOrderDocumentHandler extends GeneralHandler {

	/**
	 * Crea una nueva orden de producción
	 */
	public ResultBean productionOrderCreate(DocumentParameterBean data, boolean completeProductionOrder) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"completeProductionOrder"}, new Object[]{completeProductionOrder});
			
			/* === Procesar (logica especifica) === */
			// Persistir cabecera
			MProductionOrder aProductionOrder = new MProductionOrder(getCtx(), 0, getTrxName());
			setValues(aProductionOrder, data.getMainTable(), true);
			if (!aProductionOrder.save())
				throw new ModelException("Error al persistir la orden:" + CLogger.retrieveErrorAsString());
			
			// Instanciar y persistir las Lineas
			for (HashMap<String, String> line : data.getDocumentLines())
			{
				MProductionOrderline aProductionOrderLine = new MProductionOrderline(aProductionOrder);
				setValues(aProductionOrderLine, line, true);
				if (!aProductionOrderLine.save())
					throw new ModelException("Error al persistir linea de la orden:" + CLogger.retrieveErrorAsString());
			}
			// Completar la orden si corresponde
			if (completeProductionOrder && !DocumentEngine.processAndSave(aProductionOrder, DocAction.ACTION_Complete, false))
				throw new ModelException("Error al completar la orden:" + Msg.parseTranslation(getCtx(), aProductionOrder.getProcessMsg()));

			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("C_Production_Order_ID", Integer.toString(aProductionOrder.getC_Production_Order_ID()));
			result.put("ProductionOrder_DocumentNo", aProductionOrder.getDocumentNo());
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
	 * Elimina una orden de producción en borrador
	 */
	public ResultBean productionOrderDelete(ParameterBean data, int productionOrderID) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"productionOrderID"}, new Object[]{productionOrderID});
			
			/* === Procesar (logica especifica) === */
			// Recuperar la orden de produccion
			MProductionOrder aProductionOrder = (MProductionOrder)getPO("C_Production_Order", productionOrderID, null, null, false, true, true, true);
			if (!aProductionOrder.delete(false))
				throw new ModelException("Error al eliminar la orden:" + CLogger.retrieveErrorAsString());

			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
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
	 * Completa una orden de producción
	 */
	public ResultBean productionOrderComplete(ParameterBean data, int productionOrderID) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"productionOrderID"}, new Object[]{productionOrderID});
			
			/* === Procesar (logica especifica) === */
			// Recuperar la orden de producción
			MProductionOrder aProductionOrder = (MProductionOrder)getPO("C_Production_Order", productionOrderID, null, null, false, true, true, true);
			if (!DocumentEngine.processAndSave(aProductionOrder, DocAction.ACTION_Complete, false))
				throw new ModelException("Error al completar la order:" + Msg.parseTranslation(getCtx(), aProductionOrder.getProcessMsg()));
			
			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
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
	 * Anula una orden de producción
	 */
	public ResultBean productionOrderVoid(ParameterBean data, int productionOrderID) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"productionOrderID"}, new Object[]{productionOrderID});
			
			/* === Procesar (logica especifica) === */
			// Recuperar la orden de producción
			MProductionOrder aProductionOrder = (MProductionOrder)getPO("C_Production_Order", productionOrderID, null, null, false, true, true, true);
			if (!DocumentEngine.processAndSave(aProductionOrder, DocAction.ACTION_Void, false))
				throw new ModelException("Error al anular la order:" + Msg.parseTranslation(getCtx(), aProductionOrder.getProcessMsg()));
			
			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
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
}
