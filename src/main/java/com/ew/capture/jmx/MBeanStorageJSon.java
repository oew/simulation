package com.ew.capture.jmx;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanInfo;

import com.ew.gson.typeadapters.GsonFactory;
import com.ew.util.FileHelper;
import com.ew.util.SerializationHelper;
import com.google.gson.Gson;

public class MBeanStorageJSon implements MBeanStorage {

	@Override
	public void StoreMBean(String location, String domain, int index, Map<String, Object> data, MBeanInfo info) {

		String fileBase = domain + formatIndex(index); 
		
		String jsonData = jsonFromMap(data);
		FileHelper.append(location + fileBase + DATASUFFIX,jsonData);

		String jsonInfo = jsonFromMbi(info);
		FileHelper.append(location + fileBase + INFOSUFFIX, jsonInfo); 
	}

	@Override
	public void StoreMBeanNdx(String location, String domain, int index, Map<String, String> ndx) {
		String jsonNdx;

		jsonNdx = jsonFromMap(ndx);
		FileHelper.append(location + domain + INDEXSUFFIX,jsonNdx); 
	}

	@Override
	public Map<String, String> GetMBeanNdx(String location, String domain) {
		String file = location + domain + INDEXSUFFIX;

		String jsonNdx = FileHelper.getText(file);
		return mapFromJSon(jsonNdx);
	}

	@Override
	public MBeanInfo GetMBeanInfo(String location, String domain, int index) {
		String fileBase = location + domain + formatIndex(index); 
		String file = fileBase + INFOSUFFIX;

		String jsonMbi = FileHelper.getText(file);
		MBeanInfo mbi = mbiFromJSon(jsonMbi); 
		return mbi;
	}

	@Override
	public Map<String, Object> GetMBeanData(String location, String domain, int index) {
		String fileBase = location + domain + formatIndex(index); 
		String file = fileBase + DATASUFFIX;
		String jsonMap = FileHelper.getText(file);
		return mapFromJSon(jsonMap);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public String jsonFromMap(Map data) {
		Gson gson = new Gson();
		String json = gson.toJson(data);
		return json;
	}

	public String jsonFromMbi(MBeanInfo info) {
		Gson gson = new Gson();
		String json = gson.toJson(info);
		return json;
	}

	public MBeanInfo mbiFromJSon(String json) {
		Gson gson = new Gson();
		MBeanInfo fromDisk;
		fromDisk = gson.fromJson(json, MBeanInfo.class);
		
		// this is a hack to deal with an serialization issue of the MBeanInfo
		// JConsole reports a unmarshaled exception when using the one from disk
		return new MBeanInfo(fromDisk.getClassName(), 
						fromDisk.getDescription(), 
						fromDisk.getAttributes(), 
						fromDisk.getConstructors(), 
						fromDisk.getOperations(), 
						fromDisk.getNotifications());
	}

	public Map mapFromJSon(String data) {
		Gson gson = new Gson();
		Map ret;
		ret = GsonFactory.getGSon().fromJson(data, HashMap.class);
		return ret;
	}
	
	@Override
	public String formatIndex(int index) {
		DecimalFormat format = new DecimalFormat("00000000");	 
		return format.format(index);
	}

	@Override
	public int getIndex(String index) {
		return Integer.valueOf(index).intValue();
	}
	
	public static String INDEXSUFFIX = "-ndx.json";
	public static String INFOSUFFIX = "-nfo.json";;
	public static String DATASUFFIX = "-dat.json";	
	
}
