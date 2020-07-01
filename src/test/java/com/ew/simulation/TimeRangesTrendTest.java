package com.ew.simulation;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.junit.Test;

public class TimeRangesTrendTest {

	interface TestTrend {
		public Number getNext(Trend t, long time, Number prior);
	}
	
	@Test
	public void testNextDouble() {
		long lStart = System.currentTimeMillis();
		lStart = lStart + (1000 - (lStart %1000));
		long lRuntime = 20000L;
		TestTrend tt = (trend, time, prior) -> trend.nextDouble(time, prior.doubleValue());
		TimeRangesTrend trend = getTrend(lStart);
		boolean isError = callTrend(tt, trend, lStart, lRuntime);
		if (isError) fail("incorrect Next Double trend range calculations");
	}
	

	@Test
	public void testNextInteger() {
		long lStart = System.currentTimeMillis();
		lStart = lStart + (1000 - (lStart %1000));
		long lRuntime = 20000L;
		TestTrend tt = (trend, time, prior) -> trend.nextInteger(time, prior.intValue());
		TimeRangesTrend trend = getTrend(lStart);
		boolean isError = callTrend(tt, trend, lStart, lRuntime);
		if (isError) fail("incorrect Next Integer trend range calculations");
	}

	@Test
	public void testNextLong() {
		long lStart = System.currentTimeMillis();
		lStart = lStart + (1000 - (lStart %1000));
		long lRuntime = 20000L;
		TestTrend tt = (trend, time, prior) -> trend.nextLong(time, prior.longValue());
		TimeRangesTrend trend = getTrend(lStart);
		boolean isError = callTrend(tt, trend, lStart, lRuntime);
		if (isError) fail("incorrect Next Long trend range calculations");
	}

	@Test
	public void testNextString() {
	}
	
	protected TimeRangesTrend getTrend(long lCurrent) {

		long lRange1 = lCurrent+5000;
		long lRange2 = lRange1+5000;
		String format = "HH:mm:ss";
		SimpleDateFormat df = new SimpleDateFormat(format);
		
		String Start1 = df.format(new Date(lRange1));
		String End1 = df.format(new Date(lRange2-1));
		String Start2 = df.format(new Date(lRange2));
		String End2 = df.format(new Date(lRange2+5000));
		
		TimeRangesTrend tr = new TimeRangesTrend();
		tr.attribute = "RefreshCount";
		tr.isIncremental = false;
		tr.trends = new LinkedList<TimeRangeTrend>();
		
		TimeRangeTrend t = new TimeRangeTrend();
		t.attribute = "RefreshCount";
		t.rangeStart = Start1;
		t.rangeEnd = End1;
		t.rangeFormat= format;

		ConstantTrend subTrend = new ConstantTrend();
		subTrend.value = 10000;
		subTrend.attribute = t.attribute;
		t.trend = subTrend;
		tr.trends.add(t);

		TimeRangeTrend t2 = new TimeRangeTrend();
		t2.attribute = "RefreshCount";
		t2.rangeStart = Start2;
		t2.rangeEnd = End2;
		t2.rangeFormat= format;
		
		ConstantTrend subTrend2 = new ConstantTrend();
		subTrend2.value = 11000;
		subTrend2.attribute = t.attribute;
		t2.trend = subTrend2;

		tr.trends.add(t2);
		tr.defaultTrend = new ConstantTrend(tr.attribute, 10, false);
		
		return tr;
	}

	public boolean callTrend(TestTrend tt, Trend t, long start, long runtime) {
		boolean isError = false;
		long current= System.currentTimeMillis();
		Number next = 0;
		while (current-start < runtime && !isError) {
			Number prior = next;
			next = tt.getNext(t, current, prior);
			isError = checkNumber(start, current, next);
			if (isError) return true;
			current= System.currentTimeMillis();
		}
		return isError;
	}
	
	public boolean checkNumber(long start, long current, Number val) {

		if (start+5000 > current && val.longValue() != 10) return true;
		if (start+10000 > current && current > start+5000 && val.longValue() != 10000) return true;
		if (start+15000 > current && current > start+10000 && val.longValue() != 11000) return true;
		return false;
	}
}
