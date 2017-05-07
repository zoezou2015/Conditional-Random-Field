package com.function.optimization;



import java.math.BigDecimal;

import com.function.DerivableFunction;
import com.utilities.ArithmeticUtilities;
import com.utilities.CRFException;
import static com.utilities.ArithmeticUtilities.*;

import static com.function.optimization.LineSearchUtilities.*;

/**
 * The Armijo line search is a relatively efficient inexact line search method.
 * <BR>
 * More information about Armijo line search can be found at the <B>Hebrew</B> notebook
 * about Optimization computational methods - theory and exercises notebook by
 * Dori Peleg for Technion course 046197 version 3.0, page 65.
 * <BR>
 * The notebook can be downloaded from here: http://webee.technion.ac.il/people/dorip/optimization%20book.pdf
 * 
 * @author 1001937
 *
 * @param <F>
 */
public class ArmijoLineSearch <F extends DerivableFunction> implements LineSearch<F>{
	
	public static final BigDecimal DEFAULT_BETA_RATE_OF_ALPHA = new BigDecimal(0.2, ArithmeticUtilities.MC);
	public static final BigDecimal DEFAULT_SIGMA_CONVERGENCE_COEFFICIENT = new BigDecimal(0.3, ArithmeticUtilities.MC);
	public static final BigDecimal DEFAULT_INITIAL_ALPHA = new BigDecimal(0.1,ArithmeticUtilities.MC);
	
	public static final BigDecimal MINIMUN_ALLOWED_ALPHA_VALUE_SO_SHOULD_BE_ZERO = new BigDecimal(0.000001, ArithmeticUtilities.MC);
	
	private final BigDecimal _beta_rateOfAlpha = DEFAULT_BETA_RATE_OF_ALPHA;
	private final BigDecimal _sigma_convergenceCoefficient = DEFAULT_SIGMA_CONVERGENCE_COEFFICIENT;
	private final BigDecimal _initialAlpha = DEFAULT_INITIAL_ALPHA;
	
	@Override
	public BigDecimal findRate(final F function, final BigDecimal[] point, final BigDecimal[] direction) {
		final BigDecimal valueForAlphaZero = valueForAlpha(function, point,direction,BigDecimal.ZERO);
		final BigDecimal derivableForAlphaZero = derivationForAlpha(function, point, direction, BigDecimal.ZERO);
		
		if(derivableForAlphaZero.compareTo(BigDecimal.ZERO)>=0)
			throw new CRFException("Tried to perform a line search, in a point and direction in which the function does not decrease.");
		
		BigDecimal ret = BigDecimal.ZERO;
		BigDecimal alpha = this._initialAlpha;
		
		if(safeSubstract(valueForAlpha(function, point, direction, alpha),valueForAlphaZero).compareTo(safeMultiply(this._sigma_convergenceCoefficient, alpha, derivableForAlphaZero))<0)
		{
			BigDecimal previousAlpha = alpha;
			do {
				previousAlpha = alpha;
				alpha = safeDivide(previousAlpha, this._beta_rateOfAlpha);
			} while (safeSubstract(valueForAlpha(function, point, direction, alpha), valueForAlphaZero).compareTo(safeMultiply(this._sigma_convergenceCoefficient, alpha, derivableForAlphaZero))<0);
			ret = previousAlpha;
		} else {
			do{
				alpha = safeMultiply(this._beta_rateOfAlpha, alpha);
				if(alpha.compareTo(MINIMUN_ALLOWED_ALPHA_VALUE_SO_SHOULD_BE_ZERO)<=0){
					alpha = BigDecimal.ZERO;
					break;
				}
			} while (safeSubstract(valueForAlpha(function, point, direction, alpha), valueForAlphaZero).compareTo(safeMultiply(this._sigma_convergenceCoefficient, alpha, derivableForAlphaZero))>=0);
			ret = alpha;
		}
		
		return ret;
		
	}
}
