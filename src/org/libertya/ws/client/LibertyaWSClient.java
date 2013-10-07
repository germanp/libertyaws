package org.libertya.ws.client;


import java.util.ArrayList;
import java.util.HashMap;

import org.libertya.ws.bean.parameter.AllocationParameterBean;
import org.libertya.ws.bean.parameter.BPartnerParameterBean;
import org.libertya.ws.bean.parameter.CustomServiceParameterBean;
import org.libertya.ws.bean.parameter.DocumentParameterBean;
import org.libertya.ws.bean.parameter.FilteredColumnsParameterBean;
import org.libertya.ws.bean.parameter.InvoiceParameterBean;
import org.libertya.ws.bean.parameter.OrderParameterBean;
import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.BPartnerResultBean;
import org.libertya.ws.bean.result.InvoiceResultBean;
import org.libertya.ws.bean.result.MultipleDocumentsResultBean;
import org.libertya.ws.bean.result.MultipleRecordsResultBean;
import org.libertya.ws.bean.result.ResultBean;

import ws.libertya.org.LibertyaWSServiceLocator;

public class LibertyaWSClient {

	/**
	 * For test-purposes only!
	 */
	public static void main(String[] args)
	{
		try
		{
			// Conexión al WS
			LibertyaWSServiceLocator locator = new LibertyaWSServiceLocator();
			// Redefinir URL del servicio?
			if (args.length == 0)
				System.err.println("No se ha especificado URL del servicio.  Utilizando valor por defecto: http://localhost:8080/axis/services/LibertyaWS");
			else
				locator.setLibertyaWSEndpointAddress(args[0]);
			// Recuperar el servicio
			ws.libertya.org.LibertyaWS lyws = locator.getLibertyaWS();
//			org.libertya.ws.LibertyaWS lyws = new org.libertya.ws.LibertyaWSImpl();
			
			// Prueba 1: Eliminar una factura
			InvoiceParameterBean data = new InvoiceParameterBean("AdminLibertya", "AdminLibertya", 1010016, 1010053);
			System.out.println(lyws.invoiceDeleteByColumn(data, "DocumentNo", "100012"));
			System.out.println(" -------------- \n ");
			
			// Prueba 2: Crear una entidad comercial
			BPartnerParameterBean data2 = new BPartnerParameterBean("AdminLibertya", "AdminLibertya", 1010016, 1010053);
			data2.addColumnToBPartner("value", "value2881");
			data2.addColumnToBPartner("taxid", "20277467284");
			data2.addColumnToBPartner("taxidtype", "80");
			data2.addColumnToBPartner("name", "pruebaName3");
			data2.addColumnToBPartner("name2", "un nombre");
			data2.addColumnToBPartner("c_bp_group_id", "1010045");
			data2.addColumnToBPartner("isonetime", "N");
			data2.addColumnToBPartner("isprospect", "Y");
			data2.addColumnToBPartner("isvendor", "N");
			data2.addColumnToBPartner("iscustomer", "N");
			data2.addColumnToBPartner("isemployee", "N");
			data2.addColumnToBPartner("issalesrep", "N");
			data2.addColumnToBPartner("c_paymentterm_id", "1010083");
			data2.addColumnToBPartner("m_pricelist_id", "1010595");
			data2.addColumnToLocation("address1", "una direccion ");
			data2.addColumnToLocation("phone", "999999");
			ResultBean resultado = lyws.bPartnerCreate(data2);
			System.out.println(resultado.getMainResult().get("C_BPartner_ID"));
			System.out.println(resultado.getMainResult().get("C_BPartner_Location_ID"));
			System.out.println(lyws.bPartnerCreate(data2));
			System.out.println(" -------------- \n ");
			
			// Prueba 3: Recuperar una E.C.
			ParameterBean data3 = new ParameterBean("AdminLibertya", "AdminLibertya", 1010016, 1010053);
			BPartnerResultBean bean = lyws.bPartnerRetrieveByValue(data3, "CF");
			System.out.println(bean.toString());
			System.out.println(" -------------- \n ");
			
			// Prueba 4: Crear una Factura
			InvoiceParameterBean data4 = new InvoiceParameterBean("AdminLibertya", "AdminLibertya", 1010016, 1010053);
			// Opcion 1: indicando DocTypeTarget
			data4.addColumnToHeader("C_DocTypeTarget_ID", "1010507");
			// Opcion 2: indicando PuntoDeVenta + TipoComprobante
			data4.addColumnToHeader("TipoComprobante", "FC");
			data4.addColumnToHeader("PuntoDeVenta", "1");
			data4.addColumnToHeader("DateInvoiced", "2012-08-01 11:25:00");		 // OJO CON EL FORMATO: YYYY-MM-DD HH:mm:ss  
			data4.addColumnToHeader("C_BPartner_Location_ID", "1012158");
			data4.addColumnToHeader("M_PriceList_ID", "1010595");
			data4.addColumnToHeader("C_Currency_ID", "118");
			data4.addColumnToHeader("PaymentRule", "Tr");
			data4.addColumnToHeader("C_PaymentTerm_ID", "1000073");
			data4.addColumnToHeader("CreateCashLine", "N");
			data4.addColumnToHeader("ManualGeneralDiscount", "0.00");
			data4.addColumnToHeader("Description", "Una factura desde WS");
			data4.newDocumentLine();											// Especifico nueva linea
			data4.addColumnToCurrentLine("Line", "1");							// Datos de línea 1
			data4.addColumnToCurrentLine("QtyEntered", "1");
			data4.addColumnToCurrentLine("PriceEntered", "43.01");
			data4.addColumnToCurrentLine("C_Tax_ID", "1010084");
			data4.addColumnToCurrentLine("M_Product_ID", "1015400");
			data4.addColumnToCurrentLine("Description", "LINEA 1");
			data4.addColumnToCurrentLine("C_UOM_ID", "1000000");  // no se debe setear, es readonly
			ResultBean resultI = lyws.invoiceCreateCustomer(data4, 1012145, null, null, false); 
			System.out.println(resultI);
			System.out.println(" -------------- \n ");
			
			if (!resultI.isError()) {
				ParameterBean edit = new ParameterBean("AdminLibertya", "AdminLibertya", 1010016, 0);
				edit.addColumnToMainTable("Description", "Una actualizacion");
				System.out.println(lyws.invoiceUpdateByID(edit, 1021717));
			}
			
			// Prueba 5: Recuperar facturas
			ParameterBean data5 = new ParameterBean("AdminLibertya", "AdminLibertya", 1010016, 0);
			MultipleDocumentsResultBean invoices = lyws.documentQueryInvoices(data5, 1012145, null, null, true, false, false, false, "2011-01-01", "2012-08-03", null);
			System.out.println(invoices);
			System.out.println(" -------------- \n ");
			
			// Prueba 6: Recuperar una factura a partir de su DocumentNo
			ParameterBean data6 = new ParameterBean("AdminLibertya", "AdminLibertya", 1010016, 0);
			InvoiceResultBean detalleFactura = lyws.documentRetrieveInvoiceByColumn(data6, "DocumentNo", "100088");
			System.out.println(detalleFactura);
			System.out.println(" -------------- \n ");

			// Prueba 7: Crear un producto
			ParameterBean pData = new ParameterBean("AdminLibertya", "AdminLibertya", 1010016, 0);
			pData.addColumnToMainTable("value", "Articulo de Ejemplo N 33");
			pData.addColumnToMainTable("name", "un nombre de Articulo");
			pData.addColumnToMainTable("c_uom_id", "1000000");
			pData.addColumnToMainTable("m_product_category_id", "1010146");
			pData.addColumnToMainTable("c_taxcategory_id", "1010047");
			ResultBean newProduct = lyws.productCreate(pData); 
			System.out.println(newProduct);
			System.out.println(" -------------- \n ");
			
			// Prueba 8: Recuperar el producto recien creado
			if (!newProduct.isError()) {
				ParameterBean pData2 = new ParameterBean("AdminLibertya", "AdminLibertya", 1010016, 0);
				System.out.println(lyws.productRetrieveByID(pData2, Integer.parseInt(newProduct.getMainResult().get("M_Product_ID"))));
			}
			
			// Prueba 9: Crear un nuevo pedido de venta. Completarlo.  Crear factura a partir del pedido.  Completarla.
			//			 Modificado para soportar determinacion de tipos de documentos de manera dinamica a partir de ptoVta y tipoComprobante
			OrderParameterBean dBean2 = new OrderParameterBean("AdminLibertya", "AdminLibertya", 1010016, 1010096);
			dBean2.addColumnToHeader("C_DocTypeTarget_ID", "1010507");
			dBean2.addColumnToHeader("C_BPartner_Location_ID", "1012158");
			dBean2.addColumnToHeader("M_PriceList_ID", "1010642");
			dBean2.addColumnToHeader("C_Currency_ID", "118");
			dBean2.addColumnToHeader("PaymentRule", "Tr");
			dBean2.addColumnToHeader("C_PaymentTerm_ID", "1000073");
			dBean2.addColumnToHeader("CreateCashLine", "N");
			dBean2.addColumnToHeader("ManualGeneralDiscount", "0.00");
			dBean2.addColumnToHeader("M_Warehouse_ID", "1010091");
			dBean2.addColumnToHeader("Description", "Una pedido desde WS");
			dBean2.newDocumentLine();
			dBean2.addColumnToCurrentLine("Line", "1");
			dBean2.addColumnToCurrentLine("QtyEntered", "15");
			dBean2.addColumnToCurrentLine("PriceEntered", "25");
			dBean2.addColumnToCurrentLine("C_Tax_ID", "1010085");
			dBean2.addColumnToCurrentLine("M_Product_ID", "1015400");
			dBean2.addColumnToCurrentLine("Description", "LINEA 1");
			// Opcion 1: Indicar el C_DocTypeTarget_ID directamente (comentado para utilizar la opción 2)
//			dBean2.setInvoiceDocTypeTargetID(1010587);
			// Opcion 2: Indicar el Punto de Venta + Tipo de Comprobante
			dBean2.setInvoicePuntoDeVenta(1);
			dBean2.setInvoiceTipoComprobante(OrderParameterBean.TIPO_COMPROBANTE_FACTURA);
			ResultBean orderResult = lyws.orderCreateCustomer(dBean2, 1012145, null, null, true, true, true);
			System.out.println(orderResult);
			System.out.println(" -------------- \n ");
			
			// Prueba 10: Anular el pedido recien creado
			if (!orderResult.isError()) {
				int orderID = Integer.parseInt(orderResult.getMainResult().get("C_Order_ID"));
				System.out.println(lyws.orderVoidByID(dBean2, orderID));
			}
			System.out.println(" -------------- \n ");
			
			// Prueba 11: Nuevo remito de entrada.  Completarlo
			DocumentParameterBean rBean = new DocumentParameterBean("AdminLibertya", "AdminLibertya", 1010016, 1010053);
			rBean.addColumnToHeader("C_DocTypeTarget_ID", "1010522");
			rBean.addColumnToHeader("C_BPartner_Location_ID", "1012158");
			rBean.addColumnToHeader("M_Warehouse_ID", "1010048");
			rBean.addColumnToHeader("Description", "Una remito desde WS");
			rBean.newDocumentLine();
			rBean.addColumnToCurrentLine("Line", "1");
			rBean.addColumnToCurrentLine("QtyEntered", "300");		
			rBean.addColumnToCurrentLine("M_Product_ID", "1015446");
			rBean.addColumnToCurrentLine("Description", "LINEA 1"); 
			ResultBean inOutResult = lyws.inOutCreateVendor(rBean, 1012145, null, null, true);
			System.out.println(inOutResult);
			
			// Prueba 12: Intentar eliminar el remito creado (deberia dar error ya que el mismo se encuentra completado)
			if (!inOutResult.isError()) {
				int inOutID = Integer.parseInt(inOutResult.getMainResult().get("M_InOut_ID"));
				System.out.println(lyws.inOutDeleteByID(rBean, inOutID));
			}
			System.out.println(" -------------- \n ");
			
			// Prueba 13: Recuperar informacion sobre suma de pedidos no facturados, cheques en cartera, facturas emitidas, y cobros
			ParameterBean beanX = new ParameterBean("AdminLibertya", "AdminLibertya", 1010016, 1010053);
			System.out.println(lyws.bPartnerBalanceSumOrdersNotInvoiced(beanX, -1, new int[]{1012145, 1012196}, null, 1010016, 0));
			System.out.println(lyws.bPartnerBalanceSumChecks(beanX, -1, new int[]{1012145, 1012196}, null, 1010016, 0));
			System.out.println(lyws.bPartnerBalanceSumInvoices(beanX, -1, new int[]{1012145, 1012196}, null, 1010016, 0));
			System.out.println(lyws.bPartnerBalanceSumPayments(beanX, -1, new int[]{1012145, 1012196}, null, 1010016, 0));
			
			// Prueba 14: Crear un recibo de cliente cancelando una factura existente con varios medios de pago
			AllocationParameterBean beanRC = new AllocationParameterBean("AdminLibertya", "AdminLibertya", 1010016, 1010099);
			beanRC.addColumnToHeader("Description", "Un RC desde WS");
			// Factura a cobrar
			beanRC.newInvoice();
			beanRC.addColumnToCurrentInvoice("C_Invoice_ID", "1021753");
			beanRC.addColumnToCurrentInvoice("Amount", "43.0100");
			
			// Medio de pago: Nota de credito
			beanRC.newPayment();
			beanRC.addColumnToCurrentPayment("C_POSPaymentMedium_ID", "1010038");
			beanRC.addColumnToCurrentPayment("Amount", "70");
			beanRC.addColumnToCurrentPayment("C_Invoice_ID", "1021695");
			// Medio de pago: Transferencia bancaria
			beanRC.newPayment();
			beanRC.addColumnToCurrentPayment("C_POSPaymentMedium_ID", "1010052");
			beanRC.addColumnToCurrentPayment("Amount", "43.01");
			beanRC.addColumnToCurrentPayment("C_BankAccount_ID", "1010133");
			beanRC.addColumnToCurrentPayment("C_Bank_ID", "1010099");
			beanRC.addColumnToCurrentPayment("A_Bank", "Un Banco");
			beanRC.addColumnToCurrentPayment("TransferNo", "1234");
			beanRC.addColumnToCurrentPayment("TransferDate", "2012-09-03 10:20:00");
			// Medio de pago: Tarjeta de credito
			beanRC.newPayment();
			beanRC.addColumnToCurrentPayment("C_POSPaymentMedium_ID", "1010036");
			beanRC.addColumnToCurrentPayment("Amount", "20");
			beanRC.addColumnToCurrentPayment("M_EntidadFinancieraPlan_ID", "1010033");
			beanRC.addColumnToCurrentPayment("A_Bank", "Comafi");
			beanRC.addColumnToCurrentPayment("CreditCardNumber", "102929281810");
			beanRC.addColumnToCurrentPayment("CouponNumber", "12341234");
			// Medio de pago: Cheque
			beanRC.newPayment();
			beanRC.addColumnToCurrentPayment("C_POSPaymentMedium_ID", "1010037");
			beanRC.addColumnToCurrentPayment("Amount", "40");
			beanRC.addColumnToCurrentPayment("C_BankAccount_ID", "1010070");
			beanRC.addColumnToCurrentPayment("CheckNo", "12345");
			beanRC.addColumnToCurrentPayment("DateTrx", "2012-09-03 10:26:15");
			beanRC.addColumnToCurrentPayment("DueDate", "2012-09-04 10:26:15");
			// Medio de pago: Efectivo
			beanRC.newPayment();
			beanRC.addColumnToCurrentPayment("C_POSPaymentMedium_ID", "1010033");
			beanRC.addColumnToCurrentPayment("Amount", "40");
			beanRC.addColumnToCurrentPayment("C_Cash_ID", "1010062");
			// Medio de pago: Cobro adelantado en efectivo
			beanRC.newPayment();
			beanRC.addColumnToCurrentPayment("C_POSPaymentMedium_ID", "1010035");
			beanRC.addColumnToCurrentPayment("Amount", "70");
			beanRC.addColumnToCurrentPayment("C_CashLine_ID", "1010100");
			// Medio de pago: Cobro adelantado mediante transf. bancaria
			beanRC.newPayment();
			beanRC.addColumnToCurrentPayment("C_POSPaymentMedium_ID", "1010035");
			beanRC.addColumnToCurrentPayment("Amount", "30");
			beanRC.addColumnToCurrentPayment("C_Payment_ID", "1011960");
			// Medio de pago: Retencion sufrida
			beanRC.newPayment();
			beanRC.addColumnToCurrentPayment("C_POSPaymentMedium_ID", "1010039");
			beanRC.addColumnToCurrentPayment("Amount", "50");
			beanRC.addColumnToCurrentPayment("C_RetencionSchema_ID", "1010054");
			beanRC.addColumnToCurrentPayment("Retenc_DocumentNo", "5311181");
			beanRC.addColumnToCurrentPayment("Retenc_Date", "2012-09-04 10:44:18");
			// Invocar a la crecion de recibo para la entidad comercial cuyo value sea MC
			ResultBean result = lyws.allocationCreateReceipt(beanRC, 1012221, null, null);
			System.out.println(result);
			System.out.println(" -------------- \n ");
			
			// Prueba 15: Intentar anular el recibo de cliente recien creado, incluyendo pagos y retenciones
			if (!result.isError()) {
				int allocationHdrID = Integer.parseInt(result.getMainResult().get("C_AllocationHdr_ID"));
				AllocationParameterBean voidBean = new AllocationParameterBean("AdminLibertya", "AdminLibertya", 1010016, 1010053);
				System.out.println(lyws.allocationVoidByID(voidBean, allocationHdrID, AllocationParameterBean.ALLOCATIONACTION_VoidPaymentsRetentions));
				System.out.println(" -------------- \n ");
			}
			
			// Prueba 16: Recuperar una factura contemplando nuevos datos de retorno
			InvoiceParameterBean pData2 = new InvoiceParameterBean("AdminLibertya", "AdminLibertya", 1010016, 1010053);
			System.out.println(lyws.documentRetrieveInvoiceByID(pData2, 1021701));
			
			// Prueba 17: Creacion de un usuario
			ParameterBean data1 = new ParameterBean("AdminLibertya", "AdminLibertya", 1010016, 1010053);
			data1.addColumnToMainTable("name", "fulanito55");
			data1.addColumnToMainTable("password", "198798217");
			data1.addColumnToMainTable("phone", "66666666");
			ResultBean resUser = lyws.userCreate(data1); 
			System.out.println(resUser);
			
			// Prueba 18: Recuperacion, Modificacion y Eliminacion logica de usuario
			if (!resUser.isError()) {
				// Recuperacion de usuario
				int userID = Integer.parseInt(resUser.getMainResult().get("AD_User_ID"));
				System.out.println(lyws.userRetrieveByID(data1, userID));
				
				// Modificacion de usuario
				ParameterBean updateUser = new ParameterBean("AdminLibertya", "AdminLibertya", 1010016, 1010053);
				updateUser.addColumnToMainTable("password", "153284");
				System.out.println(lyws.userUpdateByID(updateUser, userID));
				
				// Eliminacion de usuario
				System.out.println(lyws.userDeleteByID(updateUser, userID));
			}
			
			// Prueba 19: Consulta de stock varias. NOTAR QUE Org = 0, en caso de ser > 0, entonces filtrará también por este criterio
			ParameterBean storageBean = new ParameterBean("AdminLibertya", "AdminLibertya", 1010016, 0);
			// Stock de todos los articulos en todas los almacenes
			System.out.println(lyws.storageQuery(storageBean, null, 0, null, 0, null, null));
			// Stock del artículo Standard, en los almacenes 1010048 y 1010087.
			System.out.println(lyws.storageQuery(storageBean, new int[]{1010048, 1010087}, 0, "Standard", 0, null, null));
			// Stock del artículo 1915452, en la ubicación 1010317
			System.out.println(lyws.storageQuery(storageBean, null, 1010317, null, 1015452, null, null));
			
			// Prueba 20: Recuperación genérica de registros
			FilteredColumnsParameterBean recParam = new FilteredColumnsParameterBean("AdminLibertya", "AdminLibertya", 1010016, 0);
			recParam.addColumnToFilter("m_pricelist_version_id");
			recParam.addColumnToFilter("m_product_id");
			recParam.addColumnToFilter("pricelist");
			MultipleRecordsResultBean recResult = lyws.recordQuery(recParam, "M_ProductPrice", "created > '2001-01-01'", false);
			System.out.println(recResult);
			
			// Prueba 21: Actualización de un pedido.  Si el mismo está completado, reabrirlo.  Luego completar dicho pedido.
			ParameterBean test21 = new ParameterBean("AdminLibertya", "AdminLibertya", 1010016, 0);
			test21.addColumnToMainTable("Description", "Una modificacion");
			System.out.println(lyws.orderUpdateByID(test21, 1014059, true));
			
			// Prueba 22: Servicio genérico.  Invocar a un servicio en clase Example, que recibe 4 parámetros:
			//				param1: un String; param2: un String; param3: un entero; param 4: una lista de valores
			//			  En todos los casos, los valores son enviados como Strings
			CustomServiceParameterBean test22 = new CustomServiceParameterBean("AdminLibertya", "AdminLibertya", 1010016, 0);
			// Nombre de la clase a invocar
			test22.setClassName("org.libertya.example.customService.Example");
			// Definir Argumento 1
			HashMap<String, ArrayList<String>> argument1 = new HashMap<String, ArrayList<String>>();
			ArrayList<String> argument1Value = new ArrayList<String>();
			argument1Value.add("foo");
			argument1.put("param1", argument1Value);
			// Definir Argumento 2
			HashMap<String, ArrayList<String>> argument2 = new HashMap<String, ArrayList<String>>();
			ArrayList<String> argument2Value = new ArrayList<String>();
			argument2Value.add("bar");
			argument2.put("param2", argument2Value);
			// Definir Argumento 3
			HashMap<String, ArrayList<String>> argument3 = new HashMap<String, ArrayList<String>>();
			ArrayList<String> argument3Value = new ArrayList<String>();
			argument3Value.add("43");
			argument3.put("param3", argument3Value);
			// Definir Argumento 4
			HashMap<String, ArrayList<String>> argument4 = new HashMap<String, ArrayList<String>>();
			ArrayList<String> argument4Value = new ArrayList<String>();
			argument4Value.add("x");
			argument4Value.add("y");
			argument4Value.add("z");
			argument4.put("param4", argument4Value);
			// Nomina de argumentos e incorporar los argumentos previamente definidos
			ArrayList<HashMap<String, ArrayList<String>>> arguments = new ArrayList<HashMap<String, ArrayList<String>>>();
			arguments.add(argument1);
			arguments.add(argument2);
			arguments.add(argument3);
			arguments.add(argument4);
			test22.setArguments(arguments);
			// Invocar al servicio
			System.out.println(lyws.customService(test22));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
