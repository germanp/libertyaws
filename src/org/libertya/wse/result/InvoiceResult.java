package org.libertya.wse.result;

import java.util.HashMap;

import org.libertya.ws.bean.result.InvoiceResultBean;
import org.libertya.wse.common.RecordContent;
import org.libertya.wse.common.SimpleMap;
import org.libertya.wse.utils.MapTranslator;

public class InvoiceResult extends DocumentResult {
	
	/** Informacion de los impuestos */
	protected RecordContent[] taxes;

	public RecordContent[] getTaxes() {
		return taxes;
	}

	public void setTaxes(RecordContent[] taxes) {
		this.taxes = taxes;
	}
	
	public InvoiceResult() { }
	
	public InvoiceResult(InvoiceResultBean data) {
		super(data);
		int i=0;
		if (data.getDocumentLines() != null) {
			content = new RecordContent[data.getDocumentLines().size()];
			for (HashMap<String, String> aRecord : data.getDocumentLines()) {
				SimpleMap[] newRecord = MapTranslator.hashMap2SimpleMap(aRecord);
				RecordContent aContent = new RecordContent();
				aContent.setData(newRecord);
				content[i++] = aContent;  
			}
		}
		if (data.getTaxes() != null) {
			taxes = new RecordContent[data.getTaxes().size()];
			for (HashMap<String, String> aTax : data.getTaxes()) {
				SimpleMap[] newTax = MapTranslator.hashMap2SimpleMap(aTax);
				RecordContent aContent = new RecordContent();
				aContent.setData(newTax);
				content[i++] = aContent;  
			}
		}
	}

}
