package org.libertya.ws.client;

import java.rmi.RemoteException;
import java.util.Vector;
import java.util.logging.Level;

import org.libertya.ws.bean.parameter.ReplicationParameterBean;
import org.libertya.ws.bean.result.ReplicationResultBean;
import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MChangeLog;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.MReplicationHost;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.replication.AbstractReplicationProcess;
import org.openXpertya.replication.ReplicationBuilderWS;
import org.openXpertya.replication.ReplicationConstants;
import org.openXpertya.replication.ReplicationConstantsWS;
import org.openXpertya.replication.ReplicationTableManager;
import org.openXpertya.replication.ReplicationUtils;
import org.openXpertya.util.DB;
import org.openXpertya.util.EMail;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trx;

import ws.libertya.org.LibertyaWSServiceLocator;
import ws.libertya.org.LibertyaWSSoapBindingStub;

public class ReplicationClientProcess extends AbstractReplicationProcess {

	/** Discriminacion entre proceso cliente y servicio WS */
	public static final String CLIENT_PROCESS = "[WSCLIENT] ";

	@Override
	protected String doIt() throws Exception {
		try
		{
			// Verificar marca de "este host"
			if (thisOrgID == -1)
				throw new Exception (" Sin marca de host.  Debe realizar la configuración correspondiente en la ventana Hosts de Replicación ");

			// Verificar si hay que enviar un mail de aviso por acumulacion de registros pendientes a replicar
			checkForWarning();
			
			// Limpiar la tabla de errores viejos
			emptyOldErrorsLog();
			
			// Instanciar el builder encargado de generar los XMLs a enviar a las colas
			ReplicationBuilderWS builder = new ReplicationBuilderWS(null, this);
			
			// El builder se encarga de generar los XMLs para cada host destino y cargar el replicationActionsForHost
			builder.fillDocument();

			/* Iterar por todas los hosts */
			int[] orgs = PO.getAllIDs("AD_Org", " AD_Client_ID = " + getAD_Client_ID() + " AND AD_Org_ID != " + thisOrgID, null);
			for (int i=0; i<orgs.length; i++)
			{
				// Recuperar posicion del host en el repArray
				int targetHost = MReplicationHost.getReplicationPositionForOrg(orgs[i], null);
				if (targetHost == -1 || builder.replicationActionsForHost.get(targetHost)==null)
					continue;
				if (!MReplicationHost.isHostActive(orgs[i], null)) {
					saveLog(Level.INFO, false, "Omitiendo. Host inactivo: ", targetHost, true);
					continue;
				}
				// Invocar al WS agrupando un conjunto de acciones según el máximo en ReplicationConstantsWS.EVENTS_PER_CALL
				for (int currentFrame = 0; currentFrame < builder.replicationActionsForHost.get(targetHost).size(); currentFrame+=ReplicationConstantsWS.EVENTS_PER_CALL) 
				{
					try {
						// Cargar la nomina de acciones
						StringBuffer actionsXML = new StringBuffer();
						for (int currentPos = currentFrame; currentPos < currentFrame + ReplicationConstantsWS.EVENTS_PER_CALL && currentPos < builder.replicationActionsForHost.get(targetHost).size(); currentPos++) 
							actionsXML.append(builder.completeReplicationXMLData.get(builder.replicationActionsForHost.get(targetHost).get(currentPos)));	
						
						// Iniciar la transacción
		        		rep_trxName = Trx.createTrxName();
		        		Trx.getTrx(rep_trxName).start();

		        		// Invocar al WS y actualizar los registros según corresponda
		        		saveLog(Level.INFO, false, "Replicando registros hacia Host: ", targetHost, true);
		        		callWSReplication(orgs[i], actionsXML, rep_trxName, ReplicationConstantsWS.TIME_OUT_BASE + ReplicationConstantsWS.EVENTS_PER_CALL * ReplicationConstantsWS.TIME_OUT_EXTRA_FACTOR);
		        		
		        		// Commitear la transaccion
		        		Trx.getTrx(rep_trxName).commit();
					}
					catch (RemoteException e) {
	            		String error = "WARNING.  Error remoto al invocar el WS: " + e + ". Error: " + e.getMessage();
	            		saveLog(Level.SEVERE, true, error, targetHost, true);
		            	Trx.getTrx(rep_trxName).rollback();
		            	break;		// No tiene sentido seguir intentando con este host
					}
					catch (Exception e) {
	            		String error = "WARNING.  Error inesperado al procesar el mensaje: " + e + ". Error: " + e.getMessage();
	            		saveLog(Level.SEVERE, true, error, targetHost, true);
		            	Trx.getTrx(rep_trxName).rollback();
					}
					finally {
						Trx.getTrx(rep_trxName).close();
					}
				}
			}
		}
		catch (Exception e)
		{
			saveLog(Level.SEVERE, true, "Error en replicación: " + e.getMessage(), null, true);
			throw new Exception(e);
		}
		
		saveLog(Level.INFO, false, "Proceso finalizado", null, false);
		return "FINALIZADO";

	}
	
