package com.ew.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonHelper {
	/**
	 * Convert the Json generic map of data to the class information using the classname to determine the java object to create.
	 *  
	 * @param mapData a map of key value pairs of data extracted from the json.
	 * 
	 * @return an object with the items updated using reflection.
	 */
	public static Object mapToClass(Map<String, Object> mapData) {
		Object instance  = null;
		String classname = (String)mapData.get("classname");
		String sPackage  = "com.ew.simulation.";
		
		// allows for class shorthand in simulation
		if (!classname.contains(".")) classname = sPackage + classname;
		try {
			instance = Class.forName(classname).newInstance();
			for (String key : mapData.keySet()) {
				try {
					Class itemClass = instance.getClass();
					Field field = getDeclaredField(itemClass, key);
					if (field != null) {
						Object value = mapData.get(key);
						if (value instanceof Map && !field.getClass().isAssignableFrom(Map.class)) {
							Object subValue = mapToClass((Map<String, Object>) value);
							value = subValue;
						}
						if (value instanceof List) {
							List list = (List)value;
							List subValue = new LinkedList();
							for (Object i : list) {
								subValue.add(mapToClass((Map<String, Object>) i));
							}
							value = subValue;
						}
						
						JsonHelper.set(instance, key, value);
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Invalid Classname " + classname + " check the simulation file for the correct trend."
					+ "");
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instance;
	}

	/**
	 * Reflectively set the instance with the field and the value.
	 * 
	 * @param object the instance to update.
	 * @param fieldName the field to update.
	 * @param fieldValue the value to set the field too.
	 * @return true if the update is successful.
	 */
	public static boolean set(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                Class fieldType = field.getType();
                if (fieldType.getName().equals("long")) {
                	long lval = Math.round((Double)fieldValue);
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
	
	/**
	 * Recursively search the class hierarchy for the field.
	 * 
	 * @param clazz The class to search
	 * @param fieldName the field name.
	 * @return the field definition object.
	 */
	public static Field getDeclaredField(Class clazz, String fieldName) {
		Field field = null;
		
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			// not on the current class;
		} catch (SecurityException e) {
			// not on the current class;
		}
		if (field != null) return field;
				
	    if (clazz.getSuperclass() != null) {
	    	field = getDeclaredField(clazz.getSuperclass(), fieldName);
	    	if (field != null) {
	    		return field;
	    	} else {
	    		return null;
	    	}
	    	
	    };
	    return field;
	}
}
