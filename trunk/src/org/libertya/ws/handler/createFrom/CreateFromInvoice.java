package org.libertya.ws.handler.createFrom;

/**
 * Creacion de facturas a partir de pedido
 * Funcionalidad basada en org.openXpertya.grid.VCreateFromInvoice
 */

import java.math.BigDecimal;
import java.util.Properties;

import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.util.CLogger;

public class CreateFromInvoice {

	public static void copyHeaderValuesFromOrder(MInvoice invoice, MOrder order, Properties ctx, String trxName) throws ModelException, Exception
	{
		// Obtener el docType
//		MDocType docType = new MDocType(ctx, invoice.getC_DocTypeTarget_ID(), trxName);
		
    	// Actualiza el encabezado de la factura
//		invoice.setDragDocumentDiscountAmts(docType.isDragOrderDocumentDiscounts());
		
        // Asociación con el pedido
        if( order != null ) {
            invoice.setOrder( order, true );    // overwrite header values
//			invoice.setManageDragOrderDiscounts(docType
//					.isDragOrderDocumentDiscounts()
//					|| docType.isDragOrderLineDiscounts());
//			invoice.setIsExchange(order.isExchange());
            if (!invoice.save()) {
            	throw new ModelException(CLogger.retrieveErrorAsString());
            }
        }
	}
	
	public static void copyLineValuesFromOrderLine(MInvoice invoice, MOrder order, MInvoiceLine invoiceLine, MOrderLine orderLine, Properties ctx, String trxName) throws ModelException, Exception
	{
		// Obtener el docType
//		MDocType docType = new MDocType(ctx, invoice.getC_DocTypeTarget_ID(), trxName);
		
    	// variable values
        BigDecimal  QtyEntered = orderLine.getQtyEntered();
        int C_UOM_ID = orderLine.getC_UOM_ID();
        int M_Product_ID = orderLine.getM_Product_ID();
        
        invoiceLine.setC_OrderLine_ID(orderLine.getC_OrderLine_ID());
        invoiceLine.setM_Product_ID(M_Product_ID, C_UOM_ID);    // Line UOM
        invoiceLine.setQty(QtyEntered);    // Invoiced/Entered
        invoiceLine.setDescription(orderLine.getDescription());
//		invoiceLine.setDragDocumentDiscountAmts(docType
//				.isDragOrderDocumentDiscounts());
//		invoiceLine.setDragLineDiscountAmts(docType
//				.isDragOrderLineDiscounts());
//		invoiceLine.setDragOrderPrice(docType.isDragOrderPrice());

        // Order Info
        invoiceLine.setOrderLine( orderLine );    // overwrites
        
        if( orderLine.getQtyEntered().compareTo( orderLine.getQtyOrdered()) != 0 ) {
            invoiceLine.setQtyInvoiced( QtyEntered.multiply( orderLine.getQtyOrdered()).divide( orderLine.getQtyEntered(),BigDecimal.ROUND_HALF_UP ));
        }

        if( !invoiceLine.save()) {
            throw new ModelException(
         		   "@InvoiceLineSaveError@ (# " + orderLine.getLine() + "):" + 
         		   CLogger.retrieveErrorAsString()
         	);
        }

    
		// Actualización de la cabecera por totales de descuentos e impuestos
		// siempre y cuando el tipo de documento lo permita
//	    if(docType.isDragOrderDocumentDiscounts() && order != null){
//			try{
//				invoice.updateTotalDocumentDiscount();	
//			} catch(Exception e){
//				throw new ModelException(e.getMessage());
//			}
//	    }
	}
}