	/**
	 * Efectiviza la llamada de replicación al host destino
	 * @param targetOrg ID de organizacion destino
	 * @param xml registros a replicar
	 * @param trxName nombres de la transaccion
	 * @throws Exception
	 */
	protected void callWSReplication(int targetOrg, StringBuffer xml, String trxName, int timeOut) throws Exception 
	{
		// Recuperar la configuración para el host destino.	Se utiliza el campo HostAccessKey para la URL de recupero del WS
		MReplicationHost rh = MReplicationHost.getForOrg(targetOrg, trxName, Env.getCtx());
		LibertyaWSServiceLocator locator = new LibertyaWSServiceLocator();
		locator.setLibertyaWSEndpointAddress(	"http://" + 
												rh.getHostName() + ":" + 	// Ejemplo:  200.54.291.212
												rh.getHostPort() + 			// Ejemplo:  "8080"
												rh.getHostAccessKey());		// Ejemplo:  "/axis/services/LibertyaWS"
		// Recuperar el servicio e indicar el Timeout
		ws.libertya.org.LibertyaWS lyws = locator.getLibertyaWS();
		((LibertyaWSSoapBindingStub)lyws).setTimeout(timeOut);
		
		ReplicationParameterBean data = new ReplicationParameterBean(rh.getUserName(), rh.getPassword(), 0, 0);
		
		/* Comprimir el string a fin de reducir los tiempos de transmision entre hosts e incorporarlo al Vector */
		byte[] compressedXML = ReplicationUtils.compressString(xml.toString()); 
		data.setActionsXML(compressedXML);
		data.setSourceHostPos(thisOrgPos);
		data.setTargetHostPos(rh.getReplicationArrayPos());
		ReplicationResultBean result = lyws.replicate(data);
		if (result.isError())
			saveLog(Level.SEVERE, true, result.getErrorMsg(), rh.getReplicationArrayPos(), true);
		Vector<String[]> results = result.getEventLog();
		// Procesar el resultado en cada caso
    	updateRepArray( rh.getReplicationArrayPos(), results, rep_trxName);
	}

	
	/**
	 *	TODO: Refactor 
	 */
	protected void updateRepArray(int repArrayPos, Vector<String[]> notifications, String trxName) throws Exception
	{
		String tempCurrStat = null;
		Character currentStatus;
		int okCount = 0;
		int errorCount = 0;
		for (Object[] parts : notifications)
		{
			String tablename 	= (String)parts[0];
			String uid 			= (String)parts[1];
			String opType 		= (String)parts[2];
			String result 		= (String)parts[3];
			
			try {
				
				// Cual es el estado actual del registro?
				String currentRepArray  = DB.getSQLValueString(trxName, " SELECT repArray " +
																		" FROM " + (MChangeLog.OPERATIONTYPE_Deletion.equals(opType) ? ReplicationConstants.DELETIONS_TABLE : tablename) + 
																		" WHERE retrieveUID = ? ", uid, true);
	
				// ¿Es un caso especial en el cual el registro se eliminó en el origen justo cuando estaba corriendo replicación (y ya replicó en destino)?
				// (dependiendo la ejecucion podría pasar que el registro original se esta por eliminar, pero replicacion  
				// ya lo tomo para insertar en destino aunque todavía no actualizó su repArray.
				// O sea: recibo un OK del destino, pero en el origen el registro no exite (currentRepArray = null)
				if (ReplicationConstants.JMS_ACK_OK.equals(result) && currentRepArray == null && !MChangeLog.OPERATIONTYPE_Deletion.equals(opType) && !ReplicationConstants.DELETIONS_TABLE.equalsIgnoreCase(tablename)) 
				{
					saveLog(Level.INFO, false, "Iniciando correccion de eliminacion para registro:" + uid, repArrayPos, true);
					// Validar si efectivamente el registro que en destino se insertó, en origen ya no existe más
					int count = DB.getSQLValue(trxName, " SELECT count(1) FROM " + tablename + " WHERE retrieveUID = ?", uid);
					// Si el registro efectivamente ya no existe, ingresarlo en la tabla de eliminaciones
					if (count == 0) {
						saveLog(Level.INFO, false, "Correccion de eliminacion - En origen ya no existe el registro:" + uid, repArrayPos, true);	
						// Obtener la configuración de repArray para la tabla en cuestión
						int tableID = M_Table.getTableID(tablename, trxName);
						saveLog(Level.INFO, false, "Correccion de eliminacion - TableID:" + tableID, -1, true);
						String baseRepArray = DB.getSQLValueString(trxName, "SELECT replicationArray FROM AD_TableReplication WHERE AD_Table_ID = ?", tableID);
						if (baseRepArray == null || baseRepArray.length()==0) {
							saveLog(Level.SEVERE, true, "Correccion de eliminacion imposible. Sin repArray para tabla: " + tablename + ". Registro:" + uid, repArrayPos, true);
							continue;
						}
						saveLog(Level.INFO, false, "Correccion de eliminacion - baseRepArray:" + baseRepArray, -1, true);
						// Covnertir conf bidireccional a accion enviar, y conf recibir a accion ninguna
						baseRepArray = baseRepArray.replace(ReplicationConstants.REPLICATION_CONFIGURATION_SENDRECEIVE, ReplicationConstants.REPLICATION_CONFIGURATION_SEND);
						baseRepArray = baseRepArray.replace(ReplicationConstants.REPLICATION_CONFIGURATION_RECEIVE, ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION);
						// Insertar en tabla de eliminaciones
						String sql = 
									" INSERT INTO ad_changelog_replication (AD_Changelog_Replication_ID, AD_Client_ID, AD_Org_ID, isActive, Created, CreatedBy, Updated, UpdatedBy, AD_Table_ID, retrieveUID, operationtype, binaryvalue, reparray, columnvalues, includeInReplication) " +
									" SELECT nextval('seq_ad_changelog_replication'),"+getAD_Client_ID()+",0,'Y',now(),0,now(),0,"+tableID+",'"+uid+"','"+MChangeLog.OPERATIONTYPE_Insertion+"',null,'"+baseRepArray+"',null,'Y'";
						saveLog(Level.INFO, false, "Correccion de eliminacion - sql:" + sql, -1, true);
						int insertOK = DB.executeUpdate(sql, trxName);
						if (insertOK == 1)
							saveLog(Level.INFO, false, "Correccion de eliminacion realizada. Insertado en ad_changelog_replication el registro:" + uid, repArrayPos, true);
						else
							saveLog(Level.SEVERE, true, "Correccion de eliminacion. Error al insertar en ad_changelog_replication para registro:" + uid + ". Query:" + sql, repArrayPos, true);
					}
					else {
						saveLog(Level.WARNING, true, "Correccion de eliminacion no pudo ser realizada. Registro todavía existe con repArray en null? Registro:" + uid, repArrayPos, true);
					}
					continue;
				}
				
				// Cual es el estado actual del registro para la posicion dada?
				tempCurrStat = currentRepArray.substring(repArrayPos-1,repArrayPos);
						
				// de no existir el registro en el origen, omitir
				if (tempCurrStat == null || tempCurrStat.length() == 0)
					continue;
				
				currentStatus = tempCurrStat.charAt(0);
				
				// si se recibe OK...
				if (ReplicationConstants.JMS_ACK_OK.equals(result))
				{
					// Omitir trancisiones no esperadas (por ejemplo por reprocesamiento del mensaje)
					if (ReplicationConstantsWS.nextStatusWhenOK.get(currentStatus) == null)
						continue;
					
					okCount++;
					// Es una tabla que contiene referencias ciclicas?  En ese caso, debería setear el registro para volver a replicarlo
					// a fin de que se replique también el valor inicialmente seteado en null (ver ReplicationXMLUpdater.appendSpecialValues())
					boolean requiresResend = false;
					if ((MChangeLog.OPERATIONTYPE_Insertion.equals(opType) || (opType!=null && opType.startsWith(MChangeLog.OPERATIONTYPE_InsertionModification))) && ReplicationConstantsWS.cyclicReferences.get(tablename.toLowerCase()) != null) {
						StringBuffer sql = new StringBuffer();
						sql.append("SELECT count(1) FROM ").append(tablename).append(" WHERE retrieveUID = '").append(uid).append("' AND ( ");
						// Iterar por las columnas de la tabla con referencias ciclicas a fin de determinar si alguna de éstas tiene un valor cargado (si todas son null no tiene sentido reenviar)
						for (String column : ReplicationConstantsWS.cyclicReferences.get(tablename.toLowerCase()))
								sql.append(column).append(" IS NOT NULL OR ");
						sql.append(" 1=2 )");
						requiresResend = (1 == DB.getSQLValue(trxName, sql.toString()));
					}
					// Actualizar el repArray en función del OK recibido
					if (requiresResend)
						repArrayUpdateQuery(repArrayPos, tablename, uid, currentRepArray, currentStatus, ReplicationConstants.REPARRAY_REPLICATE_MODIFICATION, trxName, opType);
					else
						repArrayUpdateQuery(repArrayPos, tablename, uid, currentRepArray, currentStatus, ReplicationConstantsWS.nextStatusWhenOK.get(currentStatus), trxName, opType);
				}
				// si se recibe ERROR...
				else
				{
					errorCount++;
					// omitir mensajes mal formados 
					if ((opType.startsWith(""+ReplicationConstants.REPARRAY_RETRY_PREFIX) && opType.length() <= 1) || opType.length() == 0)
						continue;
					
					// recuperar la accion (I o M) o el reintento (A, B, C, etc.) en caso que exista un segundo caracter
					Character currentRetryStatus = null;
					String action = null; 
					if (opType.length() > 1) {
						currentRetryStatus = opType.charAt(1);
						action = " - Reintento: ";
					}
					else {
						currentRetryStatus = opType.charAt(0);
						action = " - Accion: ";
					}
					
					
					// Detalles del error
					String error = "Recepción de error desde host:" + repArrayPos + " - " + " Tabla: " + tablename + " - retrieveUID: " + uid + action + currentRetryStatus + ". ";
									
					// Quedan reintentos disponibles?  Si el siguiente estado es X, significa que ya no quedan
					Character nextStatus = ReplicationConstantsWS.nextStatusWhenERR.get( opType.length() > 1 ? currentRetryStatus : currentStatus);
					
					// Omitir trancisiones no esperadas (por ejemplo por reprocesamiento del mensaje)
					if (nextStatus == null)
						continue;
					
					if (nextStatus == ReplicationConstants.REPARRAY_NO_RETRY || 			// Bajo WS estos casos no deberian darse
						nextStatus == ReplicationConstants.REPARRAY_REPLICATE_NO_RETRY)		// Bajo WS estos casos no deberian darse
					{
						// Si no quedan reintentos disponibles, setear el ERROR a nivel SEVERE.
						error = "FATAL. REITERADOS REINTENTOS DE REPLICACION FALLIDOS. " + error;
						saveLog(Level.SEVERE, true, error + result, repArrayPos, false);
					}
					else
					{
						error = "ERROR AL REPLICAR. " + error;
						saveLog(Level.WARNING, true, error + result, repArrayPos, true);
						System.out.println(error);
					}
	
					// Actualizar el repArray en cualquier caso
					repArrayUpdateQuery(repArrayPos, tablename, uid, currentRepArray, currentStatus, nextStatus, trxName, opType);
				}
			}
			catch (Exception e) {
				errorCount++;
				StringBuffer errorDetail = new StringBuffer();
				errorDetail.append("Error al procesar respuesta.").append(" Host:"+repArrayPos).append(", Table:"+tablename).append(", UID:"+uid).append(", OP:"+opType).append(", RESULT:"+result).append(". ERROR:"+e);
				saveLog(Level.SEVERE, true, errorDetail.toString(), repArrayPos, true);
			}
		}
		saveLog(Level.INFO, false, "Registros replicados:" + okCount + ", Registros con error:" + errorCount, -1, true);
	}
	
