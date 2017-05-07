package com.function.optimization;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

import com.function.Function;
import com.utilities.ArithmeticUtilities;
import static com.utilities.ArithmeticUtilities.*;

import org.apache.log4j.Logger;

import com.function.DerivableFunction;
import com.utilities.CRFException;
import com.utilities.DerivableFunctionWithLastCache;
import com.utilities.StringUtilities;
import static  com.utilities.VectorUtilities.*;



/**
 * Implementation of L-BFGS algorithm for minimizing a function.
 * <BR>
 * L-BFGS stands for "Limited memory BFGS", where "BFGS" is an acronym of
 * "Broyden Fletcher Goldfarb Shanno" who developed the BFGS algorithm.
 * <BR>
 * The BFGS algorithm approximates Newton method for optimization, by approximating
 * the inverse of the Hessian without calculating the exact Hessian.
 * The L-BFGS algorithm approximates the BFGS algorithm by approximating calculations
 * that are performed with the inverse of the Hessian, but stores neither the
 * inverse of the Hessian nor its approximation in the memory.
 * Thus the L-BFGS algorithm is much cheaper in space complexity notion.
 * <BR>
 * The L-BFGS algorithm is described in the book "Numerical Optimization" by Jorge Nocedal and Stephen J. Wright,
 * Chapter 9. The book can be downloaded from http://www.bioinfo.org.cn/~wangchao/maa/Numerical_Optimization.pdf 
 *  
 * @author 1001937
 *
 * @param <F>
 */
public class LbfgsMinimizer extends Minimizer<DerivableFunction>{
	
	public static final int DEFAULT_NUMBER_OF_PREVIOUS_ITERATIONS_TO_MEMORIZE = 20;
	public static final BigDecimal DEFAULT_GRADIENT_CONVERGENCE = big(0.01);
	
	//input
	private final int numberOfPreviousIterationsToMemorize; 
	private final BigDecimal convergence;
	private final BigDecimal convergenceSquare;
	
	private BigDecimal[] initialPoint = null;
	private DebugInfo debugInfo = null;
	
	//internals
	private LinkedList<PointAndGradientSubstraction> previousIterations;
	private boolean calculated;
	
	//output
	private BigDecimal[] point = null;
	private BigDecimal value = BigDecimal.ZERO;
	private static final Logger logger = Logger.getLogger(LbfgsMinimizer.class);
	public LbfgsMinimizer(DerivableFunction function){
		this(function, DEFAULT_NUMBER_OF_PREVIOUS_ITERATIONS_TO_MEMORIZE, DEFAULT_GRADIENT_CONVERGENCE);
	}
	
	public LbfgsMinimizer(DerivableFunction function, int numberOfPreviousIterationToMemorize, BigDecimal convergence){
//		super(function);
		super(new DerivableFunctionWithLastCache(function));
		this.numberOfPreviousIterationsToMemorize = numberOfPreviousIterationToMemorize;
		this.convergence = convergence;
		this.convergenceSquare = safeMultiply(convergence, convergence);
	}
	
	public void setInitialPoint(BigDecimal[] initialPoint){
		if(initialPoint.length!= function.size())
			throw new CRFException("Wrong length of initial point specificed by the caller.");
		this.initialPoint = initialPoint;
	}
	
	private void initializeInitialPoint(){
		point = new BigDecimal[function.size()];
		if(this.initialPoint == null){
			for(int i =0; i<point.length;i++){
				point[i] = 	BigDecimal.ZERO;
			}
		} else {
			if(this.initialPoint.length != point.length)
				throw new CRFException("Wrong length of initial point specified by the caller.");
			for(int i=0; i<this.point.length;i++){
				point[i] = this.initialPoint[i];
			}
		}
	}
	
