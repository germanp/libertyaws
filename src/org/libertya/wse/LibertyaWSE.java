package org.libertya.wse;

import org.libertya.ws.bean.parameter.DocumentParameterBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.wse.common.ListedMap;
import org.libertya.wse.common.RecordContent;
import org.libertya.wse.common.SimpleMap;
import org.libertya.wse.param.DocumentLine;
import org.libertya.wse.param.Login;
import org.libertya.wse.result.BPartnerResult;
import org.libertya.wse.result.DocumentResult;
import org.libertya.wse.result.InvoiceResult;
import org.libertya.wse.result.MultipleRecordsResult;
import org.libertya.wse.result.Result;
import org.libertya.wse.result.SimpleResult;

public interface LibertyaWSE {

	/* ================================================================== */
	/* ==================== Entidades Comerciales ======================= */
	/* ================================================================== */
	
	/**
	 * Wrapper para <code>bPartnerCreate(BPartnerParameterBean data)</code> 
	 */
	public SimpleResult bPartnerCreate(Login login, SimpleMap[] data, SimpleMap[] location);
	
	/**
	 * Wrapper para <code>bPartnerRetrieveByID(ParameterBean data, int bPartnerID)</code>
	 */
	public BPartnerResult bPartnerRetrieveByID(Login login, int bPartnerID);

	/**
	 * Wrapper para <code>bPartnerRetrieveByValue(ParameterBean data, String value)</code>
	 */	
	public BPartnerResult bPartnerRetrieveByValue(Login login, String value);
	
	/**
	 * Wrapper para <code>bPartnerRetrieveByTaxID(ParameterBean data, String taxID)</code>
	 */	
	public BPartnerResult bPartnerRetrieveByTaxID(Login login, String taxID);
	
	/**
	 * Wrapper para <code>bPartnerUpdate(BPartnerParameterBean data, int bPartnerID, int bPartnerLocationID)</code>
	 */	
	public SimpleResult bPartnerUpdate(Login login, SimpleMap[] data, SimpleMap[] location, int bPartnerID, int bPartnerLocationID);
	
	/**
	 * Wrapper para <code>bPartnerDelete(ParameterBean data, int bPartnerID)</code>
	 */	
	public SimpleResult bPartnerDelete(Login login, int bPartnerID);
	
	
	/* ===================================================== */
	/* ==================== Facturas ======================= */
	/* ===================================================== */

	/**
	 * Wrapper para <code>invoiceCreateCustomer(InvoiceParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeDocument)</code>
	 */
	public SimpleResult invoiceCreateCustomer(Login login, SimpleMap[] data, DocumentLine[] lines, DocumentLine[] otherTaxes, int bPartnerID, String bPartnerValue, String taxID, boolean completeDocument);

	/**
	 * Wrapper para <code>invoiceCreateCustomerFromOrderByID(InvoiceParameterBean data, int orderID, boolean completeDocument)</code>
	 */
	public SimpleResult invoiceCreateCustomerFromOrderByID(Login login, SimpleMap[] data, DocumentLine[] lines, DocumentLine[] otherTaxes, int orderID, boolean completeDocument);

	/**
	 * Wrapper para <code>invoiceCreateCustomerFromOrderByColumn(InvoiceParameterBean data, String searchColumn, String searchCriteria, boolean completeDocument)</code>
	 */
	public SimpleResult invoiceCreateCustomerFromOrderByColumn(Login login, SimpleMap[] data, DocumentLine[] lines, DocumentLine[] otherTaxes, String searchColumn, String searchCriteria, boolean completeDocument);

	/**
	 * Wrapper para <code>invoiceCreateVendor(InvoiceParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeDocument)</code>
	 */
	public SimpleResult invoiceCreateVendor(Login login, SimpleMap[] data, DocumentLine[] lines, DocumentLine[] otherTaxes, int bPartnerID, String bPartnerValue, String taxID, boolean completeDocument);

	/**
	 * Wrapper para <code>invoiceCreateVendorFromOrderByID(InvoiceParameterBean data, int orderID, boolean completeDocument)</code>
	 */
	public SimpleResult invoiceCreateVendorFromOrderByID(Login login, SimpleMap[] data, DocumentLine[] lines, DocumentLine[] otherTaxes, int orderID, boolean completeDocument);