	/**
	 *	TODO: Refactor 
	 */	
	protected void repArrayUpdateQuery(int pos, String tableName, String uid, String currentRepArray, Character fromState, Character toState, String trxName, String opType)
	{
		StringBuilder currentRepArraySB = new StringBuilder(currentRepArray);
		currentRepArraySB.setCharAt(pos-1, toState);
		String newRepArray = currentRepArraySB.toString();
		String includeInReplicationValue = (newRepArray.indexOf(ReplicationConstantsWS.REPARRAY_REPLICATE_INSERT) >= 0 ||  
											newRepArray.indexOf(ReplicationConstantsWS.REPARRAY_REPLICATE_MODIFICATION) >= 0 ||
											newRepArray.indexOf(ReplicationConstantsWS.REPARRAY_RETRY1) >= 0 ? "Y":"N" );
		
		boolean isDeletion = MChangeLog.OPERATIONTYPE_Deletion.equals(opType);
		// El uso de prefijo SET para el repArray solo es para tablas con triggerEvent.  La tabla AD_Changelog_Replication obviamente no lo tiene seteado
		String set = isDeletion ? "" : "SET";
		String tableNameQuery = MChangeLog.OPERATIONTYPE_Deletion.equals(opType)?ReplicationConstants.DELETIONS_TABLE:tableName;
		// A diferencia que en JMS, aqui hay que setear posibles casos de includeInReplication = 'N' (dado que estado waiting for ack no existe)
		DB.executeUpdate(" UPDATE " + (tableNameQuery) +
				 			" SET repArray = '"+set+newRepArray+"', " +
				 			"	  includeInReplication = '"+includeInReplicationValue+"' " +
				 			" WHERE retrieveUID = '" + uid + "'" +
				 			" AND (AD_Client_ID = 0 OR AD_Client_ID = " + getAD_Client_ID() + ")", false, trxName, true);

//		Comentado: 	En realidad aunque una vez replicadas las eliminaciones podrían eliminarse,
//					las mismas se mantienen en caso que deba utilizarse la funcion de reenvio
//					de eventos de replicación, en donde dichas eliminaciones deberían ser contempladas
//
//					En caso de necesitar eliminar entradas en esta tabla, se deberá verificar que el
//					repArray de cada registro contenga únicamente en sus posiciones los valores:
//					ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION o ReplicationConstants.REPARRAY_REPLICATED
//
//		WS: EN CASO DE DESCOMENTAR ESTO, HAY QUE CORREGIR.  NOTAR QUE AHORA SE RECIBE EL CURRENTREPARRAY
//		
//		// Si es una eliminacion quedan tareas adicionales pendientes
//		if (MChangeLog.OPERATIONTYPE_Deletion.equals(opType))
//		{
//			// Verificar si el registro tiene hosts pendientes de replicacion 
//			String repArray = DB.getSQLValueString(trxName, " SELECT repArray FROM " + ReplicationConstants.DELETIONS_TABLE + " WHERE retrieveUID = '" + uid + "' AND AD_Client_ID = ? ",  getAD_Client_ID(), true);
//			boolean ok = true;
//			for (int i=0; i < repArray.length() && ok; i++)
//				if (ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION != repArray.charAt(i) && ReplicationConstants.REPARRAY_REPLICATED != repArray.charAt(i))
//					ok = false;
//				
//			// Si no quedan pendientes, entonces eliminar la entrada
//			if (ok)
//				DB.executeUpdate(" DELETE FROM " + ReplicationConstants.DELETIONS_TABLE + " WHERE retrieveUID = '" + uid + "' AND AD_Client_ID = " + getAD_Client_ID(), false, trxName, true);
//		}
	}
	
	
	@Override
	protected String getProcessName() {
		return CLIENT_PROCESS;
	}

