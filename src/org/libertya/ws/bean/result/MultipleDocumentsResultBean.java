package org.libertya.ws.bean.result;

import java.util.ArrayList;
import java.util.HashMap;

public class MultipleDocumentsResultBean extends ResultBean {

	/** Lineas de documento */
	protected ArrayList<HashMap<String, String>> documentHeaders = new ArrayList<HashMap<String, String>>();
	
	/**
	 * Constructor por defecto.  Ver superclase
	 */
	public MultipleDocumentsResultBean() {
		super();
	}
	
	/**
	 * Constructor por defecto.  Ver superclase
	 */
	public MultipleDocumentsResultBean(boolean error, String errorMsg, HashMap<String, String> map) {
		super(error, errorMsg, map);
	}

	/**
	 * Agrega un nuevo encabezado de documento a la lista
	 * @param map
	 */
	public void addDocumentHeader(HashMap<String, String> map) {
		documentHeaders.add(map);
	}
	
	/** Basic getter para lista de encabezado */
	public ArrayList<HashMap<String, String>> getDocumentHeaders() {
		return documentHeaders;
	}

	/** Basic setter para lista de encabezado */
	public void setDocumentHeaders(
			ArrayList<HashMap<String, String>> documentHeaders) {
		this.documentHeaders = documentHeaders;
	}

	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n - Document Headers: ");
		if (documentHeaders!=null)
			for (HashMap<String, String> documentHeader : documentHeaders)
				if (documentHeader!=null) {
					out.append("\n");
					for (String key : documentHeader.keySet())
						out.append(key).append(" = ").
							append(documentHeader.get(key)).
							append("; ");
				}
		return out.toString();
	}

	
	
}
