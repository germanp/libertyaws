package org.libertya.ws.bean.parameter;

import java.util.ArrayList;
import java.util.Arrays;
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
	 * 
	 * IMPORTANTE: Esta es una estructura interna a utilizar.  
	 * 			   Es necesario cargar los argumentos mediante rawArguments.
	 */
	public HashMap<String, ArrayList<String>> arguments = new HashMap<String, ArrayList<String>>();

	/**
	 * Se usa internamente esta estructura para cargar los datos y luego se vuelca a la estructura tradicional.
	 * El problema radica en que a Java2WSDL no le gusta el anidamiento HashMap<String, ArrayList<String>>,
	 * y ésto hace que en el servidor en lugar de generarse un ArrayList dentro de la map, se recuperen
	 * objetos que no son ArrayLists 
	 */
	public ListedMap[] rawArguments = new ListedMap[0]; 
	
	
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

	public ListedMap[] getRawArguments() {
		return rawArguments;
	}

	public void setRawArguments(ListedMap[] rawArguments) {
		this.rawArguments = rawArguments;
	}
	
	public void addParameter(String argName, String ... values) {
		rawArguments = Arrays.copyOf(rawArguments, rawArguments.length+1);
		String[] argVals = new String[values.length];
		int i=0;
		for (String value : values)
			argVals[i++] = value;
		rawArguments[rawArguments.length-1] = new ListedMap();
		rawArguments[rawArguments.length-1].setKey(argName);
		rawArguments[rawArguments.length-1].setValues(argVals);
	}
	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		if (className!=null)
			out.append("\n  ClassName: ").append(className);
		if (methodName!=null)
			out.append("\n  MethodName: ").append(methodName);
		out.append("\n  Dynamic Arguments: ");
		if (arguments != null) {
			for (String argName : arguments.keySet()) {
				if (arguments.get(argName)!=null) {
					out.append("\n ").append(argName).append(" : ");
					ArrayList<String> values = arguments.get(argName); 
					for (String value : values) {
						if (value!=null)
							out.append(value).append(" ");
					}
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
