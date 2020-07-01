package com.ew.simulation;

import java.util.LinkedList;
import java.util.List;

import com.ew.gson.typeadapters.GsonFactory;

public class TimeRangesTrend extends Trend {
	List<TimeRangeTrend> trends;
	Trend defaultTrend;

	public TimeRangesTrend() {
		classname = this.getClass().getName(); 
	}
	public static void main(String[] args) {
		
		TimeRangesTrend tr = TimeRangesTrend.getDefault();
		String json = GsonFactory.getGSon().toJson(tr);
		System.out.println(json);
		TimeRangesTrend tr2 = (TimeRangesTrend) GsonFactory.getGSon().fromJson(json, TimeRangesTrend.class);
		long now = System.currentTimeMillis();
		long next = tr2.nextLong(now, 0L);
		System.out.println(next);
		
	//	System.out.println(t.checkTime(System.currentTimeMillis()));
	}
	
	public static TimeRangesTrend getDefault() {
		TimeRangesTrend tr = new TimeRangesTrend();
		tr.attribute = "SystemCpuLoad";
		tr.isIncremental = false;
		
		TimeRangeTrend t = new TimeRangeTrend();
		t.attribute = "SystemCpuLoad";
		t.rangeStart = "10:00:00";
		t.rangeEnd = "11:00:00";
		t.rangeFormat= "HH:mm:ss";
		UniformTrend subTrend = new UniformTrend();
		subTrend.max = 100.0;
		subTrend.min = 50.0;
		subTrend.attribute = t.attribute;
		t.trend = subTrend;

		tr.trends = new LinkedList<TimeRangeTrend>();
		tr.trends.add(t);
		tr.defaultTrend = new ConstantTrend(tr.attribute, 10, false);
		return tr;
	}
	

	@Override
	public Double nextDouble(long timestamp, Double prior) {
		for (TimeRangeTrend t : trends) {
			if (t.checkTime(timestamp))
				return t.nextDouble(timestamp, prior);
		}
		// TODO Auto-generated method stub
		return defaultTrend.nextDouble(timestamp, prior);
	}

	@Override
	public Integer nextInteger(long timestamp, Integer prior) {
		for (TimeRangeTrend t : trends) {
			if (t.checkTime(timestamp))
				return t.nextInteger(timestamp, prior);
		}
		// TODO Auto-generated method stub
		return defaultTrend.nextInteger(timestamp, prior);
	}

	@Override
	public Long nextLong(long timestamp, Long prior) {
		for (TimeRangeTrend t : trends) {
			if (t.checkTime(timestamp))
				return t.nextLong(timestamp, prior);
		}
		// TODO Auto-generated method stub
		return defaultTrend.nextLong(timestamp, prior);
	}

	@Override
	public String nextString(long timestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long nextDatetime(long timestamp) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("TimeRangesTrend [attribute=").append(attribute)
		.append(", incremental=").append(isIncremental())
		.append(", DefaultTrend = ").append(this.defaultTrend);
		sb.append(", Ranges  = [");
		for (TimeRangeTrend t : trends) 
			sb.append(t.toString()).append(", ");
			
		sb.append("]]");
		return sb.toString();
	}

}
