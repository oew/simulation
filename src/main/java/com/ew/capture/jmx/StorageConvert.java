package com.ew.capture.jmx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanInfo;

//import com.sl.util.coherence.ssc.SLPOFWrapper;

public class StorageConvert {

	public static void main(String[] args) {
		/*
		 * String fromDir = "/home/everett/SL/zip/riot/riot/files/demo/"; String
		 * fromDomain = "Coherence"; String toDomain = "Coherence"; String toDir =
		 * "/home/everett/ss/riot/"; String fromClass = ""; String toClass = "";
		 * 
		 * MBeanStorageBinaryOld fromStore = new MBeanStorageBinaryOld();
		 * MBeanStorageBinary toStore = new MBeanStorageBinary(); Map<String, String>
		 * fromNdx = fromStore.GetMBeanNdx(fromDir, fromDomain); Set<String> classNames
		 * = new HashSet<String>(); Map<String, String> toNdx = new HashMap<String,
		 * String>();
		 * 
		 * for (String key : fromNdx.keySet()) { String val = fromNdx.get(key); int ndx
		 * = Integer.valueOf(val.replace(fromDomain, "")); Map<String, Object> mapData =
		 * fromStore.GetMBeanData(fromDir, fromDomain, ndx);
		 * 
		 * System.out.println("Keys = " + key + ", Val = " + val + ", ndx = " + ndx +
		 * ", attibCnt =" + mapData.size()); for (Object oAttribute : mapData.values())
		 * { String name = oAttribute != null ? oAttribute.getClass().getName() :
		 * "null"; classNames.add(name); } MBeanInfo nfo=
		 * fromStore.GetMBeanInfo(fromDir, fromDomain, ndx); toStore.StoreMBean(toDir,
		 * toDomain, ndx, mapData, nfo); toNdx.put(Integer.toString(ndx), key); }
		 * toStore.StoreMBeanNdx(toDir, toDomain, 0, toNdx);
		 */
	}

}