	/**
	 * Wrapper para <code>invoiceCreateVendorFromOrderByColumn(InvoiceParameterBean data, String searchColumn, String searchCriteria, boolean completeDocument)</code>
	 */
	public SimpleResult invoiceCreateVendorFromOrderByColumn(Login login, SimpleMap[] data, DocumentLine[] lines, DocumentLine[] otherTaxes, String searchColumn, String searchCriteria, boolean completeDocument);
	
	/**
	 * Wrapper para <code>invoiceDeleteByID(ParameterBean data, int invoiceID)</code>
	 */
	public SimpleResult invoiceDeleteByID(Login login, int invoiceID);

	/**
	 * Wrapper para <code>invoiceDeleteByColumn(ParameterBean data, String columnName, String columnCriteria)</code>
	 */
	public SimpleResult invoiceDeleteByColumn(Login login, String columnName, String columnCriteria);

	/**
	 * Wrapper para <code>invoiceCompleteByID(ParameterBean data, int invoiceID)</code>
	 */
	public SimpleResult invoiceCompleteByID(Login login, int invoiceID);

	/**
	 * Wrapper para <code>invoiceCompleteByColumn(ParameterBean data, String columnName, String columnCriteria)</code>
	 */
	public SimpleResult invoiceCompleteByColumn(Login login, String columnName, String columnCriteria);

	/**
	 * Wrapper para <code>invoiceVoidByID(ParameterBean data, int invoiceID)</code>
	 */
	public SimpleResult invoiceVoidByID(Login login, int invoiceID);

	/**
	 * Wrapper para <code>invoiceVoidByColumn(ParameterBean data, String columnName, String columnCriteria)</code>
	 */
	public SimpleResult invoiceVoidByColumn(Login login, String columnName, String columnCriteria);

	/**
	 * Wrapper para <code>invoiceUpdateByID(ParameterBean data, int invoiceID)</code>
	 */
	public SimpleResult invoiceUpdateByID(Login login, SimpleMap[] data, int invoiceID);

	
	/* ===================================================== */
	/* ==================== Artículos ====================== */
	/* ===================================================== */
	
	/**
	 * Wrapper para <code>productCreate(ParameterBean data)</code>
	 */
	public SimpleResult productCreate(Login login, SimpleMap[] data);
	
	/**
	 * Wrapper para <code>productCreate(ParameterBean data, boolean createDefaultProductPrice)</code>
	 */
	public SimpleResult productCreate(Login login, SimpleMap[] data, boolean createDefaultProductPrice);

	/**
	 * Wrapper para <code>productRetrieveByID(ParameterBean data, int productID)</code>
	 */
	public SimpleResult productRetrieveByID(Login login, int productID);

	/**
	 * Wrapper para <code>productRetrieveByValue(ParameterBean data, String value)</code>
	 */
	public SimpleResult productRetrieveByValue(Login login, String value);
	
	/**
	 * Wrapper para <code>productUpdateByID(ParameterBean data, int productID)</code>
	 */	
	public SimpleResult productUpdateByID(Login login, SimpleMap[] data, int productID);
	
	/**
	 * Wrapper para <code>productUpdateByValue(ParameterBean data, String value)</code>
	 */
	public SimpleResult productUpdateByValue(Login login, SimpleMap[] data, String value);
	
	/**
	 * Wrapper para <code>productDelete(ParameterBean data, int productID)</code>
	 */
	public SimpleResult productDelete(Login login, int productID);
		
	/* ===================================================== */
	/* ============= Consulta de Comprobantes ============== */
	/* ===================================================== */

	/**
	 * Wrapper de <code>documentRetrieveOrderByID(ParameterBean data, int orderID)</code> 
	 */
	public DocumentResult documentRetrieveOrderByID(Login login, int orderID);
	
	/**
	 * Wrapper de <code>documentRetrieveOrderByColumn(ParameterBean data, String column, String value)</code> 
	 */
	public DocumentResult documentRetrieveOrderByColumn(Login login, String column, String value);
	
