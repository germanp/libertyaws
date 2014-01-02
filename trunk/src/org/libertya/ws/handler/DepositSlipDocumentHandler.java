package org.libertya.ws.handler;

import java.util.HashMap;

import org.libertya.ws.bean.parameter.DocumentParameterBean;
import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.MBoletaDeposito;
import org.openXpertya.model.MBoletaDepositoLine;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;

public class DepositSlipDocumentHandler extends GeneralHandler {

	/**
	 * Crea una boleta de depósito
	 */
	public ResultBean depositSlipCreate(DocumentParameterBean data, boolean completeDepositSlip) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"completeDepositSlip"}, new Object[]{completeDepositSlip});
			
			/* === Procesar (logica especifica) === */
			// Crear cabecera y setear valores
			MBoletaDeposito aBoletaDeposito = new MBoletaDeposito(getCtx(), 0, getTrxName());
			setValues(aBoletaDeposito, data.getMainTable(), true);
			
			// A diferencia del resto de tipos de documentos, estos valores no se setean automaticamente, e impiden persistir
			if (aBoletaDeposito.getDocStatus() == null || aBoletaDeposito.getDocStatus().length() == 0)
				aBoletaDeposito.setDocStatus(DocAction.STATUS_Drafted);
			if (aBoletaDeposito.getDocAction() == null || aBoletaDeposito.getDocAction().length() == 0)
				aBoletaDeposito.setDocAction(DocAction.ACTION_Complete);

			// Persistir cabecera
			if (!aBoletaDeposito.save())
				throw new ModelException("Error al persistir la boleta de deposito:" + CLogger.retrieveErrorAsString());
			
			// Instanciar y persistir las Lineas
			for (HashMap<String, String> line : data.getDocumentLines())
			{
				MBoletaDepositoLine aBoletaDepositoLine = new MBoletaDepositoLine(getCtx(), 0, getTrxName());
				aBoletaDepositoLine.setM_BoletaDeposito_ID(aBoletaDeposito.getM_BoletaDeposito_ID());
				setValues(aBoletaDepositoLine, line, true);
				if (!aBoletaDepositoLine.save())
					throw new ModelException("Error al persistir linea de la boleta de deposito:" + CLogger.retrieveErrorAsString());
			}
			
			// Completar la boleta si corresponde
			if (completeDepositSlip && !DocumentEngine.processAndSave(aBoletaDeposito, DocAction.ACTION_Complete, false))
				throw new ModelException("Error al completar la boleta de deposito:" + Msg.parseTranslation(getCtx(), aBoletaDeposito.getProcessMsg()));

			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("M_BoletaDeposito_ID", Integer.toString(aBoletaDeposito.getM_BoletaDeposito_ID()));
			result.put("BoletaDeposito_DocumentNo", aBoletaDeposito.getDocumentNo());
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
	 * Elimina una boleta de depósito en borrador
	 */
	public ResultBean depositSlipDelete(ParameterBean data, int depositSlipID) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"depositSlipID"}, new Object[]{depositSlipID});
			
			/* === Procesar (logica especifica) === */
			// Recuperar la boleta de deposito
			MBoletaDeposito aBoletaDeposito = (MBoletaDeposito)getPO("M_BoletaDeposito", depositSlipID, null, null, false, true, true, true);
			if (!aBoletaDeposito.delete(false))
				throw new ModelException("Error al eliminar la boleta de deposito:" + CLogger.retrieveErrorAsString());

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
	 * Completa una boleta de depósito
	 */
	public ResultBean depositSlipComplete(ParameterBean data, int depositSlipID) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"depositSlipID"}, new Object[]{depositSlipID});
			
			/* === Procesar (logica especifica) === */
			// Recuperar la boleta de deposito
			MBoletaDeposito aBoletaDeposito = (MBoletaDeposito)getPO("M_BoletaDeposito", depositSlipID, null, null, false, true, true, true);
			if (!DocumentEngine.processAndSave(aBoletaDeposito, DocAction.ACTION_Complete, false))
				throw new ModelException("Error al completar la boleta de deposito:" + Msg.parseTranslation(getCtx(), aBoletaDeposito.getProcessMsg()));

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
	 * Anula una boleta de depósito
	 */
	public ResultBean depositSlipVoid(ParameterBean data, int depositSlipID) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"depositSlipID"}, new Object[]{depositSlipID});
			
			/* === Procesar (logica especifica) === */
			// Recuperar la boleta de deposito
			MBoletaDeposito aBoletaDeposito = (MBoletaDeposito)getPO("M_BoletaDeposito", depositSlipID, null, null, false, true, true, true);
			if (!DocumentEngine.processAndSave(aBoletaDeposito, DocAction.ACTION_Void, false))
				throw new ModelException("Error al anular la boleta de deposito:" + Msg.parseTranslation(getCtx(), aBoletaDeposito.getProcessMsg()));

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
