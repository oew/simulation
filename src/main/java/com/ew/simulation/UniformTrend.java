package com.ew.simulation;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.random.RandomDataGenerator;

import com.ew.gson.typeadapters.GsonFactory;
import com.ew.util.InterfaceSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UniformTrend extends Trend {
	public long    seed;
	public boolean reseed = true;
	public double  max = 10.0D;
	public double  min = 3.0D;
	
	public static void  main(String[] asArgs) { 

	}
	
	public UniformTrend() {
		classname = this.getClass().getName();
	}

	@Override
	public Double nextDouble(long timestamp, Double prior) {
		if (isIncremental) 
			return prior + getRandom().nextUniform(min, max);
		else
			return getRandom().nextUniform(min, max);	
	}

	@Override
	public Integer nextInteger(long timestamp, Integer prior) {
		if (isIncremental)
			return prior + getRandom().nextInt((int) Math.round(min), (int)Math.round(max));
		else
			return getRandom().nextInt((int) Math.round(min), (int)Math.round(max));
	}

	@Override
	public Long nextLong(long timestamp, Long prior) {
		if (isIncremental)
			return prior + getRandom().nextLong((int) Math.round(min), (int)Math.round(max));
		else
			return getRandom().nextLong((int) Math.round(min), (int)Math.round(max));	
	}

	@Override
	public String nextString(long timestamp) {
		return null;
	}

	@Override
	public long nextDatetime(long timestamp) {
		return 0;
	}
	
	protected RandomDataGenerator getRandom() {
		if (rdg == null) {
			rdg = new RandomDataGenerator();
			rdg.reSeed(this.seed);
		}
		if (reseed) rdg.reSeed();
		return rdg;
	}
	
	public String toJSon() {
		String json = GsonFactory.getGSon().toJson(this);
		return json;
	}
	
	public static Trend fromJSon(String json) {
		Object ret;
		ret = GsonFactory.getGSon().fromJson(json, UniformTrend.class);
		return (Trend)ret;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("UniforTrend [attribute=")
		.append(attribute).append(", max=").append(max).append(", min=").append(min)
		.append(", reseed=").append(reseed).append("]");
		return sb.toString();
	}
	
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}	
	
	private RandomDataGenerator rdg;


}