	/**
	 * Wrapper de <code>documentRetrieveInvoiceByID(ParameterBean data, int invoiceID)</code> 
	 */
	public InvoiceResult documentRetrieveInvoiceByID(Login login, int invoiceID);
	
	/**
	 * Wrapper de <code>documentRetrieveInvoiceByColumn(ParameterBean data, String column, String value)</code> 
	 */
	public InvoiceResult documentRetrieveInvoiceByColumn(Login login, String column, String value);
	
	/**
	 * Wrapper de <code>documentRetrieveInOutByID(ParameterBean data, int inoutID)</code> 
	 */
	public DocumentResult documentRetrieveInOutByID(Login login, int inoutID);
	
	/**
	 * Wrapper de <code>documentRetrieveInOutByColumn(ParameterBean data, String column, String value)</code> 
	 */
	public DocumentResult documentRetrieveInOutByColumn(Login login, String column, String value);
	
	/**
	 * Wrapper de <code>documentRetrieveAllocationByID(ParameterBean data, int allocationID)</code> 
	 */
	public DocumentResult documentRetrieveAllocationByID(Login login, int allocationID);
	
	/**
	 * Wrapper de <code>documentRetrieveAllocationByColumn(ParameterBean data, String column, String value)</code> 
	 */
	public DocumentResult documentRetrieveAllocationByColumn(Login login, String column, String value);
	
	/**
	 * Wrapper de <code>documentQueryOrders(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate)</code> 
	 */
	public MultipleRecordsResult documentQueryOrders(Login login, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate);

	/**
	 * Wrapper de <code>documentQueryOrders(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate)</code> 
	 */
	public MultipleRecordsResult documentQueryOrders(Login login, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate, String additionalWhereClause, String[] referencedTablesColumns);

	/**
	 * Wrapper de <code>documentQueryInvoices(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate, String additionalWhereClause)</code> 
	 */
	public MultipleRecordsResult documentQueryInvoices(Login login, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate, String additionalWhereClause);

	/**
	 * Wrapper de <code>documentQueryInvoices(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate, String additionalWhereClause, String[] referencedTablesColumns)</code> 
	 */
	public MultipleRecordsResult documentQueryInvoices(Login login, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate, String additionalWhereClause, String[] referencedTablesColumns);
	
	/**
	 * Wrapper de <code>documentQueryInOuts(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate)</code> 
	 */
	public MultipleRecordsResult documentQueryInOuts(Login login, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate);

	/**
	 * Wrapper de <code>documentQueryInOuts(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate)</code> 
	 */
	public MultipleRecordsResult documentQueryInOuts(Login login, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate, String additionalWhereClause, String[] referencedTablesColumns);

	/**
	 * Wrapper de <code>documentQueryAllocations(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate)</code> 
	 */
	public MultipleRecordsResult documentQueryAllocations(Login login, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate);
	
	/**
	 * Wrapper de <code>documentQueryAllocations(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate)</code> 
	 */
	public MultipleRecordsResult documentQueryAllocations(Login login, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate, String additionalWhereClause, String[] referencedTablesColumns);

	/* ===================================================== */
	/* ===================== Pedidos ======================= */
	/* ===================================================== */

	/**
	 * Wrapper para <code>orderCreateCustomer(OrderParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeOrder, boolean createInvoice, boolean completeInvoice)</code>
	 */
	public SimpleResult orderCreateCustomer(Login login, SimpleMap[] data, DocumentLine[] lines, int bPartnerID, String bPartnerValue, String taxID, boolean completeOrder, boolean createInvoice, boolean completeInvoice, int invoiceDocTypeTargetID, int invoicePuntoDeVenta, String invoiceTipoComprobante);
	
	/**
	 * Wrapper para <code>orderCreateCustomer(OrderParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeOrder, boolean createInvoice, boolean completeInvoice, boolean createShipment, boolean completeShipment)</code>
	 */
	public SimpleResult orderCreateCustomer(Login login, SimpleMap[] data, DocumentLine[] lines, int bPartnerID, String bPartnerValue, String taxID, boolean completeOrder, boolean createInvoice, boolean completeInvoice, int invoiceDocTypeTargetID, int invoicePuntoDeVenta, String invoiceTipoComprobante, boolean createShipment, boolean completeShipment);
	
