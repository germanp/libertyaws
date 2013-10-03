package org.libertya.ws.handler;

import java.lang.reflect.Method;
import java.util.Properties;

import org.libertya.ws.bean.parameter.CustomServiceParameterBean;
import org.libertya.ws.bean.result.CustomServiceResultBean;
import org.libertya.ws.exception.ModelException;
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
			
			// parametros para la invocacion (Argumentos, contexto, transaccion)
			Method method = CustomServiceInterface.class.getMethod(data.getMethodName(), DynamicArgument.class, Properties.class, String.class);
			DynamicArgument arguments = new DynamicArgument();
			arguments.setContent(data.arguments);
			DynamicResult result = (DynamicResult)method.invoke(instance, arguments, Env.getCtx(), getTrxName());
			
			// instanciar el objeto a fin de iniciar el procesamiento 
			CustomServiceResultBean response = new CustomServiceResultBean();
			
			// setear valores de respuesta
			response.setError(result.isError());
			response.setErrorMsg(result.getErrorMsg());
			response.setResult(result.getContent());
			
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
	
}