	/**
	 * Si el numero pendientes de registros a enviar (entre la compañía y client 0) supera el limite, enviar un mail de alerta
	 */
	protected void checkForWarning() {
		// Registros pendientes de replicacion de compañía en producción
		int count = DB.getSQLValue(null, " SELECT SUM(recordcount) FROM replication_record_count(" + Env.getAD_Client_ID(Env.getCtx()) + ", 'includeinreplication=''Y''')");
		// Registros pendientes de replicacion de compañía 0
		count += DB.getSQLValue(null, " SELECT SUM(recordcount) FROM replication_record_count(0, 'includeinreplication=''Y''')");
		// Si supera el limite, enviar el mail
		if (count >= ReplicationConstantsWS.WARNING_LIMIT) {
			// Recuperar informacion para incorporar al cuerpo del mail
			MClient client = new MClient(Env.getCtx(), Env.getAD_Client_ID(Env.getCtx()), null);
			MOrg org = new MOrg(Env.getCtx(), thisOrgID, null);
			String to = 		MPreference.searchCustomPreferenceValue(ReplicationConstantsWS.REPLICATION_ADMIN, 0, 0, 0, true);
			String subject = 	" WARNING EN REPLICACION. LIMITE DE REGISTROS PENDIENTES SUPERADO. "; 
			String body = 		" Compañía: " + client.getName() + "\n" +
								" Organización: " + org.getName() + "\n" +
								" Posicion de host: " + thisOrgPos + "\n" +
								" Registros pendientes: " + count + "\n" +
								" Limite aceptable: " + ReplicationConstantsWS.WARNING_LIMIT; 
			// Enviar mail, loggear y presentar en consola
			EMail mail = new EMail( client, null, to, subject, body);
			// EMail mail = new EMail( client.getSMTPHost(), from, to, subject, body);
			String sendError = "";
			String response = mail.send();
			if (!EMail.SENT_OK.equals(response))
				sendError = "IMPOSIBLE ENVIAR MAIL A " + to + ": " + response + ". ";
			saveLog(Level.SEVERE, true, sendError + subject + body, -1, true);
		}
	}
	
