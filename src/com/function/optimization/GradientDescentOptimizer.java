package com.function.optimization;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.omg.CORBA.portable.ValueBase;

import static com.utilities.ArithmeticUtilities.*;
import com.function.DerivableFunction;
import com.function.Function;
import com.utilities.CRFException;
import com.utilities.StringUtilities;
import com.utilities.VectorUtilities;
/**
 * A {@link Minimizer} which updates the function's input by moving it along the negation of its
 * gradient.
 * This method is simple but <B> inefficient</B>.
 * 
 * @author 1001937
 *
 */
public class GradientDescentOptimizer extends Minimizer<DerivableFunction>{
	public static final BigDecimal DEFAULT_RATE = big(0.01);
	public static final BigDecimal DEFAULT_CONVERGENCE_THRESHOLD = big(0.0001);
	
	@SuppressWarnings("unused")
	private final BigDecimal rate;
	private final BigDecimal convergenceThreshold;
	
	private boolean calculated = false;
	private BigDecimal value;
	private BigDecimal[] point;
	
	private static final Logger  logger = Logger.getLogger(GradientDescentOptimizer.class);
	
	public GradientDescentOptimizer(DerivableFunction function){
		this(function, DEFAULT_RATE, DEFAULT_CONVERGENCE_THRESHOLD);
	}
	
	public GradientDescentOptimizer(DerivableFunction function, BigDecimal rate, BigDecimal convergenceThreshold){
		super(function);
		this.rate = rate;
		this.convergenceThreshold = convergenceThreshold;
	}
	
	@Override
	public void find() {
		LineSearch<DerivableFunction> lineSearch = new ArmijoLineSearch<DerivableFunction>();
		
		int size = function.size();
		point = new BigDecimal[size];
		for(int i=0; i<size; i++)
			point[i] = BigDecimal.ZERO;
		
		value = function.value(point);
		BigDecimal oldValue = value; 
		int debug_iterationIndex = 0;
		do{
			oldValue = value;
			BigDecimal[] gradient = function.gradient(point);
			BigDecimal actualRate = lineSearch.findRate(function, point, VectorUtilities.multipyByScalar(BigDecimal.ONE.negate(), gradient));	
			singleStepUpdate(size, point, gradient, actualRate);
			value = function.value(point);
			if(logger.isDebugEnabled()){
				logger.debug(StringUtilities.arrayOfBigDecimalToString(point)+" = "+String.format("%-3.3f", value));
			}
			debug_iterationIndex++;

		}while(safeSubstract(oldValue, value).abs().compareTo(this.convergenceThreshold)>0);
		if(logger.isDebugEnabled()){
			logger.debug("Gradient -descent: number of iterations: "+debug_iterationIndex);
		}
		calculated = true;
	}
	
	@Override
	public BigDecimal getValue() {
		if (!calculated) throw new CRFException("Not calculated");
		return value;
	}
	
	@Override
	public BigDecimal[] getPoint() {
		if (!calculated) throw new CRFException("Not calculated");
		return point;
	}
	
	public static final void singleStepUpdate(final int size, final BigDecimal[] point, BigDecimal[] gradient, final BigDecimal rate){
		//size must be equal to point.length
		for(int i=0; i<size; i++)
		{
			point[i] = safeAdd(point[i], safeMultiply(gradient[i].negate(), rate));
		}
	}
	
}
