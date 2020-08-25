package com.tmm.capture.jmx;

import com.tmm.simulation.MapFactory;
import com.tmm.util.LogHelper;
import java.util.HashMap;
import java.util.Map;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to create an MBean interface to an underlying management map.
 * 
 * @author Everett Williams
 * @since 1.0
 *
 */
@SuppressWarnings("rawtypes")
public class MBeanWrapper implements DynamicMBean {
  private static final Logger logger = LogManager.getLogger(MBeanWrapper.class);

  /**
   * Create a MBean to expose the underlying map entry.
   * 
   * @param domain The JMX domain.
   * @param name The name of the map key.
   * @param info The MBean Info.
   * @param factory The MapFactory to connect and retrieve the MBean Attributes.
   */
  public MBeanWrapper(String domain, String name, MBeanInfo info, MapFactory factory) {
    cacheKey = name;
    this.domain = domain;
    this.info = info;

    this.factory = factory;
  }

  MapFactory factory;
  MBeanInfo info = null;

  @Override
  public Object getAttribute(String attribute)
      throws AttributeNotFoundException, MBeanException, ReflectionException {

    Map ncAttrib = factory.getSimulationMap();

    Map al = (Map) ncAttrib.get(cacheKey);

    if (al != null) {
      Object value = al.get(attribute);
      Attribute v = makeAttribute(value, attribute);
      return v;
    } else {
      logger.error(LogHelper.format("SIM0013", this.domain, this.cacheKey));
      return null;
    }
  }

  @Override
  public void setAttribute(Attribute attribute) throws AttributeNotFoundException,
      InvalidAttributeValueException, MBeanException, ReflectionException {

  }

  @Override
  public AttributeList getAttributes(String[] attributes) {
    Map ncAttrib = factory.getSimulationMap();
    String key = cacheKey;

    Map al = (Map) ncAttrib.get(key);
    if (al != null) {
      AttributeList ret = new AttributeList();
      for (int i = 0; i < attributes.length; i++) {
        Object value = al.get(attributes[i]);
        
        if (value instanceof Object[]) {
          // This is a hack created due to a bug where the serializer is losing the type
          // of the array.
          if (value instanceof Object[]) {
            Object[] valueArray = (Object[]) value;
            int valLen = valueArray.length;
            String[] valueStringArray = new String[valLen];
            int stringCnt = 0;
            for (int curString = 0; curString < valLen; curString++) {
              if (valueArray[curString] instanceof String) {
                stringCnt++;
                valueStringArray[curString] = (String) valueArray[curString];
              }
            }
            if (stringCnt == valLen) {
              Attribute a;
              a = new Attribute(attributes[i], valueStringArray);
              ret.add(a);
            } else {
              Attribute a;
              a = new Attribute(attributes[i], valueArray);
              ret.add(a);
            }
          }
        } else {
          Attribute a;
          a = makeAttribute(value, attributes[i]);
          ret.add(a);
        }
      }
      return ret;
    } else {
      logger.error(LogHelper.format("SIM0012", this.domain, this.cacheKey));
      return null;
    }
  }

  /**
   * Convert the Map Entry into an Attribute.
   * 
   * @param value The Map Entry.
   * @param name The Name of the attribute
   * @return an MBean Attribute.
   */
  protected Attribute makeAttribute(Object value, String name) {
    Attribute a;
    MBeanAttributeInfo info = getNameToInfo().get(name);
    if (value instanceof Attribute) {
      return (Attribute) value;
    } else {
      String type = info.getType();
      if (type.equals("java.lang.Long") && value instanceof Double) {
        Long l = Math.round((Double) value);
        a = new Attribute(name, l);
      } else if (type.equals("java.lang.Integer") && value instanceof Double) {
        Long l = Math.round((Double) value);
        Integer i = l.intValue();
        a = new Attribute(name, i);

      } else {
        a = new Attribute(name, value);
      }
      return a;
    }
  }

  @Override
  public AttributeList setAttributes(AttributeList attributes) {
    return null;
  }

  @Override
  public MBeanInfo getMBeanInfo() {
    return this.info;
  }

  String cacheKey;
  String domain;

  HashMap mapAttribToIndex;
  Map<String, MBeanAttributeInfo> mapMbi;

  /**
   * Convert the MBean AttributeInfo into a Map for easy access.
   * @return a Map with the Attribute name and the MBean Attribute info as the entry.
   */
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

  @Override
  public Object invoke(String actionName, Object[] params, String[] signature)
      throws MBeanException, ReflectionException {
    return null;
  }
}
