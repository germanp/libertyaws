package org.libertya.wse;

import org.libertya.ws.bean.parameter.CustomServiceParameterBean;
import org.libertya.ws.bean.result.CustomServiceResultBean;
import org.libertya.ws.handler.CustomServiceHandler;
import org.libertya.wse.common.ListedMap;
import org.libertya.wse.param.Login;
import org.libertya.wse.result.Result;

public class LibertyaWSEImpl implements LibertyaWSE {

	public Result customService(Login login, String className, ListedMap[] data) {
		CustomServiceParameterBean bean = new CustomServiceParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		bean.setClassName(className);
		CustomServiceResultBean resultBean = new CustomServiceHandler().customService(bean);
		Result result = new Result();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.toSimpleMap());
		return result;
	}
}
