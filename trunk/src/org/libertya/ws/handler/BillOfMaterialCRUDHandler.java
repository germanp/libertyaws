package org.libertya.ws.handler;

import java.util.HashMap;

import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.MProductBOM;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Trx;

/**
 * 		Un M_Product_BOM está compuesto por:
 * 			- m_product_id (artículo padre)
 * 			- m_productbom_id (artículo perteneciente al bom)
 */

public class BillOfMaterialCRUDHandler extends GeneralHandler {

	/**
	 * Adiciona una entrada a la configuración LDM para un artículo dado
	 */
	public ResultBean billOfMaterialCreate(ParameterBean data) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{}, new Object[]{});
			
			/* === Procesar (logica especifica) === */
			// Persistir el nuevo LDM
			MProductBOM aProductBOM = new MProductBOM(getCtx(), 0, getTrxName());
			setValues(aProductBOM, data.getMainTable(), true);
			if (!aProductBOM.save())
				throw new ModelException("Error al persistir el LDM:" + CLogger.retrieveErrorAsString());
			
			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("M_Product_BOM_ID", Integer.toString(aProductBOM.getM_Product_BOM_ID()));
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
	 * Elimina una entrada en la configuración LDM
	 */
	public ResultBean billOfMaterialDelete(ParameterBean data, int productBOMId) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{}, new Object[]{});
			
			/* === Procesar (logica especifica) === */
			// Persistir el nuevo LDM
			MProductBOM aProductBOM = (MProductBOM)getPO("M_Product_BOM", productBOMId, null, null, false, true, true, true);
			setValues(aProductBOM, data.getMainTable(), true);
			if (!aProductBOM.delete(false))
				throw new ModelException("Error al eliminar el LDM:" + CLogger.retrieveErrorAsString());
			
			/* === Commitear transaccion === */
			Trx.getTrx(getTrxName()).commit();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("M_Product_BOM_ID", Integer.toString(aProductBOM.getM_Product_BOM_ID()));
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
