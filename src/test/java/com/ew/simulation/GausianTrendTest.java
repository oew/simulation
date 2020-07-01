package com.ew.simulation;

import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.ew.gson.typeadapters.GsonFactory;

public class GausianTrendTest {
	
	@Test
	public void testNextDouble() {
		GausianTrend trend = makeTrend();
		int total = 1000;
		
		TrendBucket <Double> callDouble = (Trend t, long time, Number prior) -> {
			DataPoint<Double> dp = new DataPoint<Double>();
			Double dNew = t.nextDouble(time, prior.doubleValue());
			dp.bucket = Integer.valueOf(Long.toString(Math.round(dNew))); 
			dp.val = dNew;
			return dp;
		};
		
		DataPoint<Double> dp = new DataPoint<Double>();
		// make the distribution curve.
		int[] iResult = makeCurve(trend, 100, 1000, callDouble, dp);

		// determine if the curve is "normal".
		boolean bError = checkCurve(iResult, 50, 5, total);
		if (bError) fail("Uniform data does not conform to the correct distribution");
		
	}
	
	@Test
	public void testNextInteger() {
		GausianTrend trend = makeTrend();
		int total = 1000;
		
		TrendBucket <Integer> callDouble = (Trend t, long time, Number prior) -> {
			DataPoint<Integer> dp = new DataPoint<Integer>();
			Integer iNew = t.nextInteger(time, prior.intValue());
			dp.bucket = Integer.valueOf(Long.toString(Math.round(iNew))); 
			dp.val = iNew;
			return dp;
		};
		
		DataPoint<Integer> dp = new DataPoint<Integer>();

		// make the distribution curve.
		int[] iResult = makeCurve(trend, 100, 1000, callDouble, dp);

		// determine if the curve is "normal".
		boolean bError = checkCurve(iResult, 50, 5, total);
		if (bError) fail("Uniform data does not conform to the correct distribution");
	}

	@Test
	public void testNextLong() {
		GausianTrend trend = makeTrend();
		int total = 1000;
		
		TrendBucket <Long> callDouble = (Trend t, long time, Number prior) -> {
			DataPoint<Long> dp = new DataPoint<Long>();
			Long lNew = t.nextLong(time, prior.longValue());
			dp.bucket = Integer.valueOf(Long.toString(Math.round(lNew))); 
			dp.val = lNew;
			return dp;
		};
		
		DataPoint<Integer> dp = new DataPoint<Integer>();

		// make the distribution curve.
		int[] iResult = makeCurve(trend, 100, 1000, callDouble, dp);

		// determine if the curve is "normal".
		boolean bError = checkCurve(iResult, 50, 5, total);
		if (bError) fail("Uniform data does not conform to the correct distribution");
	}
	
	@Test
	public void testFromJSon() {
		List<Trend> list = new LinkedList<Trend>();
		
		GausianTrend gw = new GausianTrend();
		gw.mu = 10.0D;
		gw.sigma = 3.0D;
		gw.reseed = true;
		gw.attribute = "G1";
		list.add(gw);

		GausianTrend gw2 = new GausianTrend();
		gw2.mu = 100.0D;
		gw2.sigma = 30.0D;
		gw2.reseed = true;
		gw2.attribute = "G2";
		list.add(gw2);

		String sTmp = gw.toJSon();
		Trend t = (Trend) gw;
		String s = GsonFactory.getGSon().toJson(t);
		System.out.println(s);

		Trend tgw = GsonFactory.getGSon().fromJson(s, Trend.class);
		System.out.println(tgw);
		
		String listString = GsonFactory.getGSon().toJson(list);
		List<Trend> t2 = GsonFactory.getGSon().fromJson(listString, List.class);
		
		System.out.println(listString); 
	}
	
	@Test
	public void testToJSon() {
		
	}
	
	protected GausianTrend makeTrend() {
		GausianTrend gt = new GausianTrend();
		gt.mu = 50.0D;
		gt.sigma = 5.0D;
		gt.reseed = false;
		gt.seed = System.currentTimeMillis();
		System.out.println(gt);
		return gt;
	}
	
	class DataPoint<T> {
		T val;
		int bucket;
	}
	
	interface TrendBucket<T> {
		public DataPoint<T> GetBucket(Trend t, long time, Number val); 
	}
	
	public int[] makeCurve(Trend trend, int size, int total, TrendBucket bucket, DataPoint dp) {
		int iResult[] = new int[size];
		int iTotal = total;
		Object dNew = 0.0D;
		long time = System.currentTimeMillis();
		for (int i = 0; i < iTotal; i++) {
			Number prior = dp.val == null ? 0 : (Number)dp.val;
			dp = bucket.GetBucket(trend, time, prior);
			iResult[dp.bucket] ++;
		}
		return iResult;
	}
	
	private boolean checkCurve(int[] result, int mu, int sigma, int total) {

		boolean bError = false;

		bError = checkResultPct(result, mu, sigma, 1, total, 0.64D, 0.71D) || bError;

		bError = checkResultPct(result, mu, sigma, 2, total, 0.935D, 0.965D) || bError;

		bError = checkResultPct(result, mu, sigma, 3, total, 0.99D, 1.01D) || bError;
		
		return bError;
	}

	private boolean checkResultPct(int[] result, int mu, int sigma, int idev, int total, double lower, double high) {
		int iCheck = 0;
		int start = mu - (sigma*idev);
		int end = mu + (sigma*idev);
		
		for (int iCnt=start; iCnt < end; iCnt++) {
			iCheck += result[iCnt];
		}
		double dPct = Double.valueOf(iCheck)/Double.valueOf(total);
		boolean bError = (dPct < lower || dPct > high); 
//		System.out.println("+-" + sigma + " sigma " + dPct + ", " + bError);
		
		return bError; 
	}
	


}
