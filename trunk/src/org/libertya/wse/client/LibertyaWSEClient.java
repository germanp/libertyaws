package org.libertya.wse.client;

import org.libertya.wse.common.ListedMap;
import org.libertya.wse.param.Login;
import org.libertya.wse.result.Result;

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
	//		org.libertya.wse.LibertyaWSE lywse = new org.libertya.wse.LibertyaWSEImpl();
			
			// Prueba 1: Custom Service
			Login aLogin = new Login();
			aLogin.setUserName("AdminLibertya");
			aLogin.setPassword("AdminLibertya");
			aLogin.setClientID(1010016);
			aLogin.setOrgID(0);
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
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
