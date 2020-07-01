package com.ew.simulation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

import com.ew.gson.typeadapters.GsonFactory;

public class TimeRangeTrend extends Trend {
	String rangeStart;
	String rangeEnd;
	String rangeFormat;
	Trend  trend;
	
	public TimeRangeTrend() {
		classname = this.getClass().getName(); 
	}
	
	
	public static void main(String[] args) {
		TimeRangeTrend t = new TimeRangeTrend();
		t.attribute = "RefreshCount";
		t.rangeStart = "Mon 10:00:00";
		t.rangeEnd = "Mon 11:00:00";
		t.rangeFormat= "EEE HH:mm:ss";
		
		UniformTrend subTrend = new UniformTrend();
		subTrend.max = 100.0;
		subTrend.min = 50.0;
		subTrend.attribute = t.attribute;
		t.trend = subTrend;
		
		String json  = GsonFactory.getGSon().toJson(t);
		System.out.println(json);
		TimeRangeTrend t2 = (TimeRangeTrend) GsonFactory.getGSon().fromJson(json, Trend.class);
		System.out.println(t.checkTime(System.currentTimeMillis()));
	}
	
	public boolean checkTime(long trendTime) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(rangeFormat); 
		    Date checkDate = new Date(trendTime);
		    Date timeCurrent = format.parse(format.format(checkDate));
		    Calendar calCheck = Calendar.getInstance();
		    calCheck.setTime(timeCurrent);
		    
		    String string1 = rangeStart;
		    Date timeStart = new SimpleDateFormat(rangeFormat).parse(rangeStart);
		    Calendar calStart = Calendar.getInstance();
		    calStart.setTime(timeStart);
		    

		    Date timeEnd = new SimpleDateFormat(rangeFormat).parse(rangeEnd);
		    Calendar calEnd = Calendar.getInstance();
		    calEnd.setTime(timeEnd);
		   
		    if (timeStart.getTime() <= timeCurrent.getTime() && timeCurrent.getTime() <= timeEnd.getTime()) 
		    	return true;

		} catch (ParseException e) {
		    e.printStackTrace();
		}
		return false;
	}

	@Override
	public Double nextDouble(long timestamp, Double prior) {
		if (this.checkTime(timestamp)) return trend.nextDouble(timestamp, prior);
		return prior;
	}

	@Override
	public Integer nextInteger(long timestamp, Integer prior) {
		if (this.checkTime(timestamp)) return trend.nextInteger(timestamp, prior);
		return prior;
	}

	@Override
	public Long nextLong(long timestamp, Long prior) {
		if (this.checkTime(timestamp)) return trend.nextLong(timestamp, prior);
		return prior;
	}

	@Override
	public String nextString(long timestamp) {
		return "";
	}

	@Override
	public long nextDatetime(long timestamp) {
		return timestamp;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("TimeRangeTrend [attribute=").append(attribute)
		.append(", rangeStart =").append(rangeStart)
		.append(", rangeEnd =").append(rangeEnd)
		.append(", rangeFormat =").append(rangeFormat)
		.append(", incremental=").append(isIncremental())
		.append(", Trend =").append(trend.toString()).append("]");
		return sb.toString();
	}
	
	public static TimeRangeTrend getDefaultClass() {
		TimeRangeTrend t = new TimeRangeTrend();
		t.attribute = "RefreshCount";
		t.rangeStart = "Mon 10:00:00";
		t.rangeEnd = "Mon 11:00:00";
		t.rangeFormat= "EEE HH:mm:ss";
		
		UniformTrend subTrend = new UniformTrend();
		subTrend.max = 100.0;
		subTrend.min = 50.0;
		subTrend.attribute = t.attribute;
		t.trend = subTrend;
		return t;
	}
	
}
