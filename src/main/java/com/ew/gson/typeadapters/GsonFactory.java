package com.ew.gson.typeadapters;

import java.lang.reflect.Field;
import java.util.Map;

import com.ew.simulation.Trend;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class GsonFactory {
	/**
	 * Setup the custom serialization.
	 * @return a gson serializer.
	 */
	public static Gson getGSon2() {
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeAdapterFactory(new PolyMorphicAdapterFactory());
			Gson gson = builder.create();
			return gson;
	}
	
	public static Gson getGSon() {
	// which format has the response of the server
		final TypeToken<Trend> requestListTypeToken = new TypeToken<Trend>() {};

	// adding all different container classes with their flag
/*	final RuntimeTypeAdapterFactory<Trend> typeFactory = RuntimeTypeAdapterFactory  
	        .of(Trend.class)
	        .registerSubtype(UniformTrend.class)
	        .registerSubtype(GausianTrend.class)
	        .registerSubtype(TimeRangeTrend.class)
	        .registerSubtype(TimeRangesTrend.class)
	        .registerSubtype(ConstantTrend.class); 

	final RuntimeTypeAdapterFactory<MapFactory> typeMapFactory = RuntimeTypeAdapterFactory  
	        .of(MapFactory.class)
	        .registerSubtype(JmxDomainSimulation.class); // Here you specify which is the parent class and what field particularizes the child class.

	final RuntimeTypeAdapterFactory<MapSimulation> typeSimFactory = RuntimeTypeAdapterFactory   
	        .of(MapSimulation.class); // Here you specify which is the parent class and what field particularizes the child class. */

	// add the polymorphic specialization
	final Gson gson = new GsonBuilder().create();
	return gson;
	}
	
	
	public static Object mapToClass(Map<String, Object> map) {
    	Object instance = null;
    	String classname = (String)map.get("classname");
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

	public static boolean set(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                Class o = field.getType();
                if (o.getName().equals("long")) {
                	long lval = Math.round((Double)fieldValue);
                	field.set(object, lval);
                }
                else 
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
