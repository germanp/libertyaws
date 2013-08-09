package org.libertya.ws.handler;

import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.PO;

public abstract class DocumentHandler extends GeneralHandler {

	
	/**
	 * En caso de que la cabecera cuente con campos de dirección ad-hoc, y que los 
	 * mismos no se hayan cargado a partir de la información recibida, tomar los
	 * datos de la dirección de la entidad comercial para rellenar la cabecera.
	 * @param header cabecera de documento (factura, pedido, etc.)
	 * @param bPartnerID entidad comercial a tomar como referencia
	 */
	protected void setBPartnerAddressInDocument(PO header, int bPartnerID) 
	{
		// Recuperar el bpartnerlocation
		MBPartnerLocation[] aBPartnerLocation = MBPartnerLocation.getForBPartner(getCtx(), bPartnerID);
		if (aBPartnerLocation==null || aBPartnerLocation.length==0)
			return;
		// Cargar el encabezado con los eventuales datos no contigurados en la cabecera relacionados con bPartnerLocation 
		copyPOValues(aBPartnerLocation[0], header);
		// Recuperar el location
		MLocation aLocation = MLocation.getBPLocation(getCtx(), aBPartnerLocation[0].getC_BPartner_Location_ID(), getTrxName());
		if (aLocation==null || aLocation.getC_Location_ID()==0)
			return;
		// Cargar el encabezado con los eventuales datos no contigurados en la cabecera relacionados con Location		
		copyPOValues(aLocation, header);
	}

	
}
