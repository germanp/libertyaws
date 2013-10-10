package org.libertya.wse;

import org.libertya.wse.common.ListedMap;
import org.libertya.wse.param.Login;
import org.libertya.wse.result.Result;

public interface LibertyaWSE {

	/**
	 * Wrapper para <code>customService(CustomServiceParameterBean data)</code>
	 */
	public Result customService(Login login, String className, ListedMap[] data);
}
