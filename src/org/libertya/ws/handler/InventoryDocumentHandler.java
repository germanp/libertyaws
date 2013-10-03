package org.libertya.ws.handler;

import org.libertya.ws.bean.parameter.DocumentParameterBean;
import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.ResultBean;

public class InventoryDocumentHandler extends GeneralHandler {

	public ResultBean inventoryCreate(DocumentParameterBean data, boolean completeInventory) {
		// TODO: Implementation pending
		return null;
	}

	public ResultBean inventoryCompleteByID(ParameterBean data, int inventoryID) {
		// TODO: Implementation pending		
		return null;
	}

	public ResultBean inventoryCompleteByColumn(ParameterBean data, String columnName, String value) {
		// TODO: Implementation pending
		return null;
	}

	public ResultBean inventoryDeleteByID(ParameterBean data, int inventoryID) {
		// TODO: Implementation pending
		return null;
	}

	public ResultBean inventoryDeleteByColumn(ParameterBean data, String columnName, String value) {
		// TODO: Implementation pending
		return null;
	}

	public ResultBean inventoryVoidByID(ParameterBean data, int inventoryID) {
		// TODO: Implementation pending
		return null;
	}

	public ResultBean inventoryVoidByColumn(ParameterBean data, String columnName, String value) {
		// TODO: Implementation pending
		return null;
	}
}
