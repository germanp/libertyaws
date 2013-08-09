package org.libertya.ws.bean.result;

import java.util.HashMap;

public class BPartnerResultBean extends ResultBean {

	/** Valores de retorno de la última dirección de facturación */
	protected HashMap<String, String> billAddress = new HashMap<String, String>();
	/** Indica si existen más direcciones */
	protected boolean moreAddresses = false;
	/** Valores de retorno del contacto más nuevo */
	protected HashMap<String, String> userContact = new HashMap<String, String>();

	/**
	 * Constructor por defecto.  Ver superclase
	 */
	public BPartnerResultBean() {
		super();
	}
	
	/**
	 * Constructor por defecto.  Ver superclase
	 */
	public BPartnerResultBean(boolean error, String errorMsg, HashMap<String, String> map) {
		super(error, errorMsg, map);
	}
	
	/** Basic getter para la direccion de facturacion */
	public HashMap<String, String> getBillAddress() {
		return billAddress;
	}

	/** Basic setter para la direccion de facturacion */
	public void setBillAddress(HashMap<String, String> billAddress) {
		this.billAddress = billAddress;
	}

	/** Basic getter para marca de mas direcciones */
	public boolean isMoreAddresses() {
		return moreAddresses;
	}

	/** Basic setter para marca de mas direcciones */
	public void setMoreAddresses(boolean moreAddresses) {
		this.moreAddresses = moreAddresses;
	}

	/** Basic getter para usuario de contacto */
	public HashMap<String, String> getUserContact() {
		return userContact;
	}

	/** Basic setter para usuario de contacto */
	public void setUserContact(HashMap<String, String> userContact) {
		this.userContact = userContact;
	}

	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(super.toString());
		out.append("\n - BillAddress: ");
			if (billAddress!=null)
				for (String key : billAddress.keySet())
					out.append(key).append(" = ").
						append(billAddress.get(key)).
						append("; ");
			out.append("\n - UserContact: ");
			if (userContact!=null)
				for (String key : userContact.keySet())
					out.append(key).append(" = ").
						append(userContact.get(key)).
						append("; ");
		return out.toString();
	}



}
