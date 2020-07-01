package com.ew.gson.typeadapters;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.Adapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class PolyMorphicAdapterFactory implements TypeAdapterFactory {
	
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    	
      final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
      
      Class<T> rawType = (Class<T>) type.getRawType();
      
      String raw = rawType.getName();

/*      if (raw.startsWith("com.ew.") && rawType.isInterface()) {
        try {
			Method classname = rawType.getMethod("getClassname", null);
		} catch ( SecurityException | NoSuchMethodException e) {
	        return null;
		}
      } else {
    	  return null;
      }*/
      
      System.out.println ("Custom adapter for  " + raw);
      
      PolyTypeAdapter<T> pta = new PolyTypeAdapter<T>();
      pta.setDelegate(delegate);
      pta.setClassname(raw);
      return pta;
    		 /* new TypeAdapter<T>() {
        public void write(JsonWriter out, T value) throws IOException {
        	Gson gson = GsonFactory.getGSon();
        	TypeAdapter t = gson.getAdapter(value.getClass());
        	t.write(out, value);
        }

        public T read(JsonReader reader) throws IOException {

        	Map<String, Object> mapData = new HashMap<String, Object>();
        	
        	JsonToken t2 = reader.peek();
        	while (reader.hasNext()) {
        		JsonToken t = reader.peek();
        		if (t == JsonToken.BEGIN_OBJECT ) {
        			reader.beginObject();
        		} else if (t == JsonToken.BEGIN_ARRAY) {
        			reader.beginArray();
        		} else if (t == JsonToken.END_ARRAY) {
        			reader.endArray();
        		} else if (t == JsonToken.END_OBJECT) {
        			reader.endObject();
        		} else {
	        		String attribute = reader.nextName();
	        		System.out.println(attribute);
	        		Object value = null;
	        		t = reader.peek();
	        		switch(t) {
	        		case BOOLEAN:
	        			value = reader.nextBoolean();
	        			break;
	        			
	        		case NUMBER:
	        			value = reader.nextDouble();
	        			break;
	        			
	        		case STRING:
	        			value = reader.nextString();
	        			break;
	        		case BEGIN_OBJECT:
	        			System.out.println("Reading sub object ="  + t.);
	        			value = delegate.read(reader); 
	        			break;
	        		}
	        		if (value != null)
	        			mapData.put(attribute, value);
        		}
        	}
        		
        	
        	Object instance = null;
        	String classname = (String)mapData.get("classname");
        	try {
				instance = Class.forName(classname).newInstance();
				for (String key : mapData.keySet()) {
					Object value = mapData.get(key);
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
        	 JsonToken next = reader.peek();
       		if (next == JsonToken.END_OBJECT ) reader.endObject();
        	return (T) instance;
        }
      };
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
        return false; */
    } 


  }