package org.libertya.wse;

import org.libertya.ws.bean.parameter.AllocationParameterBean;
import org.libertya.ws.bean.parameter.BPartnerParameterBean;
import org.libertya.ws.bean.parameter.CustomServiceParameterBean;
import org.libertya.ws.bean.parameter.DocumentParameterBean;
import org.libertya.ws.bean.parameter.FilteredColumnsParameterBean;
import org.libertya.ws.bean.parameter.InvoiceParameterBean;
import org.libertya.ws.bean.parameter.OrderParameterBean;
import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.BPartnerResultBean;
import org.libertya.ws.bean.result.CustomServiceResultBean;
import org.libertya.ws.bean.result.DocumentResultBean;
import org.libertya.ws.bean.result.InvoiceResultBean;
import org.libertya.ws.bean.result.MultipleDocumentsResultBean;
import org.libertya.ws.bean.result.MultipleRecordsResultBean;
import org.libertya.ws.bean.result.ResultBean;
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
import org.libertya.ws.handler.ProductCRUDHandler;
import org.libertya.ws.handler.ProductPriceCRUDHandler;
import org.libertya.ws.handler.ProductionOrderDocumentHandler;
import org.libertya.ws.handler.UserCRUDHandler;
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

public class LibertyaWSEImpl implements LibertyaWSE {

	/* ================================================================== */
	/* ==================== Entidades Comerciales ======================= */
	/* ================================================================== */
	