	/**
	 * Wrapper para <code>orderCreateVendor(OrderParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeOrder, boolean createInvoice, boolean completeInvoice)</code>
	 */
	public SimpleResult orderCreateVendor(Login login, SimpleMap[] data, DocumentLine[] lines, int bPartnerID, String bPartnerValue, String taxID, boolean completeOrder, boolean createInvoice, boolean completeInvoice, int invoiceDocTypeTargetID, int invoicePuntoDeVenta, String invoiceTipoComprobante);

	/**
	 * Wrapper para <code>orderDeleteByID(ParameterBean data, int orderID)</code>
	 */
	public SimpleResult orderDeleteByID(Login login, int orderID);

	/**
	 * Wrapper para <code>orderDeleteByColumn(ParameterBean data, String columnName, String columnCriteria)</code>
	 */
	public SimpleResult orderDeleteByColumn(Login login, String columnName, String columnCriteria);

	/**
	 * Wrapper para <code>orderCompleteByID(OrderParameterBean data, int orderID, boolean createInvoice, boolean completeInvoice)</code>
	 */
	public SimpleResult orderCompleteByID(Login login, int orderID, boolean createInvoice, boolean completeInvoice, int invoiceDocTypeTargetID, int invoicePuntoDeVenta, String invoiceTipoComprobante);
	
	/**
	 * Wrapper para <code>orderCompleteByID(OrderParameterBean data, int orderID, boolean createInvoice, boolean completeInvoice, boolean createShipment, boolean completeShipment)</code>
	 */
	public SimpleResult orderCompleteByID(Login login, int orderID, boolean createInvoice, boolean completeInvoice, int invoiceDocTypeTargetID, int invoicePuntoDeVenta, String invoiceTipoComprobante, boolean createShipment, boolean completeShipment);

	/**
	 * Wrapper para <code>orderCompleteByColumn(OrderParameterBean data, String columnName, String columnCriteria, boolean createInvoice, boolean completeInvoice)</code>
	 */
	public SimpleResult orderCompleteByColumn(Login login, String columnName, String columnCriteria, boolean createInvoice, boolean completeInvoice, int invoiceDocTypeTargetID, int invoicePuntoDeVenta, String invoiceTipoComprobante);

	/**
	 * Wrapper para <code>orderCompleteByColumn(OrderParameterBean data, String columnName, String columnCriteria, boolean createInvoice, boolean completeInvoice, boolean createShipment, boolean completeShipment)</code>
	 */
	public SimpleResult orderCompleteByColumn(Login login, String columnName, String columnCriteria, boolean createInvoice, boolean completeInvoice, int invoiceDocTypeTargetID, int invoicePuntoDeVenta, String invoiceTipoComprobante, boolean createShipment, boolean completeShipment);
	
	/**
	 * Wrapper para <code>orderVoidByID(ParameterBean data, int orderID)</code>
	 */
	public SimpleResult orderVoidByID(Login login, int orderID);
	
	/**
	 * Wrapper para <code>orderVoidByColumn(ParameterBean data, String columnName, String columnCriteria)</code>
	 */
	public SimpleResult orderVoidByColumn(Login login, String columnName, String columnCriteria);
	
	/**
	 * Wrapper para <code>orderUpdateByID(ParameterBean data, int orderID, boolean completeOrder)</code>
	 */
	public SimpleResult orderUpdateByID(Login login, SimpleMap[] data, int orderID, boolean completeOrder);

	/**
	 * Wrapper para <code>orderUpdateByColumn(ParameterBean data, String columnName, String columnCriteria, boolean completeOrder)</code>
	 */
	public SimpleResult orderUpdateByColumn(Login login, SimpleMap[] data, String columnName, String columnCriteria, boolean completeOrder);
	
	
	/* ===================================================== */
	/* ===================== Remitos ======================= */
	/* ===================================================== */

	/**
	 * Wrapper para <code>inOutCreateCustomer(DocumentParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut)</code>
	 */
	public SimpleResult inOutCreateCustomer(Login login, SimpleMap[] header, DocumentLine[] lines, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut);

