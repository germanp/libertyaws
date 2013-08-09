package org.libertya.ws.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.StorageResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.util.DB;

public class StorageQueryHandler extends GeneralHandler {

	/**
	 * Consulta general de stock.  Devolverá el totalizado de stock POR ARTÍCULO 
	 * @param data parametros de login.  Si orgID = 0 entonces no filtrará por organización la consulta
	 * @param warehouseList nomina de warehouseIDs a usar como filtro (o null en caso de no querer filtrar por este criterio).
	 * @param locatorID id de ubicación a usar como filtro (o 0 en caso de no querer filtrar por este criterio)
	 * @param productValue clave de búsqueda del artículo (o null en caso de no querer filtrar por este criterio)
	 * @param productID id de artículo (o 0 en caso de no querer filtrar por este criterio)
	 * @param lot lote del conjunto de atributos del artículo (o null en caso de no querer filtrar por este criterio)
	 * @param serno nro de serie del conjunto de atributos del artículo (o null en caso de no querer filtrar por este criterio)
	 * @return 
	 * 		El listado de stock totalizado por cada artículo, con datos del artículo y sus cantidades:
	 *			QtyOnHand: Cantidad en stock
	 *			QtyReserved: Cantidad reservada
	 *			QtyOrdered: Cantidad pendiente de recepción
	 *			QtyAvailable: Cantidad disponible (QtyOnHand - QtyReserved)
	 */
	public StorageResultBean storageQuery(ParameterBean data, int[] warehouseList, int locatorID, String productValue, int productID, String lot, String serno) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"warehouseList", "locatorID", "productValue", "productID", "lot", "serno"}, new Object[]{warehouseList, locatorID, productValue, productID, lot, serno});
			
			/* === Procesar (logica especifica) === */	
			// Consulta principal
			StringBuffer query = new StringBuffer("");
			query.append(" SELECT 	p.m_product_id, p.value, p.name, asi.lot, asi.serno, ") 
				 .append(" 			sum(coalesce(s.qtyonhand,0)) as qtyonhand, ")
				 .append(" 			sum(coalesce(s.qtyreserved,0)) as qtyreserved, ") 
				 .append(" 			sum(coalesce(s.qtyordered,0)) as qtyordered, ")
				 .append(" 			sum(coalesce(s.qtyonhand,0) - coalesce(s.qtyordered,0)) as qtyavailable ")
				 .append(" FROM m_storage s ")
				 .append(" INNER JOIN m_locator l ON l.m_locator_id = s.m_locator_id ")
				 .append(" INNER JOIN m_product p ON p.m_product_id = s.m_product_id ")
				 .append(" INNER JOIN c_uom uom ON uom.c_uom_id = p.c_uom_id ")
				 .append(" INNER JOIN m_warehouse w ON w.m_warehouse_id = l.m_warehouse_id ")
				 .append(" LEFT JOIN m_attributesetinstance asi ON s.m_attributesetinstance_id = asi.m_attributesetinstance_id ");
			
			// Filtro por compañía
			query.append(" WHERE s.ad_client_id = ").append(data.getClientID());
			// Filtro por organización, si la misma != 0
			if (data.getOrgID() != 0) {
				query.append(" AND s.ad_org_id = ").append(data.getOrgID());
			}
			// Filtro por almacenes
			if (warehouseList != null && warehouseList.length > 0) {
				query.append(" AND w.m_warehouse_id IN (");
				for (int i=0; i<warehouseList.length; i++)
					query.append(warehouseList[i]).append((i+1==warehouseList.length?"":","));
				query.append(" ) ");
			}
			// Filtro por locator
			if (locatorID > 0) {
				query.append(" AND s.m_locator_id = ").append(locatorID);
			}
			// Filtro por value del articulo 
			if (productValue != null && productValue.length() > 0) {
				query.append(" AND p.value = '").append(productValue).append("'");
			}
			// Filtro por productID del articulo
			if (productID > 0) {
				query.append(" AND p.m_product_id = ").append(productID);
			}
			// Filtro por lote del articulo (conjunto de atributos)
			if (lot != null && lot.length() > 0) {
				query.append(" AND asi.lot = '").append(lot).append("'");
			}
			// Filtro por nro de serie del articulo (conjunto de atributos)
			if (serno != null && serno .length() > 0) {
				query.append(" AND asi.serno = '").append(serno).append("'");
			}
			// Agrupacion por artículo
			query.append(" GROUP BY p.m_product_id, p.value, p.name, asi.lot, asi.serno ");						
			
			/* === Retornar valor === */
			StorageResultBean result = new StorageResultBean(false, null, new HashMap<String, String>());
			PreparedStatement stmt = DB.prepareStatement(query.toString(), getTrxName());
			ResultSet rs = stmt.executeQuery();
			// Cargar la lista de map a partir del result set
			fillResult(rs, result, data, warehouseList, locatorID);
			return result;
		}
		catch (ModelException me) {
			return (StorageResultBean)processException(me, new StorageResultBean(), wsInvocationArguments(data));
		}
		catch (Exception e) {
			return (StorageResultBean)processException(e, new StorageResultBean(), wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	
	}
	
	/**
	 * Carga la lista de resultados a partir del ResultSet recibido
	 */
	protected void fillResult(ResultSet rs, StorageResultBean result, ParameterBean data, int[] warehouseList, int locatorID) throws Exception {
		// Iterar cada tupla
		while (rs.next()) {
			// Nuevo item en la lista de reusltados
			HashMap<String, String> map = new HashMap<String, String>();
			
			/* Cargar la map */
			// Datos segun el resultset
			map.put("M_Product_ID", 	Integer.toString(rs.getInt("m_product_id")));
			map.put("Value", 			rs.getString("value"));
			map.put("Name", 			rs.getString("name"));
			map.put("Lot", 				rs.getString("lot"));
			map.put("SerNo", 			rs.getString("serno"));
			map.put("QtyOnHand",  		rs.getBigDecimal("qtyonhand").toString());
			map.put("QtyReserved",  	rs.getBigDecimal("qtyreserved").toString());
			map.put("QtyOrdered",  		rs.getBigDecimal("qtyordered").toString());
			map.put("QtyAvailable",  	rs.getBigDecimal("qtyavailable").toString());
			// Datos segun los parametros
			map.put("AD_Client_ID", 	Integer.toString(data.getClientID()));
			map.put("AD_Org_ID", 		Integer.toString(data.getOrgID()));
			map.put("M_Warehouse_ID", 	getWarehouseReturnValue(warehouseList));
			map.put("M_Locator_ID", 	getLocatorReturnValue(locatorID));
			
			// Incorporar la map cargada a la lista de resultados
			result.getStockList().add(map);
		}
	}

	/**
	 * Retorna el valor de resultado para el dato warehouse en función del parametro cargado en la consulta
	 */
	protected String getWarehouseReturnValue(int[] warehoueList) {
		if (warehoueList == null)
			return null;
		if (warehoueList.length == 1)
			return Integer.toString(warehoueList[0]);
		else
			return "0";
	}
	
	/**
	 * Retorna el valor de resultado para el dato locator en función del parametro cargado en la consulta
	 */
	protected String getLocatorReturnValue(int locatorID) {
		if (locatorID <= 0)
			return null;
		return Integer.toString(locatorID);
	}
}
