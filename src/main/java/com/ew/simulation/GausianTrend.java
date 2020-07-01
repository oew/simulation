package com.ew.simulation;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.random.RandomDataGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.ew.gson.typeadapters.GsonFactory;
import com.ew.util.InterfaceSerializer;

public class GausianTrend extends Trend {

	long    seed;
	boolean reseed = true;
	double  mu    = 0.0D;
	double  sigma = 0.0D;
	
	public static void  main(String[] asArgs) {
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
		String listString = GsonFactory.getGSon().toJson(list);
		List<GausianTrend> t2 = GsonFactory.getGSon().fromJson(listString, List.class);
		System.out.println(listString);
		Number prior = 10;
		long time = System.currentTimeMillis();
		for (Trend incT : t2) {
			Number next = incT.nextLong(time, prior.longValue());
			prior = next;
			next = incT.nextDouble(time, prior.doubleValue());
			prior = next;
			next = incT.nextInteger(time, prior.intValue());
			prior = next;
		}		
	}
	
	public GausianTrend() {
		classname = this.getClass().getName(); 
	}
	
	public double pctSigma(int[] result, int mu, int sigma, int idev, int total) {
		int iCheck = 0;
		int start = mu - (sigma*idev);
		int end = mu + (sigma*idev);
		
		for (int iCnt=start; iCnt < end; iCnt++) {
			iCheck += result[iCnt];
		}
		return Double.valueOf(iCheck)/Double.valueOf(total);
	}
	
	@Override
	public Double nextDouble(long timestamp, Double prior) {
		if (isIncremental) 
			return prior + getRandom().nextGaussian(mu, sigma);
		else
			return getRandom().nextGaussian(mu, sigma);
	}

	@Override
	public Integer nextInteger(long timestamp, Integer prior) {
		if (isIncremental) 
			return prior + (int)Math.round(getRandom().nextGaussian(mu, sigma));
		else
			return (int)Math.round(getRandom().nextGaussian(mu, sigma));
	}

	@Override
	public Long nextLong(long timestamp, Long prior) {
		if (isIncremental) 
			return prior + (int)Math.round(getRandom().nextGaussian(mu, sigma));
		else
			return (Long)Math.round(getRandom().nextGaussian(mu, sigma));
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
	
	protected RandomDataGenerator getRandom() {
		if (rdg == null) {
			rdg = new RandomDataGenerator();
			if (reseed) rdg.reSeed(System.currentTimeMillis());
		}
		return rdg;
	}
	
	public String toJSon() {

		String json = GsonFactory.getGSon().toJson(this);
		return json;
	}
	
	public static Trend fromJSon(String json) {
		Object ret;
		ret = GsonFactory.getGSon().fromJson(json, GausianTrend.class);
		return (Trend)ret;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GausianWrapper [attribute=").append(attribute).
		append(", mu=").append(mu)
		.append(", sigma=").append(sigma)
		.append(", incremental=").append(isIncremental())
		.append(", reseed=").append(reseed).append("]");
		return sb.toString();
	}
	
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	
	private RandomDataGenerator rdg;
	
}