	/**
	 * Wrapper para <code>inOutCreateVendor(DocumentParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut)</code>
	 */
	public SimpleResult inOutCreateVendor(Login login, SimpleMap[] header, DocumentLine[] lines, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut);

	/**
	 * Wrapper para <code>inOutCreateFromOrder(DocumentParameterBean data, int orderID, boolean completeInOut)</code> 
	 */
	public SimpleResult inOutCreateFromOrder(Login login, SimpleMap[] header, DocumentLine[] lines, int orderID, boolean completeInOut);
	
	/**
	 * Wrapper para <code>inOutDeleteByID(ParameterBean data, int inOutID)</code>
	 */
	public SimpleResult inOutDeleteByID(Login login, SimpleMap[] data, int inOutID);

	/**
	 * Wrapper para <code>inOutDeleteByColumn(ParameterBean data, String columnName, String columnCriteria)</code>
	 */
	public SimpleResult inOutDeleteByColumn(Login login, SimpleMap[] data, String columnName, String columnCriteria);

	/**
	 * Wrapper para <code>inOutCompleteByID(ParameterBean data, int inOutID)</code>
	 */
	public SimpleResult inOutCompleteByID(Login login, SimpleMap[] data, int inOutID);

	/**
	 * Wrapper para <code>inOutCompleteByColumn(ParameterBean data, String columnName, String columnCriteria)</code>
	 */
	public SimpleResult inOutCompleteByColumn(Login login, SimpleMap[] data, String columnName, String columnCriteria);

	/**
	 * Wrapper para <code>inOutVoidByID(ParameterBean data, int inOutID)</code>
	 */
	public SimpleResult inOutVoidByID(Login login, SimpleMap[] data, int inOutID);
	
	/**
	 * Wrapper para <code>inOutVoidByColumn(ParameterBean data, String columnName, String columnCriteria)</code>
	 */
	public SimpleResult inOutVoidByColumn(Login login, SimpleMap[] data, String columnName, String columnCriteria);
	
	
	/* ================================================================== */
	/* ================== Saldos Entidades Comerciales ================== */
	/* ================================================================== */

	/**
	 * Wrapper para <code>bPartnerBalanceSumOrdersNotInvoiced(ParameterBean data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID)</code>
	 */
	public SimpleResult bPartnerBalanceSumOrdersNotInvoiced(Login login, SimpleMap[] data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID);
	
	/**
	 * Wrapper para <code>bPartnerBalanceSumInvoices(ParameterBean data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID)</code>
	 */
	public SimpleResult bPartnerBalanceSumInvoices(Login login, SimpleMap[] data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID);
	
	/**
	 * Wrapper para <code>bPartnerBalanceSumPayments(ParameterBean data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID)</code>
	 */
	public SimpleResult bPartnerBalanceSumPayments(Login login, SimpleMap[] data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID);
	
	/**
	 * Wrapper para <code>bPartnerBalanceSumChecks(ParameterBean data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID)</code>
	 */
	public SimpleResult bPartnerBalanceSumChecks(Login login, SimpleMap[] data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID);
	
	/* ================================================================== */
	/* ====================== Recibos de clientes ======================= */
	/* ================================================================== */

	/**
	 * Wrapper para <code>allocationCreateReceipt(AllocationParameterBean data, int bPartnerID, String bPartnerValue, String taxID)</code> 
	 */
	public SimpleResult allocationCreateReceipt(Login login, SimpleMap[] data, RecordContent[] invoices, RecordContent[] payments, int bPartnerID, String bPartnerValue, String taxID);
	
	/**
	 * Wrapper para <code>allocationCreateEarlyReceipt(AllocationParameterBean data, int bPartnerID, String bPartnerValue, String taxID)</code> 
	 */
	public SimpleResult allocationCreateEarlyReceipt(Login login, SimpleMap[] data, RecordContent[] payments, int bPartnerID, String bPartnerValue, String taxID);

	/**
	 * Wrapper para <code>allocationVoidByID(AllocationParameterBean data, int allocationID, String allocationAction)</code> 
	 */
	public SimpleResult allocationVoidByID(Login login, int allocationID, String allocationAction);

