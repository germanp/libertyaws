package org.libertya.ws.handler;

import java.math.BigDecimal;
import java.util.HashMap;

import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.MProductPrice;
import org.openXpertya.util.CLogger;

public class ProductPriceCRUDHandler extends GeneralHandler {
	
	/**
	 *  Crea o actualiza el precio de un artículo.
	 * @param data parametros del precio a crear o actualizar
	 * @return ResultBean con OK o ERROR en caso de error.
	 */
	public ResultBean productPriceCreateUpdate(ParameterBean data) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{}, new Object[]{});
			
			/* === Procesar (logica especifica) === */
			HashMap<String, String> values = toLowerCaseKeys(data.getMainTable());
			// Recuperar los valores de ID de articulo y version de lista de precio
			int productID = -1;
			int priceListVersionID = -1;
			try {
				productID = Integer.parseInt(values.get("m_product_id"));
			} catch (Exception e) {
				throw new ModelException(" Error al recuperar el m_product_id:" + e.getMessage());
			}
			try {
				priceListVersionID = Integer.parseInt(values.get("m_pricelist_version_id"));
			} catch (Exception e) {
				throw new ModelException(" Error al recuperar el m_pricelist_version_id:" + e.getMessage());
			}
			// Recuperar precio standard. El mismo es obligatorio
			BigDecimal priceStd = null;
			try {
				priceStd = new BigDecimal(values.get("pricestd"));
			} catch (Exception e) {
				throw new ModelException(" Error al recuperar el PriceStd:" + e.getMessage());
			}			
			
			// Recuperar productPrice actual a partir de priceListVersionID y productID. Si no existe, crearlo
			boolean newRecord = false;
			MProductPrice aProductPrice = MProductPrice.get(getCtx(), priceListVersionID, productID, getTrxName());
			if (aProductPrice == null) {
				newRecord = true;
				aProductPrice = new MProductPrice(getCtx(), priceListVersionID, productID, getTrxName());
				// Por defecto, el valor limite es cero
				aProductPrice.setPriceLimit(BigDecimal.ZERO);
			}
			
			// Definir inicialmente el valor de priceList a partir de priceStd
			aProductPrice.setPriceList(priceStd);
			// Setear restantes valores (redefiniendo priceList si está en la map) y persistir
			setValues(aProductPrice, values, newRecord);
			if (!aProductPrice.save()) {
				throw new ModelException("Error al persistir el productPrice:" + CLogger.retrieveErrorAsString());
			}

			/* === Commitear transaccion === */ 
			commitTransaction();
			
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
	 * Borrado logico del precio de un artículo
	 * @param data parametros de acceso
	 * @param productID id del producto
	 * @param priceListVersionID id de la lista de precio
	 * @return ResultBean OK o ERROR en caso de error.
	 */
	public ResultBean productPriceDelete(ParameterBean data, int productID, int priceListVersionID) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"productID", "priceListVersionID"}, new Object[]{productID, priceListVersionID});
			
			/* === Procesar (logica especifica) === */
			MProductPrice aProductPrice = MProductPrice.get(getCtx(), priceListVersionID, productID, getTrxName());
			if (aProductPrice == null) {
				throw new ModelException("No se ha podido recuperar un ProductPrice con el articulo y version de lista de precio indicado");
			}
			aProductPrice.setIsActive(false);
			if (!aProductPrice.save()) {
				throw new ModelException("Error al desactivar el productPrice:" + CLogger.retrieveErrorAsString());
			}
			
			/* === Commitear transaccion === */ 
			commitTransaction();
			
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

	public ResultBean productPriceRetrieve(ParameterBean data, int productID, int priceListVersionID) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"productID", "priceListVersionID"}, new Object[]{productID, priceListVersionID});
			
			/* === Procesar (logica especifica) === */
			MProductPrice aProductPrice = MProductPrice.get(getCtx(), priceListVersionID, productID, getTrxName());
			if (aProductPrice == null) {
				throw new ModelException("No se ha podido recuperar un ProductPrice con el articulo y version de lista de precio indicado");
			}
			
			/* === Retornar valor === */
			HashMap<String, String> result = poToMap(aProductPrice, true);
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
