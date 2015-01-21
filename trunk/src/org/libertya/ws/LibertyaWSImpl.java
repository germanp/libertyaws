package org.libertya.ws;

import org.libertya.ws.bean.parameter.AllocationParameterBean;
import org.libertya.ws.bean.parameter.BPartnerParameterBean;
import org.libertya.ws.bean.parameter.CustomServiceParameterBean;
import org.libertya.ws.bean.parameter.DocumentParameterBean;
import org.libertya.ws.bean.parameter.FilteredColumnsParameterBean;
import org.libertya.ws.bean.parameter.InvoiceParameterBean;
import org.libertya.ws.bean.parameter.OrderParameterBean;
import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.parameter.ReplicationParameterBean;
import org.libertya.ws.bean.result.BPartnerResultBean;
import org.libertya.ws.bean.result.CustomServiceResultBean;
import org.libertya.ws.bean.result.DocumentResultBean;
import org.libertya.ws.bean.result.InvoiceResultBean;
import org.libertya.ws.bean.result.MultipleDocumentsResultBean;
import org.libertya.ws.bean.result.MultipleRecordsResultBean;
import org.libertya.ws.bean.result.ReplicationResultBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.bean.result.StorageResultBean;
import org.libertya.ws.handler.AllocationDocumentHandler;
import org.libertya.ws.handler.BPartnerBalanceHandler;
import org.libertya.ws.handler.BPartnerCRUDHandler;
import org.libertya.ws.handler.BPartnerLocationCRUDHandler;
import org.libertya.ws.handler.BillOfMaterialCRUDHandler;
import org.libertya.ws.handler.CustomServiceHandler;
import org.libertya.ws.handler.DepositSlipDocumentHandler;
import org.libertya.ws.handler.DocumentQueryHandler;
import org.libertya.ws.handler.GeneralRecordQueryHandler;
import org.libertya.ws.handler.InOutDocumentHandler;
import org.libertya.ws.handler.InventoryDocumentHandler;
import org.libertya.ws.handler.InvoiceDocumentHandler;
import org.libertya.ws.handler.OrderDocumentHandler;
import org.libertya.ws.handler.ProcessExecuteHandler;
import org.libertya.ws.handler.ProductCRUDHandler;
import org.libertya.ws.handler.ProductPriceCRUDHandler;
import org.libertya.ws.handler.ProductionOrderDocumentHandler;
import org.libertya.ws.handler.ReplicationServerHandler;
import org.libertya.ws.handler.StorageQueryHandler;
import org.libertya.ws.handler.UserCRUDHandler;


public class LibertyaWSImpl implements LibertyaWS {

	/* ================================================================== */
	/* ==================== Entidades Comerciales ======================= */
	/* ================================================================== */
	
	public synchronized ResultBean bPartnerCreate(BPartnerParameterBean data) {
		return new BPartnerCRUDHandler().bPartnerCreate(data);
	}
	
	public synchronized ResultBean bPartnerDelete(ParameterBean data, int bPartnerID) {
		return new BPartnerCRUDHandler().bPartnerDelete(data, bPartnerID);
	}
	
	public synchronized BPartnerResultBean bPartnerRetrieveByID(ParameterBean data, int bPartnerID) {
		return new BPartnerCRUDHandler().bPartnerRetrieveByID(data, bPartnerID);
	}
	
	public synchronized BPartnerResultBean bPartnerRetrieveByValue(ParameterBean data, String value) {
		return new BPartnerCRUDHandler().bPartnerRetrieveByValue(data, value);
	}
	
	public synchronized BPartnerResultBean bPartnerRetrieveByTaxID(ParameterBean data, String taxID) {
		return new BPartnerCRUDHandler().bPartnerRetrieveByTaxID(data, taxID);
	}
	
	public synchronized ResultBean bPartnerUpdate(BPartnerParameterBean data, int bPartnerID, int bPartnerLocationID) {
		return new BPartnerCRUDHandler().bPartnerUpdate(data, bPartnerID, bPartnerLocationID);	
	}