	/**
	 * Wrapper para <code>allocationVoidByColumn(AllocationParameterBean data, String columnName, String columnCriteria, String allocationAction)</code> 
	 */
	public SimpleResult allocationVoidByColumn(Login login, String columnName, String columnCriteria, String allocationAction);

	/* ================================================================== */
	/* =========================== Usuarios ============================= */
	/* ================================================================== */

	/**
	 * Wrapper para <code>userCreate(ParameterBean data)</code>
	 */
	public SimpleResult userCreate(Login login, SimpleMap[] data);
	
	/**
	 * Wrapper para <code>userRetrieveByID(ParameterBean data, int userID)</code>
	 */
	public SimpleResult userRetrieveByID(Login login, int userID);
	
	/**
	 * Wrapper para <code>userUpdateByID(ParameterBean data, int userID)</code>
	 */
	public SimpleResult userUpdateByID(Login login, SimpleMap[] data, int userID);
	
	/**
	 * Wrapper para <code>userDeleteByID(ParameterBean data, int userID)</code>
	 */
	public SimpleResult userDeleteByID(Login login, int userID);
	
	/* ================================================================== */
	/* ========================= Inventario ============================= */
	/* ================================================================== */
	
	/**
	 * Wrapper para <code>inventoryCreate(DocumentParameterBean data, boolean completeInventory)</code>
	 */
	public SimpleResult inventoryCreate(Login login, SimpleMap[] header, DocumentLine[] lines, boolean completeInventory);
	
	/**
	 * Wrapper para <code>inventoryCompleteByID(ParameterBean data, int inventoryID)</code>
	 */
	public SimpleResult inventoryCompleteByID(Login login, SimpleMap[] data, int inventoryID);
	
	/**
	 * Wrapper para <code>inventoryCompleteByColumn(ParameterBean data, String columnName, String value)</code>
	 */
	public SimpleResult inventoryCompleteByColumn(Login login, SimpleMap[] data, String columnName, String value);
	
	/**
	 * Wrapper para <code>inventoryDeleteByID(ParameterBean data, int inventoryID)</code>
	 */
	public SimpleResult inventoryDeleteByID(Login login, SimpleMap[] data, int inventoryID);
	
	/**
	 * Wrapper para <code>inventoryDeleteByColumn(ParameterBean data, String columnName, String value)</code>
	 */
	public SimpleResult inventoryDeleteByColumn(Login login, SimpleMap[] data, String columnName, String value);
		
	/**
	 * Wrapper para <code>inventoryVoidByID(ParameterBean data, int inventoryID)</code>
	 */
	public SimpleResult inventoryVoidByID(Login login, SimpleMap[] data, int inventoryID);
	
	/**
	 * Wrapper para <code>inventoryVoidByColumn(ParameterBean data, String columnName, String value)</code>
	 */
	public SimpleResult inventoryVoidByColumn(Login login, SimpleMap[] data, String columnName, String value);
	
	/* ================================================================== */
	/* ====================== Direcciones de EC ========================= */
	/* ================================================================== */
	
	/**
	 * Wrapper para <code>bPartnerLocationCreate(ParameterBean data)</code> 
	 */
	public SimpleResult bPartnerLocationCreate(Login login, SimpleMap[] data);	

	/**
	 * Wrapper para <code>bPartnerLocationUpdate(ParameterBean data, int bPartnerLocationID)</code> 
	 */
	public SimpleResult bPartnerLocationUpdate(Login login, SimpleMap[] data, int bPartnerLocationID);
	
	/**
	 * Wrapper para <code>bPartnerLocationDelete(ParameterBean data, int bPartnerLocationID)</code> 
	 */
	public SimpleResult bPartnerLocationDelete(Login login, int bPartnerLocationID);

	/**
	 * Wrapper para <code>bPartnerLocationRetrieve(ParameterBean data, int bPartnerLocationID)</code> 
	 */
	public SimpleResult bPartnerLocationRetrieve(Login login, int bPartnerLocationID);

	
	/* ================================================================== */
	/* ===================== Precios de artículos ======================= */
	/* ================================================================== */

	/**
	 * Wrapper para <code>productPriceCreateUpdate(ParameterBean data)</code> 
	 */
	public SimpleResult productPriceCreateUpdate(Login login, SimpleMap[] data);	

