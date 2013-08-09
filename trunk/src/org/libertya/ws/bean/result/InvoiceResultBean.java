package org.libertya.ws.bean.result;

import java.util.ArrayList;
import java.util.HashMap;

public class InvoiceResultBean extends DocumentResultBean {

	/** Impuestos de la factura */
	protected ArrayList<HashMap<String, String>> taxes = new ArrayList<HashMap<String, String>>(); 

	
	/**
	 * Constructor por defecto.  Ver superclase.
	 */
	public InvoiceResultBean() {
		super();
	}
	
	/**
	 * Constructor por defecto.  Ver superclase
	 */
	public InvoiceResultBean(boolean error, String errorMsg, HashMap<String, String> map) {
		super(error, errorMsg, map);
	}
	
	
	/** Basic getter para los impuestos */
	public ArrayList<HashMap<String, String>> getTaxes() {
		return taxes;
	}

	/** Basic setter para los impuestos */
	public void setTaxes(ArrayList<HashMap<String, String>> taxes) {
		this.taxes = taxes;
	}
	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n - Taxes: ");
		if (taxes!=null)
			for (HashMap<String, String> tax : taxes) {
				out.append("\n Tax: ");
				for (String key : tax.keySet())
					out.append(key).append(" = ").
						append(tax.get(key)).
						append("; ");
			}
		return out.toString();
	}

}
