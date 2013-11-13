package org.libertya.ws.bean.parameter;

import java.util.Arrays;

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
	 * 
	 * Se usa esta estructura para cargar los datos y luego se vuelca a la estructura tradicional en el Handler. 
	 * El problema radica en que a Java2WSDL no le gusta el anidamiento HashMap<String, ArrayList<String>> de DynamicArgument
	 * y ésto hace que en el servidor en lugar de generarse un ArrayList dentro de la map, se genere/recupere
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
		rawArguments = arguments;
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
		if (rawArguments != null) {
			for (int i=0; i < rawArguments.length; i++) {
				if (rawArguments[i]!=null) {
					out.append("\n ").append(rawArguments[i].getKey()).append(" : ");
					if (rawArguments[i].getValues()!=null) {
						for (int j=0; j<rawArguments[i].getValues().length; j++) {
							if (rawArguments[i].getValues()[j]!=null)
								out.append(rawArguments[i].getValues()[j]).append(" ");
						}
					}
				}
			}
		}
		return out.toString();
	}

}
