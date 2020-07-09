package com.ew.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Field;
import java.util.Map;


/**
 * A class to create the Gson as well as helper methods to implement polymorphism with GSon.
 * 
 * @author everett
 */
public class GsonFactory {

  /**
   * Configure and return the Gson Serializer.
   * 
   * @return the GSON serializer.
   */
  public static Gson getGSon() {
    final Gson gson = new GsonBuilder().create();
    return gson;
  }

  /**
   * Convert a GSon map to a class based on a "classname" attribute.
   * 
   * @param map A map containing the Gson deserialized map.
   * 
   * @return a class of type classname.
   */
  public static Object mapToClass(Map<String, Object> map) {
    Object instance = null;
    String classname = (String) map.get("classname");
    try {
      instance = Class.forName(classname).newInstance();
      for (String key : map.keySet()) {
        Object value = map.get(key);
        set(instance, key, value);
      }
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return instance;
  }

  /**
   * Set an Object Field name based using reflection
   * 
   * @param object Object to update.
   * @param fieldName the field to update.
   * @param fieldValue the value.
   * @return true if the value was set.
   */
  public static boolean set(Object object, String fieldName, Object fieldValue) {
    Class<?> clazz = object.getClass();
    while (clazz != null) {
      try {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        Class o = field.getType();
        if (o.getName().equals("long")) {
          long lval = Math.round((Double) fieldValue);
          field.set(object, lval);
        } else
          field.set(object, fieldValue);
        return true;
      } catch (NoSuchFieldException e) {
        clazz = clazz.getSuperclass();
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }
    return false;
  }
}
