package org.libertya.wse.result;

import java.util.HashMap;

import org.libertya.ws.bean.result.DocumentResultBean;
import org.libertya.wse.common.RecordContent;
import org.libertya.wse.common.SimpleMap;
import org.libertya.wse.utils.MapTranslator;

public class DocumentResult extends SimpleResult {

	/** Informacion de los registros */
	protected RecordContent[] content;

	public RecordContent[] getContent() {
		return content;
	}

	public void setContent(RecordContent[] content) {
		this.content = content;
	}
	
	public DocumentResult() { }
	
	public DocumentResult(DocumentResultBean data) {
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
	}
}