	/**
	 * Elimina registros antiguos de la tabla de errores
	 */
	protected void emptyOldErrorsLog() {
		DB.executeUpdate(" DELETE FROM AD_ReplicationError WHERE CREATED < ('now'::text)::timestamp(6) - interval '10 days'");
	}
	
	
	/* ================================================ INVOCACION DESDE TERMINAL ================================================ */
	
	// Parametro cantidad de eventos por llamada
	static final String PARAM_HELP			 		=	"-h";
	// Parametro cantidad de eventos por llamada
	static final String PARAM_EVENTS_PER_CALL 		=	"-q";
	// Parametro tiempo de espera base en una invocacion
	static final String PARAM_TIMEOUT_BASE 			=	"-t";
	// Parametro limitar numero de registros a enviar
	static final String PARAM_MAX_RECORDS 			=	"-l";
	// Parametro envio de mail al tener un cantidad de registros pendiente de replicacion mayor a la indicada
	static final String PARAM_WARNING_LIMIT 		=	"-w";
	// Parametro solo envio tabla indicada
	static final String PARAM_REPLICATE_TABLE 		=	"-rt";
	// Parametro solo envio registro indicado
	static final String PARAM_REPLICATE_RECORD 		=	"-rr";
	// Parametro solo envio a host indicado 
	static final String PARAM_REPLICATE_HOST 		=	"-rh";


