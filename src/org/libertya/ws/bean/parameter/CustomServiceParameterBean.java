package org.libertya.ws.bean.parameter;

import java.util.ArrayList;
import java.util.HashMap;

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
	 * 		<code>execute(String param1, String param2, int param3, ArrayList<Integer> param4)</code> <br>
	 * <br>
	 * la invocación <code>execute('foo', 'bar', 43, {x, y, z}) se convierte en</code><br>
	 * <br>
	 * <code>
	 * 	content[0]: param1 = {'foo'}<br>
	 * 	content[1]: param2 = {'bar'}<br>
	 * 	content[2]: param3 = {43}<br>
	 * 	content[3]: param4 = {x, y, z}<br>
	 * </code>
	 */
	public ArrayList<HashMap<String, ArrayList<String>>> arguments = new ArrayList<HashMap<String, ArrayList<String>>>();
	
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
	
	public ArrayList<HashMap<String, ArrayList<String>>> getArguments() {
		return arguments;
	}

	public void setArguments(ArrayList<HashMap<String, ArrayList<String>>> arguments) {
		this.arguments = arguments;
	}
	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n  Dynamic Arguments: ");
		if (arguments != null)
			out.append(arguments.toString());
		return out.toString();
	}

	
}
