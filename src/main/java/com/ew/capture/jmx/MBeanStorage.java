package com.ew.capture.jmx;
import java.util.Map;

import javax.management.MBeanInfo;

/**
 * an inteface that will store MBeanInformation to disk using different storage methods.  
 * 
 * @author Everett Williams
 * @Date 2020-06-32
 */
public interface MBeanStorage {

	public void StoreMBean(String location, String domain, int index, Map<String, Object> data, MBeanInfo info);
	
	public void StoreMBeanNdx(String location, String domain, int index, Map<String,String> ndx);
	
	public Map<String, String> GetMBeanNdx(String location, String domain);

	public MBeanInfo GetMBeanInfo(String location, String domain, int index);

	public Map<String, Object> GetMBeanData(String location, String domain, int index);
	
	public String formatIndex(int index);

	public int getIndex(String index);
	
}
