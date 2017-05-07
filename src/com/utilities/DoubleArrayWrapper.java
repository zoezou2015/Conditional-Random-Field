package com.utilities;

import java.util.Arrays;

/**
 * Wrap double[] with {@link #equals(Object)} and {@link #hashCode()}.
 * @author 1001937
 *
 */
public class DoubleArrayWrapper {

	private final double[] _array;
	public DoubleArrayWrapper(double[] array){
		super();
		this._array = array;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime*result + Arrays.hashCode(this._array);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(this.getClass()!=obj.getClass())
			return false;
		DoubleArrayWrapper other = (DoubleArrayWrapper) obj;
		if(!Arrays.equals(this._array, other._array)){
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		
		return StringUtilities.arrayOfDoubleToString(this._array);
	}
}
