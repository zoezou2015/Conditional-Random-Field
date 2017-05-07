package com.function.optimization;

import java.math.BigDecimal;

import com.function.Function;
import com.utilities.ArithmeticUtilities;

/**
 * A simple inexact and inaccurate non-efficient line search which merely returns a small
 * constant for any input.
 * @author 1001937
 *
 * @param <F>
 */
public class ConstantLineSearch <F extends Function> implements LineSearch<F> {
	
	public static final BigDecimal DEFAULT_RATE = new BigDecimal(0.01, ArithmeticUtilities.MC);
	private final BigDecimal _constantRate;
	
	public ConstantLineSearch(){
		this(DEFAULT_RATE);
	}
	
	public ConstantLineSearch(BigDecimal constantRate){
		super();
		this._constantRate = constantRate;
	}
	
	@Override
	public BigDecimal findRate(F function, BigDecimal[] point, BigDecimal[] direction) {
		
		return this._constantRate;
	}

}