	public static void main(String args[])
	{
		boolean cacheInvalidated = false;
		for (String arg : args) {
			if (arg.toLowerCase().startsWith(PARAM_HELP))
				showHelp(" Ayuda ");
			if (arg.toLowerCase().startsWith(PARAM_EVENTS_PER_CALL))
				ReplicationConstantsWS.EVENTS_PER_CALL = Integer.parseInt(arg.substring(PARAM_EVENTS_PER_CALL.length()));
			else if (arg.toLowerCase().startsWith(PARAM_TIMEOUT_BASE))
				ReplicationConstantsWS.TIME_OUT_BASE = Integer.parseInt(arg.substring(PARAM_TIMEOUT_BASE.length()));
			else if (arg.toLowerCase().startsWith(PARAM_MAX_RECORDS)) 
				ReplicationConstants.REPLICATION_SOURCE_MAX_RECORDS = Integer.parseInt(arg.substring(PARAM_MAX_RECORDS.length()));
			else if (arg.toLowerCase().startsWith(PARAM_REPLICATE_TABLE)) {
				if (cacheInvalidated) {
					ReplicationTableManager.invalidateCache();
					cacheInvalidated = true;
				}
				ReplicationTableManager.filterTable = arg.substring(PARAM_REPLICATE_TABLE.length());
			}
			else if (arg.toLowerCase().startsWith(PARAM_REPLICATE_RECORD)) {
				if (cacheInvalidated) {
					ReplicationTableManager.invalidateCache();
					cacheInvalidated = true;
				}
				ReplicationTableManager.filterRecord = arg.substring(PARAM_REPLICATE_RECORD.length());
			}
			else if (arg.toLowerCase().startsWith(PARAM_REPLICATE_HOST)) {
				if (cacheInvalidated) {
					ReplicationTableManager.invalidateCache();
					cacheInvalidated = true;
				}
				ReplicationTableManager.filterHost = Integer.parseInt(arg.substring(PARAM_REPLICATE_HOST.length()));
			}
			else if (arg.toLowerCase().startsWith(PARAM_WARNING_LIMIT)) {
				ReplicationConstantsWS.WARNING_LIMIT = Integer.parseInt(arg.substring(PARAM_WARNING_LIMIT.length()));
			}
		}
		
	  	// OXP_HOME seteada?
	  	String oxpHomeDir = System.getenv("OXP_HOME"); 
	  	if (oxpHomeDir == null)
	  		showHelp("ERROR: La variable de entorno OXP_HOME no está seteada ");
	
	  	// Cargar el entorno basico
	  	System.setProperty("OXP_HOME", oxpHomeDir);
	  	if (!OpenXpertya.startupEnvironment( false ))
	  		showHelp("ERROR: Error al iniciar la configuracion de replicacion ");

	  	// Configuracion 
	  	Env.setContext(Env.getCtx(), "#AD_Client_ID", DB.getSQLValue(null, " SELECT AD_Client_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' "));
	  	Env.setContext(Env.getCtx(), "#AD_Org_ID", DB.getSQLValue(null, " SELECT AD_Org_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' "));
	  	if (Env.getContext(Env.getCtx(), "#AD_Client_ID") == null || Env.getContext(Env.getCtx(), "#AD_Client_ID") == null)
	  		showHelp("ERROR: Sin marca de host.  Debe realizar la configuración correspondiente en la ventana Hosts de Replicación. ");

	  	// Informar a usuario e Iniciar la transacción
		String message = "[Client] Iniciando proceso. ";
	  	System.out.println(message + "(" + DB.getDatabaseInfo() + ")");

	  	ReplicationClientProcess rcp = new ReplicationClientProcess();
	  	try {
	  		rcp.prepare();
	  		System.out.println(rcp.doIt());
	  	}
	  	catch (Exception e) {
	  		e.printStackTrace();
	  	}
	}

