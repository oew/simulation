package com.ew.simulation;


/**
 * <p>The Trend interface is the primary mechanism for extending the Simulator. </p>
 * 
 * <p>
 * Each trend definitions stored in the configuration Json.  These definitions are loaded, and executed by the tredn  
 * </p>
 * Implementations of this interface are the  {@link ConstantTrend}
 * 
 * @author Everett Williams
 * @date 05/21/2020
 * @version 1.0
 *
 */
public class Trend {
	public String classname;
	public String attribute;
	public boolean isIncremental;
	/**
	 * The Trend class name for the underlying JSon implementation.  This is used by the GSon {@ling PolyMorphicAdapterFactory} when instantiating the class in to a trend list.
	 *  
	 * @return  the classname for the trend.
	 */
	public String  getType() {
		return "";
	};
	/**
	 * The map attribute the trend is applied to.
	 * 
	 * @return a string containing the map attribute which the trend is applied.
	 */
	public String  getAttribute() {
		return attribute;
	};
	/**
	 * Set if the trend is applied incrementally to the the prior value.
	 * 
	 * @param isIncremental  true if the trend value is applied incrementally.  false if the prior value is over written.
	 */
	public void setIncremental(boolean incremental)
	{
		isIncremental = incremental;
	};
	/**
	 * Determine if the trend values are applied incrementatlly to the prior value.
	 * 
	 * @return true if the trend value is applied incrementally.  false if the prior value is over written.
	 */
	public boolean isIncremental() {
		return isIncremental;
	};
	
	/** 
	 * Returns the next value in the trend sequence.
	 * 
	 * @param timestamp  The time stamp of the sequence.
	 * @param prior The prior value of the sequence.
	 * @return the next Double value of the sequence.
	 */
	public Double  nextDouble(long timestamp, Double prior)
	{
		throw new UnsupportedOperationException();
	};
	
	/** 
	 * Returns the next value in the trend sequence.
	 * 
	 * @param timestamp  The time stamp of the sequence.
	 * @param prior The prior value of the sequence.
	 * @return the next Integer value of the sequence.
	 */
	public Integer nextInteger(long timestamp, Integer prior) {
		throw new UnsupportedOperationException();
	};
	
	/** 
	 * Returns the next value in the trend sequence.
	 * 
	 * @param timestamp  The time stamp of the sequence.
	 * @param prior The prior value of the sequence.
	 * @return the next Long value of the sequence.
	 */
	public Long    nextLong(long timestamp, Long prior) {
		throw new UnsupportedOperationException();
	};

	/** 
	 * Returns the next value in the trend sequence.
	 * 
	 * @param timestamp  The time stamp of the sequence.
	 * @param prior The prior value of the sequence.
	 * @return the next String value of the sequence.
	 */
	public String  nextString(long timestamp) {
		throw new UnsupportedOperationException();
	};

	/** 
	 * Returns the next value in the trend sequence.
	 * 
	 * @param timestamp  The time stamp of the sequence.
	 * @return the next long date time value of the sequence.
	 */
	public long    nextDatetime(long timestamp) {
		throw new UnsupportedOperationException();
	};

}