	private BigDecimal[] twoLoopRecursion(BigDecimal[] point)
	{
		ArrayList<BigDecimal> rhoList = new ArrayList<BigDecimal>(previousIterations.size());
		ArrayList<BigDecimal> alphaList = new ArrayList<BigDecimal>(previousIterations.size());
		
		BigDecimal[] q = function.gradient(point);
		for (PointAndGradientSubstraction substractions : previousIterations)
		{
			BigDecimal rho = safeDivide(BigDecimal.ONE,  product(substractions.getGradientSubstraction(), substractions.getPointSubstraction()));
			rhoList.add(rho);
			BigDecimal alpha = safeMultiply(rho,  product(substractions.getPointSubstraction(), q));
			alphaList.add(alpha);
			
			q =  substractVectors(q,  multipyByScalar(alpha, substractions.getGradientSubstraction()) );
		}
		
		BigDecimal[] r = calculateInitial_r_forTwoLoopRecursion(q);

		ListIterator<PointAndGradientSubstraction> previousIterationsIterator = previousIterations.listIterator(previousIterations.size());
		ListIterator<BigDecimal> rhoIterator = rhoList.listIterator(rhoList.size());
		ListIterator<BigDecimal> alphaIterator = alphaList.listIterator(alphaList.size());
		while (previousIterationsIterator.hasPrevious()&&rhoIterator.hasPrevious()&&alphaIterator.hasPrevious())
		{
			PointAndGradientSubstraction substractions = previousIterationsIterator.previous();
			BigDecimal rho = rhoIterator.previous();
			BigDecimal alpha = alphaIterator.previous();
			
			BigDecimal beta = safeMultiply(rho,  product(substractions.getGradientSubstraction(), r));
			r =  addVectors( r, multipyByScalar(safeSubstract(alpha,beta) , substractions.getPointSubstraction()) );
		}
		if ((previousIterationsIterator.hasPrevious()||rhoIterator.hasPrevious()||alphaIterator.hasPrevious())) {throw new CRFException("BUG");}
		
		return r;
	}
	
	
	private BigDecimal[] calculateInitial_r_forTwoLoopRecursion(BigDecimal[] q)
	{
		BigDecimal gamma = BigDecimal.ONE;
		if (previousIterations.size()>=1)
		{
			PointAndGradientSubstraction lastSubstraction = previousIterations.get(0);
			gamma = safeDivide(
					 product(lastSubstraction.getPointSubstraction(), lastSubstraction.getGradientSubstraction())
					,
					 product(lastSubstraction.getGradientSubstraction(), lastSubstraction.getGradientSubstraction())
					);
		}
		
		BigDecimal[] r = multipyByScalar(gamma, q);
		return r;
	}
	
	
	@Override
	public void find() {
		previousIterations = new LinkedList<PointAndGradientSubstraction>();
		LineSearch<DerivableFunction> lineSearch = new ArmijoLineSearch<DerivableFunction>();
		
		initializeInitialPoint();
		value = function.value(point);
		if (logger.isInfoEnabled()) {logger.info("LBFGS: initial value = "+StringUtilities.bigDecimalToString(value));}
		BigDecimal[] gradient = function.gradient(point);
		BigDecimal previousValue = value;
		int forLogger_iterationIndex=0;
		while ( euclideanNormSquare(gradient).compareTo(convergenceSquare)>0)
		{
			if (logger.isDebugEnabled()) {logger.debug(String.format("Gradient norm square = %s", StringUtilities.bigDecimalToString(euclideanNormSquare(gradient)) ));}
			previousValue = value;
			BigDecimal[] previousPoint = Arrays.copyOf(point, point.length);
			BigDecimal[] previousGradient = Arrays.copyOf(gradient, gradient.length);

			// 1. Update point (which is the vector "x").
			
			BigDecimal[] direction =  multipyByScalar(BigDecimal.ONE.negate(), twoLoopRecursion(point));
			BigDecimal alpha_rate = lineSearch.findRate(function, point, direction);
			point =  addVectors(point,  multipyByScalar(alpha_rate, direction));
			
			// 2. Prepare next iteration
			value = function.value(point);
			gradient = function.gradient(point);

			previousIterations.add(new PointAndGradientSubstraction(substractVectors(point, previousPoint), substractVectors(gradient, previousGradient)));
			if (previousIterations.size()>numberOfPreviousIterationsToMemorize)
			{
				previousIterations.removeFirst();
			}
			if (previousIterations.size()>numberOfPreviousIterationsToMemorize) {throw new CRFException("BUG");}

			
			// 3. Print log messages
			++forLogger_iterationIndex;
			if (value.compareTo(previousValue)>0) {logger.error("LBFGS: value > previous value");}
			if (logger.isInfoEnabled()) {logger.info("LBFGS iteration "+forLogger_iterationIndex+": value = "+StringUtilities.bigDecimalToString(value)  );}
			if ( (debugInfo!=null) && (logger.isInfoEnabled()) )
			{
				logger.info(debugInfo.info(point));
			}
		}
		calculated = true;
		
	}
	
	public static interface DebugInfo
	{
		public String info(BigDecimal[] point);
	}
	
	@Override
	public BigDecimal[] getPoint() {
		if(!this.calculated)
			throw new CRFException("Not calculated");
		return this.point;
	}
	
	@Override
	public BigDecimal getValue() {
		if(!this.calculated)
			throw new CRFException("Not calculated");
		return this.value;
	}
	
	
}
