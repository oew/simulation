package com.ew.gson.typeadapters;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ew.simulation.Trend;
import com.ew.util.JsonHelper;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class PolyTypeAdapter<T> extends TypeAdapter<T> {

    	TypeAdapter<T> delegate = null;
    	String classname = null;
    	
    	public void setDelegate(TypeAdapter<T> defTypeAdapter) {
    		delegate = defTypeAdapter;
    	}

    	public void setClassname(String ClassName) {
    		classname = ClassName;
    	}
    	
		@Override
	    public void write(JsonWriter out, T value) throws IOException {
        	Gson gson = GsonFactory.getGSon();
        	TypeAdapter t = gson.getAdapter(value.getClass());
        	t.write(out, value);
        }

		@Override
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
        			reader.endArray();
        		} else if (t == JsonToken.END_DOCUMENT) {
        			break;
        			
        		} else {
	        		String attribute = reader.nextName();
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
	        			
	        		case END_DOCUMENT:
	        			reader.close();
	        			break;
	        			
	        		case BEGIN_OBJECT:
	        			String sClassImp = (String) mapData.get("classname");
                		try {
							Class main = Class.forName(sClassImp);

							Field field = main.getDeclaredField(attribute);
		                	Gson gson = GsonFactory.getGSon();
		                	field.getClass().getInterfaces();
		                	TypeAdapter tSub = gson.getAdapter(field.getType());
		                	value = tSub.read(reader);
		                	
		                	boolean b = reader.hasNext();
		                	System.out.println("HasNext = " + b + ", next = " + reader.peek());
		                	if (reader.peek() == JsonToken.END_OBJECT) reader.endObject();
						} catch (ClassNotFoundException | NoSuchFieldException | SecurityException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	        			break;

	        		case BEGIN_ARRAY:
	        			String sClassImp2 = (String) mapData.get("classname");
                		try {
		                	Gson gson = GsonFactory.getGSon();
							Class main = Class.forName(sClassImp2);
							Field field = main.getDeclaredField(attribute);
		                	TypeAdapter tSub = gson.getAdapter(field.getType());
		                	
		                	value = tSub.read(reader);

		                	if (reader.peek() == JsonToken.END_ARRAY) reader.endArray();
						} catch (ClassNotFoundException | NoSuchFieldException | SecurityException e1) {
							e1.printStackTrace();
						}
	        			break;
	        		}
	        		if (value != null)
	        			mapData.put(attribute, value);
        		}
        		
        		
           	}
        	if (reader.peek() == JsonToken.END_OBJECT) reader.endObject();
        		
        	
        	System.out.println("next " + reader.peek());
        	return (T) JsonHelper.mapToClass(mapData);
        }

    
    public boolean set(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                Class o = field.getType();
                if (fieldValue instanceof Map) {
                	
                }
                if (o.getName().equals("long")) {
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
    
    protected T[] processArray(JsonReader reader) {
    	List<T> list = new LinkedList<T>();
    	int iSize = list.size();
    	T[] retVal = null; 
    	try {
			while (reader.hasNext()) {
				
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
    	return list.toArray(retVal);
    }

	public Object mapToClass(Map<String, Object> mapData) {
		Object instance = null;
		String classname = (String)mapData.get("classname");
		String sPackage = "com.ew.simulation.";
		if (!classname.contains(".")) classname = sPackage + classname;
		try {
			instance = Class.forName(classname).newInstance();
			System.out.println("Updating Class " + classname);
			for (String key : mapData.keySet()) {
				Object value = mapData.get(key);
				if (value instanceof Map) {
					
				}
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
	/*      	while (reader.peek() == JsonToken.END_OBJECT) {
			reader.endObject();
			System.out.println("END_OBJECT");
		} */ 
		System.out.println("Returning " + instance);
		return (T) instance;
	}    
}
