package org.libertya.ws.handler;

import java.math.BigDecimal;
import java.util.HashMap;

import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.MPriceList;
import org.openXpertya.model.MPriceListVersion;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MProductPrice;
import org.openXpertya.util.CLogger;

public class ProductCRUDHandler extends GeneralHandler {

	
	/** Sobrecarga de metodo para compatibilidad */
	public ResultBean productCreate(ParameterBean data) 	{
		return productCreate(data, false);
	}
	
	/**
	 * Alta de un artículo
	 * """""""""""""""""""
	 * @param data parametros correspondientes
	 * @param createDefaultProductPrice si se quiere generan los productPrices iniciales para el articulo creado 
	 * @return ResultBean con OK, ERROR, etc.
	 */
	public ResultBean productCreate(ParameterBean data, boolean createDefaultProductPrice)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{}, new Object[]{});
			
			/* === Procesar (logica especifica) === */			
			// Instanciar y persistir articulo
			MProduct newProduct = new MProduct(getCtx(), 0, getTrxName());
			setValues(newProduct, data.getMainTable(), true);
			if (!newProduct.save())
				throw new ModelException("Error al persistir el articulo:" + CLogger.retrieveErrorAsString());

			if (createDefaultProductPrice) {
				// Recuperar tarifas predeterminadas de compra y venta 
				MPriceList salesPriceList = MPriceList.getDefault(getCtx(), true);
				if (salesPriceList==null || salesPriceList.getM_PriceList_ID()==0)
					throw new ModelException("No se puede configurar el precio por defecto del artículo.  No existe tarifa de ventas predeterminada");
				MPriceList purchasePriceList = MPriceList.getDefault(getCtx(), false);
				if (purchasePriceList==null || purchasePriceList.getM_PriceList_ID()==0)
					throw new ModelException("No se puede configurar el precio por defecto del artículo.  No existe tarifa de compras predeterminada");
	
				// Recuperar versiones de lista de precio para cada tarifa 
				MPriceListVersion salesPriceListVersion = salesPriceList.getPriceListVersion(null);
				if (salesPriceListVersion==null || salesPriceListVersion.getM_PriceList_Version_ID()==0)
					throw new ModelException("No se puede configurar el precio por defecto del artículo.  No existe una versión lista de precio de ventas para la tarifa predeterminada");
				MPriceListVersion purchasePriceListVersion = purchasePriceList.getPriceListVersion(null);
				if (purchasePriceListVersion==null || purchasePriceListVersion.getM_PriceList_Version_ID()==0)
					throw new ModelException("No se puede configurar el precio por defecto del artículo.  No existe una versión lista de precio de compras para la tarifa predeterminada");
				
				// Crear las entradas para los precios de compra del articulo 
				MProductPrice purchaseProductPrice = new MProductPrice(getCtx(), 0, getTrxName());
				purchaseProductPrice.setM_Product_ID(newProduct.getM_Product_ID());
				purchaseProductPrice.setM_PriceList_Version_ID(purchasePriceListVersion.getM_PriceList_Version_ID());
				purchaseProductPrice.setPrices(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
				if (!purchaseProductPrice.save())
					throw new ModelException("Error al persistir el precio de compra por defecto:" + CLogger.retrieveErrorAsString());
				
				// Crear las entradas para los precios de venta del articulo
				MProductPrice salesProductPrice = new MProductPrice(getCtx(), 0, getTrxName());
				salesProductPrice.setM_Product_ID(newProduct.getM_Product_ID());
				salesProductPrice.setM_PriceList_Version_ID(salesPriceListVersion.getM_PriceList_Version_ID());
				salesProductPrice.setPrices(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
				if (!salesProductPrice.save())
					throw new ModelException("Error al persistir el precio de venta por defecto:" + CLogger.retrieveErrorAsString());
			}
			
			/* === Commitear transaccion === */ 
			commitTransaction();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("M_Product_ID", Integer.toString(newProduct.getM_Product_ID()));
			result.put("Value", newProduct.getValue());
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
	 * Recupera un articulo a partir de su ID
	 */
	public ResultBean productRetrieveByID(ParameterBean data, int productID) {
		return productRetrieve(data, productID, null);
	}
	
	/**
	 * Recupera un articulo a partir de su value
	 */
	public ResultBean productRetrieveByValue(ParameterBean data, String value) {
		return productRetrieve(data, -1, value);
	}
	
	
	/**
	 * Recupera de un articulo
	 * """""""""""""""""""""""
	 * @param data parametros correspondientes
	 * @param productID si se desea recuperar por este criterio debe ser valor mayor a cero (o -1 en CC)
	 * @param value si se desea recuperar por este criterio debse ser distinto de null (o null en cc)
	 * @return ResultBean con los datos correspondientes
	 */
	protected ResultBean productRetrieve(ParameterBean data, int productID, String value)
	{
		try
		{	
			/* === Configuracion inicial === */
			init(data, new String[]{"productID", "value"}, new Object[]{productID, value});
			
			/* === Procesar (logica especifica) === */
			// Recuperar el articulo (si existe) por algún criterio. 
			// 1) Buscar por ID o por value (obtener null si no se encuentra)
			MProduct aProduct = (MProduct)getPO("M_Product", productID, "value", value, false, true, true, false);
			if (aProduct == null || aProduct.getM_Product_ID()==0)
				throw new ModelException("No se ha podido recuperar un articulo a partir de los parametros indicados");

			/* === Retornar valores === */
			ResultBean result = new ResultBean(false, null, poToMap(aProduct, true));
			return result;
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
	 * Actualiza un articulo a partir de su ID
	 */
	public ResultBean productUpdateByID(ParameterBean data, int productID) {
		return productUpdate(data, productID, null);
	}
	
	/**
	 * Actualiza un articulo a partir de su value
	 */
	public ResultBean productUpdateByValue(ParameterBean data, String value) {
		return productUpdate(data, -1, value);
	}

	
	/**
	 * Actualización de un articulo
	 * @param data parametros correspondientes
	 * @param productID identificador del articulo a modificar
	 * @param value identificador del articulo
	 * @return ResultBean con OK, ERROR, etc.
	 */
	protected ResultBean productUpdate(ParameterBean data, int productID, String value)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"productID", "value"}, new Object[]{productID, value});

			/* === Procesar (logica especifica) === */			
			// Recuperar y persistir el articulo
			MProduct aProduct = (MProduct)getPO("M_Product", productID, "value", value, false, true, true, false);
			if (aProduct==null || aProduct.getM_Product_ID()==0 )
				throw new ModelException("No se ha podido recuperar un articulo a partir de los parametros indicados");
			setValues(aProduct, data.getMainTable(), false);
			if (!aProduct.save())
				throw new ModelException("Error al actualizar el articulo:" + CLogger.retrieveErrorAsString());

			/* === Commitear transaccion === */ 
			commitTransaction();
			
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
	 * Eliminación logica de un articulo
	 * @param data parametros correspondientes
	 * @param productID identificador del articulo a eliminar
	 * @return ResultBean con OK, ERROR, etc.
	 */
	public ResultBean productDelete(ParameterBean data, int productID)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"productID"}, new Object[]{productID});

			/* === Procesar (logica especifica) === */			
			// Recuperar y persistir el articulo
			MProduct aProduct = new MProduct(getCtx(), productID, getTrxName());
			if (aProduct==null || aProduct.getM_Product_ID()==0 )
				throw new ModelException("No se ha podido recuperar un articulo a partir de los parametros indicados");
			aProduct.setIsActive(false);
			if (!aProduct.save())
				throw new ModelException("Error al eliminar el articulo:" + CLogger.retrieveErrorAsString());

			/* === Commitear transaccion === */ 
			commitTransaction();
			
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
