package org.libertya.wse.client;

import java.io.FileOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.libertya.wse.common.SimpleMap;
import org.libertya.wse.param.Login;
import org.libertya.wse.result.SimpleResult;

import wse.libertya.org.LibertyaWSEServiceLocator;

public class LibertyaWSEClient {

	public static void main(String[] args) {
		try {
			// Conexión al WS
			LibertyaWSEServiceLocator locator = new LibertyaWSEServiceLocator();
			// Redefinir URL del servicio?
			if (args.length == 0)
				System.err.println("No se ha especificado URL del servicio.  Utilizando valor por defecto: http://localhost:8080/axis/services/LibertyaWSE");
			else
				locator.setLibertyaWSEEndpointAddress(args[0]);
			// Recuperar el servicio
			wse.libertya.org.LibertyaWSE lywse = locator.getLibertyaWSE();
//			org.libertya.wse.LibertyaWSE lywse = new org.libertya.wse.LibertyaWSEImpl();

/*			
			// Definicion de login 
			Login aLogin = new Login();
			aLogin.setUserName("AdminLibertya");
			aLogin.setPassword("AdminLibertya");
			aLogin.setClientID(1010016);
			aLogin.setOrgID(1010053);
			
			
			SimpleResult result = lywse.processGeneratePromotionCode(aLogin, null);
			if (result.isError()) {
				System.out.println(result.getErrorMsg());
			} else {
				
				for (SimpleMap aMap : result.getResultValues()) {
					System.out.println(aMap.getKey() + ": " + aMap.getValue());
				}
			}
	*/		
			
			
			
			// Definicion de login 
			Login aLogin = new Login();
			aLogin.setUserName("AdminLibertya");
			aLogin.setPassword("AdminLibertya");
			aLogin.setClientID(1010016);
			aLogin.setOrgID(1010053);


			// Argumento tableName
			SimpleMap tableName = new SimpleMap();
			tableName.setKey("tableName");
			tableName.setValue("C_Invoice");
			// Argumento documentID
			SimpleMap documentID = new SimpleMap();
			documentID.setKey("recordID");
			documentID.setValue("1021812");
			// Nomina de argumentos 
			SimpleMap[] params = new SimpleMap[2];
			params[0] = tableName;
			params[1] = documentID;
			
			SimpleResult result = lywse.processRetrievePdfFromDocument(aLogin, params);
			if (!result.isError()) {
				System.out.println(result.getResultValues()[0].getValue());
				byte[] pdf = Base64.decodeBase64(result.getResultValues()[0].getValue());
				FileOutputStream fos = new FileOutputStream("/tmp/documento2.pdf");
				fos.write(pdf);
				fos.close();
			} else 
				System.out.println(result.getErrorMsg());

			
			
//			MultipleRecordsResult resU = lywse.userClientOrgAccessQuery(aLogin);
//			System.out.print(resU.getContent());
//			
//			
//			if (1==1)
//				return;
//			
//			// Cargar los parametros
//			ListedMap param1 = new ListedMap();
//			param1.setKey("1139812");
//			param1.setValues(new String[]{"1185386,1019541,XYZ123", "1185387,1017616"});
//			ListedMap param2 = new ListedMap();
//			param2.setKey("1139822");
//			param2.setValues(new String[]{"1185397,1017699,ABC987"});
//			// Nomina de argumentos
//			ListedMap[] params = new ListedMap[2];
//			params[0] = param1;
//			params[1] = param2;
//			Result aResult = lywse.customService(aLogin, "org.libertya.custom.ccmd.process.ReceivedOrderConfirmation", params);
//			System.out.println(aResult.isError());
//			System.out.println(aResult.getErrorMsg());
//			System.out.println(aResult.getResultValues());
//
//			if (1==1)
//				return;
//			
//			
//			SimpleMap headVal1 = new SimpleMap();
//			headVal1.setKey("C_DocTypeTarget_ID");
//			headVal1.setValue("1010532");
//			SimpleMap headVal2 = new SimpleMap();
//			headVal2.setKey("C_BPartner_Location_ID");
//			headVal2.setValue("1368058");
//			SimpleMap headVal3 = new SimpleMap();
//			headVal3.setKey("M_PriceList_ID");
//			headVal3.setValue("1010647");
//			SimpleMap headVal4 = new SimpleMap();
//			headVal4.setKey("C_Currency_ID");
//			headVal4.setValue("118");
//			SimpleMap headVal5 = new SimpleMap();
//			headVal5.setKey("PaymentRule");
//			headVal5.setValue("Tr");
//			SimpleMap headVal6 = new SimpleMap();
//			headVal6.setKey("C_PaymentTerm_ID");
//			headVal6.setValue("1010130");
//			SimpleMap headVal7 = new SimpleMap();
//			headVal7.setKey("CreateCashLine");
//			headVal7.setValue("N");
//			SimpleMap headVal8 = new SimpleMap();
//			headVal8.setKey("ManualGeneralDiscount");
//			headVal8.setValue("0.00");
//			SimpleMap headVal9 = new SimpleMap();
//			headVal9.setKey("Description");
//			headVal9.setValue("Un pedido");
//			SimpleMap headVal10 = new SimpleMap();
//			headVal10.setKey("M_Warehouse_ID");
//			headVal10.setValue("1010096");
//			SimpleMap[] header = new SimpleMap[10];
//			header[0] = headVal1;
//			header[1] = headVal2;
//			header[2] = headVal3;
//			header[3] = headVal4;
//			header[4] = headVal5;			
//			header[5] = headVal6;			
//			header[6] = headVal7;			
//			header[7] = headVal8;			
//			header[8] = headVal9;			
//			header[9] = headVal10;			
//			SimpleMap lineVal1 = new SimpleMap();
//			lineVal1.setKey("QtyEntered");
//			lineVal1.setValue("7");
//			SimpleMap lineVal2 = new SimpleMap();
//			lineVal2.setKey("Line");
//			lineVal2.setValue("1");
//			SimpleMap lineVal3 = new SimpleMap();
//			lineVal3.setKey("M_Product_ID__byValue"); // bien
////			lineVal3.setKey("Product_Value"); // mal
//			lineVal3.setValue("TEST");
//			SimpleMap lineVal4 = new SimpleMap();
//			lineVal4.setKey("C_Uom_ID");
//			lineVal4.setValue("100");
//			SimpleMap lineVal5 = new SimpleMap();
//			lineVal5.setKey("Description");
//			lineVal5.setValue("RETAK® 12,5 (96UN) ");
//			SimpleMap lineVal6 = new SimpleMap();
//			lineVal6.setKey("M_Product_ID");
//			lineVal6.setValue(null);
//			SimpleMap[] lineContent = new SimpleMap[6];
//			lineContent[0] = lineVal1;
//			lineContent[1] = lineVal2;
//			lineContent[2] = lineVal3;
//			lineContent[3] = lineVal4;
//			lineContent[4] = lineVal5;
//			lineContent[5] = lineVal6;
//			DocumentLine aLine = new DocumentLine();
//			aLine.setContent(lineContent);
//			DocumentLine[] lines = new DocumentLine[1];
//			lines[0] = aLine;
//			
//			SimpleResult res = lywse.orderCreateCustomer(aLogin, header, lines, 1368085, null, null, false, false, false, -1, 33, "FC");
//			System.out.println(res);
			
//			
//			// Prueba 4: Crear un Pedido
//			OrderParameterBean data4 = new OrderParameterBean("AdminCMD", "AdminCMD", 1010016, 1010099);
//			// Opcion 1: indicando DocTypeTarget
//			data4.addColumnToHeader("C_DocTypeTarget_ID", "1010532");
//			// Opcion 2: indicando PuntoDeVenta + TipoComprobante
//			data4.setInvoiceTipoComprobante("FC"); // addColumnToHeader("TipoComprobante", "FC");
//			data4.setInvoicePuntoDeVenta(33); //data4.addColumnToHeader("PuntoDeVenta", "33");
//			data4.addColumnToHeader("C_BPartner_Location_ID", "1368058");
//			data4.addColumnToHeader("M_PriceList_ID", "1010647");
//			data4.addColumnToHeader("C_Currency_ID", "118");
//			data4.addColumnToHeader("PaymentRule", "Tr");
//			data4.addColumnToHeader("C_PaymentTerm_ID", "1010130");
//			data4.addColumnToHeader("CreateCashLine", "N");
//			data4.addColumnToHeader("ManualGeneralDiscount", "0.00");
//			data4.addColumnToHeader("Description", "Una factura desde WS");
//			data4.addColumnToHeader("M_Warehouse_ID", "1010096");
//			data4.newDocumentLine();											// Especifico nueva linea
//			data4.addColumnToCurrentLine("Line", "1");							// Datos de línea 1
//			data4.addColumnToCurrentLine("QtyOrdered", "33.37");
//			data4.addColumnToCurrentLine("QtyEntered", "33.37");
////			data4.addColumnToCurrentLine("PriceEntered", "199.99");
////			data4.addColumnToCurrentLine("PriceActual", "199.99");
//			data4.addColumnToCurrentLine("C_Tax_ID", "1010085");
//			data4.addColumnToCurrentLine("M_Product_ID", "1024063");
//			data4.newDocumentLine();											// Especifico nueva linea
//			data4.addColumnToCurrentLine("Line", "2");							// Datos de línea 2
//			data4.addColumnToCurrentLine("QtyOrdered", "13.21");
//			data4.addColumnToCurrentLine("QtyEntered", "13.21");
////			data4.addColumnToCurrentLine("PriceEntered", "40.73");
////			data4.addColumnToCurrentLine("PriceActual", "40.73");
//			data4.addColumnToCurrentLine("C_Tax_ID", "1010085");
//			data4.addColumnToCurrentLine("M_Product_ID", "1024063");			
//			data4.newDocumentLine();											// Especifico nueva linea
//			data4.addColumnToCurrentLine("Line", "3");							// Datos de línea 3
//			data4.addColumnToCurrentLine("QtyOrdered", "27.78");
//			data4.addColumnToCurrentLine("QtyEntered", "27.78");
////			data4.addColumnToCurrentLine("PriceEntered", "199.51");
////			data4.addColumnToCurrentLine("PriceActual", "199.51");
//			data4.addColumnToCurrentLine("C_Tax_ID", "1010085");
//			data4.addColumnToCurrentLine("M_Product_ID", "1024063");
//			data4.newDocumentLine();											// Especifico nueva linea
//			data4.addColumnToCurrentLine("Line", "4");							// Datos de línea 4
//			data4.addColumnToCurrentLine("QtyOrdered", "1.5");
//			data4.addColumnToCurrentLine("QtyEntered", "1.5");
////			data4.addColumnToCurrentLine("PriceEntered", "40.99");
////			data4.addColumnToCurrentLine("PriceActual", "40.99");
//			data4.addColumnToCurrentLine("C_Tax_ID", "1010085");
//			data4.addColumnToCurrentLine("M_Product_ID", "1024063");			
//			ResultBean resultI = lyws.orderCreateCustomer(data4, 1368085, null, null, true, true, true); 
//			System.out.println(resultI);
//			System.out.println(" -------------- \n ");
//			
//			
//			MultipleRecordsResult res = lywse.documentQueryInvoices(aLogin, 1398714, null, null, true, false, false, false, null, null, null, new String[]{"CreatedBy.Description", "CreatedBy.Name", "C_DocType_ID.doctypekey", "C_DocType_ID.iscreatecounter", "C_DocType_ID.signo_issotrx", "C_DocType_ID.created"});
//			System.out.println(res);
//			for (RecordContent aRecord : res.getContent()) {
//				for (SimpleMap aMap : aRecord.getData()) {
//					System.out.print(aMap.getKey() + "=" + aMap.getValue() + "; ");
//				}
//				System.out.println("---");
//			}
//			SimpleMap headVal1 = new SimpleMap();
//			headVal1.setKey("C_DocTypeTarget_ID");
//			headVal1.setValue("1010522");
//			SimpleMap headVal2 = new SimpleMap();
//			headVal2.setKey("C_Order_ID");
//			headVal2.setValue("1109402");
//			SimpleMap headVal3 = new SimpleMap();
//			SimpleDateFormat sDate = new SimpleDateFormat("mmssSSS");
//			String time = sDate.format(new Date());
//			headVal3.setKey("DocumentNo");
//			headVal3.setValue("docno" + time);
//			SimpleMap headVal4 = new SimpleMap();
//			headVal4.setKey("Attr01");
//			headVal4.setValue("sap");
//			SimpleMap[] header = new SimpleMap[4];
//			header[0] = headVal1;
//			header[1] = headVal2;
//			header[2] = headVal3;
//			header[3] = headVal4;
//			SimpleMap lineVal1 = new SimpleMap();
//			lineVal1.setKey("QtyEntered");
//			lineVal1.setValue("1");
//			SimpleMap lineVal3 = new SimpleMap();
//			lineVal3.setKey("C_OrderLine_ID");
//			lineVal3.setValue("1131390");
//			SimpleMap lineVal4 = new SimpleMap();
//			lineVal4.setKey("Product__ByValue");
//			lineVal4.setValue("NUEVOTEST");
//			SimpleMap[] lineContent = new SimpleMap[3];
//			lineContent[0] = lineVal1;
//			lineContent[1] = lineVal3;
//			lineContent[2] = lineVal4;
//			DocumentLine aLine = new DocumentLine();
//			aLine.setContent(lineContent);
//			DocumentLine[] lines = new DocumentLine[1];
//			lines[0] = aLine;
//			// Invocar al servicio
//			SimpleResult result = lywse.inOutCreateVendor(aLogin, header, lines, 0, null, null, true);
//			System.out.println(result.getErrorMsg());
//			
//			/* === Prueba 1: Custom Service === */
//			// Cargar los parametros
//			ListedMap param1 = new ListedMap();
//			param1.setKey("param1");
//			param1.setValues(new String[]{"foo"});
//			ListedMap param2 = new ListedMap();
//			param2.setKey("param2");
//			param2.setValues(new String[]{"bar"});
//			ListedMap param3 = new ListedMap();
//			param3.setKey("param3");
//			param3.setValues(new String[]{"43"});
//			ListedMap param4 = new ListedMap();
//			param4.setKey("param4");
//			param4.setValues(new String[]{"x", "y", "z"});
//			// Nomina de argumentos
//			ListedMap[] params = new ListedMap[4];
//			params[0] = param1;
//			params[1] = param2;
//			params[2] = param3;
//			params[3] = param4;
//			Result aResult = lywse.customService(aLogin, "org.libertya.example.customService.Example", params);
//			System.out.println(aResult.isError());
//			System.out.println(aResult.getErrorMsg());
//			System.out.println(aResult.getResultValues());
//			
//			/* === Prueba 2: Crear un remito === */
//			// Datos de la cabecera
//			SimpleMap headVal1 = new SimpleMap();
//			headVal1.setKey("C_DocTypeTarget_ID");
//			headVal1.setValue("1010522");
//			SimpleMap headVal2 = new SimpleMap();
//			headVal2.setKey("C_BPartner_Location_ID");
//			headVal2.setValue("1012158");
//			SimpleMap headVal3 = new SimpleMap();
//			headVal3.setKey("M_Warehouse_ID");
//			headVal3.setValue("1010048");
//			SimpleMap headVal4 = new SimpleMap();
//			headVal4.setKey("Description");
//			headVal4.setValue("Una remito desde WS");
//			SimpleMap[] header = new SimpleMap[4];
//			header[0] = headVal1;
//			header[1] = headVal2;
//			header[2] = headVal3;
//			header[3] = headVal4;
//			// Datos de una linea
//			SimpleMap lineVal1 = new SimpleMap();
//			lineVal1.setKey("Line");
//			lineVal1.setValue("1");
//			SimpleMap lineVal2 = new SimpleMap();
//			lineVal2.setKey("QtyEntered");
//			lineVal2.setValue("300");
//			SimpleMap lineVal3 = new SimpleMap();
//			lineVal3.setKey("M_Product_ID");
//			lineVal3.setValue("1015400");
//			SimpleMap lineVal4 = new SimpleMap();
//			lineVal4.setKey("Description");
//			lineVal4.setValue("Linea 1");
//			SimpleMap[] lineContent = new SimpleMap[4];
//			lineContent[0] = lineVal1;
//			lineContent[1] = lineVal2;
//			lineContent[2] = lineVal3;
//			lineContent[3] = lineVal4;
//			DocumentLine aLine = new DocumentLine();
//			aLine.setContent(lineContent);
//			// Incorporar a la nomina de lineas
//			DocumentLine[] lines = new DocumentLine[1];
//			lines[0] = aLine;
//			//Invocar al servicio
//			SimpleResult result2 = lywse.inOutCreateVendor(aLogin, header, lines, 1012145, null, null, true);
//			System.out.println(result2.isError());
//			System.out.println(result2.getErrorMsg());
//			System.out.println(result2.getResultValues());
//			
//			/* === Prueba 3: Gestion de inventario === */
//			// Datos de la cabecera
//			headVal1 = new SimpleMap();
//			headVal1.setKey("c_doctype_id");
//			headVal1.setValue("1010529");
//			headVal2 = new SimpleMap();
//			headVal2.setKey("m_warehouse_id");
//			headVal2.setValue("1010048");
//			headVal3 = new SimpleMap();
//			headVal3.setKey("inventoryKind");
//			headVal3.setValue("PI");
//			header = new SimpleMap[3];
//			header[0] = headVal1;
//			header[1] = headVal2;
//			header[2] = headVal3;
//			// Datos de una linea
//			lineVal1 = new SimpleMap();
//			lineVal1.setKey("Line");
//			lineVal1.setValue("10");
//			lineVal2 = new SimpleMap();
//			lineVal2.setKey("m_locator_id");
//			lineVal2.setValue("1010278");
//			lineVal3 = new SimpleMap();
//			lineVal3.setKey("M_Product_ID");
//			lineVal3.setValue("1015506");
//			lineVal4 = new SimpleMap();
//			lineVal4.setKey("qtyCount");
//			lineVal4.setValue("33");
//			SimpleMap lineVal5 = new SimpleMap();
//			lineVal5.setKey("inventorytype");
//			lineVal5.setValue("D");
//			lineContent = new SimpleMap[5];
//			lineContent[0] = lineVal1;
//			lineContent[1] = lineVal2;
//			lineContent[2] = lineVal3;
//			lineContent[3] = lineVal4;
//			lineContent[4] = lineVal5;
//			aLine = new DocumentLine();
//			aLine.setContent(lineContent);
//			// Incorporar a la nomina de lineas
//			lines = new DocumentLine[1];
//			lines[0] = aLine;
//			//Invocar al servicio
//			result2 = lywse.inventoryCreate(aLogin, header, lines, true);
//			System.out.println(result2.isError());
//			System.out.println(result2.getErrorMsg());
//			System.out.println(result2.getResultValues());
//

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
