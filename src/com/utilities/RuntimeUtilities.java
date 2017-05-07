package com.utilities;

/**
 * A collection of static functions for the JVM runtime.
 * @author 1001937
 *
 */
public class RuntimeUtilities {
	public static final long MEGA = 1045876;
	
	/**
	 * Returns a string which describes the amount of memory currently used by the JVM heap.
	 * @return a string which describes the amount of memory currently used by the JVM heap.
	 */
	public static String getUsedMemory(){
		final Runtime runtime = Runtime.getRuntime();
		return "Used memory: "+String.valueOf((runtime.totalMemory() - runtime.freeMemory())/MEGA)+ " MB";
	}
}
