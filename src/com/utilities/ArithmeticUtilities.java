package com.utilities;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import javax.swing.tree.ExpandVetoException;

import org.apache.log4j.Logger;



public class ArithmeticUtilities {
	
	public static final MathContext MC = MathContext.DECIMAL128;
	public static final BigDecimal DOUBLE_MAX = big(Double.MAX_VALUE);
	
	public static final BigDecimal BIG_DECIMAL_E = big(Math.E);
	public static final BigDecimal BIG_DECIMAL_TWO = new BigDecimal("2.0", MC);
	public static final BigDecimal BIG_DECIMAL_E_TO_512 = BIG_DECIMAL_E.pow(512);
	public static final BigDecimal BIG_DECIMAL_512 = new BigDecimal("512", MC);
	public static final BigDecimal BIG_DECIMAL_LOG_DOUBLE_MAX = big(Math.log(Double.MAX_VALUE));
	
	public static BigDecimal big(double d)
	{
		return new BigDecimal(d, MC);
	}
	
	public static BigDecimal log(BigDecimal d){
		BigDecimal ret = BigDecimal.ZERO;
		while(d.compareTo(DOUBLE_MAX)>0){
			ret = safeAdd(ret, BIG_DECIMAL_512);
			d = safeDivide(d, BIG_DECIMAL_E_TO_512);
			if(d.compareTo(BigDecimal.ONE)<0){
				throw new CRFException("Anomaly");
			}
		}
		ret = safeAdd(ret, big(Math.log(d.doubleValue())));
		return ret;
		
	}
	
	public static BigDecimal exp(BigDecimal d){
		if(d.compareTo(BIG_DECIMAL_LOG_DOUBLE_MAX)<=0){
			return big(Math.exp(d.doubleValue()));
		} else {
			BigDecimal half = safeDivide(d, BIG_DECIMAL_TWO);
			BigDecimal halfExp = exp(half);
			return safeMultiply(halfExp, halfExp);
		}
	}
	
	public static BigDecimal safeAdd(final BigDecimal d1, final BigDecimal d2){
		return d1.add(d2, MC);
	}
	
	public static BigDecimal safeSubstract(final BigDecimal d1, final BigDecimal d2){
		return d1.subtract(d2, MC);
	}
	
	public static BigDecimal safeMultiply(final BigDecimal d1, final BigDecimal d2){
	
		return d1.multiply(d2, MC);
	}
	
	public static BigDecimal safeMultiply(final BigDecimal d1, final BigDecimal d2, final BigDecimal...ds){
		BigDecimal ret = d1.multiply(d2, MC);
		for (BigDecimal d: ds){
			ret = safeMultiply(ret, d);
		}
		return ret;
	}
	public static BigDecimal safeDivide(final BigDecimal d1, final BigDecimal d2){
		return d1.divide(d2, MC);
	}
}
