package org.libertya.ws.handler;

import java.sql.Timestamp;
import java.util.HashMap;

import org.libertya.ws.bean.parameter.BPartnerParameterBean;
import org.libertya.ws.bean.parameter.ParameterBean;
import org.libertya.ws.bean.result.BPartnerResultBean;
import org.libertya.ws.bean.result.ResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MUser;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

public class BPartnerCRUDHandler extends GeneralHandler {

	/**
	 * Alta de una entidad comercial
	 * """""""""""""""""""""""""""""
	 * @param data parametros correspondientes
	 * @return ResultBean con OK, ERROR, etc.
	 */
	public ResultBean bPartnerCreate(BPartnerParameterBean data)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{}, new Object[]{});
			
			/* === Procesar (logica especifica) === */			
			// Instanciar y persistir BPartner
			MBPartner newBPartner = new MBPartner(getCtx(), 0, getTrxName());
			// Cargar los valores de la map, contemplando eventuales valores por defecto
			setValues(newBPartner, data.getBPartnerColumns(), true, false, true);
			if (!newBPartner.save())
				throw new ModelException("Error al persistir entidad comercial:" + CLogger.retrieveErrorAsString());
			// Instanciar y persistir Location
			MLocation newLocation = new MLocation(getCtx(), 0, getTrxName());
			setValues(newLocation, data.getLocationColumns(), true);
			if (!newLocation.save())
				throw new ModelException("Error al persistir la dirección:" + CLogger.retrieveErrorAsString());
			// Instanciar y persistir BPartnerLocation			
			MBPartnerLocation newBPartnerLocation = new MBPartnerLocation(newBPartner);
			newBPartnerLocation.setC_Location_ID(newLocation.getC_Location_ID());
			setValues(newBPartnerLocation, data.getLocationColumns(), true);
			if (!newBPartnerLocation.save())
				throw new ModelException("Error al persistir BPartnerLocation:" + CLogger.retrieveErrorAsString());
			
			/* === Commitear transaccion === */ 
			commitTransaction();
			
			/* === Retornar valor === */
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("C_BPartner_ID", Integer.toString(newBPartner.getC_BPartner_ID()));
			result.put("C_BPartner_Location_ID", Integer.toString(newBPartnerLocation.getC_BPartner_Location_ID()));
			return new ResultBean(false, null, result);
		}
		catch (ModelException me) {
			// En caso de error por CUIT duplicado, incorporar dicha información al resultado 
			return appendBPartnerIDFromTaxID(processException(me, wsInvocationArguments(data)), toLowerCaseKeys(data.getBPartnerColumns()).get("taxid"), data.getClientID()); 
		}
		catch (Exception e) {
			return processException(e, wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}
	
	/**
	 * Baja logica de una entidad comercial
	 * """""""""""""""""""""""""""""""""""" 
	 * @param data parametros correspondientes
	 * @param bPartnerID identificador de la entidad comercial a eliminar
	 * @return ResultBean con OK, ERROR, etc.
	 */
	public ResultBean bPartnerDelete(ParameterBean data, int bPartnerID)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"bPartnerID"}, new Object[]{bPartnerID});
			
			/* === Procesar (logica especifica) === */
			// Recuperar el bPartner (si existe) y setear isActive a falso
			if (bPartnerID<=0)
				throw new ModelException("Se debe indicar bPartnerID mayor a cero");
			MBPartner aBPartner = new MBPartner(getCtx(), bPartnerID, getTrxName());
			if (aBPartner.getC_BPartner_ID()==0)
				throw new ModelException("ID de entidad comercial indicada (" + bPartnerID + ") no encontrada");
			aBPartner.setIsActive(false);
			if (!aBPartner.save())
				throw new ModelException("Error al eliminar entidad comercial:" + CLogger.retrieveErrorAsString());
			
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
	 * Recuperacion de una entidad comercial por ID
	 */
	public BPartnerResultBean bPartnerRetrieveByID(ParameterBean data, int bPartnerID) {
		return bPartnerRetrieve(data, bPartnerID, null, null);	
	}

	/**
	 * Recuperacion de una entidad comercial por Value
	 */
	public BPartnerResultBean bPartnerRetrieveByValue(ParameterBean data, String value) {
		return bPartnerRetrieve(data, -1, value, null);
	}

	/**
	 * Recuperacion de una entidad comercial por TaxID
	 */
	public BPartnerResultBean bPartnerRetrieveByTaxID(ParameterBean data, String taxID) {
		return bPartnerRetrieve(data, -1, null, taxID);
	}
	
	/**
	 * Recupera de una entidad comercial
	 * """""""""""""""""""""""""""""""""
	 * @param data parametros correspondientes
	 * @param bPartnerID si se desea recuperar por este criterio debe ser valor mayor a cero (o -1 en CC)
	 * @param value si se desea recuperar por este criterio debse ser distinto de null (o null en cc)
	 * @param taxID si se desea recuperar por este criterio debse ser distinto de null (o null en cc)
	 * @return BPartnerResultBean con los datos correspondientes
	 */
	protected BPartnerResultBean bPartnerRetrieve(ParameterBean data, int bPartnerID, String value, String taxID)
	{
		try
		{	
			/* === Configuracion inicial === */
			init(data, new String[]{"bPartnerID", "value", "taxID"}, new Object[]{bPartnerID, value, taxID});
			
			/* === Procesar (logica especifica) === */
			// Recuperar el bPartner (si existe) por algún criterio. 
			// 1) Buscar por ID o por value (obtener null si no se encuentra)
			MBPartner aBPartner = (MBPartner)getPO("C_BPartner", bPartnerID, "value", value, false, true, true, false);
			if (aBPartner == null)
				// 2) Buscar por taxID (obtener null si no se encuentra)
				aBPartner = (MBPartner)getPO("C_BPartner", -1, "taxID", taxID, false, true, true, false);
			if (aBPartner == null || aBPartner.getC_BPartner_ID()==0)
				throw new ModelException("No se ha podido recuperar una entidad comercial indicada a partir de los parametros indicados");

			// Recuperar la última dirección de facturación en el sistema
			HashMap<String, String> locationMap = new HashMap<String, String>();
			MBPartnerLocation bPartnerLocation = getBPartnerLocationFromBPartner(aBPartner.getC_BPartner_ID(), true);
			if (bPartnerLocation!=null)
			{
				// Incorporar a la map de direccion de facturacion la info de C_BPartner_Location
				locationMap = poToMap(bPartnerLocation, true);
				// Incorporar a la map de direccion de facturacion la info de C_Location	
				MLocation location = MLocation.get(getCtx(), bPartnerLocation.getC_Location_ID(), getTrxName());
				if (location != null)
					locationMap.putAll(poToMap(location, true));
			}
			
			// Recuperar los datos de contacto
			MUser users[] = MUser.getOfBPartner(getCtx(), aBPartner.getC_BPartner_ID());
			HashMap<String, String> userMap = new HashMap<String, String>();
			int pos = -1;
			Timestamp newest = null;
			for (int i=0; i<users.length; i++)
				if (users[i].getName()!=null && users[i].getName().length()>0 && 
					(newest == null || users[i].getCreated().compareTo(newest) > 0)) {
					pos = i;
					newest = users[i].getCreated();
				}
			if (users.length>0 && pos>=0)
				userMap = poToMap(users[pos], true);
			
			/* === Retornar valores === */
			BPartnerResultBean result = new BPartnerResultBean(false, null, poToMap(aBPartner, true));
			result.setBillAddress(locationMap);
			result.setMoreAddresses(MBPartnerLocation.getForBPartner(getCtx(), aBPartner.getC_BPartner_ID()).length>1);
			result.setUserContact(userMap);
			return result;
		}
		catch (ModelException me) {
			return (BPartnerResultBean)processException(me, new BPartnerResultBean(), wsInvocationArguments(data));
		}
		catch (Exception e) {
			return (BPartnerResultBean)processException(e, new BPartnerResultBean(), wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}

	}

	/**
	 * Actualización de una entidad comercial
	 * @param data parametros correspondientes
	 * @param bPartnerID identificador de la entidad comercial a modificar
	 * @param bPartnerLocationID identificador de la direccion de la entidad comercial (0 si no se desea modificar)
	 * @return ResultBean con OK, ERROR, etc.
	 */
	public ResultBean bPartnerUpdate(BPartnerParameterBean data, int bPartnerID, int bPartnerLocationID)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{"bPartnerID", "bPartnerLocationID"}, new Object[]{bPartnerID, bPartnerLocationID});

			/* === Procesar (logica especifica) === */			
			// Recuperar y persistir BPartner
			MBPartner aBPartner = new MBPartner(getCtx(), bPartnerID, getTrxName());
			if (aBPartner.getC_BPartner_ID()==0)
				throw new ModelException("ID de entidad comercial indicada (" + bPartnerID + ") no encontrada");
			setValues(aBPartner, data.getBPartnerColumns(), false);
			if (!aBPartner.save())
				throw new ModelException("Error al actualizar la entidad comercial:" + CLogger.retrieveErrorAsString());
			// Recuperar y persistir BPartnerLocation
			MBPartnerLocation aBPartnerLocation = null;
			if (bPartnerLocationID > 0)
			{
				aBPartnerLocation = new MBPartnerLocation(getCtx(), bPartnerLocationID, getTrxName());
				if (aBPartnerLocation.getC_BPartner_Location_ID()==0)
					throw new ModelException("ID de dirección de entidad comercial indicada (" + bPartnerLocationID + ") no encontrada");
				setValues(aBPartnerLocation, data.getLocationColumns(), false);
				if (!aBPartnerLocation.save())
					throw new ModelException("Error al actualizar BPartnerLocation:" + CLogger.retrieveErrorAsString());
			}			
			// Recuperar y persistir Location (si corresponde)
			if (bPartnerLocationID > 0 && aBPartnerLocation!=null)
			{
				MLocation aLocation = new MLocation(getCtx(), aBPartnerLocation.getC_Location_ID(), getTrxName());
				setValues(aLocation, data.getLocationColumns(), false);
				if (!aLocation.save())
					throw new ModelException("Error al actualizar la dirección:" + CLogger.retrieveErrorAsString());
			}
			
			/* === Commitear transaccion === */ 
			commitTransaction();
			
			/* === Retornar valor === */
			return new ResultBean(false, null, null);
		}
		catch (ModelException me) {
			// En caso de error por CUIT duplicado, incorporar dicha información al resultado 
			return appendBPartnerIDFromTaxID(processException(me, wsInvocationArguments(data)), toLowerCaseKeys(data.getBPartnerColumns()).get("taxid"), data.getClientID()); 

		}
		catch (Exception e) {
			return processException(e, wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}

	/**
	 * Devuelve un MBPartnerLocation a partir de una entidad comercial dada, priorizando siempre la más recientemente creada
	 * @param aBPartner ID de E.C. de referencia
	 * @param billToOnly restringir unicamente a direcciones de facturacion
	 * @return una instancia de MBPartnerLocation si se pudo recuperar una, o null en CC
	 */
	protected MBPartnerLocation getBPartnerLocationFromBPartner(int bPartnerID, boolean billToOnly) {
		// Obtener todos los bPartnerLocations de la E.C.
		MBPartnerLocation[] bPartnerLocations = MBPartnerLocation.getForBPartner(getCtx(), bPartnerID);
		int pos = -1;
		Timestamp newest = null;
		// Iterar por las mismas y quedarme la adecueda
		for (int i=0; i<bPartnerLocations.length; i++)
			if (bPartnerLocations[i].isActive() && (!billToOnly || bPartnerLocations[i].isBillTo()) && (newest == null || bPartnerLocations[i].getCreated().compareTo(newest) > 0)) {
				pos = i;
				newest = bPartnerLocations[i].getCreated();
			}
		// Si pude recuperar una, devolverla, o devolver null en cc.
		if (bPartnerLocations.length>0 && pos>=0)
			return bPartnerLocations[pos];
		return null;
	}
	
	
	/**
	 * Adiciona al resultado el C_BPartner_ID recuperado a partir del taxID, unicamente
	 * si LocaleAR se encuentra activo y el taxID efectivamente es un CUIT.
	 * Incorpora además el C_BPartner_Location_ID correspondiente a la EC, priorizando 
	 * 	la dirección de facturación más recientemente creada
	 * @param result resultado original donde adicional el dato 
	 * @param taxID CUIT a usar como base para la búsqueda
	 * @param clientID compañía por la cual filtrar el dato
	 * @return el mismo ResultBean, pero aumentado con el dato C_BPartner_ID 
	 */
	protected ResultBean appendBPartnerIDFromTaxID(ResultBean result, String taxID, int clientID) {
		// Si LocaleAR no esta activo, o bien el taxID indicado no es un CUIT valido, retornar el result tradicional
		if (!CalloutInvoiceExt.ComprobantesFiscalesActivos() || !CalloutInvoiceExt.ValidarCUIT(taxID))
			return result;
		
		// Quitar guiones tanto en la aguja como en el pajar; intentar recuperar un bPartnerID y agregarlo a la informacion de retorno
		taxID = taxID.replaceAll("-", "");
		int bPartnerID = DB.getSQLValue(null, " SELECT C_BPartner_ID FROM C_BPartner WHERE replace(taxID, '-', '') = '" + taxID + "' AND AD_Client_ID = " + clientID );
		if (bPartnerID > 0) {
			// Setear C_BPartner_ID como parte del resultado
			result.getMainResult().put("C_BPartner_ID", Integer.toString(bPartnerID));
			
			// Setear C_BPartner_Location_ID como parte del resultado (buscando una dir. de facturacion, y si no una cualquiera)
			MBPartnerLocation bPartnerLocation = getBPartnerLocationFromBPartner(bPartnerID, true);
			if (bPartnerLocation==null)
				bPartnerLocation = getBPartnerLocationFromBPartner(bPartnerID, false);
			if (bPartnerLocation!=null)
				result.getMainResult().put("C_BPartner_Location_ID", Integer.toString(bPartnerLocation.getC_BPartner_Location_ID()));
		}
		return result;
	}
}
