package org.libertya.wse.result;

import java.util.HashMap;

import org.libertya.ws.bean.result.MultipleDocumentsResultBean;
import org.libertya.ws.bean.result.MultipleRecordsResultBean;
import org.libertya.wse.common.RecordContent;
import org.libertya.wse.common.SimpleMap;
import org.libertya.wse.utils.MapTranslator;

public class MultipleRecordsResult extends SimpleResult {

	/** Informacion de los registros */
	protected RecordContent[] content;

	public RecordContent[] getContent() {
		return content;
	}

	public void setContent(RecordContent[] content) {
		this.content = content;
	}
	
	public MultipleRecordsResult() { }
	
	public MultipleRecordsResult(MultipleRecordsResultBean data) {
		super(data);
		int i=0;
		if (data.getRecords() != null) {
			content = new RecordContent[data.getRecords().size()];
			for (HashMap<String, String> aRecord : data.getRecords()) {
				SimpleMap[] newRecord = MapTranslator.hashMap2SimpleMap(aRecord);
				RecordContent aContent = new RecordContent();
				aContent.setData(newRecord);
				content[i++] = aContent;  
			}
		}
	}
	
	public MultipleRecordsResult(MultipleDocumentsResultBean data) {
		super(data);
		int i=0;
		if (data.getDocumentHeaders() != null) {
			content = new RecordContent[data.getDocumentHeaders().size()];
			for (HashMap<String, String> aRecord : data.getDocumentHeaders()) {
				SimpleMap[] newRecord = MapTranslator.hashMap2SimpleMap(aRecord);
				RecordContent aContent = new RecordContent();
				aContent.setData(newRecord);
				content[i++] = aContent;  
			}
		}
	}
}
