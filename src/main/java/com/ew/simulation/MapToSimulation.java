package com.ew.simulation;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;

import com.ew.capture.jmx.JmxDomainSimulation;
import com.ew.gson.typeadapters.GsonFactory;
import com.ew.util.MBeanHelper;

public class MapToSimulation {
	String source;
	private static final Logger logger = LogManager.getLogger("MapToSimulation");

	public static void main(String[] args) {
		new MapToSimulation().run(args);
		System.exit(1);
	}
	
	public void run(String[] args) {
		String location = "./";
		String srcDomain = "%";
		String targetDomain = "%";
		String fullSimFile = "auto.json";
		String keyPart = "";
		if (args.length > 0) {
			location = args[0]; 
		}
		if (args.length > 1) {
			srcDomain = args[1]; 
		}

		if (args.length > 2) {
			targetDomain = args[2]; 
		}

		if (args.length > 3) {
			fullSimFile = args[3]; 
		}

		if (args.length > 4) {
			keyPart = args[4]; 
		}
		
		
		JmxDomainSimulation sim = new JmxDomainSimulation();
		sim.location = location; 
		sim.sourceDomain = srcDomain;
		sim.targetDomain = targetDomain;
		sim.serialization = "BINARY";
		sim.load();
		Map mapData = sim.getDataMap();
		Map mapInfo = sim.getInfoMap();
		
		MapSimulation ms = new MapSimulation();
		
		for (Iterator items = mapData.keySet().iterator(); items.hasNext();) {
			String sKey = (String) items.next();
			Map mapAttributes = (Map) mapData.get(sKey);
			
			TrendExecutor te = getTrendExecutor(sKey, keyPart);
			if (te == null) {
				te = new TrendExecutor();
				te.query = sKey;
				for (Iterator att = mapAttributes.keySet().iterator(); att.hasNext();) {
					String attrib = (String) att.next();
					Object value = mapAttributes.get(attrib);
					if (value instanceof Number) {
						UniformTrend trend = new UniformTrend();
						trend.attribute = attrib;
						trend.isIncremental = false;
						trend.max = 100;
						trend.min = 50;
						trend.reseed = true;
						trend.classname = "UniformTrend";
						te.addTrend(trend);
					}
				}
				putTrendExecutor(sKey, te, keyPart);
				ms.getTrendExecutors().add(te);
			}
		}

		JmxDomainSimulation simDef = new JmxDomainSimulation();
		simDef.location = location; 
		simDef.sourceDomain = srcDomain;
		simDef.targetDomain = targetDomain;
		simDef.serialization = "BINARY";
		ms.getMaploaders().add(simDef);

		ms.save(location + fullSimFile);
	}
	
	Map<String, TrendExecutor> mapCache;
	private Map<String, TrendExecutor> getMapCache(){
		if (mapCache == null) mapCache = new HashMap<String, TrendExecutor>();
		return mapCache;
	}
	
	private String formatKey(String key, String keyPart) {
		if (keyPart.length() > 0)
			return MBeanHelper.getNamePart(key, keyPart);
		else
			return key;
	}
	private TrendExecutor getTrendExecutor(String key, String keyPart) {
		return getMapCache().get(formatKey(key, keyPart));
	}

	private TrendExecutor putTrendExecutor(String key, TrendExecutor te, String keyPart) {
		return getMapCache().put(formatKey(key, keyPart), te);
	}
}
