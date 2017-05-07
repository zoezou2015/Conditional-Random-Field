package com.utilities;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Wraps a {@link BigDecimal} array with equals() and hashCode()
 * @author ZOE
 *
 */

public class BigDecimalArrayWrapper {
	private BigDecimal[] _array;
	
	public BigDecimalArrayWrapper(BigDecimal[] array){
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
		if(this.getClass() != obj.getClass())
			return false;
		BigDecimalArrayWrapper other = (BigDecimalArrayWrapper) obj;
		if(!Arrays.equals(this._array, other._array))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		
		return StringUtilities.arrayOfBigDecimalToString(this._array);
	}
	
	
}
