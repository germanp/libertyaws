package org.libertya.wse.utils;

import java.util.HashMap;

import org.libertya.wse.common.SimpleMap;

public class MapTranslator {

	/**
	 * Convierte una HashMap a un array de SimpleMap
	 * @param data HashMap a convertir
	 * @return un array de SimpleMap
	 */
	public static SimpleMap[] hashMap2SimpleMap(HashMap<String, String> data) {
		int i=0;
		SimpleMap[] retValue = new SimpleMap[0];
		if (data != null) {
			retValue = new SimpleMap[data.size()];
			for (String argName : data.keySet()) {
				SimpleMap aMap = new SimpleMap();
				aMap.setKey(argName);
				aMap.setValue(data.get(argName));
				retValue[i++] = aMap;
			}
		}
		return retValue;
	}
	
	/**
	 * Convierte un array de SimpleMap a una HashMap
	 * @param data array de SimpleMap a converir
	 * @return un HashMap
	 */
	public static HashMap<String, String> simpleMap2HashMap(SimpleMap[] data) {
		HashMap<String, String> retValue = new HashMap<String, String>();
		if (data != null) {
			for (SimpleMap simpleMap : data)
				retValue.put(simpleMap.getKey(), simpleMap.getValue());
		}
		return retValue;
	}
}
