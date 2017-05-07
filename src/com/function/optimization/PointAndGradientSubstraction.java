package com.function.optimization;

import java.math.BigDecimal;

/**
 * Encapsulates the substraction of two given points, and the substraction of the gradient calculated for them (for a given
 * function).
 * This class is used by {@link LbfgsMinimizer}.
 * 
 * 
 * @author 1001937
 *
 */
public class PointAndGradientSubstraction {

	private final BigDecimal[] _pointSubstraction;
	private final BigDecimal[] _gradientSubstraction;

	
	public PointAndGradientSubstraction(BigDecimal[] pointSubstrction, BigDecimal[] gradientSubstraction){
		super();
		this._pointSubstraction = pointSubstrction;
		this._gradientSubstraction = gradientSubstraction;
	}
	
	public BigDecimal[] getPointSubstraction(){
		return this._pointSubstraction;
	}
	
	public BigDecimal[] getGradientSubstraction(){
		return this._gradientSubstraction;
	}

}
