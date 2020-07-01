package com.ew.simulation;

import static org.junit.Assert.*;

import org.junit.Test;

public class UniformTrendTest {

	@Test
	public void testNextDouble() {
		UniformTrend ut = new UniformTrend();
		ut.min = 1.0D;
		ut.reseed = false;
		ut.seed = System.currentTimeMillis();
		System.out.println(ut);
		long lNow = System.currentTimeMillis();
		int iResult[] = new int[100];
		int iTotal = 10000;
		long iNew = 0;
		double dNew = 0.0D;
		for (int i = 0; i < iTotal; i++) {
			double prior = dNew;
			dNew = ut.nextDouble(lNow, prior);
			iResult[Integer.valueOf(Long.toString(Math.round(dNew)))]++;
		}
		int iCheck = 0;
		boolean bError = false;
		for (int iCnt=1; iCnt < 11; iCnt++) {
			iCheck += iResult[iCnt];
			double dPct =Double.valueOf(iResult[iCnt])/Double.valueOf(iTotal);
			System.out.println(iCnt + ", " + dPct);
			if (dPct < 0.05D || dPct > 0.2D) {
				bError = true;
			}
		}
		if (bError) fail("Uniform data does not conform to the correct distribution");
	}

	@Test
	public void testNextInteger() {
		UniformTrend ut = new UniformTrend();
		ut.min = 1.0D;
		ut.reseed = false;
		ut.seed = System.currentTimeMillis();
		System.out.println(ut);
		long lNow = System.currentTimeMillis();
		int iResult[] = new int[100];
		int iTotal = 1000;
		int iNew = 0; 
		for (int i = 0; i < iTotal; i++) {
			int prior = iNew;
			iNew = ut.nextInteger(lNow, prior);
			iResult[iNew]++;
		}
		int iCheck = 0;
		boolean bError = false;
		for (int iCnt=1; iCnt < 11; iCnt++) {
			iCheck += iResult[iCnt];
			double dPct =Double.valueOf(iResult[iCnt])/Double.valueOf(iTotal);
			if (dPct < 0.05D || dPct > 0.2D) {
				bError = true;
			}
		}
		if (bError) fail("Uniform data does not conform to the correct distribution");
	}

	@Test
	public void testNextLong() {
		UniformTrend ut = new UniformTrend();
		ut.min = 1.0D;
		ut.reseed = false;
		ut.seed = System.currentTimeMillis();
		System.out.println(ut);
		long lNow = System.currentTimeMillis();
		int iResult[] = new int[100];
		int iTotal = 1000;
		long iNew = 0; 
		for (int i = 0; i < iTotal; i++) {
			long prior = iNew;
			iNew = ut.nextLong(lNow, prior);
			iResult[Integer.valueOf(Long.toString(iNew))]++;
		}
		int iCheck = 0;
		boolean bError = false;
		for (int iCnt=1; iCnt < 11; iCnt++) {
			iCheck += iResult[iCnt];
			double dPct =Double.valueOf(iResult[iCnt])/Double.valueOf(iTotal);
//			System.out.println(iCnt + ", " + dPct);
			if (dPct < 0.05D || dPct > 0.2D) {
				bError = true;
			}
		}
		if (bError) fail("Uniform data does not conform to the correct distribution");
	}

	@Test
	public void testToJSon() {
		
		UniformTrend gw = new UniformTrend();
		gw.max = 10.0D;
		gw.min = 3.0D;
		gw.reseed = true;
		gw.attribute = "G1";
		String json = gw.toJSon();
		Trend ut = UniformTrend.fromJSon(json);

		try {
			Integer i = ut.nextInteger(System.currentTimeMillis(), 1);
		} catch(Exception e) {
			fail("Serialization to and from JSON does not create a valid object");
		}
	}

	@Test
	public void testFromJSon() {
		UniformTrend trend = new UniformTrend();
		trend.max = 10.0D;
		trend.min = 3.0D;
		trend.reseed = true;
		trend.attribute = "G1";
		String json = trend.toJSon();
		Trend retrend = UniformTrend.fromJSon(json);

		try {
			Integer i = retrend.nextInteger(System.currentTimeMillis(), 1);
			Long l = retrend.nextLong(System.currentTimeMillis(), 1L);
		} catch(Exception e) {
			fail("Serialization to and from JSON does not create a valid object");
		}
	}
}
