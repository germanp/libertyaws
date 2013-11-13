package org.libertya.wse;

import org.libertya.ws.bean.parameter.CustomServiceParameterBean;
import org.libertya.ws.bean.parameter.DocumentParameterBean;
import org.libertya.ws.bean.result.CustomServiceResultBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.handler.CustomServiceHandler;
import org.libertya.ws.handler.InOutDocumentHandler;
import org.libertya.ws.handler.InventoryDocumentHandler;
import org.libertya.wse.common.ListedMap;
import org.libertya.wse.common.SimpleMap;
import org.libertya.wse.param.DocumentLine;
import org.libertya.wse.param.Login;
import org.libertya.wse.result.Result;
import org.libertya.wse.result.SimpleResult;

public class LibertyaWSEImpl implements LibertyaWSE {

	
	/* ===================================================== */
	/* ===================== Remitos ======================= */
	/* ===================================================== */

	@Override
	public SimpleResult inOutCreateCustomer(Login login, SimpleMap[] header, DocumentLine[] lines, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), header, lines);
		ResultBean resultBean = new InOutDocumentHandler().inOutCreateCustomer(bean, bPartnerID, bPartnerValue, taxID, completeInOut);
		SimpleResult result = new SimpleResult();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.toSimpleMap());
		return result;
	}

	@Override
	public SimpleResult inOutCreateVendor(Login login, SimpleMap[] header, DocumentLine[] lines, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), header, lines);
		ResultBean resultBean = new InOutDocumentHandler().inOutCreateVendor(bean, bPartnerID, bPartnerValue, taxID, completeInOut);
		SimpleResult result = new SimpleResult();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.toSimpleMap());
		return result;
	}

	@Override
	public SimpleResult inOutDeleteByID(Login login, SimpleMap[] data, int inOutID) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InOutDocumentHandler().inOutDeleteByID(bean, inOutID);
		SimpleResult result = new SimpleResult();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.toSimpleMap());
		return result;
	}

	@Override
	public SimpleResult inOutDeleteByColumn(Login login, SimpleMap[] data, String columnName, String columnCriteria) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InOutDocumentHandler().inOutDeleteByColumn(bean, columnName, columnCriteria);
		SimpleResult result = new SimpleResult();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.toSimpleMap());
		return result;
	}

	@Override
	public SimpleResult inOutCompleteByID(Login login, SimpleMap[] data, int inOutID) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InOutDocumentHandler().inOutCompleteByID(bean, inOutID);
		SimpleResult result = new SimpleResult();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.toSimpleMap());
		return result;
	}

	@Override
	public SimpleResult inOutCompleteByColumn(Login login, SimpleMap[] data, String columnName, String columnCriteria) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InOutDocumentHandler().inOutCompleteByColumn(bean, columnName, columnCriteria);
		SimpleResult result = new SimpleResult();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.toSimpleMap());
		return result;
	}

	@Override
	public SimpleResult inOutVoidByID(Login login, SimpleMap[] data, int inOutID) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InOutDocumentHandler().inOutVoidByID(bean, inOutID);
		SimpleResult result = new SimpleResult();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.toSimpleMap());
		return result;
	}

	@Override
	public SimpleResult inOutVoidByColumn(Login login, SimpleMap[] data, String columnName, String columnCriteria) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InOutDocumentHandler().inOutVoidByColumn(bean, columnName, columnCriteria);
		SimpleResult result = new SimpleResult();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.toSimpleMap());
		return result;
	}
	
	/* ================================================================== */
	/* ========================= Inventario ============================= */
	/* ================================================================== */

	@Override
	public SimpleResult inventoryCreate(Login login, SimpleMap[] header, DocumentLine[] lines, boolean completeInventory) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), header, lines);
		ResultBean resultBean = new InventoryDocumentHandler().inventoryCreate(bean, completeInventory);
		SimpleResult result = new SimpleResult();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.toSimpleMap());
		return result;
	}

	@Override
	public SimpleResult inventoryCompleteByID(Login login, SimpleMap[] data, int inventoryID) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InventoryDocumentHandler().inventoryCompleteByID(bean, inventoryID);
		SimpleResult result = new SimpleResult();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.toSimpleMap());
		return result;
	}

	@Override
	public SimpleResult inventoryCompleteByColumn(Login login, SimpleMap[] data, String columnName, String value) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InventoryDocumentHandler().inventoryCompleteByColumn(bean, columnName, value);
		SimpleResult result = new SimpleResult();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.toSimpleMap());
		return result;
	}

	@Override
	public SimpleResult inventoryDeleteByID(Login login, SimpleMap[] data, int inventoryID) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InventoryDocumentHandler().inventoryDeleteByID(bean, inventoryID);
		SimpleResult result = new SimpleResult();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.toSimpleMap());
		return result;
	}

	@Override
	public SimpleResult inventoryDeleteByColumn(Login login, SimpleMap[] data, String columnName, String value) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InventoryDocumentHandler().inventoryDeleteByColumn(bean, columnName, value);
		SimpleResult result = new SimpleResult();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.toSimpleMap());
		return result;
	}

	@Override
	public SimpleResult inventoryVoidByID(Login login, SimpleMap[] data, int inventoryID) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InventoryDocumentHandler().inventoryVoidByID(bean, inventoryID);
		SimpleResult result = new SimpleResult();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.toSimpleMap());
		return result;
	}

	@Override
	public SimpleResult inventoryVoidByColumn(Login login, SimpleMap[] data, String columnName, String value) {
		DocumentParameterBean bean = new DocumentParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data, null);
		ResultBean resultBean = new InventoryDocumentHandler().inventoryVoidByColumn(bean, columnName, value);
		SimpleResult result = new SimpleResult();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.toSimpleMap());
		return result;
	}

	/* ================================================================== */
	/* ==================== Funciones de uso general ==================== */
	/* ================================================================== */
	@Override
	public Result customService(Login login, String className, ListedMap[] data) {
		CustomServiceParameterBean bean = new CustomServiceParameterBean(login.getUserName(), login.getPassword(), login.getClientID(), login.getOrgID(), data);
		bean.setClassName(className);
		CustomServiceResultBean resultBean = new CustomServiceHandler().customService(bean);
		Result result = new Result();
		result.setError(resultBean.isError());
		result.setErrorMsg(resultBean.getErrorMsg());
		result.setResultValues(resultBean.getResult());
		return result;
	}
}
