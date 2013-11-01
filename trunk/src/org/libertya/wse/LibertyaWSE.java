package org.libertya.wse;

import org.libertya.wse.common.ListedMap;
import org.libertya.wse.common.SimpleMap;
import org.libertya.wse.param.DocumentLine;
import org.libertya.wse.param.Login;
import org.libertya.wse.result.Result;
import org.libertya.wse.result.SimpleResult;

public interface LibertyaWSE {

	
	/* ===================================================== */
	/* ===================== Remitos ======================= */
	/* ===================================================== */

	/**
	 * Wrapper para <code>inOutCreateCustomer(DocumentParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut)</code>
	 */
	public SimpleResult inOutCreateCustomer(Login login, SimpleMap[] header, DocumentLine[] lines, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut);

	/**
	 * Wrapper para <code>inOutCreateVendor(DocumentParameterBean data, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut)</code>
	 */
	public SimpleResult inOutCreateVendor(Login login, SimpleMap[] header, DocumentLine[] lines, int bPartnerID, String bPartnerValue, String taxID, boolean completeInOut);

	/**
	 * Wrapper para <code>inOutDeleteByID(ParameterBean data, int inOutID)</code>
	 */
	public SimpleResult inOutDeleteByID(Login login, SimpleMap[] data, int inOutID);

	/**
	 * Wrapper para <code>inOutDeleteByColumn(ParameterBean data, String columnName, String columnCriteria)</code>
	 */
	public SimpleResult inOutDeleteByColumn(Login login, SimpleMap[] data, String columnName, String columnCriteria);

	/**
	 * Wrapper para <code>inOutCompleteByID(ParameterBean data, int inOutID)</code>
	 */
	public SimpleResult inOutCompleteByID(Login login, SimpleMap[] data, int inOutID);

	/**
	 * Wrapper para <code>inOutCompleteByColumn(ParameterBean data, String columnName, String columnCriteria)</code>
	 */
	public SimpleResult inOutCompleteByColumn(Login login, SimpleMap[] data, String columnName, String columnCriteria);

	/**
	 * Wrapper para <code>inOutVoidByID(ParameterBean data, int inOutID)</code>
	 */
	public SimpleResult inOutVoidByID(Login login, SimpleMap[] data, int inOutID);
	
	/**
	 * Wrapper para <code>inOutVoidByColumn(ParameterBean data, String columnName, String columnCriteria)</code>
	 */
	public SimpleResult inOutVoidByColumn(Login login, SimpleMap[] data, String columnName, String columnCriteria);
	
	
	/* ================================================================== */
	/* ========================= Inventario ============================= */
	/* ================================================================== */
	
	/**
	 * Wrapper para <code>inventoryCreate(DocumentParameterBean data, boolean completeInventory)</code>
	 */
	public SimpleResult inventoryCreate(Login login, SimpleMap[] header, DocumentLine[] lines, boolean completeInventory);
	
	/**
	 * Wrapper para <code>inventoryCompleteByID(ParameterBean data, int inventoryID)</code>
	 */
	public SimpleResult inventoryCompleteByID(Login login, SimpleMap[] data, int inventoryID);
	
	/**
	 * Wrapper para <code>inventoryCompleteByColumn(ParameterBean data, String columnName, String value)</code>
	 */
	public SimpleResult inventoryCompleteByColumn(Login login, SimpleMap[] data, String columnName, String value);
	
	/**
	 * Wrapper para <code>inventoryDeleteByID(ParameterBean data, int inventoryID)</code>
	 */
	public SimpleResult inventoryDeleteByID(Login login, SimpleMap[] data, int inventoryID);
	
	/**
	 * Wrapper para <code>inventoryDeleteByColumn(ParameterBean data, String columnName, String value)</code>
	 */
	public SimpleResult inventoryDeleteByColumn(Login login, SimpleMap[] data, String columnName, String value);
		
	/**
	 * Wrapper para <code>inventoryVoidByID(ParameterBean data, int inventoryID)</code>
	 */
	public SimpleResult inventoryVoidByID(Login login, SimpleMap[] data, int inventoryID);
	
	/**
	 * Wrapper para <code>inventoryVoidByColumn(ParameterBean data, String columnName, String value)</code>
	 */
	public SimpleResult inventoryVoidByColumn(Login login, SimpleMap[] data, String columnName, String value);
	
	
	/* ================================================================== */
	/* ==================== Funciones de uso general ==================== */
	/* ================================================================== */
	
	/**
	 * Wrapper para <code>customService(CustomServiceParameterBean data)</code>
	 */
	public Result customService(Login login, String className, ListedMap[] data);
}
