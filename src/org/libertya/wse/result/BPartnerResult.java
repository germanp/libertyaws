package org.libertya.wse.result;

import org.libertya.ws.bean.result.BPartnerResultBean;
import org.libertya.wse.common.SimpleMap;
import org.libertya.wse.utils.MapTranslator;

public class BPartnerResult extends SimpleResult {

	/** Valores de retorno de la última dirección de facturación */
	protected SimpleMap[] billAddress;
	/** Indica si existen más direcciones */
	protected boolean moreAddresses = false;
	/** Valores de retorno del contacto más nuevo */
	protected SimpleMap[] userContact;
	
	public SimpleMap[] getBillAddress() {
		return billAddress;
	}
	public void setBillAddress(SimpleMap[] billAddress) {
		this.billAddress = billAddress;
	}
	public boolean isMoreAddresses() {
		return moreAddresses;
	}
	public void setMoreAddresses(boolean moreAddresses) {
		this.moreAddresses = moreAddresses;
	}
	public SimpleMap[] getUserContact() {
		return userContact;
	}
	public void setUserContact(SimpleMap[] userContact) {
		this.userContact = userContact;
	}
	
	public BPartnerResult() { }
	
	public BPartnerResult(BPartnerResultBean data) {
		super(data);
		billAddress = MapTranslator.hashMap2SimpleMap(data.getBillAddress());
		moreAddresses = data.isMoreAddresses();
		userContact = MapTranslator.hashMap2SimpleMap(data.getUserContact());
	}


}