	protected static void showHelp(String message)
	{
		String help = " [[ " + message + " ]] " +
				"\n" + 	
				" ------------ FRAMEWORK DE REPLICACION VIA WS. MODO DE INSTANCIACION DEL PROCESO CLIENTE --------------- " +
				" Ejemplos de uso de proceso origen (caso tipico de uso y parametros completos): \n" +
				" java -classpath lib/OXP.jar:lib/OXPLib.jar:lib/OXPXLib.jar org.libertya.ws.client.ReplicationClientProcess " + PARAM_EVENTS_PER_CALL + "500 " + PARAM_TIMEOUT_BASE + "120000 " + PARAM_MAX_RECORDS + "1500 " + PARAM_REPLICATE_TABLE + "C_Invoice " + PARAM_REPLICATE_RECORD + "h1_1394_C_Invoice " + PARAM_REPLICATE_HOST + "2 \n" +
				" donde \n" +
				" " + PARAM_EVENTS_PER_CALL  + "    es la cantidad de eventos que se envian en una misma llamada al WS. Si no se especifica, el valor por defecto es " + ReplicationConstantsWS.EVENTS_PER_CALL + ".  Si la cantidad de registros es mayor que este valor, se realizarán varias llamadas independientes (en distintas transacciones). \n" +
				" " + PARAM_TIMEOUT_BASE     + "    redefinicion del timeout base para la invocación al WS (milisegundos). Si no se especifica, el valor por defecto es " + ReplicationConstantsWS.TIME_OUT_BASE + ". A este valor se le adiciona el parametro "+PARAM_EVENTS_PER_CALL+" * " + ReplicationConstantsWS.TIME_OUT_EXTRA_FACTOR + " \n" +
				" " + PARAM_MAX_RECORDS      + "    especifica el numero maximo de registros a procesar para su envio a los demas hosts (0 = todos) \n" +
				" " + PARAM_WARNING_LIMIT    + "    envio de warning por mail al tener una cantidad de registros pendientes a replicar mayor al valor indicado.  Valor por defecto: " + ReplicationConstantsWS.WARNING_LIMIT + ". El mail sera enviado a la direccion especificada en AD_Preference, atributo: ReplicationAdmin. La cuenta origen se configura en AD_Client \n" +
				" " + PARAM_REPLICATE_TABLE  + "    limita la replicación unicamente a la tabla especificada \n" +
				" " + PARAM_REPLICATE_RECORD + "    limita la replicación unicamente al registro especificado por su retrieveUID (ver parametro de filtro por tabla) \n" +
				" " + PARAM_REPLICATE_HOST   + "    limita la replicación unicamente hacia el host especificado por su replicationPos (ver parametro de filtro por tabla) \n" +
				" ------------ IMPORTANTE: NO DEBEN DEJARSE ESPACIOS ENTRE EL PARAMETRO Y EL VALOR DEL PARAMETRO! --------------- \n";
		System.out.println(help);
		System.exit(1);
	}
	
	/**
	 * Recarga de metodo para mostrar errores/mensajes en terminal
	 */
	protected void saveLog(Level aLevel, boolean persistError, String logMessage, Integer targetOrgPosOrID, boolean displayInTerminal) {
		saveLog(aLevel, persistError, logMessage, targetOrgPosOrID);
		if (displayInTerminal) {
			if (targetOrgPosOrID > 0)
				System.out.println("" + logMessage + ". Target: " + targetOrgPosOrID);
			else
				System.out.println("" + logMessage);
		}
	}
}
