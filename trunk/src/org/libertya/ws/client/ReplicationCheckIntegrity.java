package org.libertya.ws.client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.libertya.ws.bean.parameter.FilteredColumnsParameterBean;
import org.libertya.ws.bean.result.MultipleRecordsResultBean;
import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MReplicationHost;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.replication.ReplicationConstantsWS;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

import ws.libertya.org.LibertyaWSServiceLocator;
import ws.libertya.org.LibertyaWSSoapBindingStub;

public class ReplicationCheckIntegrity extends SvrProcess {

	/** Host sobre el cual validar inconsistencia */
	public static Integer hostNumber = null;
	/** Nombre de tabla a verificar */
	public static String tableName = null;
	/** Nombre de columna a utilizar para verificar integridad */	
	public static String columnName = "Updated";
	/** Fecha desde de los registros, basado en columna CREATED */	
	public static String dateFrom = null;
	/** Fecha hasta de los registros, basado en columna CREATED */
	public static String dateTo = null;
	/** Numero de registros a recuperar en cada invocacion al WS */
	public static Integer recordCount = 1000;
	/** Tiempo a dormir entre un lote de validacion y el proximo */
	public static Integer sleepMS = 1000;
	/** Tiempo a esperar la respuesta del WS previo a timeout */
	public static Integer timeoutMS = ReplicationConstantsWS.TIME_OUT_BASE;
		
	/** ID de esta organización */
	protected int thisOrgID = -1; 
	/** Posicion de esta organización en el array de organización */
	protected int thisOrgPos = -1; 
	/** ID de la compañía configurada para replicación */
	int thisInstanceClient = -1;
	/** AD_Org_ID de la organizacion destino */
	protected int targetOrgID = -1;
	
	/** Numero de registros validados en total */
	protected int totalRecordsChecked;
	/** Numero de registros con diferencias de integridad */
	protected int totalRecordsMarkedForFix;
	
	@Override
	protected void prepare() {
		/* Setear el ID y Pos de esta organizacion (ignora la Org del login, utiliza la conf. de thisHost), asi como su cia. */
		thisOrgID  = DB.getSQLValue(null, " SELECT AD_Org_ID FROM AD_ReplicationHost WHERE thisHost = 'Y'" );
		thisOrgPos = MReplicationHost.getReplicationPositionForOrg(thisOrgID, null);
		thisInstanceClient = DB.getSQLValue(null, " SELECT AD_Client_ID FROM AD_ReplicationHost WHERE thisHost = 'Y'" );
		targetOrgID = MReplicationHost.getReplicationOrgForPosition(hostNumber, null);
		/* Setear variables del contexto, para los posibles casos en que las mismas sean utilizdas */
        Env.setContext(Env.getCtx(), "#AD_Client_ID", thisInstanceClient);
        Env.setContext(Env.getCtx(), "#AD_Org_ID", thisOrgID);
	}

	
	
	@Override
	protected String doIt() throws Exception {
		
		if (thisOrgPos == hostNumber)
			showHelp("ERROR: No es posible comparar un host con si mismo!");
		if (tableName == null) 
			showHelp("ERROR: El parametro tabla es obligatoria");
		if (hostNumber == null) 
			showHelp("ERROR: El parametro host destino es obligatorio");
		
		System.out.println("-----------------------------------------");
		System.out.println("Este host: " + thisOrgPos);
		System.out.println("Host a validar: " + hostNumber);
		System.out.println("Tabla: " + tableName);
		System.out.println("Columna: " + columnName);
		System.out.println("Desde: " + (dateFrom != null ? dateFrom : "-"));
		System.out.println("Hasta: " + (dateTo != null ? dateTo : "-"));
		System.out.println("-----------------------------------------");
		
		PreparedStatement pstmt = DB.prepareStatement(getSQLQuery());
		ResultSet rs = pstmt.executeQuery();
		
		int count = 0;
		// retrieveUID -> Campo a Comparar (ejemplo Updated)
		HashMap<String, String> sourceValues = new HashMap<String, String>();
		while (rs.next()) {
			sourceValues.put(rs.getString("retrieveuid"), rs.getString(columnName));
			count++;
			totalRecordsChecked++;
			if (count == recordCount) {
				doCheckIntegrity(sourceValues);
				// Reiniciar valores
				count = 0;
				sourceValues = new HashMap<String, String>();
				Thread.sleep(sleepMS);
			}
		}
		// Remanente final almacenado previo al ultimo rs.next()
		if (count > 0) {
			doCheckIntegrity(sourceValues);
		}
		
		String status = "Finalizado. Total evaluados: " + totalRecordsChecked + ". Total detectados: " + totalRecordsMarkedForFix;
		System.out.println(status);
		return status;
	}

	
	/** Retorna el SQL que recupera todos los registros localmente a ser validados en destino */
	protected String getSQLQuery() {
		StringBuffer query = new StringBuffer();
		query	.append(" SELECT retrieveuid, ").append(columnName)
				.append(" FROM ").append(tableName)
				.append(" WHERE 1=1 ");
		if (dateFrom != null)
			query.append(" AND Created >= ").append("'").append(dateFrom).append("'");
		if (dateTo != null)
			query.append(" AND Created <= ").append("'").append(dateTo).append("'");
		
		return query.toString();
	}
	
