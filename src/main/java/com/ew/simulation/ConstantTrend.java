package com.ew.simulation;

import com.ew.gson.typeadapters.GsonFactory;

/**
 * <p>A Constant trend for setting defaults and applying stair step trend lines in correlation with the (@link TimeRangesTrend) 
 * </p>
 * 
 * 
 * @author Everett Williams
 * @version 1.0
 * @since 2020/05/19
 */
public class ConstantTrend extends Trend {
	Object value;
	
	public static void main(String[] args) {
		ConstantTrend t = new ConstantTrend();
		t.attribute = "LastUpdated";
		t.value = 10000;
		t.setIncremental(true);
		Integer i = (new ConstantTrend("slop", 10, false)).nextInteger(System.currentTimeMillis(), 0);
		System.out.println("i = " + i);
		String json = GsonFactory.getGSon().toJson(t);
		Trend trend = GsonFactory.getGSon().fromJson(json, Trend.class);
		System.out.println(json);
		System.out.println(trend);
	}

	public ConstantTrend() {
		classname = this.getClass().getName(); 
	}
	
	public ConstantTrend(String attribute, Object value, boolean isIncremental) {
		this.attribute = attribute;
		this.value = value;
		this.isIncremental = isIncremental;
	}

	@Override
	public Double nextDouble(long timestamp, Double prior) {
		if (isIncremental) return Double.valueOf(value.toString()) + prior;
		return Double.valueOf(value.toString());
	}

	@Override
	public Integer nextInteger(long timestamp, Integer prior) {
		if (isIncremental) return Double.valueOf(value.toString()).intValue() + prior;
		return Double.valueOf(value.toString()).intValue();
	}

	@Override
	public Long nextLong(long timestamp, Long prior) {
		if (isIncremental) return Double.valueOf(value.toString()).longValue() + prior;
		return Double.valueOf(value.toString()).longValue();
	}

	@Override
	public String nextString(long timestamp) {
		return value.toString();
	}

	@Override
	public long nextDatetime(long timestamp) {
		return timestamp;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ConstantTrend [attribute=")
		.append(attribute).append(", value=").append(value)
		.append(", isIncremenatal=").append(isIncremental).append("]");
		return sb.toString();
	}
}
