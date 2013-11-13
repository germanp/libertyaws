package org.libertya.ws.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.libertya.ws.bean.parameter.CustomServiceParameterBean;
import org.libertya.ws.bean.result.CustomServiceResultBean;
import org.libertya.ws.exception.ModelException;
import org.libertya.wse.common.ListedMap;
import org.openXpertya.plugin.common.CustomServiceInterface;
import org.openXpertya.plugin.common.DynamicArgument;
import org.openXpertya.plugin.common.DynamicResult;
import org.openXpertya.util.Env;

public class CustomServiceHandler extends GeneralHandler {

	public CustomServiceResultBean customService(CustomServiceParameterBean data) {
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{}, new Object[]{});
		
			// obtener la clase a instanciar
			Class<?> clazz = Class.forName(data.getClassName());
			CustomServiceInterface instance = (CustomServiceInterface)clazz.newInstance();
			
			// Generar la estructura de parametros que requiere DynamicArgument
			HashMap<String, ArrayList<String>> content = new HashMap<String, ArrayList<String>>();
			load(data.getRawArguments(), content);
			
			// parametros para la invocacion (Argumentos, contexto, transaccion)
			Method method = CustomServiceInterface.class.getMethod(data.getMethodName(), DynamicArgument.class, Properties.class, String.class);
			DynamicArgument arguments = new DynamicArgument();
			arguments.setContent(content);
			DynamicResult result = (DynamicResult)method.invoke(instance, arguments, Env.getCtx(), getTrxName());
			
			// instanciar el objeto a fin de iniciar el procesamiento 
			CustomServiceResultBean response = new CustomServiceResultBean();
			
			// setear valores de respuesta
			response.setError(result.isError());
			response.setErrorMsg(result.getErrorMsg());
			response.setResult(toListedMap(result.getContent()));
			
			// devolver respuesta
			return response;
		}
		catch (ModelException me) {
			return (CustomServiceResultBean)processException(me, new CustomServiceResultBean(), wsInvocationArguments(data));
		}
		catch (Exception e) {
			return (CustomServiceResultBean)processException(e, new CustomServiceResultBean(), wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}

		
	}
	
	/**
	 * Vuelca el contenido almacenado bajo arguments en content 
	 */
	public void load(ListedMap[] arguments, HashMap<String, ArrayList<String>> content) {
		for (ListedMap listedMap : arguments) {
			ArrayList<String> aMapValue = new ArrayList<String>(); 
			if (aMapValue != null) {
				for (String value : listedMap.getValues())
					aMapValue.add(value);
			}
			content.put(listedMap.getKey(), aMapValue);
		}
	}
	
	
	public ListedMap[] toListedMap(HashMap<String, ArrayList<String>> result) {
		int i=0;
		ListedMap[] retValue = new ListedMap[result.size()];
		if (result != null) {
			for (String argName : result.keySet()) {
				int j = 0;
				String[] argValues = new String[result.get(argName).size()];
				for (String argValue : result.get(argName)) {
					argValues[j] = argValue;
					j++;
				}
				ListedMap aMap = new ListedMap();
				aMap.setKey(argName);
				aMap.setValues(argValues);
				retValue[i++] = aMap;
			}
		}
		return retValue;
	}
	
}
