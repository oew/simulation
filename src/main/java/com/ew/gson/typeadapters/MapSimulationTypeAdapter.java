package com.ew.gson.typeadapters;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ew.simulation.MapFactory;
import com.ew.simulation.MapSimulation;
import com.ew.simulation.TrendExecutor;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.Adapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class MapSimulationTypeAdapter<T> extends TypeAdapter<T> {

	TypeAdapter<T> delegate = null;
	String classname = null;
	
	public void setDelegate(TypeAdapter<T> defTypeAdapter) {
		delegate = defTypeAdapter;
	}

	public void setClassname(String ClassName) {
		classname = ClassName;
	}
	
	@Override
	public T read(JsonReader reader) throws IOException {

    	Map<String, Object> mapData = new HashMap<String, Object>();
    	
    	MapSimulation ret = new MapSimulation();
    	JsonToken t2 = reader.peek();
    	System.out.println("reading " + t2);
    	
    	while (reader.hasNext()) {
    		JsonToken t = reader.peek();
        	System.out.println("reading " + t);
    		if (t == JsonToken.BEGIN_OBJECT ) {
    			reader.beginObject();
    		} else if (t == JsonToken.BEGIN_ARRAY) {
    			reader.beginArray();
    		} else if (t == JsonToken.END_ARRAY) {
    			reader.endArray();

    		} else if (t == JsonToken.END_OBJECT) {
    			reader.endArray();
    		} else if (t == JsonToken.END_DOCUMENT) {
    			break;
    			
    		} else {
        		String attribute = reader.nextName();
        		System.out.println("Deserializing " + classname + ", " + attribute);
        		Object value = null;
        		t = reader.peek();
        		switch(t) {
        		case BOOLEAN:
        			value = reader.nextBoolean();
        			break;
        			
        		case NUMBER:
        			if (attribute.equals("refreshMillis")){
        				ret.refreshMillis = reader.nextLong();
        			};
        			break;
        			
        		case STRING:
        			break;
        			
        		case END_DOCUMENT:
        			reader.close();
        			break;
        			
        		case BEGIN_OBJECT:
        			if (attribute.equals("maploaders")) {
            			TypeAdapter<MapFactory> adapter = new PolyTypeAdapter<MapFactory>();
            			ret.maploaders = (List<MapFactory>)adapter.read(reader);
        			};
        			
        		break;

        		case BEGIN_ARRAY:
        			if (attribute.equals("trendExecutors"))         			{
            			TypeAdapter<TrendExecutor> adapter = new PolyTypeAdapter<TrendExecutor>();
            	//		ret.addExecutor((TrendExecutor)adapter.read(reader));
        			};
;
        			break;
        		}
        		if (value != null)
        			mapData.put(attribute, value);
    		}
    		
    		
       	}
    	if (reader.peek() == JsonToken.END_OBJECT) reader.endObject();
    		

    	System.out.println("Returning " + ret);
    	System.out.println("next " + reader.peek());
    	return (T) ret;
    }


public boolean set(Object object, String fieldName, Object fieldValue) {
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

	public void processList() {
	}
	
	@Override
	public void write(JsonWriter out, T value) throws IOException {
	
	}
}