	/** Realiza el chequeo de integridad y eventual correccion */
	protected void doCheckIntegrity(HashMap<String, String> sourceValues) throws Exception {
		HashMap<String, String> targetValues = null;
		
		System.out.println("Checkeando " + sourceValues.keySet().size() + " registros de tabla " + tableName + ".  Total evaluados: " + totalRecordsChecked + ". Total detectados: " + totalRecordsMarkedForFix);
		
		// A fin de resolver eventuales problemas por time-out, (re)intentar hasta obtener los valores 
		int count = 0;
		while (targetValues == null) {
			targetValues = getTargetValues(sourceValues);
			if (targetValues == null) {
				count++;
				System.out.println("Intento " + count + " de recuperacion de datos en host remoto fallido, reintentando en breve...");
				Thread.sleep(5000);	// Esperar un momento antes de reintentar
			}
		}
		
		// Realizar la comparacion caso por caso
		for (String sourceUID : sourceValues.keySet()) {
			// La columna columnName (por ejemplo Updated) tiene mismo valor tanto en el origen como en el destino?
			if ( !(sourceValues.get(sourceUID)).equals(targetValues.get(sourceUID)) ) {
				// Si no son iguales, simplemente hacer una modificacion dummy para que el trigger cambie el reparray a fin de forzar re-replicación
				totalRecordsMarkedForFix++;
				System.out.println("Valor en origen: " + sourceValues.get(sourceUID) + ". Valor en destino: " + targetValues.get(sourceUID) + ". Marcando registro " + sourceUID + " para su re-replicacion");
				DB.executeUpdate(" UPDATE " + tableName + " SET created = created WHERE retrieveUID = '"  + sourceUID + "'" );
			}
		}
	}
	
	/** */
	protected HashMap<String, String> getTargetValues(HashMap<String, String> sourceValues) {
		
		try {
		// Recuperar la configuración para el host destino.	Se utiliza el campo HostAccessKey para la URL de recupero del WS
		MReplicationHost rh = MReplicationHost.getForOrg(targetOrgID, null, Env.getCtx());
		LibertyaWSServiceLocator locator = new LibertyaWSServiceLocator();
		locator.setLibertyaWSEndpointAddress(	"http://" + 
												rh.getHostName() + ":" + 	// Ejemplo:  200.54.291.212
												rh.getHostPort() + 			// Ejemplo:  "8080"
												rh.getHostAccessKey());		// Ejemplo:  "/axis/services/LibertyaWS"
		// Recuperar el servicio e indicar el Timeout
		ws.libertya.org.LibertyaWS lyws = locator.getLibertyaWS();
		((LibertyaWSSoapBindingStub)lyws).setTimeout(timeoutMS);
		
		// Recuperar unicamente las columnas necesarias
		FilteredColumnsParameterBean data = new FilteredColumnsParameterBean(rh.getUserName(), rh.getPassword(), 0, 0);
		data.addColumnToFilter("retrieveuid");
		data.addColumnToFilter(columnName);
		MultipleRecordsResultBean result = lyws.recordQueryDirect(data, tableName, getWSWhereClause(sourceValues));

		// Pasar a hashmap: RetrieveUID -> columnName
		HashMap<String, String> targetValues = new HashMap<String, String>();
		for (HashMap<String, String> aRecord : result.getRecords()) {
			targetValues.put(aRecord.get("retrieveuid"), aRecord.get(columnName));
		}
		
		return targetValues;
		
		} catch (Exception e) {
			System.out.println("Excepcion al recuperar targetValues: " + e.getMessage());
			return null;
		}
		
		
	}
	
