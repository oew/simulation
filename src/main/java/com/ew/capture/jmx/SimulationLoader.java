package com.ew.capture.jmx;

public interface SimulationLoader {
	public void configure(String config);
	public void load();
	public void reload();
	public void load(long timestamp);
	public void reload(long timestamp);
	public void getMap();
	public void getMap(long timestamp);
}
