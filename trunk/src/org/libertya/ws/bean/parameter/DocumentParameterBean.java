package org.libertya.ws.bean.parameter;

import java.util.ArrayList;
import java.util.HashMap;

import org.libertya.wse.common.SimpleMap;
import org.libertya.wse.param.DocumentLine;

public class DocumentParameterBean extends ParameterBean {

	/** Lineas de documento */
	protected ArrayList<HashMap<String, String>> documentLines = new ArrayList<HashMap<String, String>>();
	
	/**
	 * Constructor por defecto.  Ver superclase.
	 */
	public DocumentParameterBean() {
		super();
	}
	
	/**
	 * Constructor por defecto.  Ver superclase
	 */
	public DocumentParameterBean(String userName, String password,int clientID, int orgID) {
		super(userName, password, clientID, orgID);
	}
	
	/**
	 * Constructor para wrapper
	 */
	public DocumentParameterBean(String userName, String password, int clientID, int orgID, SimpleMap[] header, DocumentLine[] lines) {
		super(userName, password, clientID, orgID);
		load(header, lines);
	}
	
	/**
	 * Incorpora una nueva columna a los datos de parámetro la factura. 
	 * @param columnName nombre de la columna
	 * @param columnValue valor de la columna
	 */
	public void addColumnToHeader(String columnName, String columnValue) {
		addColumnToMainTable(columnName, columnValue);
	}
	
	/**
	 * Adiciona una nueva línea de documento, correspondiente a un conjunto de pares columna / valor
	 */
	public void newDocumentLine() {
		documentLines.add(new HashMap<String, String>());
	}

	/**
	 * Incorpora una nueva columna a los datos de parámetro la línea de documento actual. 
	 * @param columnName nombre de la columna
	 * @param columnValue valor de la columna
	 */
	public void addColumnToCurrentLine(String columnName, String columnValue) {
		addColumnOnTable(documentLines.get(documentLines.size()-1), columnName, columnValue);
	}
	
	
	/** Basic getter para las lineas de documento */
	public ArrayList<HashMap<String, String>> getDocumentLines() {
		return documentLines;
	}

	/** Basic setter para las lineas de documento */
	public void setDocumentLines(ArrayList<HashMap<String, String>> documentLines) {
		this.documentLines = documentLines;
	}
	

	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n  Document Lines: ");
		if (documentLines!=null)
			for (HashMap<String, String> documentLine : documentLines)
				if (documentLine!=null) {
					out.append("\n    ");
					for (String key : documentLine.keySet())
						out.append(key).append(" = ").
							append(documentLine.get(key)).
							append("; ");
				}
		return out.toString();
	}
	
	public void load(SimpleMap[] header, DocumentLine[] lines) {
		// Cargar la cabecera
		if (header != null) {
			for (SimpleMap simpleMap : header)
				mainTable.put(simpleMap.getKey(), simpleMap.getValue());
		}
		// Cargar las lineas
		if (lines != null) {
			for (DocumentLine aLine : lines) {
				HashMap<String, String> newLine = new HashMap<String, String>();
				for (SimpleMap aLineContent : aLine.getContent()) {
					newLine.put(aLineContent.getKey(), aLineContent.getValue());
				}
				documentLines.add(newLine);
			}
		}
	}
	
}
