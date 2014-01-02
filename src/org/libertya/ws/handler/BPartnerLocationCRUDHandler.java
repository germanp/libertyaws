package org.libertya.ws.handler;

import java.util.HashMap;

import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MLocation;
import org.openXpertya.util.CLogger;

public class BPartnerLocationCRUDHandler extends GeneralHandler {

	/**
	 * Alta de una dirección de entidad comercial
	 * @param data parametros correspondientes a la direccion
	 * @return ResultBean con OK C_BPartner_Location_ID o ERROR en caso de error.
	 */
	public ResultBean bPartnerLocationCreate(ParameterBean data) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{}, new Object[]{});
			
			/* === Procesar (logica especifica) === */
			// Recuperar el bPartner
			int bPartnerID = -1;
			MBPartner aBPartner = null;
			try {
				bPartnerID = Integer.parseInt(toLowerCaseKeys(data.getMainTable()).get("c_bpartner_id"));
				aBPartner = (MBPartner)getPO("C_BPartner", bPartnerID, null, null, false, true, true, true);
			} catch (Exception e) {
				throw new ModelException(" Error al recuperar el c_bpartner_id:" + e.getMessage());
			}
			
			// Instanciar y persistir Location
			MLocation newLocation = new MLocation(getCtx(), 0, getTrxName());
			setValues(newLocation, data.getMainTable(), true);
			if (!newLocation.save())
				throw new ModelException("Error al persistir la dirección:" + CLogger.retrieveErrorAsString());
			
			// Instanciar y persistir BPartnerLocation			
			MBPartnerLocation newBPartnerLocation = new MBPartnerLocation(aBPartner);
			newBPartnerLocation.setC_Location_ID(newLocation.getC_Location_ID());
			setValues(newBPartnerLocation, data.getMainTable(), true);
			if (!newBPartnerLocation.save())
				throw new ModelException("Error al persistir BPartnerLocation:" + CLogger.retrieveErrorAsString());
			
			/* === Commitear transaccion === */ 
			commitTransaction();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("C_BPartner_Location_ID", Integer.toString(newBPartnerLocation.getC_BPartner_Location_ID()));
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
	 * Actualización de una dirección de entidad comercial
	 * @param data parametros correspondientes a la direccion
	 * @param bPartnerLocationID id de la dirección de la EC
	 * @return ResultBean con OK o ERROR en caso de error.
	 */
	public ResultBean bPartnerLocationUpdate(ParameterBean data, int bPartnerLocationID) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"bPartnerLocationID"}, new Object[]{bPartnerLocationID});
			
			/* === Procesar (logica especifica) === */
			// Instanciar y persistir BPartnerLocation			
			MBPartnerLocation aBPartnerLocation = (MBPartnerLocation)getPO("C_BPartner_Location", bPartnerLocationID, null, null, false, true, true, true);
			setValues(aBPartnerLocation, data.getMainTable(), true);
			if (!aBPartnerLocation.save())
				throw new ModelException("Error al actualizar BPartnerLocation:" + CLogger.retrieveErrorAsString());

			// Instanciar y persistir Location
			MLocation aLocation = (MLocation)getPO("C_Location", aBPartnerLocation.getC_Location_ID(), null, null, false, true, true, true);
			setValues(aLocation, data.getMainTable(), true);
			if (!aLocation.save())
				throw new ModelException("Error al actualizar la dirección:" + CLogger.retrieveErrorAsString());
			
			/* === Commitear transaccion === */ 
			commitTransaction();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
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
	 * Eliminación logica de una dirección de entidad comercial
	 * @param data parametros de acceso
	 * @param bPartnerLocationID id de la dirección de la EC
	 * @return ResultBean con OK o ERROR en caso de error.
	 */
	public ResultBean bPartnerLocationDelete(ParameterBean data, int bPartnerLocationID) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"bPartnerLocationID"}, new Object[]{bPartnerLocationID});
			
			/* === Procesar (logica especifica) === */
			// Recuperar el BPartnerLocation			
			MBPartnerLocation aBPartnerLocation = (MBPartnerLocation)getPO("C_BPartner_Location", bPartnerLocationID, null, null, false, true, true, true);
			aBPartnerLocation.setIsActive(false);
			if (!aBPartnerLocation.save())
				throw new ModelException("Error al desactivar BPartnerLocation:" + CLogger.retrieveErrorAsString());

			// Recuperar el Location y desactivarlo
			MLocation aLocation = (MLocation)getPO("C_Location", aBPartnerLocation.getC_Location_ID(), null, null, false, true, true, true);
			aLocation.setIsActive(false);
			if (!aLocation.save())
				throw new ModelException("Error al desactivar la dirección:" + CLogger.retrieveErrorAsString());
			
			/* === Commitear transaccion === */ 
			commitTransaction();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
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
	 * Recuperar una dirección de entidad comercial
	 * @param data parametros de acceso
	 * @param bPartnerLocationID id de la dirección de la EC
	 * @return ResultBean con el detalle o ERROR en caso de error.
	 */
	public ResultBean bPartnerLocationRetrieve(ParameterBean data, int bPartnerLocationID) 
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"bPartnerLocationID"}, new Object[]{bPartnerLocationID});
			
			/* === Procesar (logica especifica) === */
			// Recuperar el BPartnerLocation y el Location 
			MBPartnerLocation aBPartnerLocation = (MBPartnerLocation)getPO("C_BPartner_Location", bPartnerLocationID, null, null, false, true, true, true);
			MLocation aLocation = (MLocation)getPO("C_Location", aBPartnerLocation.getC_Location_ID(), null, null, false, true, true, true);

			/* === Retornar valor === */
			// Incluir en result la info de aBPartnerLocation y luego adicionarle la info de aLocation
			HashMap<String, String> result = poToMap(aBPartnerLocation, true);
			poToMap(aLocation, true, result, null, null);
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

}
