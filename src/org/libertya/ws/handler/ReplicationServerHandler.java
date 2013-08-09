package org.libertya.ws.handler;

import java.util.HashMap;

import org.libertya.ws.bean.parameter.ReplicationParameterBean;
import org.libertya.ws.bean.result.ReplicationResultBean;
import org.libertya.ws.exception.ModelException;
import org.openXpertya.model.MReplicationHost;
import org.openXpertya.replication.ReplicationUtils;
import org.openXpertya.replication.ReplicationXMLUpdater;
import org.openXpertya.util.DB;

public class ReplicationServerHandler extends GeneralHandler {

	/**
	 * Procesa el XML recibido, el cual contiene los eventos de replicación correspondientes
	 * (inserción, eliminación, modificación), y retorna los resultados en cada caso
	 * @param datos a replicar
	 * @return resultado de la ejecución
	 */
	public ReplicationResultBean replicate(ReplicationParameterBean data)
	{
		try
		{
			/* === Configuracion inicial === */
			init(data, new String[]{}, new Object[]{});
			
			/* === Procesar (logica especifica) === */
			// Si no hay información a procesar, no hay mucho más que hacer
			if (data.getActionsXML()==null)
				return new ReplicationResultBean(false, null, new HashMap<String, String>());
			
			String contentXML = ReplicationUtils.decompressString(data.getActionsXML());
			if (contentXML == null || contentXML.length() == 0)
				return new ReplicationResultBean(false, null, new HashMap<String, String>());
			
			// Validar que la posicion destino especificada en los datos recibidos coincida con la posición del host actual
			int thisHostPos = DB.getSQLValue(null, " SELECT replicationarraypos FROM AD_ReplicationHost WHERE thisHost = 'Y' ");
			if (data.getTargetHostPos() != thisHostPos)
				throw new Exception("La posición de replicación del host local (" + thisHostPos + ") no coincide con la posición del host destino especificado en el paquete recibido ("+ data.getTargetHostPos() + ")");
			
        	// ...Procesar el XML recibido y actualizar el repArray
			int sourceOrgID = MReplicationHost.getReplicationOrgForPosition(data.getSourceHostPos(), getTrxName());
        	ReplicationXMLUpdater replicationXMLUpdater = new ReplicationXMLUpdater("<?xml version=\"1.0\" encoding=\"UTF-8\"?> <root> " + contentXML + " </root> ", getTrxName(), sourceOrgID);
            replicationXMLUpdater.processChangeLog();
			
			/* === Commitear transaccion === */ 
			commitTransaction();
			
			/* === Retornar valor === */
			ReplicationResultBean result = new ReplicationResultBean(false, null, new HashMap<String, String>());
			if (replicationXMLUpdater.getEventLog().size() > 0)
				result.setEventLog(replicationXMLUpdater.getEventLog());
			return result;
		}
		catch (ModelException me) {
			return (ReplicationResultBean)processException(me, new ReplicationResultBean(), wsInvocationArguments(data));
		}
		catch (Exception e) {
			return (ReplicationResultBean)processException(e, new ReplicationResultBean(), wsInvocationArguments(data));
		}
		finally	{
			closeTransaction();
		}
	}

}
