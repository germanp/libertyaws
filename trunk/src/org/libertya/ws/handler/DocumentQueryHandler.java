package org.libertya.ws.handler;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.DocumentResultBean;
import org.libertya.ws.bean.result.InvoiceResultBean;
import org.libertya.ws.bean.result.MultipleDocumentsResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_C_BPartner_Location;
import org.openXpertya.model.X_C_Currency;
import org.openXpertya.model.X_C_DocType;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_PaymentTerm;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class DocumentQueryHandler extends GeneralHandler {

	/** Numero máximo de registros a devolver en una consulta */
	private static final int MAX_RECORDS = 1000;  
	
	/**
	 * Recupera el detalle de un pedido a partir de su ID
	 */
	public DocumentResultBean documentRetrieveOrderByID(ParameterBean data, int orderID) {
		return documentRetrieve(MOrder.Table_Name, data, orderID, null, null);
	}

	/**
	 * Recupera el detalle de un pedido a partir de indicar una columna y un valor de busqueda
	 */
	public DocumentResultBean documentRetrieveOrderByColumn(ParameterBean data, String column, String value) {
		return documentRetrieve(MOrder.Table_Name, data, -1, column, value);
	}
	
	/**
	 * Recupera el detalle de una factura a partir de su ID
	 */
	public InvoiceResultBean documentRetrieveInvoiceByID(ParameterBean data, int invoiceID) {
		return (InvoiceResultBean)documentRetrieve(MInvoice.Table_Name, data, invoiceID, null, null);
	}

	/**
	 * Recupera el detalle de una factura a partir de indicar una columna y un valor de busqueda
	 */
	public InvoiceResultBean documentRetrieveInvoiceByColumn(ParameterBean data, String column, String value) {
		return (InvoiceResultBean)documentRetrieve(MInvoice.Table_Name, data, -1, column, value);
	}
	
	/**
	 * Recupera el detalle de un remito a partir de su ID
	 */
	public DocumentResultBean documentRetrieveInOutByID(ParameterBean data, int inoutID) {
		return documentRetrieve(MInOut.Table_Name, data, inoutID, null, null);
	}

	/**
	 * Recupera el detalle de un remito a partir de indicar una columna y un valor de busqueda
	 */
	public DocumentResultBean documentRetrieveInOutByColumn(ParameterBean data, String column, String value) {
		return documentRetrieve(MInOut.Table_Name, data, -1, column, value);
	}
	
	/**
	 * Recupera el detalle de un recibo a partir de su ID
	 */
	public DocumentResultBean documentRetrieveAllocationByID(ParameterBean data, int allocationID) {
		return documentRetrieve(MAllocationHdr.Table_Name, data, allocationID, null, null);
	}

	/**
	 * Recupera el detalle de un recibo a partir de indicar una columna y un valor de busqueda
	 */
	public DocumentResultBean documentRetrieveAllocationByColumn(ParameterBean data, String column, String value) {
		return documentRetrieve(MAllocationHdr.Table_Name, data, -1, column, value);
	}
	
	
	/**
	 * Devuelve un Documento en conjunto con el detalle de sus lineas 
	 * @param tableName Nombre de tabla (C_Order, C_Invoice, M_InOut, C_AllocationHdr)
	 * @param data parametros generales de acceso
	 * @param docID número de documento a recuperar (o -1 si no se desea buscar por esta opción)
	 * @param column columna a utilizar como criterio de busqueda
	 * @param value valor a utilizar para realizar la búsqueda sobre la columna indicada
	 * @return DocumentResultBean con OK, ERROR, los datos del encabezado y sus lineas
	 */
	protected DocumentResultBean documentRetrieve(String tableName, ParameterBean data, int docID, String column, String value) 
	{
		try
		{	
			/* === Configuracion inicial === */
			init(data, new String[]{"docID", "column", "value"}, new Object[]{docID, column, value});
			
			/* === Procesar (logica especifica) === */
			// Recuperar el PO (si existe) por algún criterio. 
			// 1) Buscar por ID o por value (obtener null si no se encuentra)
			PO aPO = getPO(tableName, docID, column, value, false, false, true, false);
			if (aPO == null || aPO.getID()==0)
				throw new ModelException("No se ha podido recuperar el documento (" + tableName + ") a partir de los parametros indicados");

			// Generar valores de encabezado
			DocumentResultBean result = null;
			if (MInvoice.Table_Name.equals(tableName)) {
				// En caso de ser facturas, incorporar información adicional en las cabeceras
				result = new InvoiceResultBean(false, null, poToMap(aPO, true));
				appendAditionalInvoiceInfoToMap(aPO, result.getMainResult());
				appendInvoiceTaxesInfoToMap(aPO, ((InvoiceResultBean)result));
			}
			else
				result = new DocumentResultBean(false, null, poToMap(aPO, true));
			
			// Obtener las lineas
			PO[] docLines = null;
			if (MOrder.Table_Name.equalsIgnoreCase(tableName))
				docLines = ((MOrder)aPO).getLines();
			else if (MInvoice.Table_Name.equalsIgnoreCase(tableName))
				docLines = ((MInvoice)aPO).getLines();
			else if (MInOut.Table_Name.equalsIgnoreCase(tableName))
				docLines = ((MInOut)aPO).getLines();
			else if (MAllocationHdr.Table_Name.equalsIgnoreCase(tableName))
				docLines = ((MAllocationHdr)aPO).getLines(false);
			// Cargar lineas
			for (PO docLine : docLines) {
				HashMap<String, String> map = poToMap(docLine, true);
				// En caso de ser facturas, incorporar info adicional en las lineas
				if (MInvoice.Table_Name.equals(tableName))
					appendAditionalInvoiceLineInfoToMap(docLine, map);
				result.addDocumentLine(map);
			}
						
			/* === Retornar valores === */
			return result;
		}
		catch (ModelException me) {
			if (MInvoice.Table_Name.equals(tableName))
				return (InvoiceResultBean)processException(me, new InvoiceResultBean(), wsInvocationArguments(data));
			return (DocumentResultBean)processException(me, new DocumentResultBean(), wsInvocationArguments(data));
			
		}
		catch (Exception e) {
			if (MInvoice.Table_Name.equals(tableName))
				return (InvoiceResultBean)processException(e, new InvoiceResultBean(), wsInvocationArguments(data));
			return (DocumentResultBean)processException(e, new DocumentResultBean(), wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}

	
	/**
	 * @return retorna una serie de cabeceras de pedidos a partir de los parametros de entrada 
	 */
	public MultipleDocumentsResultBean documentQueryOrders(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate) {
		return documentQuery(MOrder.Table_Name, data, bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate, null);
	}

	/**
	 * @return retorna una serie de cabeceras de facturas a partir de los parametros de entrada 
	 */
	public MultipleDocumentsResultBean documentQueryInvoices(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate, String additionalWhereClause) {
		return documentQuery(MInvoice.Table_Name, data, bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate, additionalWhereClause);
	}

	/**
	 * @return retorna una serie de cabeceras de remitos a partir de los parametros de entrada 
	 */
	public MultipleDocumentsResultBean documentQueryInOuts(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate) {
		return documentQuery(MInOut.Table_Name, data, bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate, null);
	}

	/**
	 * @return retorna una serie de cabeceras de OP/RC a partir de los parametros de entrada 
	 */
	public MultipleDocumentsResultBean documentQueryAllocations(ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate) {
		return documentQuery(MAllocationHdr.Table_Name, data, bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate, null);
	}

	
	/**
	 * Devuelve una serie de Documentos que coincida con el criterio de busqueda indicado
	 * @param tableName Nombre de tabla (C_Order, C_Invoice, M_InOut, C_AllocationHdr)
	 * @param data parametros generales de acceso
	 * @param bPartnerID primer criterio de búsqueda.  Todos los documentos de la E.C. indicada por su ID 
	 * @param value segundo criterio de búsqueda.  Todos los documentos de la E.C. indicada por su Value
	 * @param taxID tercer criterio de búsquedda.  Todos los documentos de la E.C. indicada por su CUIT
	 * @param filterByClient optativo. filtrar los resultados por la compañía a la cual estamos logueados
	 * @param filterByOrg  optativo. filtrar los resultados por la organización a la cual estamos logueados
	 * @param purchaseTrxOnly  si se recibe como true, filtra por IsSoTrx = 'N' (o sea solo transacciones de compra)
	 * @param salesTrxOnly  si se recibe como true, filtra por IsSoTrx = 'Y' (o sea solo transacciones de venta)
	 * @param fromDate  optativo. filtrar por fecha de inicio a los resultados
	 * @param toDate  optativo. filtrar por fecha de fin a los resultados
	 * @return MultipleDocumentsResultBean con OK, ERROR, los encabezados.
	 */
	protected MultipleDocumentsResultBean documentQuery(String tableName, ParameterBean data, int bPartnerID, String value, String taxID, boolean filterByClient, boolean filterByOrg, boolean purchaseTrxOnly, boolean salesTrxOnly, String fromDate, String toDate, String additionalWhereClause) 
	{
		try
		{	
			/* === Configuracion inicial === */
			init(data, 	new String[]{"bPartnerID", "value", "taxID", "filterByClient", "filterByOrg", "purchaseTrxOnly", "salesTrxOnly", "fromDate", "toDate", "additionalWhereClause"}, 
						new Object[]{bPartnerID, value, taxID, filterByClient, filterByOrg, purchaseTrxOnly, salesTrxOnly, fromDate, toDate, additionalWhereClause});
			
			/* === Procesar (logica especifica) === */
			// Recuperar los POs (si existen) por algún criterio obligatorio 
			StringBuffer whereClause = new StringBuffer("");
			if (bPartnerID>0)
				whereClause.append(" C_BPartner_ID = " + bPartnerID);
			else if(value!=null && value.length()>0) {
				int bpID = DB.getSQLValue(getTrxName(), " SELECT C_BPartner_ID FROM C_BPartner WHERE value = ? AND AD_Client_ID = " + Env.getAD_Client_ID(getCtx()), value);
				whereClause.append(" C_BPartner_ID = " + bpID);
			}
			else if(taxID!=null && taxID.length()>0) {
				int bpID = DB.getSQLValue(getTrxName(), " SELECT C_BPartner_ID FROM C_BPartner WHERE taxID = ? AND AD_Client_ID = " + Env.getAD_Client_ID(getCtx()), taxID);
				whereClause.append(" C_BPartner_ID = " + bpID);
			}
            else {
            	whereClause.append(" 1 = 1 ");
	        }

			// Filtros optativos
			if (filterByClient)
				whereClause.append(" AND AD_Client_ID = '" + Env.getAD_Client_ID(getCtx()) + "'");
			if (filterByOrg)
				whereClause.append(" AND AD_Org_ID = '" + Env.getAD_Org_ID(getCtx()) + "'");
			if (fromDate!=null)
				whereClause.append(" AND " + getDateColumn(tableName) + " >= '" + fromDate + "'");
			if (toDate!=null)
				whereClause.append(" AND " + getDateColumn(tableName) + " <= '" + toDate + "'");

			// Filtrar solo documentos de compra o venta
			if (purchaseTrxOnly && salesTrxOnly)
				throw new ModelException("No puede filtrar por 'Solo transacciones de compra' y 'Solo transacciones de venta' a la vez");
			if (purchaseTrxOnly)
				whereClause.append(" AND IsSoTrx = 'N'");
			if (salesTrxOnly)
				whereClause.append(" AND IsSoTrx = 'Y'");
			// Incorporar filtros adicionales indicados como parámetro
			if (additionalWhereClause!=null && additionalWhereClause.trim().length()>0)
				whereClause.append(" AND ").append(additionalWhereClause);
				
			// Recuperar la nómina de ID de documentos según los criterios de búsqueda especificados
			int[] documentIDs = PO.getAllIDs(tableName, whereClause.toString(), getTrxName());
			if (documentIDs!=null && documentIDs.length > MAX_RECORDS)
				throw new ModelException("Los criterios especificados arrojan demasiados resultados (" + documentIDs.length + ").  Por favor refine su búsqueda.");
			if (documentIDs==null || documentIDs.length==0) 
				throw new ModelException("No se han recuperado documentos con los criterios de búsqueda especificados");
			
			// Generar valores de encabezado.  Iterar por cada ID, instanciar el PO y convertir a map
			MultipleDocumentsResultBean result = new MultipleDocumentsResultBean(false, null, new HashMap<String, String>());
			for (int documentID : documentIDs) {
				PO aDocument = getPO(tableName, documentID, null, null, false, false, false, false);
				HashMap<String, String> map = poToMap(aDocument, true);
				// Para la tabla de facturas, incorporar informacion adicional
				if (MInvoice.Table_Name.equals(tableName))
					appendAditionalInvoiceInfoToMap(aDocument, map);
				result.addDocumentHeader(map);
			}
			
			/* === Retornar valores === */
			return result;
		}
		catch (ModelException me) {
			return (MultipleDocumentsResultBean)processException(me, new MultipleDocumentsResultBean(), wsInvocationArguments(data));
		}
		catch (Exception e) {
			return (MultipleDocumentsResultBean)processException(e, new MultipleDocumentsResultBean(), wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}
	
	/**
	 * Devuelve la columna relacionana con la fecha del documento
	 * para cada una de las tablas de documentos.  Si se recibe
	 * cualquier otro nombre de tabla, se devuelve el campo created
	 * @param tableName nombre de la tabla de documento
	 * @return el campo relacionado con la fecha
	 */
	protected String getDateColumn(String tableName) {
		if (MInvoice.Table_Name.equalsIgnoreCase(tableName))
			return "dateinvoiced";
		else if (MOrder.Table_Name.equalsIgnoreCase(tableName))
			return "dateordered";
		else if (MInOut.Table_Name.equalsIgnoreCase(tableName))
			return "movementdate";
		else if (MAllocationHdr.Table_Name.equalsIgnoreCase(tableName))
			return "datetrx";
		else 
			return "created";
	}
	 
	
	/**
	 * Agrega campos adicionales a la map relacionada con el encabezado de una factura
	 * 
	 * Detalle de campos:
	 * 		Tipo de Documento Electrónico (C_DocType) → C_DocType_DocSubTypeCAE
	 * 		Texto a Imprimir (C_DocType) → C_DocType_PrintName
	 * 		Nota de Documento (C_DocType) → C_DocType_DocumentNote
	 * 		Campos de Dirección de Facturación (C_BPartnerLocation) → C_BPartnerLocation_Fax, C_BPartnerLocation_Phone, etc. TODOS.
	 * 		Codigo ISO de Moneda → C_Currency_ISOCode
	 * 		Símbolo de Moneda → C_Currency_CurSymbol
	 * 
	 * @param aDocument factura a tomar como base para buscar los datos
	 * @param map coleccion a incorporarle los nuevos datos
	 */
	protected void appendAditionalInvoiceInfoToMap(PO aDocument, HashMap<String, String> map) {
		// Incorporar datos relacionados con el tipo de documento
		int docTypeID = ((X_C_Invoice)aDocument).getC_DocType_ID();
		int docTypetargetID = ((X_C_Invoice)aDocument).getC_DocTypeTarget_ID();
		X_C_DocType docType = new X_C_DocType(getCtx(), (docTypeID != 0 ? docTypeID : docTypetargetID), getTrxName());
		map.put("C_DocType_DocSubTypeCAE", docType.getdocsubtypecae());
		map.put("C_DocType_PrintName", docType.getPrintName());
		map.put("C_DocType_DocumentNote", docType.getDocumentNote());

		// Incorporar datos relacionados con la dirección de facturación
		int bPartnerLocationID = ((X_C_Invoice)aDocument).getC_BPartner_Location_ID();
		X_C_BPartner_Location bpLocation = new X_C_BPartner_Location(getCtx(), bPartnerLocationID, getTrxName());
		map = poToMap(bpLocation, false, map, "C_BPartnerLocation_", null);
		
		// Incorporar datos relacionados con la moneda
		int currencyID = ((X_C_Invoice)aDocument).getC_Currency_ID();
		X_C_Currency currency = new X_C_Currency(getCtx(), currencyID, getTrxName());
		map.put("C_Currency_ISOCode", currency.getISO_Code());
		map.put("C_Currency_CurSymbol", currency.getCurSymbol());
		
		// Incorporar datos relacionados con el esquema de vencimiento
		int paymentTermID = ((X_C_Invoice)aDocument).getC_PaymentTerm_ID();
		X_C_PaymentTerm paymentTerm = new X_C_PaymentTerm(getCtx(), paymentTermID, getTrxName());
		int payScheduleCount = DB.getSQLValue(getTrxName(), "SELECT COUNT(1) FROM C_PaySchedule WHERE C_PaymentTerm_ID = " + paymentTermID); 
		map.put("C_PaymentTerm_Value", paymentTerm.getValue());
		map.put("C_PaymentTerm_Name", paymentTerm.getName());
		map.put("C_PaymentTerm_NetDays", Integer.toString(paymentTerm.getNetDays()));
		map.put("C_PaymentTerm_IsPaySchedule", Boolean.toString(payScheduleCount >= 1));	
		
		// Incorporar el monto pentiente a cancelar
		BigDecimal openAmt = DB.getSQLValueBD(getTrxName(), "SELECT invoiceopen(?,0)", ((X_C_Invoice)aDocument).getC_Invoice_ID());
		map.put("OpenAmt", openAmt==null?null:openAmt.toString());
	}
	
	/**
	 * Agrega campos adicionales a la map relacionada con la linea de una factura
	 * 
	 * Detalle de campos:
     * 		PriceTotal → Precio Bruto (independiente de tarifa)
     * 		PriceNet → Precio Neto (independiente de tarifa)
     * 		LineTotalAmt → Importe Bruto de Línea
     * 		LineNetAmt → Importe Neto de Línea
     * 		LineTaxAmt → Importe de Impuesto de Línea
     *  
	 * @param docLine linea de factura a tomar como base para buscar los datos
	 * @param map coleccion a incorporarle los nuevos datos
	 */
	protected void appendAditionalInvoiceLineInfoToMap(PO docLine, HashMap<String, String> map) {
		MInvoiceLine invoiceLine = (MInvoiceLine)docLine;
		map.put("PriceTotal", invoiceLine.getPriceEnteredWithTax() !=null 			? invoiceLine.getPriceEnteredWithTax().setScale(BD_SCALE, BD_ROUND_MODE).toString()			: null);
		map.put("PriceNet", invoiceLine.getPriceEnteredNet() !=null 				? invoiceLine.getPriceEnteredNet().setScale(BD_SCALE, BD_ROUND_MODE).toString()				: null);
		map.put("LineTotalAmt", invoiceLine.getTotalPriceEnteredWithTax() !=null 	? invoiceLine.getTotalPriceEnteredWithTax().setScale(BD_SCALE, BD_ROUND_MODE).toString()	: null);
		map.put("LineNetAmt", invoiceLine.getTotalPriceEnteredNet() !=null 			? invoiceLine.getTotalPriceEnteredNet().setScale(BD_SCALE, BD_ROUND_MODE).toString()		: null);
		map.put("LineTaxAmt", invoiceLine.getTaxAmt() !=null 						? invoiceLine.getTaxAmt().setScale(BD_SCALE, BD_ROUND_MODE).toString()						: null);
	}
	
	/**
	 * Agrega campos adicionales sobre impuestos al bean de resultado relacionado con el encabezado de una factura
	 * 
	 * @param aDocument factura a tomar como base para buscar los datos
	 * @param result bean donde incorporar los nuevos datos
	 */
	protected void appendInvoiceTaxesInfoToMap(PO aDocument,  InvoiceResultBean result) {
		int invoiceID = ((X_C_Invoice)aDocument).getC_Invoice_ID();
		List<PO> taxes = PO.find(getCtx(), "C_InvoiceTax", "C_Invoice_ID = ? ", new Object[]{invoiceID}, null, getTrxName());
		for (PO invoiceTax : taxes)
			result.getTaxes().add(poToMap(invoiceTax, true));
	}
}