	/** Obtiene el where clause requerido por el WS recordQuery() */
	protected String getWSWhereClause(HashMap<String, String> sourceValues) {
		
		StringBuffer query = new StringBuffer();
		query.append(" retrieveuid IN (");
		for (String retrieveUID : sourceValues.keySet()) {
			query.append("'").append(retrieveUID).append("',");
		}
		query.append("'DUMMY')"); // Finalizacion del where clause, el retrieveUID con valor DUMMY en realidad no existe
		
		return query.toString();
	}
	
	
	/* ================================================ INVOCACION DESDE TERMINAL ================================================ */

	// Parametro nombre de tabla a validar
	static final String PARAM_VALIDATE_HOST = "-vh";
	// Parametro nombre de tabla a validar
	static final String PARAM_TABLE_NAME = "-t";
	// Parametro columna a validar por igualdad (por defecto será campo UPDATED)
	static final String PARAM_COLUMN_NAME = "-c";
	// Parametro fecha desde (basado en campo CREATED), formato 'YYYY-MM-DD'
	static final String PARAM_DATE_FROM = "-df";
	// Parametro fecha hasta (basado en campo CREATED), formato 'YYYY-MM-DD'
	static final String PARAM_DATE_TO = "-dt";
	// Parametro numero de registros a recuperar por invocacion al WS de recuperacion de registros para su comparacion. Por defecto 1000
	static final String PARAM_RECORD_COUNT = "-r";
	// Parametro tiempo a dormir entre un lote de validacion y el proximo
	static final String PARAM_SLEEP_SECONDS = "-ss";
	// Parametro tiempo a esperar la respuesta del WS previo a timeout 
	static final String PARAM_WAIT_TIMEOUT = "-wt";
	// Muestra la ayuda
	static final String PARAM_HELP = "-h";	
	