	public SimpleResult bPartnerCreate(Login login, SimpleMap[] data, SimpleMap[] location) {
		BPartnerParameterBean bean = new BPartnerParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, location);
		ResultBean resultBean = new BPartnerCRUDHandler().bPartnerCreate(bean);
		return new SimpleResult(resultBean);
	}

	public BPartnerResult bPartnerRetrieveByID(Login login, int bPartnerID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		BPartnerResultBean resultBean = new BPartnerCRUDHandler().bPartnerRetrieveByID(bean, bPartnerID);
		return new BPartnerResult(resultBean);
	}

	public BPartnerResult bPartnerRetrieveByValue(Login login, String value) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		BPartnerResultBean resultBean = new BPartnerCRUDHandler().bPartnerRetrieveByValue(bean, value);
		return new BPartnerResult(resultBean);
	}

	public BPartnerResult bPartnerRetrieveByTaxID(Login login, String taxID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		BPartnerResultBean resultBean = new BPartnerCRUDHandler().bPartnerRetrieveByTaxID(bean, taxID);
		return new BPartnerResult(resultBean);
	}

	public SimpleResult bPartnerUpdate(Login login, SimpleMap[] data, SimpleMap[] location, int bPartnerID, int bPartnerLocationID) {
		BPartnerParameterBean bean = new BPartnerParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, location);
		ResultBean resultBean = new BPartnerCRUDHandler().bPartnerUpdate(bean, bPartnerID, bPartnerLocationID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult bPartnerDelete(Login login, int bPartnerID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new BPartnerCRUDHandler().bPartnerDelete(bean, bPartnerID);
		return new SimpleResult(resultBean);
	}
	
	/* ===================================================== */
	/* ==================== Facturas ======================= */
	/* ===================================================== */
	
	public SimpleResult invoiceCreateCustomer(Login login, SimpleMap[] data, DocumentLine[] lines, DocumentLine[] otherTaxes, int bPartnerID, String bPartnerValue, String taxID, boolean completeDocument) {
		InvoiceParameterBean bean = new InvoiceParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, lines, otherTaxes);
		ResultBean resultBean = new InvoiceDocumentHandler().invoiceCreateCustomer(bean, bPartnerID, bPartnerValue, taxID, completeDocument);
		return new SimpleResult(resultBean);
	}

	public SimpleResult invoiceCreateCustomerFromOrderByID(Login login, SimpleMap[] data, DocumentLine[] lines, DocumentLine[] otherTaxes, int orderID, boolean completeDocument) {
		InvoiceParameterBean bean = new InvoiceParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, lines, otherTaxes);
		ResultBean resultBean = new InvoiceDocumentHandler().invoiceCreateCustomerFromOrderByID(bean, orderID, completeDocument);
		return new SimpleResult(resultBean);
	}

	public SimpleResult invoiceCreateCustomerFromOrderByColumn(Login login, SimpleMap[] data, DocumentLine[] lines, DocumentLine[] otherTaxes, String searchColumn, String searchCriteria, boolean completeDocument) {
		InvoiceParameterBean bean = new InvoiceParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, lines, otherTaxes);
		ResultBean resultBean = new InvoiceDocumentHandler().invoiceCreateCustomerFromOrderByColumn(bean, searchColumn, searchCriteria, completeDocument);
		return new SimpleResult(resultBean);
	}

	public SimpleResult invoiceDeleteByID(Login login, int invoiceID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new InvoiceDocumentHandler().invoiceDeleteByID(bean, invoiceID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult invoiceDeleteByColumn(Login login, String columnName, String columnCriteria) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new InvoiceDocumentHandler().invoiceDeleteByColumn(bean, columnName, columnCriteria);
		return new SimpleResult(resultBean);

	}

	public SimpleResult invoiceCompleteByID(Login login, int invoiceID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new InvoiceDocumentHandler().invoiceCompleteByID(bean, invoiceID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult invoiceCompleteByColumn(Login login, String columnName, String columnCriteria) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new InvoiceDocumentHandler().invoiceCompleteByColumn(bean, columnName, columnCriteria);
		return new SimpleResult(resultBean);
	}

	public SimpleResult invoiceVoidByID(Login login, int invoiceID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new InvoiceDocumentHandler().invoiceVoidByID(bean, invoiceID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult invoiceVoidByColumn(Login login, String columnName, String columnCriteria) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new InvoiceDocumentHandler().invoiceVoidByColumn(bean, columnName, columnCriteria);
		return new SimpleResult(resultBean);
	}

	public SimpleResult invoiceUpdateByID(Login login, SimpleMap[] data, int invoiceID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new InvoiceDocumentHandler().invoiceUpdateByID(bean, invoiceID);
		return new SimpleResult(resultBean);
	}
	
	
	/* ===================================================== */
	/* ==================== Artículos ====================== */
	/* ===================================================== */
	
	public SimpleResult productCreate(Login login, SimpleMap[] data) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new ProductCRUDHandler().productCreate(bean);
		return new SimpleResult(resultBean);
	}

	public SimpleResult productCreate(Login login, SimpleMap[] data, boolean createDefaultProductPrice) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new ProductCRUDHandler().productCreate(bean, createDefaultProductPrice);
		return new SimpleResult(resultBean);
	}

	public SimpleResult productRetrieveByID(Login login, int productID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new ProductCRUDHandler().productRetrieveByID(bean, productID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult productRetrieveByValue(Login login, String value) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new ProductCRUDHandler().productRetrieveByValue(bean, value);
		return new SimpleResult(resultBean);
	}

	public SimpleResult productUpdateByID(Login login, SimpleMap[] data, int productID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new ProductCRUDHandler().productUpdateByID(bean, productID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult productUpdateByValue(Login login, SimpleMap[] data, String value) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new ProductCRUDHandler().productUpdateByValue(bean, value);
		return new SimpleResult(resultBean);	
	}

	public SimpleResult productDelete(Login login, int productID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new ProductCRUDHandler().productDelete(bean, productID);
		return new SimpleResult(resultBean);
	}
	
	/* ===================================================== */
	/* ============= Consulta de Comprobantes ============== */
	/* ===================================================== */
	
	public DocumentResult documentRetrieveOrderByID(Login login, int orderID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		DocumentResultBean resultBean = new DocumentQueryHandler().documentRetrieveOrderByID(bean, orderID);
		return new DocumentResult(resultBean);		
	}

	public DocumentResult documentRetrieveOrderByColumn(Login login, String column, String value) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		DocumentResultBean resultBean = new DocumentQueryHandler().documentRetrieveOrderByColumn(bean, column, value);
		return new DocumentResult(resultBean);		
	}

	public InvoiceResult documentRetrieveInvoiceByID(Login login, int invoiceID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		InvoiceResultBean resultBean = new DocumentQueryHandler().documentRetrieveInvoiceByID(bean, invoiceID);
		return new InvoiceResult(resultBean);		
	}

	public InvoiceResult documentRetrieveInvoiceByColumn(Login login, String column, String value) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		InvoiceResultBean resultBean = new DocumentQueryHandler().documentRetrieveInvoiceByColumn(bean, column, value);
		return new InvoiceResult(resultBean);	
	}

	public DocumentResult documentRetrieveInOutByID(Login login, int inoutID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		DocumentResultBean resultBean = new DocumentQueryHandler().documentRetrieveInOutByID(bean, inoutID);
		return new DocumentResult(resultBean);		
	}

	public DocumentResult documentRetrieveInOutByColumn(Login login, String column, String value) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		DocumentResultBean resultBean = new DocumentQueryHandler().documentRetrieveInOutByColumn(bean, column, value);
		return new DocumentResult(resultBean);		
	}

	public DocumentResult documentRetrieveAllocationByID(Login login, int allocationID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		DocumentResultBean resultBean = new DocumentQueryHandler().documentRetrieveAllocationByID(bean, allocationID);
		return new DocumentResult(resultBean);		
	}

	public DocumentResult documentRetrieveAllocationByColumn(Login login, String column, String value) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		DocumentResultBean resultBean = new DocumentQueryHandler().documentRetrieveAllocationByColumn(bean, column, value);
		return new DocumentResult(resultBean);		
	}

	public MultipleRecordsResult documentQueryOrders(Login login, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		MultipleDocumentsResultBean resultBean = new DocumentQueryHandler().documentQueryOrders(bean, bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate);
		return new MultipleRecordsResult(resultBean);		
	}

	public MultipleRecordsResult documentQueryInvoices(Login login, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly,	String fromDate, String toDate, String additionalWhereClause) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		MultipleDocumentsResultBean resultBean = new DocumentQueryHandler().documentQueryInvoices(bean, bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate, additionalWhereClause);
		return new MultipleRecordsResult(resultBean);	
	}

	public MultipleRecordsResult documentQueryInOuts(Login login, int bPartnerID, String value, String taxID, boolean filterByClient,	boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly,	String fromDate, String toDate) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		MultipleDocumentsResultBean resultBean = new DocumentQueryHandler().documentQueryInOuts(bean, bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate);
		return new MultipleRecordsResult(resultBean);	
	}

	public MultipleRecordsResult documentQueryAllocations(Login login, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		MultipleDocumentsResultBean resultBean = new DocumentQueryHandler().documentQueryAllocations(bean, bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate);
		return new MultipleRecordsResult(resultBean);
	}
	
	/* ===================================================== */
	/* ===================== Pedidos ======================= */
	/* ===================================================== */
	
	public SimpleResult orderCreateCustomer(Login login, SimpleMap[] data, DocumentLine[] lines, int bPartnerID, String bPartnerValue, String taxID, boolean completeOrder, boolean createInvoice, boolean completeInvoice, int invoiceDocTypeTargetID, int invoicePuntoDeVenta, String invoiceTipoComprobante) {
		OrderParameterBean bean = new OrderParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, lines, invoiceDocTypeTargetID, invoicePuntoDeVenta, invoiceTipoComprobante);
		ResultBean resultBean = new OrderDocumentHandler().orderCreateCustomer(bean, bPartnerID, bPartnerValue, taxID, completeOrder, createInvoice, completeInvoice, false, false);
		return new SimpleResult(resultBean);
	}

	public SimpleResult orderCreateCustomer(Login login, SimpleMap[] data, DocumentLine[] lines, int bPartnerID, String bPartnerValue, String taxID, boolean completeOrder, boolean createInvoice, boolean completeInvoice, int invoiceDocTypeTargetID, int invoicePuntoDeVenta, String invoiceTipoComprobante, boolean createShipment, boolean completeShipment) {
		OrderParameterBean bean = new OrderParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, lines, invoiceDocTypeTargetID, invoicePuntoDeVenta, invoiceTipoComprobante);
		ResultBean resultBean = new OrderDocumentHandler().orderCreateCustomer(bean, bPartnerID, bPartnerValue, taxID, completeOrder, createInvoice, completeInvoice, createShipment, createShipment);
		return new SimpleResult(resultBean);
	}

	public SimpleResult orderCreateVendor(Login login, SimpleMap[] data, DocumentLine[] lines, int bPartnerID, String bPartnerValue, String taxID, boolean completeOrder, boolean createInvoice, boolean completeInvoice, int invoiceDocTypeTargetID, int invoicePuntoDeVenta, String invoiceTipoComprobante) {
		OrderParameterBean bean = new OrderParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, lines, invoiceDocTypeTargetID, invoicePuntoDeVenta, invoiceTipoComprobante);
		ResultBean resultBean = new OrderDocumentHandler().orderCreateVendor(bean, bPartnerID, bPartnerValue, taxID, completeOrder, createInvoice, completeInvoice);
		return new SimpleResult(resultBean);
	}

	public SimpleResult orderDeleteByID(Login login, int orderID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new OrderDocumentHandler().orderDeleteByID(bean, orderID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult orderDeleteByColumn(Login login, String columnName, String columnCriteria) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new OrderDocumentHandler().orderDeleteByColumn(bean, columnName, columnCriteria);
		return new SimpleResult(resultBean);
	}

	public SimpleResult orderCompleteByID(Login login, int orderID, boolean createInvoice, boolean completeInvoice, int invoiceDocTypeTargetID, int invoicePuntoDeVenta, String invoiceTipoComprobante) {
		OrderParameterBean bean = new OrderParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), null, null, invoiceDocTypeTargetID, invoicePuntoDeVenta, invoiceTipoComprobante);
		ResultBean resultBean = new OrderDocumentHandler().orderCompleteByID(bean, orderID, createInvoice, completeInvoice, false, false);
		return new SimpleResult(resultBean);
	}

	public SimpleResult orderCompleteByID(Login login, int orderID, boolean createInvoice, boolean completeInvoice, int invoiceDocTypeTargetID, int invoicePuntoDeVenta, String invoiceTipoComprobante, boolean createShipment, boolean completeShipment) {
		OrderParameterBean bean = new OrderParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), null, null, invoiceDocTypeTargetID, invoicePuntoDeVenta, invoiceTipoComprobante);
		ResultBean resultBean = new OrderDocumentHandler().orderCompleteByID(bean, orderID, createInvoice, completeInvoice, createShipment, completeShipment);
		return new SimpleResult(resultBean);
	}

	public SimpleResult orderCompleteByColumn(Login login, String columnName, String columnCriteria, boolean createInvoice, boolean completeInvoice, int invoiceDocTypeTargetID, int invoicePuntoDeVenta, String invoiceTipoComprobante) {
		OrderParameterBean bean = new OrderParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), null, null, invoiceDocTypeTargetID, invoicePuntoDeVenta, invoiceTipoComprobante);
		ResultBean resultBean = new OrderDocumentHandler().orderCompleteByColumn(bean, columnName, columnCriteria, createInvoice, completeInvoice, false, false);
		return new SimpleResult(resultBean);
	}

	public SimpleResult orderCompleteByColumn(Login login, String columnName, String columnCriteria, boolean createInvoice, boolean completeInvoice, int invoiceDocTypeTargetID, int invoicePuntoDeVenta, String invoiceTipoComprobante, boolean createShipment, boolean completeShipment) {
		OrderParameterBean bean = new OrderParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), null, null, invoiceDocTypeTargetID, invoicePuntoDeVenta, invoiceTipoComprobante);
		ResultBean resultBean = new OrderDocumentHandler().orderCompleteByColumn(bean, columnName, columnCriteria, createInvoice, completeInvoice, createShipment, completeShipment);
		return new SimpleResult(resultBean);
	}

	public SimpleResult orderVoidByID(Login login, int orderID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new OrderDocumentHandler().orderVoidByID(bean, orderID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult orderVoidByColumn(Login login, String columnName, String columnCriteria) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new OrderDocumentHandler().orderVoidByColumn(bean, columnName, columnCriteria);
		return new SimpleResult(resultBean);
	}

	public SimpleResult orderUpdateByID(Login login, SimpleMap[] data, int orderID, boolean completeOrder) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new OrderDocumentHandler().orderUpdateByID(bean, orderID, completeOrder);
		return new SimpleResult(resultBean);
	}

	public SimpleResult orderUpdateByColumn(Login login, SimpleMap[] data, String columnName, String columnCriteria, boolean completeOrder) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new OrderDocumentHandler().orderUpdateByColumn(bean, columnName, columnCriteria, completeOrder);
		return new SimpleResult(resultBean);
	}
	
	/* ===================================================== */
	/* ===================== Remitos ======================= */
	/* ===================================================== */

	public SimpleResult inOutCreateCustomer(Login login, SimpleMap[] header, DocumentLine[] lines, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), header, lines);
		ResultBean resultBean = new InOutDocumentHandler().inOutCreateCustomer(bean, bPartnerID, bPartnerValue, taxID, completeInOut);
		return new SimpleResult(resultBean);
	}

	public SimpleResult inOutCreateVendor(Login login, SimpleMap[] header, DocumentLine[] lines, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), header, lines);
		ResultBean resultBean = new InOutDocumentHandler().inOutCreateVendor(bean, bPartnerID, bPartnerValue, taxID, completeInOut);
		return new SimpleResult(resultBean);
	}

	public SimpleResult inOutDeleteByID(Login login, SimpleMap[] data, int inOutID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new InOutDocumentHandler().inOutDeleteByID(bean, inOutID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult inOutDeleteByColumn(Login login, SimpleMap[] data, String columnName, String columnCriteria) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new InOutDocumentHandler().inOutDeleteByColumn(bean, columnName, columnCriteria);
		return new SimpleResult(resultBean);
	}

	public SimpleResult inOutCompleteByID(Login login, SimpleMap[] data, int inOutID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new InOutDocumentHandler().inOutCompleteByID(bean, inOutID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult inOutCompleteByColumn(Login login, SimpleMap[] data, String columnName, String columnCriteria) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InOutDocumentHandler().inOutCompleteByColumn(bean, columnName, columnCriteria);
		return new SimpleResult(resultBean);
	}

	public SimpleResult inOutVoidByID(Login login, SimpleMap[] data, int inOutID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new InOutDocumentHandler().inOutVoidByID(bean, inOutID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult inOutVoidByColumn(Login login, SimpleMap[] data, String columnName, String columnCriteria) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new InOutDocumentHandler().inOutVoidByColumn(bean, columnName, columnCriteria);
		return new SimpleResult(resultBean);
	}
	
	/* ================================================================== */
	/* ================== Saldos Entidades Comerciales ================== */
	/* ================================================================== */
	
	public SimpleResult bPartnerBalanceSumOrdersNotInvoiced(Login login, SimpleMap[] data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new BPartnerBalanceHandler().bPartnerBalanceSumOrdersNotInvoiced(bean, bPartnerID, bPartnerList, cuit, clientID, orgID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult bPartnerBalanceSumInvoices(Login login, SimpleMap[] data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new BPartnerBalanceHandler().bPartnerBalanceSumInvoices(bean, bPartnerID, bPartnerList, cuit, clientID, orgID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult bPartnerBalanceSumPayments(Login login, SimpleMap[] data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new BPartnerBalanceHandler().bPartnerBalanceSumPayments(bean, bPartnerID, bPartnerList, cuit, clientID, orgID);
		return new SimpleResult(resultBean);
	}
	
	public SimpleResult bPartnerBalanceSumChecks(Login login, SimpleMap[] data, int bPartnerID, int[] bPartnerList, String cuit, int clientID, int orgID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new BPartnerBalanceHandler().bPartnerBalanceSumChecks(bean, bPartnerID, bPartnerList, cuit, clientID, orgID);
		return new SimpleResult(resultBean);
	}

	/* ================================================================== */
	/* ====================== Recibos de clientes ======================= */
	/* ================================================================== */

	public SimpleResult allocationCreateReceipt(Login login, SimpleMap[] data, RecordContent[] invoices, RecordContent[] payments, int bPartnerID, String bPartnerValue, String taxID) {
		AllocationParameterBean bean = new AllocationParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, invoices, payments);
		ResultBean resultBean = new AllocationDocumentHandler().allocationCreateReceipt(bean, bPartnerID, bPartnerValue, taxID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult allocationCreateEarlyReceipt(Login login, SimpleMap[] data, RecordContent[] payments, int bPartnerID, String bPartnerValue, String taxID) {
		AllocationParameterBean bean = new AllocationParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null, payments);
		ResultBean resultBean = new AllocationDocumentHandler().allocationCreateReceipt(bean, bPartnerID, bPartnerValue, taxID);
		return new SimpleResult(resultBean);	}

	public SimpleResult allocationVoidByID(Login login, int allocationID, String allocationAction) {
		AllocationParameterBean bean = new AllocationParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new AllocationDocumentHandler().allocationVoidByID(bean, allocationID, allocationAction);
		return new SimpleResult(resultBean);
	}	
	
	/* ================================================================== */
	/* =========================== Usuarios ============================= */
	/* ================================================================== */
	
	public SimpleResult userCreate(Login login, SimpleMap[] data) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new UserCRUDHandler().userCreate(bean);
		return new SimpleResult(resultBean);
	}

	public SimpleResult userRetrieveByID(Login login, int userID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new UserCRUDHandler().userRetrieveByID(bean, userID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult userUpdateByID(Login login, SimpleMap[] data, int userID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new UserCRUDHandler().userUpdateByID(bean, userID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult userDeleteByID(Login login, int userID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new UserCRUDHandler().userDeleteByID(bean, userID);
		return new SimpleResult(resultBean);
	}


	/* ================================================================== */
	/* ========================= Inventario ============================= */
	/* ================================================================== */

	public SimpleResult inventoryCreate(Login login, SimpleMap[] header, DocumentLine[] lines, boolean completeInventory) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), header, lines);
		ResultBean resultBean = new InventoryDocumentHandler().inventoryCreate(bean, completeInventory);
		return new SimpleResult(resultBean);
	}

	public SimpleResult inventoryCompleteByID(Login login, SimpleMap[] data, int inventoryID) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InventoryDocumentHandler().inventoryCompleteByID(bean, inventoryID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult inventoryCompleteByColumn(Login login, SimpleMap[] data, String columnName, String value) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InventoryDocumentHandler().inventoryCompleteByColumn(bean, columnName, value);
		return new SimpleResult(resultBean);
	}

	public SimpleResult inventoryDeleteByID(Login login, SimpleMap[] data, int inventoryID) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InventoryDocumentHandler().inventoryDeleteByID(bean, inventoryID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult inventoryDeleteByColumn(Login login, SimpleMap[] data, String columnName, String value) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InventoryDocumentHandler().inventoryDeleteByColumn(bean, columnName, value);
		return new SimpleResult(resultBean);
	}

	public SimpleResult inventoryVoidByID(Login login, SimpleMap[] data, int inventoryID) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InventoryDocumentHandler().inventoryVoidByID(bean, inventoryID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult inventoryVoidByColumn(Login login, SimpleMap[] data, String columnName, String value) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InventoryDocumentHandler().inventoryVoidByColumn(bean, columnName, value);
		return new SimpleResult(resultBean);
	}

	/* ================================================================== */
	/* ====================== Direcciones de EC ========================= */
	/* ================================================================== */
	
	public SimpleResult bPartnerLocationCreate(Login login, SimpleMap[] data) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new BPartnerLocationCRUDHandler().bPartnerLocationCreate(bean);
		return new SimpleResult(resultBean);
	}

	public SimpleResult bPartnerLocationUpdate(Login login, SimpleMap[] data, int bPartnerLocationID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new BPartnerLocationCRUDHandler().bPartnerLocationUpdate(bean, bPartnerLocationID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult bPartnerLocationDelete(Login login, int bPartnerLocationID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new BPartnerLocationCRUDHandler().bPartnerLocationDelete(bean, bPartnerLocationID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult bPartnerLocationRetrieve(Login login, int bPartnerLocationID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new BPartnerLocationCRUDHandler().bPartnerLocationRetrieve(bean, bPartnerLocationID);
		return new SimpleResult(resultBean);
	}

	/* ================================================================== */
	/* ===================== Precios de artículos ======================= */
	/* ================================================================== */
	
	public SimpleResult productPriceCreateUpdate(Login login, SimpleMap[] data) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new ProductPriceCRUDHandler().productPriceCreateUpdate(bean);
		return new SimpleResult(resultBean);
	}

	public SimpleResult productPriceDelete(Login login, int productID, int priceListVersionID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new ProductPriceCRUDHandler().productPriceDelete(bean, productID, priceListVersionID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult productPriceRetrieve(Login login, int productID, int priceListVersionID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new ProductPriceCRUDHandler().productPriceRetrieve(bean, productID, priceListVersionID);
		return new SimpleResult(resultBean);
	}

	/* ================================================================== */
	/* ===================== Ordenes de Producción ====================== */
	/* ================================================================== */

	public SimpleResult productionOrderCreate(Login login, SimpleMap[] data, DocumentLine[] lines, boolean completeProductionOrder) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, lines);
		ResultBean resultBean = new ProductionOrderDocumentHandler().productionOrderCreate(bean, completeProductionOrder);
		return new SimpleResult(resultBean);
	}

	public SimpleResult productionOrderDelete(Login login, int productionOrderID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new ProductionOrderDocumentHandler().productionOrderDelete(bean, productionOrderID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult productionOrderComplete(Login login, int productionOrderID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new ProductionOrderDocumentHandler().productionOrderComplete(bean, productionOrderID);
		return new SimpleResult(resultBean);
	}

	public SimpleResult productionOrderVoid(Login login, int productionOrderID) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new ProductionOrderDocumentHandler().productionOrderVoid(bean, productionOrderID);
		return new SimpleResult(resultBean);
	}
	
	/* ================================================================== */
	/* ====================== Boletas de depósito ======================= */
	/* ================================================================== */

	public SimpleResult depositSlipCreate(Login login, SimpleMap[] data, DocumentLine[] lines, boolean completeDepositSlip) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, lines);
		ResultBean resultBean = new DepositSlipDocumentHandler().depositSlipCreate(bean, completeDepositSlip);
		return new SimpleResult(resultBean);
	}

	public SimpleResult depositSlipDelete(Login login, int depositSlipID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new DepositSlipDocumentHandler().depositSlipDelete(bean, depositSlipID);
		return new SimpleResult(resultBean);		
	}

	public SimpleResult depositSlipComplete(Login login, int depositSlipID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new DepositSlipDocumentHandler().depositSlipComplete(bean, depositSlipID);
		return new SimpleResult(resultBean);		
	}

	public SimpleResult depositSlipVoid(Login login, int depositSlipID) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new DepositSlipDocumentHandler().depositSlipVoid(bean, depositSlipID);
		return new SimpleResult(resultBean);		
	}

	/* ================================================================== */
	/* ====================== Lista de materiales ======================= */
	/* ================================================================== */
	
	public SimpleResult billOfMaterialCreate(Login login, SimpleMap[] data) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		ResultBean resultBean = new BillOfMaterialCRUDHandler().billOfMaterialCreate(bean);
		return new SimpleResult(resultBean);
	}

	public SimpleResult billOfMaterialDelete(Login login, int productBOMId) {
		ParameterBean bean = new ParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID());
		ResultBean resultBean = new BillOfMaterialCRUDHandler().billOfMaterialDelete(bean, productBOMId);
		return new SimpleResult(resultBean);
	}
	
	/* ================================================================== */
	/* ==================== Funciones de uso general ==================== */
	/* ================================================================== */

	public Result customService(Login login, String className, ListedMap[] data) {
		CustomServiceParameterBean bean = new CustomServiceParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, className);
		CustomServiceResultBean resultBean = new CustomServiceHandler().customService(bean);
		return new Result(resultBean);
	}

	public MultipleRecordsResult recordQuery(Login login, String[] data, String tableName, String whereClause, boolean includeNamedReferences) {
		FilteredColumnsParameterBean bean = new FilteredColumnsParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		MultipleRecordsResultBean resultBean = new GeneralRecordQueryHandler().recordQuery(bean, tableName, whereClause, includeNamedReferences); 
		return new MultipleRecordsResult(resultBean);
	}




	

}
