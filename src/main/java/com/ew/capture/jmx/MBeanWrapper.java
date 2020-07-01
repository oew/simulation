package com.ew.capture.jmx;

import java.util.HashMap;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.ew.simulation.MapFactory;
import com.ew.util.FileHelper;

import javax.management.Attribute;
import javax.management.AttributeList;

/**
 * Class to create an MBean interface to an underlying management cache.
 * 
 * @author Everett Williams
 *
 */
@SuppressWarnings("rawtypes")
public class MBeanWrapper implements DynamicMBean {
	
	public MBeanWrapper(String domain, String name, MBeanInfo info, Map dataMap, MapFactory factory) {
		m_sName = name;
		m_sDomain = domain;
		this.info = info;

		this.factory = factory;
	}
	
	MapFactory factory; 
	MBeanInfo info = null;

	public Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException,
			ReflectionException {
		
		Map ncAttrib = factory.getDataMap();
		
		Map al = (Map) ncAttrib.get(m_sName);

		if (al != null) {
			Object oAttrib =  al.get(attribute);
			Attribute a = makeAttribute( oAttrib, attribute);
			return a;
		} else {
			System.out.println("Unable to get MBean Attribue, " + attribute + ", for MBean, "+ m_sName);
			return null;
		}
	}

	
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException {
		
	}

	public AttributeList getAttributes(String[] attributes) {
		Map ncAttrib = factory.getDataMap();
		String sKey = m_sName;

		Map al = (Map) ncAttrib.get(sKey);
		if (al != null) {
			AttributeList ret = new AttributeList();
			for (int i = 0; i < attributes.length; i++) {
				Object oAttrib = al.get(attributes[i]);
				
				if (oAttrib instanceof Object[]) {
					// This is a hack created due to a bug where the serializer is losing the type of the array.
					if (oAttrib instanceof Object[]) {
						Object[] oAr = (Object[]) oAttrib;
						int iLen = oAr.length;
					    String[] sAr = new String[iLen];
					    int iStrCnt = 0;
						for (int iStr = 0; iStr < iLen; iStr++) {
							if (oAr[iStr] instanceof String) {
								iStrCnt ++;
								sAr[iStr] = (String)oAr[iStr];
							}
						}
						if (iStrCnt == iLen) {
							Attribute a;
							a = new Attribute(attributes[i], sAr);
							ret.add(a);
						} else {
							Attribute a;
							a = new Attribute(attributes[i], oAr);
							ret.add(a);
						}
					} 				
				} else {
					Attribute a;
					MBeanAttributeInfo info = getNameToInfo().get(attributes[i]);
					a = makeAttribute(oAttrib, attributes[i]);
					ret.add(a);
				}
			}
			return ret;
		} else {
			System.out.println("Unable get MBean Attributes for, " + "jmxdomain="+m_sDomain+","+this.m_sName);
			return null;
		}
	}
	
	protected Attribute makeAttribute(Object oAttrib, String name) {
		Attribute a;
		MBeanAttributeInfo info = getNameToInfo().get(name);
		if (oAttrib instanceof Attribute)
			return (Attribute) oAttrib;
		else {
			String type = info.getType();
			if (type.equals("java.lang.Long") && oAttrib instanceof Double) {
				Long l = Math.round((Double)oAttrib);
				a = new Attribute(name, l);
			} else if (type.equals("java.lang.Integer") && oAttrib instanceof Double ) {
				Long l = Math.round((Double)oAttrib);
				Integer i = l.intValue();
				a = new Attribute(name, i);
				
			} else {
				a = new Attribute(name, oAttrib);
			}
			return a;
		}
	}

	public AttributeList setAttributes(AttributeList attributes) {
		return null;
	}

	public MBeanInfo getMBeanInfo() {
		return this.info;
	}
	
	String m_sName;
	String m_sDomain;
	
	HashMap mapAttribToIndex;
	Map<String, MBeanAttributeInfo> mapMbi;
	
	protected Map<String, MBeanAttributeInfo> getNameToInfo() {
		if (mapMbi == null) {
			mapMbi = new HashMap<String, MBeanAttributeInfo>();
			MBeanAttributeInfo[] mbi = info.getAttributes();
			for (int i = 0; i < mbi.length; i++) {
				mapMbi.put(mbi[i].getName(), mbi[i]);
			}
		}
		return mapMbi;
	}
	
	
	@SuppressWarnings("unchecked")
	public Object invoke(String actionName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
		return null;
	}
}
