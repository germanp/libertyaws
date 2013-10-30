package org.libertya.wse.client;

import org.libertya.wse.common.ListedMap;
import org.libertya.wse.common.SimpleMap;
import org.libertya.wse.param.DocumentLine;
import org.libertya.wse.param.Login;
import org.libertya.wse.result.Result;
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

			/* Definicion de login */
			Login aLogin = new Login();
			aLogin.setUserName("AdminLibertya");
			aLogin.setPassword("AdminLibertya");
			aLogin.setClientID(1010016);
			aLogin.setOrgID(0);
			
			/* === Prueba 1: Custom Service === */
			// Cargar los parametros
			ListedMap param1 = new ListedMap();
			param1.setKey("param1");
			param1.setValues(new String[]{"foo"});
			ListedMap param2 = new ListedMap();
			param2.setKey("param2");
			param2.setValues(new String[]{"bar"});
			ListedMap param3 = new ListedMap();
			param3.setKey("param3");
			param3.setValues(new String[]{"43"});
			ListedMap param4 = new ListedMap();
			param4.setKey("param4");
			param4.setValues(new String[]{"x", "y", "z"});
			// Nomina de argumentos
			ListedMap[] params = new ListedMap[4];
			params[0] = param1;
			params[1] = param2;
			params[2] = param3;
			params[3] = param4;
			Result aResult = lywse.customService(aLogin, "org.libertya.example.customService.Example", params);
			System.out.println(aResult.isError());
			System.out.println(aResult.getErrorMsg());
			System.out.println(aResult.getResultValues());
			
			/* === Prueba 2: Crear un remito === */
			// Datos de la cabecera
			SimpleMap headVal1 = new SimpleMap();
			headVal1.setKey("C_DocTypeTarget_ID");
			headVal1.setValue("1010522");
			SimpleMap headVal2 = new SimpleMap();
			headVal2.setKey("C_BPartner_Location_ID");
			headVal2.setValue("1012158");
			SimpleMap headVal3 = new SimpleMap();
			headVal3.setKey("M_Warehouse_ID");
			headVal3.setValue("1010048");
			SimpleMap headVal4 = new SimpleMap();
			headVal4.setKey("Description");
			headVal4.setValue("Una remito desde WS");
			SimpleMap[] header = new SimpleMap[4];
			header[0] = headVal1;
			header[1] = headVal2;
			header[2] = headVal3;
			header[3] = headVal4;
			// Datos de una linea
			SimpleMap lineVal1 = new SimpleMap();
			lineVal1.setKey("Line");
			lineVal1.setValue("1");
			SimpleMap lineVal2 = new SimpleMap();
			lineVal2.setKey("QtyEntered");
			lineVal2.setValue("300");
			SimpleMap lineVal3 = new SimpleMap();
			lineVal3.setKey("M_Product_ID");
			lineVal3.setValue("1015400");
			SimpleMap lineVal4 = new SimpleMap();
			lineVal4.setKey("Description");
			lineVal4.setValue("Linea 1");
			SimpleMap[] lineContent = new SimpleMap[4];
			lineContent[0] = lineVal1;
			lineContent[1] = lineVal2;
			lineContent[2] = lineVal3;
			lineContent[3] = lineVal4;
			DocumentLine aLine = new DocumentLine();
			aLine.setContent(lineContent);
			// Incorporar a la nomina de lineas
			DocumentLine[] lines = new DocumentLine[1];
			lines[0] = aLine;
			//Invocar al servicio
			SimpleResult result2 = lywse.inOutCreateVendor(aLogin, header, lines, 1012145, null, null, true);
			System.out.println(result2.isError());
			System.out.println(result2.getErrorMsg());
			System.out.println(result2.getResultValues());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
