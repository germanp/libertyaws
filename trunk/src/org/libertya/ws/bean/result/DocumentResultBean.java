package org.libertya.ws.bean.result;

import java.util.ArrayList;
import java.util.HashMap;

public class DocumentResultBean extends ResultBean {

	/** Lineas de documento */
	protected ArrayList<HashMap<String, String>> documentLines = new ArrayList<HashMap<String, String>>();
	
	/**
	 * Constructor por defecto.  Ver superclase
	 */
	public DocumentResultBean() {
		super();
	}
	
	/**
	 * Constructor por defecto.  Ver superclase
	 */
	public DocumentResultBean(boolean error, String errorMsg, HashMap<String, String> map) {
		super(error, errorMsg, map);
	}
	
	/**
	 * Incorpora una nueva línea de documento
	 * @param map
	 */
	public void addDocumentLine(HashMap<String, String> map) {
		documentLines.add(map);
	}
	
	/** Basic getter para líneas de documento */
	public ArrayList<HashMap<String, String>> getDocumentLines() {
		return documentLines;
	}

	/** Basic setter para líneas de documento */
	public void setDocumentLines(ArrayList<HashMap<String, String>> documentLines) {
		this.documentLines = documentLines;
	}
	
	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n - Document Lines: ");
		if (documentLines!=null)
			for (HashMap<String, String> documentLine : documentLines)
				if (documentLine!=null) {
					out.append("\n");
					for (String key : documentLine.keySet())
						out.append(key).append(" = ").
							append(documentLine.get(key)).
							append("; ");
				}
		return out.toString();
	}

}
