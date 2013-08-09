package org.libertya.ws.handler;

import java.util.HashMap;

import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.MUser;
import org.openXpertya.util.CLogger;

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
	 * @param userID si se desea recuperar por este criterio debe ser valor mayor a cero (o -1 en CC)
	 * @param name si se desea recuperar por este criterio debse ser distinto de null (o null en cc)
	 * @return ResultBean con los datos correspondientes
	 */
	public ResultBean userRetrieveByID(ParameterBean data, int userID)
	{
		try
		{	
			/* === Configuracion inicial === */
			init(data, new String[]{"userID"}, new Object[]{userID});
			
			/* === Procesar (logica especifica) === */
			// Recuperar el articulo (si existe) por algún criterio. 
			// 1) Buscar por ID o por value (obtener null si no se encuentra)
			MUser aUser = (MUser)getPO("AD_User", userID, null, null, false, true, true, false);
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

}
