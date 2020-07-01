package com.ew.simulation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.random.RandomDataGenerator;

import com.ew.gson.typeadapters.GsonFactory;
import com.ew.util.InterfaceSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
/**
 * <p>
 * The Trend Executor executes a map reduce against a {@link MapFactory} and then applies the list of {@link Trend} to the Map results set.
 * </p>
 *  
 * @author everett
 * @verion 1.0
 * @since 2020/05/19
 *
 */
public class TrendExecutor {
	public String query = "domain=Coherence,Type=Management";
	public List<Trend> trends;
	public String classname;
	


	public static void main(String[] asArgs) {
		TrendExecutor te = new TrendExecutor();
		te.testTrends();
		String json = TrendExecutor.toJSon(te);
		System.out.println(json);
	}
	
	public TrendExecutor() { 
		classname = this.getClass().getName();
	}
		
	public Map executeTrends(MapFactory factory) {
		Map data = factory.getDataMap();
		long time = System.currentTimeMillis();
		
		Set<String> keys = factory.reduce(data, query);
		for (String key : keys) {
			Map values = (Map) data.get(key);
			if (trends != null) {
				for (Trend trend : trends) {
					String attribute = trend.getAttribute();
					Object oVal = values.get(attribute);
					if (oVal instanceof Double) {
						Double prior = (Double) oVal;
						Double iNext = trend.nextDouble(time, prior);
						values.put(attribute, iNext);
					} else if (oVal instanceof Long) {
						Long prior = (Long) oVal;
						Long iNext = trend.nextLong(time, prior);
						values.put(attribute, iNext);
					} else if (oVal instanceof Integer) {
						Integer prior = (Integer) oVal;
						Integer iNext = trend.nextInteger(time, prior);
						values.put(attribute, iNext);
					} else if (oVal instanceof String) {
						String sNext = trend.nextString(time);
						values.put(attribute, sNext);
					}
				}
				data.put(key, values);
			}
		}
		return data;
	}
	
	
	private Map<String, Map> getTempMap() {
		Map<String, Map> ret = new HashMap<String, Map>();
		Map tmp1 = new HashMap();
		tmp1.put("G1", 100);
		tmp1.put("G2", 200);
		tmp1.put("U1", 10);
		ret.put("A", tmp1);
		
		Map tmp2 = new HashMap();
		tmp2.put("G1", 150);
		tmp2.put("G2", 250);
		tmp2.put("U1", 15);
		ret.put("B", tmp1);
		
		Map tmp3 = new HashMap();
		tmp3.put("G1", 250);
		tmp3.put("G2", 550);
		tmp3.put("U1", 25);
		ret.put("C", tmp1);
		return ret;
	}
	
	private void testTrends() {
		GausianTrend gw = new GausianTrend();
		gw.mu = 10.0D;
		gw.sigma = 3.0D;
		gw.reseed = true;
		gw.attribute = "RefreshPredictionCount";

		UniformTrend uw = new UniformTrend();
		uw.attribute = "RefreshCount";
		uw.max = 1000.0D;
		uw.min = 940.0D;
		uw.reseed = true; 
		
		TimeRangesTrend tr = TimeRangesTrend.getDefault();
		
 		addTrend(gw);
		addTrend(uw);
		addTrend(tr);	
	}
	
	
	public static String toJSon(TrendExecutor te) {
		Gson gson = GsonFactory.getGSon();
		String json = gson.toJson(te);
		return json;
	}

	public static TrendExecutor fromJSon(String json) {
		Gson gson = GsonFactory.getGSon();
		Object ret;
		ret = gson.fromJson(json, TrendExecutor.class);
		return (TrendExecutor)ret;
	}
	
	public static String toJSonList(List list) {
		Gson gson = GsonFactory.getGSon();
		String json = gson.toJson(list);
		return json;
	}
	
	public static List<Trend> fromJSonList(String json) {
		Gson gson = GsonFactory.getGSon();
		List<Trend> fromJson;
		fromJson = gson.fromJson(json, List.class);
		
		/*
		for (Iterator iter = fromJson.iterator(); iter.hasNext(); ) {
			Object trend = (Object) iter.next();
			LinkedTreeMap m = (LinkedTreeMap) trend;
			String myClass = (String)m.get("classname");
			Method method;
			try {
				method = Class.forName(myClass).getMethod("fromJSon", String.class);
				Trend t = (Trend) method.invoke(null, trend.toString());
				listTrends.add(t);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		
		return fromJson;
	}
	
	public List<Trend> getTrends() {
		if (trends == null) trends = new LinkedList<Trend>();
		return trends;
	}
	
	public void addTrend(Trend trend) {
		getTrends().add(trend);
	}
	


}