	/* ===================================================== */
	/* ==================== Facturas ======================= */
	/* ===================================================== */
	
	public synchronized ResultBean invoiceCreateCustomer(InvoiceParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeDocument) {
		return new InvoiceDocumentHandler().invoiceCreateCustomer(data, bPartnerID, bPartnerValue, taxID, completeDocument);
	}
	
	public synchronized ResultBean invoiceCreateCustomerFromOrderByID(InvoiceParameterBean data, int orderID, boolean completeDocument) {
		return new InvoiceDocumentHandler().invoiceCreateCustomerFromOrderByID(data, orderID, completeDocument);		
	}
		
	public synchronized ResultBean invoiceCreateCustomerFromOrderByColumn(InvoiceParameterBean data, String searchColumn, String searchCriteria, boolean completeDocument) {
		return new InvoiceDocumentHandler().invoiceCreateCustomerFromOrderByColumn(data, searchColumn, searchCriteria, completeDocument);		
	}

	public synchronized ResultBean invoiceCreateVendor(InvoiceParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeDocument) {
		return new InvoiceDocumentHandler().invoiceCreateVendor(data, bPartnerID, bPartnerValue, taxID, completeDocument);
	}
	
	public synchronized ResultBean invoiceCreateVendorFromOrderByID(InvoiceParameterBean data, int orderID, boolean completeDocument) {
		return new InvoiceDocumentHandler().invoiceCreateVendorFromOrderByID(data, orderID, completeDocument);		
	}
		
	public synchronized ResultBean invoiceCreateVendorFromOrderByColumn(InvoiceParameterBean data, String searchColumn, String searchCriteria, boolean completeDocument) {
		return new InvoiceDocumentHandler().invoiceCreateVendorFromOrderByColumn(data, searchColumn, searchCriteria, completeDocument);		
	}
	
	public synchronized ResultBean invoiceDeleteByID(ParameterBean data, int invoiceID) {
		return new InvoiceDocumentHandler().invoiceDeleteByID(data, invoiceID);	
	}
	
