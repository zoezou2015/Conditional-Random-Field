package com.utilities;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.function.DerivableFunction;
/**
 * A {@link DerivableFunction} that remembers the last computed value and gradient.
 * So, if the user calls {@link #value(double[])} for the same point (and there was
 * no other call to {@link #value(double[])} in between) then the value will no be computed twice. Rather, the value
 * computed by the first call is stored, and will be returned.
 * The same applies to {@link #gradient(double[])}.
 * 
 * @author ZOE
 *
 */
public class DerivableFunctionWithLastCache extends DerivableFunction {
	
	private final DerivableFunction realFunction;
	private final LastCache<BigDecimalArrayWrapper, BigDecimal> valueCache = new LastCache<BigDecimalArrayWrapper, BigDecimal>();
	private final LastCache<BigDecimalArrayWrapper, BigDecimal[]> gradientCache = new LastCache<BigDecimalArrayWrapper, BigDecimal[]>();
	private static final Logger logger = Logger.getLogger(DerivableFunctionWithLastCache.class);
	
	public DerivableFunctionWithLastCache(DerivableFunction realFunction){
		super();
		this.realFunction = realFunction;
	}
	
	@Override
	public BigDecimal value(BigDecimal[] point) {
		BigDecimal ret = BigDecimal.ZERO;
		BigDecimalArrayWrapper wrappedPoint = new BigDecimalArrayWrapper(point);
		BigDecimal fromCache = this.valueCache.get(wrappedPoint);
		if(fromCache == null){
			BigDecimal calculatedValue = this.realFunction.value(point);
			this.valueCache.put(wrappedPoint, calculatedValue);
			ret = calculatedValue;
		} else{
			logger.debug("Returning value from cache");
			ret = fromCache;
		}
		return ret;
	}
	
	@Override
	public BigDecimal[] gradient(BigDecimal[] point) {
		BigDecimal[] ret = null;
		BigDecimalArrayWrapper wrappedPoint = new BigDecimalArrayWrapper(point);
		BigDecimal[] fromCache = this.gradientCache.get(wrappedPoint);
		if(fromCache == null){
			BigDecimal[] calculatedGradient = this.realFunction.gradient(point);
			this.gradientCache.put(wrappedPoint, calculatedGradient);
			ret = calculatedGradient;
		} else{
			logger.debug("Returning gradient from cache");
			ret = fromCache;
		}
		return ret;
	}
	
	@Override
	public int size() {
		
		return this.realFunction.size();
	}
}
