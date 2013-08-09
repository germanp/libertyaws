package org.libertya.ws.utils;

import java.io.Serializable;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.InvocationHandler;

/**
 * Interceptor for Server context properties that delegate to the threadlocal instance
 */
public class ServerContextCallback implements InvocationHandler, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6708635918931322152L;

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		LYWSServerContext context = LYWSServerContext.getCurrentInstance();
		//optimize for the 2 most common access
		if (method.getName().equals("getProperty")) {
			Class<?>[] types = method.getParameterTypes();
			if (types != null && types.length == 1 && types[0] == String.class &&
				args != null && args.length == 1 && args[0] instanceof String) {
				return context.getProperty((String)args[0]);
			}
			else if (types != null && types.length == 2 && types[0] == String.class &&
					types[1] == String.class && args != null && args[0] instanceof String &&
					args[1] instanceof String)
				return context.getProperty((String)args[0], (String)args[1]);
		}
		Method m = context.getClass().getMethod(method.getName(), method.getParameterTypes());
		return m.invoke(context, args);
	}

}
