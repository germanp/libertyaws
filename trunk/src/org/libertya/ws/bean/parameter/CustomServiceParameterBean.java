package org.libertya.ws.bean.parameter;

import java.util.ArrayList;
import java.util.HashMap;

import org.libertya.wse.common.ListedMap;
import org.openXpertya.plugin.common.CustomServiceInterface;

public class CustomServiceParameterBean extends ParameterBean {

	/** Nombre de la clase a invocar. */
	public String className = null;
	/** Nombre del metodo a invocar.  Se utiliza el nombre por defecto definido en la interfaz. */
	public String methodName = CustomServiceInterface.DEFAULT_METHOD_NAME;
	/** 
	 * Nomina dinámica de argumentos. 
	 * 	La misma es una lista que contiene una map con el nombre del parametro y su valor, 
	 * 	el cual puede bien ser un unico valor o una lista, dependiendo el caso <br>
	 * <br>
	 * Ejemplo: para el metodo con los siguientes parámetros:<br>	
	 * <br>
	 * 		<code>execute(String param1, String param2, int param3, Integer[] param4)</code> <br>
	 * <br>
	 * la invocación <code>execute('foo', 'bar', 43, {9, 8, 7}) se convierte en</code><br>
	 * <br>
	 * <code>
	 * 	param1 = {'foo'}<br>
	 * 	param2 = {'bar'}<br>
	 * 	param3 = {'43'}<br>
	 * 	param4 = {'9', '8', '7'}<br>
	 * </code>
	 */
	public HashMap<String, ArrayList<String>> arguments = new HashMap<String, ArrayList<String>>();
	
	/**
	 * Constructor por defecto.  Ver superclase.
	 */
	public CustomServiceParameterBean() {
		super();
	}
	
	/**
	 * Constructor por defecto.  Ver superclase.
	 */
	public CustomServiceParameterBean(String userName, String password, int clientID, int orgID) {
		super(userName, password, clientID, orgID);
	}
	
	/**
	 * Constructor para wrapper
	 */
	public CustomServiceParameterBean(String userName, String password, int clientID, int orgID, ListedMap[] arguments) {
		super(userName, password, clientID, orgID);
		load(arguments);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public HashMap<String, ArrayList<String>> getArguments() {
		return arguments;
	}

	public void setArguments(HashMap<String, ArrayList<String>> arguments) {
		this.arguments = arguments;
	}
	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n  Dynamic Arguments: ");
		if (arguments != null)
			for (String argName : arguments.keySet()) {
				if (arguments.get(argName)!=null) {
					out.append("\n ").append(argName).append(" : ");
					for (String value : arguments.get(argName)) {
						out.append(value).append(" ");
					}
				}
			}
		return out.toString();
	}

	public void load(ListedMap[] arguments) {
		for (ListedMap listedMap : arguments) {
			ArrayList<String> aMapValue = new ArrayList<String>(); 
			if (aMapValue != null) {
				for (String value : listedMap.getValues())
					aMapValue.add(value);
			}
			this.arguments.put(listedMap.getKey(), aMapValue);
		}
	}
	
}