	public static void main(String args[])
	{
		for (String arg : args) {
			if (arg.toLowerCase().startsWith(PARAM_HELP)) {
				showHelp(" Ayuda ");
			}
			else if (arg.toLowerCase().startsWith(PARAM_TABLE_NAME)) {
				tableName = arg.substring(PARAM_TABLE_NAME.length());
			}
			else if (arg.toLowerCase().startsWith(PARAM_COLUMN_NAME)) {
				columnName = arg.substring(PARAM_COLUMN_NAME.length());
			}
			else if (arg.toLowerCase().startsWith(PARAM_DATE_FROM)) {
				dateFrom = arg.substring(PARAM_DATE_FROM.length());
			}
			else if (arg.toLowerCase().startsWith(PARAM_DATE_TO)) {
				dateTo = arg.substring(PARAM_DATE_TO.length());
			}
			else if (arg.toLowerCase().startsWith(PARAM_RECORD_COUNT)) {
				recordCount = Integer.parseInt(arg.substring(PARAM_DATE_TO.length()));
			}
			else if (arg.toLowerCase().startsWith(PARAM_VALIDATE_HOST)) {
				hostNumber = Integer.parseInt(arg.substring(PARAM_VALIDATE_HOST.length()));
			}
			else if (arg.toLowerCase().startsWith(PARAM_SLEEP_SECONDS)) {
				sleepMS = 1000 * Integer.parseInt(arg.substring(PARAM_SLEEP_SECONDS.length()));
			}
			else if (arg.toLowerCase().startsWith(PARAM_WAIT_TIMEOUT)) {
				timeoutMS = Integer.parseInt(arg.substring(PARAM_WAIT_TIMEOUT.length()));
			}
		}
		
	  	// OXP_HOME seteada?
	  	String oxpHomeDir = System.getenv("OXP_HOME"); 
	  	if (oxpHomeDir == null)
	  		showHelp("ERROR: La variable de entorno OXP_HOME no está seteada ");

	  	// Cargar el entorno basico
	  	System.setProperty("OXP_HOME", oxpHomeDir);
	  	if (!OpenXpertya.startupEnvironment( false ))
	  		showHelp("ERROR: Error al iniciar el ambiente cliente.  Revise la configuración");

	  	// Configuracion 
	  	Env.setContext(Env.getCtx(), "#AD_Client_ID", DB.getSQLValue(null, " SELECT AD_Client_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' "));
	  	Env.setContext(Env.getCtx(), "#AD_Org_ID", DB.getSQLValue(null, " SELECT AD_Org_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' "));
	  	if (Env.getContext(Env.getCtx(), "#AD_Client_ID") == null || Env.getContext(Env.getCtx(), "#AD_Client_ID") == null)
	  		showHelp("ERROR: Sin marca de host.  Debe realizar la configuración correspondiente en la ventana Hosts de Replicación. ");

	  	// Informar a usuario e Iniciar la transacción
		String message = "[Client] Iniciando proceso. ";
	  	System.out.println(message + "(" + DB.getDatabaseInfo() + ")");

	  	ReplicationCheckIntegrity rci = new ReplicationCheckIntegrity();
	  	try {
	  		rci.prepare();
	  		System.out.println(rci.doIt());
	  	}
	  	catch (Exception e) {
	  		e.printStackTrace();
	  	}
	}

	
	protected static void showHelp(String message)
	{
		String help = " [[ " + message + " ]] " +
				"\n" + 	
				" ------------ FRAMEWORK DE REPLICACION VIA WS. APLICACION DE VALIDACION DE INTEGRIDAD EN REPLICACION --------------- " +
				" Ejemplos de uso de proceso origen (caso tipico de uso y parametros completos): \n" +
				" java -classpath ../../lib/OXP.jar:../../lib/OXPLib.jar:../../lib/OXPXLib.jar:lib/repClient.jar:lib/lyws.jar org.libertya.ws.client.ReplicationCheckIntegrity " + PARAM_VALIDATE_HOST + "1 " + PARAM_TABLE_NAME + "C_Invoice " + PARAM_DATE_FROM + "2015-01-01 " + PARAM_RECORD_COUNT + "500 \n" +
				" donde \n" +
				" " + PARAM_VALIDATE_HOST	+ " Es el numero de host contra el cual validar.  Obligatorio. \n" +
				" " + PARAM_TABLE_NAME  	+ " Es el nombre de la tabla a validar.  Obligatorio. \n" +
				" " + PARAM_COLUMN_NAME 	+ " Nombre de la columna a validar por igualdad en ambos hosts (por defecto utiliza campo UPDATED) \n" +
				" " + PARAM_DATE_FROM   	+ " Incluir unicamente los registros a partir de la fecha especificada (segun campo CREATED), en formato 'YYYY-MM-DD' \n" +
				" " + PARAM_DATE_TO      	+ " Incluir unicamente los registros hasta la fecha especificada (segun campo CREATED), en formato 'YYYY-MM-DD'  \n" +
				" " + PARAM_RECORD_COUNT 	+ " Numero de registros a recuperar por invocacion al WS. Por defecto 1000 \n" +
				" " + PARAM_SLEEP_SECONDS 	+ " Tiempo a dormir entre un lote de validacion y el proximo, expresado en segundos. Por defecto es 1 segundo \n" +
				" " + PARAM_WAIT_TIMEOUT 	+ " Tiempo a esperar la respuesta del WS previo a timeout, expresado en milisegundos.  Por defecto es " + ReplicationConstantsWS.TIME_OUT_BASE + " milisegundos \n" +
				" ------------ IMPORTANTE: NO DEBEN DEJARSE ESPACIOS ENTRE EL PARAMETRO Y EL VALOR DEL PARAMETRO! --------------- \n";

		System.out.println(help);
		System.exit(1);
	}

}