	public synchronized ResultBean invoiceDeleteByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return new InvoiceDocumentHandler().invoiceDeleteByColumn(data, columnName, columnCriteria);	
	}
	
	public synchronized ResultBean invoiceCompleteByID(ParameterBean data, int invoiceID) {
		return new InvoiceDocumentHandler().invoiceCompleteByID(data, invoiceID);	
	}

	public synchronized ResultBean invoiceCompleteByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return new InvoiceDocumentHandler().invoiceCompleteByColumn(data, columnName, columnCriteria);	
	}
	
	public synchronized ResultBean invoiceVoidByID(ParameterBean data, int invoiceID) {
		return new InvoiceDocumentHandler().invoiceVoidByID(data, invoiceID);	
	}
	
	public synchronized ResultBean invoiceVoidByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return new InvoiceDocumentHandler().invoiceVoidByColumn(data, columnName, columnCriteria);
	}
		
	public synchronized ResultBean invoiceUpdateByID(ParameterBean data, int invoiceID) {
		return new InvoiceDocumentHandler().invoiceUpdateByID(data, invoiceID);
	}
	

	/* ===================================================== */
	/* ==================== Artículos ====================== */
	/* ===================================================== */
	
	public synchronized ResultBean productCreate(ParameterBean data) {
		return new ProductCRUDHandler().productCreate(data);
	}

	public synchronized ResultBean productCreate(ParameterBean data, boolean createDefaultProductPrice) {
		return new ProductCRUDHandler().productCreate(data, createDefaultProductPrice);
	}
	
	public synchronized ResultBean productRetrieveByID(ParameterBean data, int productID) {
		return new ProductCRUDHandler().productRetrieveByID(data, productID);
	}

	public synchronized ResultBean productRetrieveByValue(ParameterBean data, String value) {
		return new ProductCRUDHandler().productRetrieveByValue(data, value);
	}
	
	public synchronized ResultBean productUpdateByID(ParameterBean data, int productID) {
		return new ProductCRUDHandler().productUpdateByID(data, productID);
	}
	
	public synchronized ResultBean productUpdateByValue(ParameterBean data, String value) {
		return new ProductCRUDHandler().productUpdateByValue(data, value);	
	}
	
	public synchronized ResultBean productDelete(ParameterBean data, int productID) {
		return new ProductCRUDHandler().productDelete(data, productID);
	}

	/* ===================================================== */
	/* ============= Consulta de Comprobantes ============== */
	/* ===================================================== */
	
	public synchronized DocumentResultBean documentRetrieveOrderByID(ParameterBean data,	int orderID) {
		return new DocumentQueryHandler().documentRetrieveOrderByID(data, orderID);
	}
	
	public synchronized DocumentResultBean documentRetrieveOrderByColumn(ParameterBean data,	String column, String value) {
		return new DocumentQueryHandler().documentRetrieveOrderByColumn(data, column, value);
	}
	
	public synchronized InvoiceResultBean documentRetrieveInvoiceByID(ParameterBean data, int invoiceID) {
		return new DocumentQueryHandler().documentRetrieveInvoiceByID(data, invoiceID);
	}
	
	public synchronized InvoiceResultBean documentRetrieveInvoiceByColumn(ParameterBean data, String column, String value) {
		return new DocumentQueryHandler().documentRetrieveInvoiceByColumn(data, column, value);
	}
	
	public synchronized DocumentResultBean documentRetrieveInOutByID(ParameterBean data, int inoutID) {
		return new DocumentQueryHandler().documentRetrieveInOutByID(data, inoutID);
	}
	
	public synchronized DocumentResultBean documentRetrieveInOutByColumn(ParameterBean data, String column, String value) {
		return new DocumentQueryHandler().documentRetrieveInOutByColumn(data, column, value);
	}
	
	public synchronized DocumentResultBean documentRetrieveAllocationByID(ParameterBean data, int allocationID) {
		return new DocumentQueryHandler().documentRetrieveAllocationByID(data, allocationID);
	}
	
	public synchronized DocumentResultBean documentRetrieveAllocationByColumn(ParameterBean data, String column, String value) {
		return new DocumentQueryHandler().documentRetrieveAllocationByColumn(data, column, value);
	}
	
	public synchronized MultipleDocumentsResultBean documentQueryOrders(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate) {
		return new DocumentQueryHandler().documentQueryOrders(data, bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate);
	}
	
	public synchronized MultipleDocumentsResultBean documentQueryOrders(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate, String additionalWhereClause, String[] referencedTablesColumns) {
		return new DocumentQueryHandler().documentQueryOrders(data, bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate, additionalWhereClause, referencedTablesColumns);
	}

	public synchronized MultipleDocumentsResultBean documentQueryInvoices(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate, String additionalWhereClause) {
		return new DocumentQueryHandler().documentQueryInvoices(data, bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate, additionalWhereClause);
	}
	
	public MultipleDocumentsResultBean documentQueryInvoices(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate, String additionalWhereClause, String[] referencedTablesColumns) {
		return new DocumentQueryHandler().documentQueryInvoices(data, bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate, additionalWhereClause, referencedTablesColumns);
	}
	
	public synchronized MultipleDocumentsResultBean documentQueryInOuts(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate) {
		return new DocumentQueryHandler().documentQueryInOuts(data, bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate);
	}
	
	public synchronized MultipleDocumentsResultBean documentQueryInOuts(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate, String additionalWhereClause, String[] referencedTablesColumns) {
		return new DocumentQueryHandler().documentQueryInOuts(data, bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate, additionalWhereClause, referencedTablesColumns);
	}
	
	public synchronized MultipleDocumentsResultBean documentQueryAllocations(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate) {
		return new DocumentQueryHandler().documentQueryAllocations(data, bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate);
	}

	public synchronized MultipleDocumentsResultBean documentQueryAllocations(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate, String additionalWhereClause, String[] referencedTablesColumns) {
		return new DocumentQueryHandler().documentQueryAllocations(data, bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate, additionalWhereClause, referencedTablesColumns);
	}
	
	/* ===================================================== */
	/* ===================== Pedidos ======================= */
	/* ===================================================== */
	
	public synchronized ResultBean orderCreateCustomer(OrderParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeOrder, boolean createInvoice, boolean completeInvoice) {
		return new OrderDocumentHandler().orderCreateCustomer(data, bPartnerID, bPartnerValue, taxID, completeOrder, createInvoice, completeInvoice, false, false);
	}
	
	public synchronized ResultBean orderCreateCustomer(OrderParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeOrder, boolean createInvoice, boolean completeInvoice, boolean createShipment, boolean completeShipment) {
		return new OrderDocumentHandler().orderCreateCustomer(data, bPartnerID, bPartnerValue, taxID, completeOrder, createInvoice, completeInvoice, createShipment, completeShipment);
	}
	
	public synchronized ResultBean orderCreateVendor(OrderParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeOrder, boolean createInvoice, boolean completeInvoice) {
		return new OrderDocumentHandler().orderCreateVendor(data, bPartnerID, bPartnerValue, taxID, completeOrder, createInvoice, completeInvoice);
	}
	
	public synchronized ResultBean orderDeleteByID(ParameterBean data, int orderID) {
		return new OrderDocumentHandler().orderDeleteByID(data, orderID);
	}
	
	public synchronized ResultBean orderDeleteByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return new OrderDocumentHandler().orderDeleteByColumn(data, columnName, columnCriteria);
	}

	public synchronized ResultBean orderCompleteByID(OrderParameterBean data, int orderID, boolean createInvoice, boolean completeInvoice) {
		return new OrderDocumentHandler().orderCompleteByID(data, orderID, createInvoice, completeInvoice, false, false);
	}

	public synchronized ResultBean orderCompleteByID(OrderParameterBean data, int orderID, boolean createInvoice, boolean completeInvoice, boolean createShipment, boolean completeShipment) {
		return new OrderDocumentHandler().orderCompleteByID(data, orderID, createInvoice, completeInvoice, createShipment, completeShipment);
	}
	
	public synchronized ResultBean orderCompleteByColumn(OrderParameterBean data, String columnName, String columnCriteria, boolean createInvoice, boolean completeInvoice) {
		return new OrderDocumentHandler().orderCompleteByColumn(data, columnName, columnCriteria, createInvoice, completeInvoice, false, false);
	}

	public synchronized ResultBean orderCompleteByColumn(OrderParameterBean data, String columnName, String columnCriteria, boolean createInvoice, boolean completeInvoice, boolean createShipment, boolean completeShipment) {
		return new OrderDocumentHandler().orderCompleteByColumn(data, columnName, columnCriteria, createInvoice, completeInvoice, createShipment, completeShipment);
	}
	
	public synchronized ResultBean orderVoidByID(ParameterBean data, int orderID) {
		return new OrderDocumentHandler().orderVoidByID(data, orderID);
	}

	public synchronized ResultBean orderVoidByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return new OrderDocumentHandler().orderVoidByColumn(data, columnName, columnCriteria);
	}
	
	public ResultBean orderUpdateByID(ParameterBean data, int orderID, boolean completeOrder) {
		return new OrderDocumentHandler().orderUpdateByID(data, orderID, completeOrder);
	}
	
	public ResultBean orderUpdateByColumn(ParameterBean data, String columnName, String columnCriteria, boolean completeOrder) {
		return new OrderDocumentHandler().orderUpdateByColumn(data, columnName, columnCriteria, completeOrder);
	}


	/* ===================================================== */
	/* ===================== Remitos ======================= */
	/* ===================================================== */
	
	public synchronized ResultBean inOutCreateCustomer(DocumentParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut) {
		return new InOutDocumentHandler().inOutCreateCustomer(data, bPartnerID, bPartnerValue, taxID, completeInOut);
	}
	
	public synchronized ResultBean inOutCreateVendor(DocumentParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut) {
		return new InOutDocumentHandler().inOutCreateVendor(data, bPartnerID, bPartnerValue, taxID, completeInOut);
	}
	
	public synchronized ResultBean inOutCreateFromOrder(DocumentParameterBean data, int orderID, boolean completeInOut) {
		return new InOutDocumentHandler().inOutCreateFromOrder(data, orderID, completeInOut);
	}

	public synchronized ResultBean inOutDeleteByID(ParameterBean data, int inOutID) {
		return new InOutDocumentHandler().inOutDeleteByID(data, inOutID);
	}
	
	public synchronized ResultBean inOutDeleteByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return new InOutDocumentHandler().inOutDeleteByColumn(data, columnName, columnCriteria);
	}

	public synchronized ResultBean inOutCompleteByID(ParameterBean data, int inOutID) {
		return new InOutDocumentHandler().inOutCompleteByID(data, inOutID);
	}
	
	public synchronized ResultBean inOutCompleteByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return new InOutDocumentHandler().inOutCompleteByColumn(data, columnName, columnCriteria);
	}
	
	public synchronized ResultBean inOutVoidByID(ParameterBean data, int inOutID) {
		return new InOutDocumentHandler().inOutVoidByID(data, inOutID);
	}
	
	public synchronized ResultBean inOutVoidByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return new InOutDocumentHandler().inOutVoidByColumn(data, columnName, columnCriteria);
	}
	
	/* ================================================================== */
	/* ================== Saldos Entidades Comerciales ================== */
	/* ================================================================== */

	public synchronized ResultBean bPartnerBalanceSumOrdersNotInvoiced(ParameterBean data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID) {
		return new BPartnerBalanceHandler().bPartnerBalanceSumOrdersNotInvoiced(data, bPartnerID, bPartnerList, cuit, clientID, orgID);
	}

	public synchronized ResultBean bPartnerBalanceSumInvoices(ParameterBean data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID) {
		return new BPartnerBalanceHandler().bPartnerBalanceSumInvoices(data, bPartnerID, bPartnerList, cuit, clientID, orgID);
	}

	public synchronized ResultBean bPartnerBalanceSumPayments(ParameterBean data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID) {
		return new BPartnerBalanceHandler().bPartnerBalanceSumPayments(data, bPartnerID, bPartnerList, cuit, clientID, orgID);
	}

	public synchronized ResultBean bPartnerBalanceSumChecks(ParameterBean data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID) {
		return new BPartnerBalanceHandler().bPartnerBalanceSumChecks(data, bPartnerID, bPartnerList, cuit, clientID, orgID);
	}

	/* ================================================================== */
	/* ====================== Recibos de clientes ======================= */
	/* ================================================================== */
	
	public synchronized ResultBean allocationCreateReceipt(AllocationParameterBean data,	int bPartnerID, String bPartnerValue, String taxID) {
		return new AllocationDocumentHandler().allocationCreateReceipt(data, bPartnerID, bPartnerValue, taxID);
	}

	public synchronized ResultBean allocationCreateEarlyReceipt(AllocationParameterBean data, int bPartnerID, String bPartnerValue,	String taxID) {
		return new AllocationDocumentHandler().allocationCreateEarlyReceipt(data, bPartnerID, bPartnerValue, taxID);
	}

	public synchronized ResultBean allocationVoidByID(AllocationParameterBean data, int allocationID, String allocationAction) {
		return new AllocationDocumentHandler().allocationVoidByID(data, allocationID, allocationAction);
	}
	
	public synchronized ResultBean allocationVoidByColumn(AllocationParameterBean data, String columnName, String columnCriteria, String allocationAction) {
		return new AllocationDocumentHandler().allocationVoidByColumn(data, columnName, columnCriteria, allocationAction);
	}

	/* ================================================================== */
	/* =========================== Usuarios ============================= */
	/* ================================================================== */
	
	public synchronized ResultBean userCreate(ParameterBean data) {
		return new UserCRUDHandler().userCreate(data);
	}

	public synchronized ResultBean userRetrieveByID(ParameterBean data, int userID) {
		return new UserCRUDHandler().userRetrieveByID(data, userID);
	}

	public synchronized ResultBean userRetrieveByColumn(ParameterBean data, String columnName, String criteria) {
		return new UserCRUDHandler().userRetrieveByColumn(data, columnName, criteria);
	}

	public synchronized ResultBean userUpdateByID(ParameterBean data, int userID) {
		return new UserCRUDHandler().userUpdateByID(data, userID);
	}

	public synchronized ResultBean userDeleteByID(ParameterBean data, int userID) {
		return new UserCRUDHandler().userDeleteByID(data, userID);
	}
	
	/* ================================================================== */
	/* ============================ Stock =============================== */
	/* ================================================================== */

	public synchronized StorageResultBean storageQuery(ParameterBean data, int[] warehouseList, int locatorID, String productValue, int productID, String lot, String serno) {
		return new StorageQueryHandler().storageQuery(data, warehouseList, locatorID, productValue, productID, lot, serno);
	}
	
	/* ================================================================== */
	/* ========================= Inventario ============================= */
	/* ================================================================== */
	
	public synchronized ResultBean inventoryCreate(DocumentParameterBean data, boolean completeInventory) {
		return new InventoryDocumentHandler().inventoryCreate(data, completeInventory);
	}

	public synchronized ResultBean inventoryCompleteByID(ParameterBean data, int inventoryID) {
		return new InventoryDocumentHandler().inventoryCompleteByID(data, inventoryID);
	}

	public synchronized ResultBean inventoryCompleteByColumn(ParameterBean data, String columnName, String value) {
		return new InventoryDocumentHandler().inventoryCompleteByColumn(data, columnName, value);
	}

	public synchronized ResultBean inventoryDeleteByID(ParameterBean data, int inventoryID) {
		return new InventoryDocumentHandler().inventoryDeleteByID(data, inventoryID);
	}

	public synchronized ResultBean inventoryDeleteByColumn(ParameterBean data, String columnName, String value) {
		return new InventoryDocumentHandler().inventoryDeleteByColumn(data, columnName, value);
	}

	public synchronized ResultBean inventoryVoidByID(ParameterBean data, int inventoryID) {
		return new InventoryDocumentHandler().inventoryVoidByID(data, inventoryID);
	}

	public synchronized ResultBean inventoryVoidByColumn(ParameterBean data, String columnName, String value) {
		return new InventoryDocumentHandler().inventoryVoidByColumn(data, columnName, value);
	}
	
	/* ================================================================== */
	/* ====================== Direcciones de EC ========================= */
	/* ================================================================== */
	
	public synchronized ResultBean bPartnerLocationCreate(ParameterBean data) {
		return new BPartnerLocationCRUDHandler().bPartnerLocationCreate(data);
	}

	public synchronized ResultBean bPartnerLocationUpdate(ParameterBean data, int bPartnerLocationID) {
		return new BPartnerLocationCRUDHandler().bPartnerLocationUpdate(data, bPartnerLocationID);
	}

	public synchronized ResultBean bPartnerLocationDelete(ParameterBean data, int bPartnerLocationID) {
		return new BPartnerLocationCRUDHandler().bPartnerLocationDelete(data, bPartnerLocationID);
	}

	public synchronized ResultBean bPartnerLocationRetrieve(ParameterBean data, int bPartnerLocationID) {
		return new BPartnerLocationCRUDHandler().bPartnerLocationRetrieve(data, bPartnerLocationID);
	}

	/* ================================================================== */
	/* ===================== Precios de artículos ======================= */
	/* ================================================================== */
	
	public synchronized ResultBean productPriceCreateUpdate(ParameterBean data) {
		return new ProductPriceCRUDHandler().productPriceCreateUpdate(data);
	}

	public synchronized ResultBean productPriceDelete(ParameterBean data, int productID, int priceListVersionID) {
		return new ProductPriceCRUDHandler().productPriceDelete(data, productID, priceListVersionID);
	}

	public synchronized ResultBean productPriceRetrieve(ParameterBean data, int productID, int priceListVersionID) {
		return new ProductPriceCRUDHandler().productPriceRetrieve(data, productID, priceListVersionID); 
	}

	/* ================================================================== */
	/* ===================== Ordenes de Producción ====================== */
	/* ================================================================== */
	
	public synchronized ResultBean productionOrderCreate(DocumentParameterBean data, boolean completeProductionOrder) {
		return new ProductionOrderDocumentHandler().productionOrderCreate(data, completeProductionOrder);
	}

	public synchronized ResultBean productionOrderDelete(ParameterBean data, int productionOrderID) {
		return new ProductionOrderDocumentHandler().productionOrderDelete(data, productionOrderID);
	}

	public synchronized ResultBean productionOrderComplete(ParameterBean data, int productionOrderID) {
		return new ProductionOrderDocumentHandler().productionOrderComplete(data, productionOrderID);
	}

	public synchronized ResultBean productionOrderVoid(ParameterBean data, int productionOrderID) {
		return new ProductionOrderDocumentHandler().productionOrderVoid(data, productionOrderID);
	}
	
	public synchronized ResultBean productionOrderVoidByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return new ProductionOrderDocumentHandler().productionOrderVoidByColumn(data, columnName, columnCriteria);
	}

	/* ================================================================== */
	/* ====================== Boletas de depósito ======================= */
	/* ================================================================== */
	
	public synchronized ResultBean depositSlipCreate(DocumentParameterBean data, boolean completeDepositSlip) {
		return new DepositSlipDocumentHandler().depositSlipCreate(data, completeDepositSlip);
	}

	public synchronized ResultBean depositSlipDelete(ParameterBean data, int depositSlipID) {
		return new DepositSlipDocumentHandler().depositSlipDelete(data, depositSlipID);
	}

	public synchronized ResultBean depositSlipComplete(ParameterBean data, int depositSlipID) {
		return new DepositSlipDocumentHandler().depositSlipComplete(data, depositSlipID);
	}

	public synchronized ResultBean depositSlipVoid(ParameterBean data, int depositSlipID) {
		return new DepositSlipDocumentHandler().depositSlipVoid(data, depositSlipID);
	}

	public synchronized ResultBean depositSlipVoidByColumn(ParameterBean data, String columnName, String columnCriteria) {
		return new DepositSlipDocumentHandler().depositSlipVoidByColumn(data, columnName, columnCriteria);
	}
	
	/* ================================================================== */
	/* ====================== Lista de materiales ======================= */
	/* ================================================================== */

	public synchronized ResultBean billOfMaterialCreate(ParameterBean data) {
		return new BillOfMaterialCRUDHandler().billOfMaterialCreate(data);
	}

	public synchronized ResultBean billOfMaterialDelete(ParameterBean data, int productBOMId) {
		return new BillOfMaterialCRUDHandler().billOfMaterialDelete(data, productBOMId);
	}

	/* ================================================================== */
	/* ========================== Procesos ============================== */
	/* ================================================================== */

	public ResultBean processFiscalPrinterClose(ParameterBean data) {
		return new ProcessExecuteHandler().processFiscalPrinterClose(data);
	}

	public ResultBean processCreditCardBatchClose(ParameterBean data) {
		return new ProcessExecuteHandler().processCreditCardBatchClose(data);
	}
	
	
	/* ================================================================== */
	/* ========================= Replicación ============================ */
	/* ================================================================== */
	
	public synchronized ReplicationResultBean replicate(ReplicationParameterBean data) {
		return new ReplicationServerHandler().replicate(data);
	}

	/* ================================================================== */
	/* ==================== Funciones de uso general ==================== */
	/* ================================================================== */
	
	public synchronized MultipleRecordsResultBean recordQuery(FilteredColumnsParameterBean data, String tableName, String whereClause, boolean includeNamedReferences) {
		return new GeneralRecordQueryHandler().recordQuery(data, tableName, whereClause, includeNamedReferences);
	}
	
	public CustomServiceResultBean customService(CustomServiceParameterBean data) {
		return new CustomServiceHandler().customService(data);
	}


}
