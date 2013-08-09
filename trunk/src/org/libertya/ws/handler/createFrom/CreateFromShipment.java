package org.libertya.ws.handler.createFrom;

import java.math.BigDecimal;
import java.util.Properties;

import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MLocator;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.X_M_Locator;
import org.openXpertya.model.X_M_Warehouse;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

/**
 * Creacion de facturas a partir de pedido
 * Funcionalidad basada en org.openXpertya.grid.VCreateFromShipment
 */

public class CreateFromShipment {

	static Integer locatorID = null; 
	
	public static void copyHeaderValuesFromOrder(MInOut inOut, MOrder order, Properties ctx, String trxName) throws ModelException, Exception {
				
		// Recuperar el locator por defecto a partir del warehouse del pedido
		locatorID = DB.getSQLValue(trxName, " SELECT M_Locator_ID FROM M_Locator WHERE isdefault = 'Y' AND M_Warehouse_ID = ?", order.getM_Warehouse_ID());
		// La ubicación es obligatoria
		if (locatorID == null || (locatorID <= 0)) {
			X_M_Warehouse warehouse = new X_M_Warehouse(ctx, order.getM_Warehouse_ID(), trxName);
			throw new ModelException("Debe configurar un depósito por defecto para el almacén especificado en el pedido (" + warehouse.getName() + ").");
		}

		// Asocia el pedido
		if (order != null) {
			inOut.setC_Order_ID(order.getC_Order_ID());
			inOut.setDateOrdered(order.getDateOrdered());
			inOut.setC_Project_ID(order.getC_Project_ID());
		}

		// Guarda el encabezado. Si hay error cancela la operación
		if (!inOut.save()) {
			throw new ModelException(CLogger.retrieveErrorAsString());
		}
	
	}
	
	public static void copyLineValuesFromOrderLine(MInOut inOut, MOrder order, MInOutLine inOutLine, MOrderLine orderLine, Properties ctx, String trxName) throws ModelException, Exception
	{
		// Lines
		Integer productLocatorID = null;
		MLocator productLocator = null;
		BigDecimal movementQty = orderLine.getQtyEntered();
		int C_UOM_ID = orderLine.getC_UOM_ID();
		int M_Product_ID = orderLine.getM_Product_ID();
		
		// Determinar la ubicación relacionada al artículo y verificar que
		// se encuentre dentro del almacén del remito. Si se encuentra en
		// este almacén, entonces setearle la ubicación del artículo, sino
		// la ubicación por defecto. Sólo para movimientos de ventas.
		productLocatorID = null;
		if(order.isSOTrx()){
			// Obtengo el id de la ubicación del artículo
			productLocatorID = MProduct.getLocatorID(M_Product_ID, trxName);
			// Si posee una configurada, verifico que sea del mismo almacén,
			// sino seteo a null el id de la ubicación para que setee el que
			// viene por defecto
			if(!Util.isEmpty(productLocatorID, true)){
				productLocator = new MLocator(ctx, productLocatorID, trxName);  // MLocator.get(ctx, productLocatorID);
				productLocatorID = (productLocator.getM_Warehouse_ID() != inOut.getM_Warehouse_ID() ? null : productLocatorID);
			}
		}
		
		// Actualiza la línea del remito
		inOutLine.setM_Product_ID(M_Product_ID, C_UOM_ID); // Line UOM
		inOutLine.setQty(movementQty); // Movement/Entered
		inOutLine.setM_Locator_ID(Util.isEmpty(productLocatorID, true) ? locatorID : productLocatorID); // Locator
		inOutLine.setDescription(orderLine.getDescription());

		// La línea del remito se crea a partir de una línea de pedido
		// Asocia línea remito -> línea pedido
		inOutLine.setC_OrderLine_ID(orderLine.getC_OrderLine_ID());
		// Proyecto
		inOutLine.setC_Project_ID(orderLine.getC_Project_ID());
		if (orderLine.getQtyEntered().compareTo(orderLine.getQtyOrdered()) != 0) {
			inOutLine.setMovementQty(movementQty.multiply(orderLine.getQtyOrdered()).divide(orderLine.getQtyEntered(),BigDecimal.ROUND_HALF_UP));
			inOutLine.setC_UOM_ID(orderLine.getC_UOM_ID());
		}
		// Instancia de atributo
		if (orderLine.getM_AttributeSetInstance_ID() != 0) {
			inOutLine.setM_AttributeSetInstance_ID(orderLine.getM_AttributeSetInstance_ID());
		}
		// Cargo (si no existe el artículo)
		if (M_Product_ID == 0 && orderLine.getC_Charge_ID() != 0) {
			inOutLine.setC_Charge_ID(orderLine.getC_Charge_ID());
		}
		
			// Este metodo es redefinido por un plugin
//				customMethod(ol,iol);

		// Guarda la línea de remito
		if (!inOutLine.save()) {
			throw new ModelException("@InOutLineSaveError@ (# "
					+ orderLine.getLine() + "):<br>"
					+ CLogger.retrieveErrorAsString());

			// Create Invoice Line Link
		} 
	}
}
