package org.libertya.ws.handler;

import java.util.ArrayList;
import java.util.HashMap;

import org.compiere.plaf.CompierePLAF;
import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.MultipleDocumentsResultBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.db.CConnection;
import org.openXpertya.model.MUser;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Login;

public class UserCRUDHandler extends GeneralHandler {

	/**
	 * Alta de un usuario
	 * @param data parametros correspondientes
	 * @return ResultBean con OK, ERROR, etc.
	 */
	public ResultBean userCreate(ParameterBean data)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{}, new Object[]{});
			
			/* === Procesar (logica especifica) === */			
			// Instanciar y persistir usuario
			MUser newUser = new MUser(getCtx(), 0, getTrxName());
			setValues(newUser, data.getMainTable(), true);
			if (!newUser.save())
				throw new ModelException("Error al persistir el usuario:" + CLogger.retrieveErrorAsString());

			/* === Commitear transaccion === */ 
			commitTransaction();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("AD_User_ID", Integer.toString(newUser.getAD_User_ID()));
			return new ResultBean(false, null, result);
		}
		catch (ModelException me) {
			return processException(me, wsInvocationArguments(data));
		}
		catch (Exception e) {
			return processException(e, wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}
	
	/**
	 * Recupera un usuario
	 * @param data parametros correspondientes
	 * @param userID si se desea recuperar por este criterio debe ser valor mayor a cero
	 * @return ResultBean con los datos correspondientes
	 */
	public ResultBean userRetrieveByID(ParameterBean data, int userID) {
		return userRetrieve(data, userID, null, null);
	}
	
	/**
	 * Recupera un usuario
	 * @param data parametros correspondientes
	 * @param columnName si se desea recuperar por este criterio, debse ser distinto de null, en conjunto con criteria
	 * @param criteria si se desea recuperar por este criterio, debse ser distinto de null, en conjunto con columnName
	 * @return ResultBean con los datos correspondientes
	 */
	public ResultBean userRetrieveByColumn(ParameterBean data, String columnName, String criteria) {
		return userRetrieve(data, -1, columnName, criteria);
	}
	
	
	/**
	 * Recupera un usuario
	 * @param data parametros correspondientes
	 * @param userID si se desea recuperar por este criterio debe ser valor mayor a cero (o -1 en CC)
	 * @param columnName si se desea recuperar por este criterio debse ser distinto de null (o null en cc), en conjunto con criteria
	 * @param criteria si se desea recuperar por este criterio debse ser distinto de null (o null en cc), en conjunto con columnName
	 * @return ResultBean con los datos correspondientes
	 */
	protected ResultBean userRetrieve(ParameterBean data, int userID, String columnName, String criteria)
	{
		try
		{	
			/* === Configuracion inicial === */
			init(data, new String[]{"userID", "columnName", "criteria"}, new Object[]{userID, columnName, criteria});
			
			/* === Procesar (logica especifica) === */
			// Recuperar el usuario (si existe) por algún criterio. 
			// 1) Buscar por ID o por value (obtener null si no se encuentra)
			MUser aUser = (MUser)getPO("AD_User", userID, columnName, criteria, false, false, true, false);
			if (aUser == null || aUser.getAD_User_ID()==0)
				throw new ModelException("No se ha podido recuperar un usuario a partir de los parametros indicados");

			/* === Retornar valores === */
			ResultBean result = new ResultBean(false, null, poToMap(aUser, true));
			return result;
		}
		catch (ModelException me) {
			return (ResultBean)processException(me, new ResultBean(), wsInvocationArguments(data));
		}
		catch (Exception e) {
			return (ResultBean)processException(e, new ResultBean(), wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}

	}
	
	
	/**
	 * Actualización de un usuario a partir de su ID
	 * @param data parametros correspondientes
	 * @param userID identificador del usuario a modificar
	 * @return ResultBean con OK, ERROR, etc.
	 */
	public ResultBean userUpdateByID(ParameterBean data, int userID)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"userID"}, new Object[]{userID});

			/* === Procesar (logica especifica) === */			
			// Recuperar y persistir el articulo
			MUser aUser = (MUser)getPO("AD_User", userID, null, null, false, true, true, false);
			if (aUser==null || aUser.getAD_User_ID()==0 )
				throw new ModelException("No se ha podido recuperar un usuario a partir de los parametros indicados");
			setValues(aUser, data.getMainTable(), false);
			if (!aUser.save())
				throw new ModelException("Error al actualizar el usuario:" + CLogger.retrieveErrorAsString());

			/* === Commitear transaccion === */ 
			commitTransaction();
			
			/* === Retornar valor === */
			return new ResultBean(false, null, null);
		}
		catch (ModelException me) {
			return processException(me, wsInvocationArguments(data));
		}
		catch (Exception e) {
			return processException(e, wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}

	/**
	 * Eliminación logica de un usuario
	 * @param data parametros correspondientes
	 * @param userID identificador del articulo a eliminar
	 * @return ResultBean con OK, ERROR, etc.
	 */
	public ResultBean userDeleteByID(ParameterBean data, int userID)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"userID"}, new Object[]{userID});

			/* === Procesar (logica especifica) === */			
			// Recuperar y persistir el articulo
			MUser aUser = new MUser(getCtx(), userID, getTrxName());
			if (aUser==null || aUser.getAD_User_ID()==0 )
				throw new ModelException("No se ha podido recuperar un usuario a partir de los parametros indicados");
			aUser.setIsActive(false);
			if (!aUser.save())
				throw new ModelException("Error al eliminar el usuario:" + CLogger.retrieveErrorAsString());

			/* === Commitear transaccion === */ 
			commitTransaction();
			
			/* === Retornar valor === */
			return new ResultBean(false, null, null);
		}
		catch (ModelException me) {
			return processException(me, wsInvocationArguments(data));
		}
		catch (Exception e) {
			return processException(e, wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}
	
	/**
	 * Consulta de acceso de compañías/organizaciones sobre las cuales el usuario puede operar
	 * @param data username y password
	 * @return nomina de accesos
	 */
	public MultipleDocumentsResultBean userClientOrgAccessQuery(ParameterBean data) {
		try
		{
			// Setear un contexto minimo.  Esta operacion es un tanto particular, con lo cual difiere del resto en este aspecto
			setOXPHome();
			saveToLogFile(INFO_LOG, "Ejecutando " + Thread.currentThread().getStackTrace()[1].getMethodName());
		  	
			/* === Procesar (logica especifica) === */			
			ArrayList<HashMap<String, String>> clientOrgAccesses = getAccesses(data.getUserName(), data.getPassword());
			
			// Retornar valores
			MultipleDocumentsResultBean result = new MultipleDocumentsResultBean(false, null, new HashMap<String, String>());
			result.setDocumentHeaders(clientOrgAccesses);
			
			/* === Retornar valor === */
			return result;
		}
		catch (Exception e) {
			return (MultipleDocumentsResultBean)processException(e, new MultipleDocumentsResultBean(), wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}

	/**
	 * Codigo tomado de ALogin.tryConnection() y adecuado.  Dado que el metodo era private,
	 * no queda otra que replicar la lógica aquí a fin de evitar que LYWS dependa de una version más nueva de CORE
	 */
    protected ArrayList<HashMap<String, String>> getAccesses(String m_user, String m_pwd) throws Exception {

        // Establish connection
    	Ini.loadProperties(false);
        DB.setDBTarget( CConnection.get());
        if( !DB.isConnected()) { 
        	throw new Exception("Sin conexion a la base de datos");
        }

        // Reference check
        Ini.setProperty(Ini.P_OXPSYS, "Reference".equalsIgnoreCase(CConnection.get().getDbUid()));

        ArrayList<HashMap<String, String>> retValues = new ArrayList<HashMap<String, String>>(); 
        
        // Roles
        Login m_login = new Login( Env.getCtx() );
        KeyNamePair[] roles = m_login.getRoles( m_user,m_pwd );
        if (roles == null) {
        	throw new Exception("No roles. " + CLogger.retrieveErrorAsString());
        }
        for (KeyNamePair role : roles) {
            // Compañías
        	KeyNamePair[] clients = m_login.getClients( role );
            if (clients == null)
            	continue;
        	for (KeyNamePair client : clients) {
                // Organizaciones
        		KeyNamePair[] orgs = m_login.getOrgs( client );
        		if (orgs == null)
        			continue;
        		for (KeyNamePair org : orgs) {
        			HashMap<String, String> retValue = new HashMap<String, String>();
        			retValue.put("AD_Role_ID", ""+role.getKey());
        			retValue.put("RoleName", role.getName());
        			retValue.put("AD_Client_ID", ""+client.getKey());
        			retValue.put("ClientName", client.getName());
        			retValue.put("AD_Org_ID", ""+org.getKey());
        			retValue.put("OrgName", org.getName());
        			retValues.add(retValue);
        		}
        	}
        }

        return retValues;

    }    
	
}
