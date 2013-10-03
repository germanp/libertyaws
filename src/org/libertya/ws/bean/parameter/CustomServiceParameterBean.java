package org.libertya.ws.bean.parameter;

import org.openXpertya.plugin.common.CustomServiceInterface;
import org.openXpertya.plugin.common.DynamicArgument;

public class CustomServiceParameterBean extends ParameterBean {

	/** Nombre de la clase a invocar. */
	public String className = null;
	/** Nombre del metodo a invocar.  Se utiliza el nombre por defecto definido en la interfaz. */
	public String methodName = CustomServiceInterface.DEFAULT_METHOD_NAME;
	/** Nomina dinámica de argumentos. */
	public DynamicArgument arguments = new DynamicArgument(); 
	
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
	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n  Dynamic Arguments: ");
		if (arguments != null)
			out.append(arguments.toString());
		return out.toString();
	}
	
}