	/**
	 * Wrapper para <code>productPriceDelete(ParameterBean data, int productID, int priceListVersionID)</code> 
	 */
	public SimpleResult productPriceDelete(Login login, int productID, int priceListVersionID);

	/**
	 * Wrapper para <code>productPriceRetrieve(ParameterBean data, int productID, int priceListVersionID)</code> 
	 */
	public SimpleResult productPriceRetrieve(Login login, int productID, int priceListVersionID);
	

	/* ================================================================== */
	/* ===================== Ordenes de Producción ====================== */
	/* ================================================================== */
	
	/**
	 * Wrapper para <code>productionOrderCreate(DocumentParameterBean data, boolean completeProductionOrder)</code> 
	 */
	public SimpleResult productionOrderCreate(Login login, SimpleMap[] data, DocumentLine[] lines, boolean completeProductionOrder);
	
	/**
	 * Wrapper para <code>productionOrderDelete(ParameterBean data, int productionOrderID)</code> 
	 */
	public SimpleResult productionOrderDelete(Login login, int productionOrderID);

	/**
	 * Wrapper para <code>productionOrderComplete(ParameterBean data, int productionOrderID)</code> 
	 */
	public SimpleResult productionOrderComplete(Login login, int productionOrderID);
	
	/**
	 * Wrapper para <code>productionOrderVoid(ParameterBean data, int productionOrderID)</code> 
	 */
	public SimpleResult productionOrderVoid(Login login, int productionOrderID);

	/**
	 * Wrapper para <code>productionOrderVoidByColumn(ParameterBean data, String columnName, String columnCriteria)</code> 
	 */
	public SimpleResult productionOrderVoidByColumn(Login login, String columnName, String columnCriteria);
	
	/* ================================================================== */
	/* ====================== Boletas de depósito ======================= */
	/* ================================================================== */

	/**
	 * Wrapper para <code>depositSlipCreate(DocumentParameterBean data, boolean completeDepositSlip)</code> 
	 */
	public SimpleResult depositSlipCreate(Login login, SimpleMap[] data, DocumentLine[] lines, boolean completeDepositSlip);
	
	/**
	 * Wrapper para <code>depositSlipDelete(ParameterBean data, int depositSlipID)</code> 
	 */
	public SimpleResult depositSlipDelete(Login login, int depositSlipID);

	/**
	 * Wrapper para <code>depositSlipComplete(ParameterBean data, int depositSlipID)</code> 
	 */
	public SimpleResult depositSlipComplete(Login login, int depositSlipID);
	
	/**
	 * Wrapper para <code>depositSlipVoid(ParameterBean data, int depositSlipID)</code> 
	 */
	public SimpleResult depositSlipVoid(Login login, int depositSlipID);
	
	/**
	 * Wrapper para <code>depositSlipVoidByColumn(ParameterBean data, String columnName, String columnCriteria)</code>
	 */
	public SimpleResult depositSlipVoidByColumn(Login login, String columnName, String columnCriteria);
	
	/* ================================================================== */
	/* ====================== Lista de materiales ======================= */
	/* ================================================================== */

	/**
	 * Wrapper para <code>billOfMaterialCreate(ParameterBean data)</code> 
	 */
	public SimpleResult billOfMaterialCreate(Login login, SimpleMap[] data);	

	/**
	 * Wrapper para <code>billOfMaterialDelete(ParameterBean data, int productBOMId)</code> 
	 */
	public SimpleResult billOfMaterialDelete(Login login, int productBOMId);
	
	/* ================================================================== */
	/* ==================== Funciones de uso general ==================== */
	/* ================================================================== */
	
	/**
	 * Wrapper para <code>customService(CustomServiceParameterBean data)</code>
	 */
	public Result customService(Login login, String className, ListedMap[] data);

	/**
	 * Wrapper para <code>recordQuery(FilteredColumnsParameterBean data, String tableName, String whereClause, boolean includeNamedReferences)</code>
	 */	
	public MultipleRecordsResult recordQuery(Login login, String[] data, String tableName, String whereClause, boolean includeNamedReferences);
}
