package org.libertya.ws.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.libertya.ws.bean.parameter.FilteredColumnsParameterBean;
import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.MultipleRecordsResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_User;
import org.openXpertya.util.DB;

public class GeneralRecordQueryHandler extends GeneralHandler {

	/**
	 * Devuelve una serie de registros de una tabla dada
	 * @param data parametros generales de acceso y columnas a recuperar 
	 * @param tableName Nombre de tabla (M_Product, C_BPartner, C_Order, C_Invoice, M_InOut, C_AllocationHdr, etc.)
	 * @param whereClause criterio de filtrado
	 * @param includeNamedReferences para las foreign keys, devolver además del ID, el name, value o identificador correspondiente al registro referenciado
	 * @return MultipleRecordsResultBean con OK, ERROR, los registros correspondientes
	 */
	public MultipleRecordsResultBean recordQuery(FilteredColumnsParameterBean data, String tableName, String whereClause, boolean includeNamedReferences) 
	{
		try
		{	
			/* === Configuracion inicial === */
			init(data, 	new String[]{"tableName", "whereClause", "includeNamedReferences"}, 
						new Object[]{tableName, whereClause, includeNamedReferences});
			
			/* === Procesar (logica especifica) === */
			// Verificar que no sea la tabla de usuarios
// Se omite la restricción
//			if (X_AD_User.Table_Name.equalsIgnoreCase(tableName))
//				throw new ModelException("Tabla invalida para este servicio");
			
			// Verificar que haya especificado un nombre de tabla
			if (tableName == null || tableName.length()==0)
				throw new ModelException("Debe especificar un nombre de tabla");
			
			// Recuperar los registros de la bbdd 
			M_Table table = M_Table.get(getCtx(), tableName);
			String sql = "SELECT * FROM " + tableName + (whereClause==null ? "" : " WHERE " + whereClause);
			PreparedStatement ps = DB.prepareStatement(sql, getTrxName());
			ResultSet rs = ps.executeQuery();

			// Generar valores.  Iterar por cada registros, instanciar el PO y convertir a map
			MultipleRecordsResultBean result = new MultipleRecordsResultBean(false, null, new HashMap<String, String>());
			while (rs.next()) {
				PO aRecord = table.getPO(rs, getTrxName());
				if (aRecord == null)
					continue;
				HashMap<String, String> map = poToMap(aRecord, includeNamedReferences, null, "", data.getFilterColumns()); 
				result.addRecord(map);
			}
			
			/* === Retornar valores === */
			return result;
		}
		catch (ModelException me) {
			return (MultipleRecordsResultBean)processException(me, new MultipleRecordsResultBean(), wsInvocationArguments(data));
		}
		catch (Exception e) {
			return (MultipleRecordsResultBean)processException(e, new MultipleRecordsResultBean(), wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}
	


	/**
	 * Devuelve una serie de registros de una tabla dada, de manera directa sin instanciar POs ni pasar por diccionario de datos.  Operacion de muy bajo nivel.
	 * @param data parametros generales de acceso y columnas a recuperar (serán las usadas en la consulta)
	 * @param tableName Nombre de tabla (M_Product, C_BPartner, C_Order, C_Invoice, M_InOut, C_AllocationHdr, etc.)
	 * @param whereClause criterio de filtrado
	 * @return MultipleRecordsResultBean con OK, ERROR, los registros correspondientes
	 */
	public MultipleRecordsResultBean recordQueryDirect(FilteredColumnsParameterBean data, String tableName, String whereClause) {
		try
		{	
			/* === Configuracion inicial === */
			init(data, 	new String[]{"tableName", "whereClause"}, 
						new Object[]{tableName, whereClause});
			
			// Verificar que haya especificado un nombre de tabla
			if (tableName == null || tableName.length()==0)
				throw new ModelException("Debe especificar un nombre de tabla");
			
			// Recuperar los registros de la bbdd 
			String sql = "SELECT " + getColumnNames(data) + " FROM " + tableName + (whereClause==null ? "" : " WHERE " + whereClause);
			PreparedStatement ps = DB.prepareStatement(sql, getTrxName());
			ResultSet rs = ps.executeQuery();

			// Generar valores.
			MultipleRecordsResultBean result = new MultipleRecordsResultBean(false, null, new HashMap<String, String>());
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				for (String columnName : data.getFilterColumns()) {
					map.put(columnName, rs.getString(columnName));
				}
				result.addRecord(map);
			}
			
			/* === Retornar valores === */
			return result;
		}
		catch (ModelException me) {
			return (MultipleRecordsResultBean)processException(me, new MultipleRecordsResultBean(), wsInvocationArguments(data));
		}
		catch (Exception e) {
			return (MultipleRecordsResultBean)processException(e, new MultipleRecordsResultBean(), wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}

	/** Genera el query para recuperar las columnas indicadas */
	protected String getColumnNames(FilteredColumnsParameterBean data) {
		StringBuffer retValue = new StringBuffer();
		int totalColumns = data.getFilterColumns().size();
		int count = 0;
		for (String column : data.getFilterColumns()) {
			count++;
			retValue.append(column);
			if (count<totalColumns)
				retValue.append(",");
		}
		return retValue.toString();
	}
	
}
